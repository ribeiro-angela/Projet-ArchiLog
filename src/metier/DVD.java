package metier;

import exceptions.ReservationException;
import exceptions.EmpruntException;

public class DVD extends DocumentAbstrait {

    private final boolean adulte; // true = réservé aux +16 ans

    public DVD(String id, String titre, boolean adulte) {
        super(id, titre);
        this.adulte = adulte;
    }

    public boolean isAdulte() { return adulte; }

    @Override
    protected void verifierDroitsReservation(Abonne ab) throws ReservationException {
        if (adulte && ab.getAge() < 16)
            throw new ReservationException("Vous devez avoir au moins 16 ans pour reserver ce DVD.");
    }

    @Override
    protected void verifierDroitsEmprunt(Abonne ab) throws EmpruntException {
        if (adulte && ab.getAge() < 16)
            throw new EmpruntException("Vous devez avoir au moins 16 ans pour emprunter ce DVD.");
    }

    @Override
    public String toString() {
        return super.toString() + " [DVD" + (adulte ? ", adulte" : "") + "]";
    }
}
