package serveur;

import exceptions.ReservationException;
import metier.Abonne;
import metier.DVD;
import metier.Document;
import metier.Livre;
import metier.Mediatheque;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServiceReservation implements Runnable {

    private Socket socket;
    private Mediatheque mediatheque;

    public ServiceReservation(Socket socket, Mediatheque mediatheque) {
        this.socket = socket;
        this.mediatheque = mediatheque;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String ligne = in.readLine();
            String[] tab = ligne.split(" ");

            if (tab.length < 2) {
                out.println("ERREUR format incorrect, envoyer : numAbonne idDoc");
                socket.close();
                return;
            }

            int numAbonne = Integer.parseInt(tab[0]);
            String idDoc = tab[1];

            Abonne ab = mediatheque.getAbonne(numAbonne);
            if (ab == null) {
                out.println("ERREUR abonne " + numAbonne + " introuvable");
                socket.close();
                return;
            }

            Document doc = mediatheque.getDocument(idDoc);
            if (doc == null) {
                out.println("ERREUR document " + idDoc + " introuvable");
                socket.close();
                return;
            }

            // Grand chaman : si reserve et moins de 60s restantes, on attend
            long restantes = 0;
            if (doc instanceof DVD) {
                restantes = ((DVD) doc).getSecondesRestantes();
            } else if (doc instanceof Livre) {
                restantes = ((Livre) doc).getSecondesRestantes();
            }

            if (restantes > 0 && restantes <= 60) {
                out.println("ATTENTE Le document est reserve encore " + restantes + "s, patientez...");
                try {
                    Thread.sleep(restantes * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                doc.reservation(ab);
                out.println("OK reservation effectuee pour " + doc.idDoc() + ", vous avez 2h pour venir l'emprunter");
            } catch (ReservationException e) {
                out.println("ERREUR " + e.getMessage());
            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
