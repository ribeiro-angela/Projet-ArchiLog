package metier;

import java.time.LocalDate;
import java.time.Period;

public class Abonne {

    private int numero;
    private String nom;
    private LocalDate dateNaissance;
    private boolean banni = false;
    private LocalDate finBan;

    public Abonne(int numero, String nom, LocalDate dateNaissance) {
        this.numero = numero;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
    }

    public int getNumero() {
        return numero;
    }

    public String getNom() {
        return nom;
    }

    public int getAge() {
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    public boolean estBanni() {
        if (banni && LocalDate.now().isAfter(finBan)) {
            banni = false;
        }
        return banni;
    }

    public void bannir() {
        banni = true;
        finBan = LocalDate.now().plusMonths(1);
        System.out.println(nom + " est banni jusqu'au " + finBan);
    }

    public LocalDate getFinBan() {
        return finBan;
    }

    public String toString() {
        return numero + " - " + nom;
    }
}
