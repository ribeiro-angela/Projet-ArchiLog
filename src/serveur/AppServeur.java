package serveur;

import metier.*;

import java.time.LocalDate;

public class AppServeur {

    public static void main(String[] args) throws InterruptedException {

        Mediatheque m = new Mediatheque();
//ajout de chat gpt car trop long a la main
        m.ajouterAbonne(new Abonne(1, "Alice", LocalDate.of(1990, 3, 15)));
        m.ajouterAbonne(new Abonne(2, "Bob", LocalDate.of(2010, 7, 20)));
        m.ajouterAbonne(new Abonne(3, "Clara", LocalDate.of(1985, 11, 5)));

        m.ajouterDocument(new Livre("L001", "Le Seigneur des Anneaux", 1200));
        m.ajouterDocument(new Livre("L002", "Harry Potter", 320));
        m.ajouterDocument(new Livre("L003", "Clean Code", 464));

        m.ajouterDocument(new DVD("D001", "Inception", true));
        m.ajouterDocument(new DVD("D002", "Toy Story", false));
        m.ajouterDocument(new DVD("D003", "The Dark Knight", true));

        new ServeurEcoute(2000, m, "reservation").start();
        new ServeurEcoute(2001, m, "emprunt").start();
        new ServeurEcoute(2002, m, "retour").start();

        Thread.currentThread().join();
    }
}
