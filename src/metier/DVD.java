package metier;

import exceptions.EmpruntException;
import exceptions.ReservationException;
import exceptions.RetourException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DVD implements Document {

    private String id;
    private String titre;
    private boolean adulte;

    // 0 = dispo, 1 = reserve, 2 = emprunte
    private int etat = 0;
    private Abonne abonneActuel;
    private LocalDateTime heureFinReservation;
    private LocalDateTime heureEmprunt;

    public DVD(String id, String titre, boolean adulte) {
        this.id = id;
        this.titre = titre;
        this.adulte = adulte;
    }

    public String idDoc() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public int getEtat() {
        return etat;
    }

    public Abonne getAbonneActuel() {
        return abonneActuel;
    }

    public LocalDateTime getHeureFinReservation() {
        return heureFinReservation;
    }

    public LocalDateTime getHeureEmprunt() {
        return heureEmprunt;
    }

    // verifie si la resa a expire
    private void checkExpiration() {
        if (etat == 1 && heureFinReservation != null && LocalDateTime.now().isAfter(heureFinReservation)) {
            etat = 0;
            abonneActuel = null;
            heureFinReservation = null;
        }
    }

    public long getSecondesRestantes() {
        if (heureFinReservation == null) return 0;
        return java.time.Duration.between(LocalDateTime.now(), heureFinReservation).getSeconds();
    }

    public synchronized void reservation(Abonne ab) throws ReservationException {
        checkExpiration();

        if (ab.estBanni())
            throw new ReservationException("Vous etes banni jusqu'au " + ab.getFinBan());

        if (adulte && ab.getAge() < 16)
            throw new ReservationException("Ce DVD est reserve aux plus de 16 ans");

        if (etat == 2)
            throw new ReservationException("Ce DVD est deja emprunte");

        if (etat == 1) {
            String heure = heureFinReservation.format(DateTimeFormatter.ofPattern("HH'h'mm"));
            throw new ReservationException("Ce DVD est deja reserve jusqu'a " + heure);
        }

        etat = 1;
        abonneActuel = ab;
        heureFinReservation = LocalDateTime.now().plusMinutes(120);
    }

    public synchronized void emprunt(Abonne ab) throws EmpruntException {
        checkExpiration();

        if (ab.estBanni())
            throw new EmpruntException("Vous etes banni jusqu'au " + ab.getFinBan());

        if (adulte && ab.getAge() < 16)
            throw new EmpruntException("Ce DVD est reserve aux plus de 16 ans");

        if (etat == 2)
            throw new EmpruntException("Ce DVD est deja emprunte");

        if (etat == 1 && !abonneActuel.equals(ab)) {
            String heure = heureFinReservation.format(DateTimeFormatter.ofPattern("HH'h'mm"));
            throw new EmpruntException("Ce DVD est reserve pour quelqu'un d'autre jusqu'a " + heure);
        }

        etat = 2;
        abonneActuel = ab;
        heureFinReservation = null;
        heureEmprunt = LocalDateTime.now();
    }

    public synchronized void retour() throws RetourException {
        if (etat == 0)
            throw new RetourException("Ce DVD n'est pas emprunte");

        // geronimo : retard > 2 semaines
        if (etat == 2 && heureEmprunt != null && abonneActuel != null) {
            if (LocalDateTime.now().isAfter(heureEmprunt.plusWeeks(2))) {
                abonneActuel.bannir();
            }
        }

        etat = 0;
        abonneActuel = null;
        heureFinReservation = null;
        heureEmprunt = null;
    }

    public synchronized void retourDegrade() throws RetourException {
        if (etat == 2 && abonneActuel != null) {
            System.out.println("Document degrade, bannissement de " + abonneActuel.getNom());
            abonneActuel.bannir();
        }
        retour();
    }
}
