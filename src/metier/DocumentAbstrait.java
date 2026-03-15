package metier;

import exceptions.ReservationException;
import exceptions.EmpruntException;
import exceptions.RetourException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe abstraite portant la logique d'état (DISPONIBLE/RESERVE/EMPRUNTE).
 * Livre et DVD héritent de cette classe et surchargent verifierDroits*
 * si besoin (ex: restriction d'âge pour DVD adulte).
 */
public abstract class DocumentAbstrait implements Document {

    protected final String id;
    protected final String titre;
    protected EtatDocument etat;
    protected Abonne abonneEnCours;
    protected LocalDateTime finReservation;
    protected LocalDateTime dateEmprunt; // Geronimo : pour détecter les retards

    public static final long DUREE_RESERVATION_MIN = 120; // 2h
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH'h'mm");

    public DocumentAbstrait(String id, String titre) {
        this.id = id;
        this.titre = titre;
        this.etat = EtatDocument.DISPONIBLE;
    }

    @Override
    public String idDoc() { return id; }

    public String getTitre() { return titre; }
    public EtatDocument getEtat() { return etat; }
    public Abonne getAbonneEnCours() { return abonneEnCours; }
    public LocalDateTime getFinReservation() { return finReservation; }
    public LocalDateTime getDateEmprunt() { return dateEmprunt; }

    /** Vérifie et annule la réservation si elle a expiré. */
    public synchronized void verifierExpiration() {
        if (etat == EtatDocument.RESERVE
                && finReservation != null
                && LocalDateTime.now().isAfter(finReservation)) {
            etat = EtatDocument.DISPONIBLE;
            abonneEnCours = null;
            finReservation = null;
        }
    }

    /** Secondes restantes sur la réservation. Négatif si expirée. */
    public long secondesRestantes() {
        if (finReservation == null) return 0;
        return java.time.Duration.between(LocalDateTime.now(), finReservation).getSeconds();
    }

    // --- interface Document ---

    @Override
    public synchronized void reservation(Abonne ab) throws ReservationException {
        verifierExpiration();

        if (ab.estBanni())
            throw new ReservationException("Vous etes banni jusqu'au " + ab.getFinBannissement() + ".");

        verifierDroitsReservation(ab);

        switch (etat) {
            case EMPRUNTE:
                throw new ReservationException("\"" + titre + "\" est deja emprunte.");
            case RESERVE:
                throw new ReservationException("\"" + titre + "\" est deja reserve jusqu'a "
                        + finReservation.format(FMT) + ".");
            default: // DISPONIBLE
                etat = EtatDocument.RESERVE;
                abonneEnCours = ab;
                finReservation = LocalDateTime.now().plusMinutes(DUREE_RESERVATION_MIN);
        }
    }

    @Override
    public synchronized void emprunt(Abonne ab) throws EmpruntException {
        verifierExpiration();

        if (ab.estBanni())
            throw new EmpruntException("Vous etes banni jusqu'au " + ab.getFinBannissement() + ".");

        verifierDroitsEmprunt(ab);

        switch (etat) {
            case EMPRUNTE:
                throw new EmpruntException("\"" + titre + "\" est deja emprunte.");
            case RESERVE:
                if (!abonneEnCours.equals(ab))
                    throw new EmpruntException("\"" + titre + "\" est reserve pour un autre abonne jusqu'a "
                            + finReservation.format(FMT) + ".");
                // réservé pour cet abonné : ok, on passe à EMPRUNTE
                break;
            default: // DISPONIBLE
                break;
        }
        etat = EtatDocument.EMPRUNTE;
        abonneEnCours = ab;
        finReservation = null;
        dateEmprunt = LocalDateTime.now();
    }

    @Override
    public synchronized void retour() throws RetourException {
        if (etat == EtatDocument.DISPONIBLE)
            throw new RetourException("\"" + titre + "\" n'est ni emprunte ni reserve.");

        // Geronimo : retard > 2 semaines
        if (etat == EtatDocument.EMPRUNTE && dateEmprunt != null && abonneEnCours != null) {
            if (LocalDateTime.now().isAfter(dateEmprunt.plusWeeks(2))) {
                System.out.println("[GERONIMO] Retard sur \"" + titre + "\" -> bannissement de "
                        + abonneEnCours.getNom());
                abonneEnCours.bannir();
            }
        }

        etat = EtatDocument.DISPONIBLE;
        abonneEnCours = null;
        finReservation = null;
        dateEmprunt = null;
    }

    /**
     * Retour avec dégradation signalée (Geronimo).
     */
    public synchronized void retourDegrade() throws RetourException {
        if (etat == EtatDocument.EMPRUNTE && abonneEnCours != null) {
            System.out.println("[GERONIMO] Degradation sur \"" + titre + "\" -> bannissement de "
                    + abonneEnCours.getNom());
            abonneEnCours.bannir();
        }
        retour();
    }

    // --- à surcharger si besoin ---
    protected void verifierDroitsReservation(Abonne ab) throws ReservationException {}
    protected void verifierDroitsEmprunt(Abonne ab) throws EmpruntException {}

    @Override
    public String toString() {
        return "[" + id + "] \"" + titre + "\" -> " + etat;
    }
}
