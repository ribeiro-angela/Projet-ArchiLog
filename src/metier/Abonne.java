package metier;

import java.time.LocalDate;
import java.time.Period;

public class Abonne {
    private final int numero;
    private final String nom;
    private final LocalDate dateNaissance;
    // Geronimo : date de fin de bannissement (null = pas banni)
    private LocalDate finBannissement;

    public Abonne(int numero, String nom, LocalDate dateNaissance) {
        this.numero = numero;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
    }

    public int getNumero() { return numero; }
    public String getNom() { return nom; }
    public LocalDate getDateNaissance() { return dateNaissance; }

    public int getAge() {
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    // Geronimo
    public boolean estBanni() {
        if (finBannissement == null) return false;
        if (LocalDate.now().isAfter(finBannissement)) {
            finBannissement = null;
            return false;
        }
        return true;
    }

    public void bannir() {
        this.finBannissement = LocalDate.now().plusMonths(1);
        System.out.println("[GERONIMO] " + nom + " banni jusqu'au " + finBannissement);
    }

    public LocalDate getFinBannissement() { return finBannissement; }

    @Override
    public String toString() {
        return "Abonne#" + numero + "(" + nom + ")";
    }
}
