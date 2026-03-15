package serveur;

import metier.*;

import java.time.LocalDate;

/**
 * Point d'entrée de l'application serveur.
 * Lance les 3 serveurs d'écoute sur les ports 2000, 2001, 2002.
 * Les données (abonnés + documents) sont créées en dur ici.
 */
public class AppServeur {

    public static void main(String[] args) throws InterruptedException {

        // --- Initialisation des données ---
        Mediatheque m = new Mediatheque();

        // Abonnés
        m.ajouterAbonne(new Abonne(1, "Alice Dupont",   LocalDate.of(1990, 3, 15)));
        m.ajouterAbonne(new Abonne(2, "Bob Martin",     LocalDate.of(2010, 7, 20))); // mineur
        m.ajouterAbonne(new Abonne(3, "Clara Leclerc",  LocalDate.of(1985, 11, 5)));

        // Livres
        m.ajouterDocument(new Livre("L001", "Le Seigneur des Anneaux", 1200));
        m.ajouterDocument(new Livre("L002", "Harry Potter T1",          320));
        m.ajouterDocument(new Livre("L003", "Clean Code",               464));

        // DVDs
        m.ajouterDocument(new DVD("D001", "Inception",       true));   // adulte
        m.ajouterDocument(new DVD("D002", "Toy Story",       false));  // tout public
        m.ajouterDocument(new DVD("D003", "The Dark Knight", true));   // adulte

        // --- Lancement des 3 serveurs ---
        new ServeurEcoute(2000, m, ServiceReservation::new).start();
        new ServeurEcoute(2001, m, ServiceEmprunt::new).start();
        new ServeurEcoute(2002, m, ServiceRetour::new).start();

        System.out.println("=== Mediatheque demarree (ports 2000/2001/2002) ===");

        // Garder le programme principal en vie indéfiniment
        Thread.currentThread().join();
    }
}
