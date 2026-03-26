package serveur;

import exceptions.ReservationException;
import metier.Abonne;
import metier.Document;
import metier.DVD;
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
            if (ligne == null) {
                socket.close();
                return;
            }

            String[] tab = ligne.split(" ");

            // format : numAbonne idDoc  (email optionnel pour sitting bull)
            if (tab.length < 2) {
                out.println("ERREUR format incorrect, envoyer : numAbonne idDoc [votreEmail]");
                socket.close();
                return;
            }

            int numAbonne = Integer.parseInt(tab[0]);
            String idDoc = tab[1];
            String emailAlerte = null;
            if (tab.length >= 3) {
                emailAlerte = tab[2];
            }

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

            // --- Certification Grand Chaman ---
            // si le doc est reserve et qu'il reste moins de 60s, on fait patienter l'abonne
            long secondesRestantes = getSecondesRestantes(doc);

            if (secondesRestantes > 0 && secondesRestantes <= 60) {
                out.println("ATTENTE Le document est reserve encore " + secondesRestantes
                        + "s... Patientez, musique celeste en cours...");
                try {
                    Thread.sleep(secondesRestantes * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                // apres l'attente on retente la reservation (cf en dessous)
            }

            // --- tentative de reservation ---
            try {
                doc.reservation(ab);
                out.println("OK reservation effectuee ! Vous avez 2h pour venir emprunter " + idDoc);

            } catch (ReservationException e) {
                out.println("ERREUR " + e.getMessage());

                // --- Certification Sitting Bull ---
                // si le doc est vraiment pas dispo et qu'un email a ete fourni, on inscrit l'alerte
                if (emailAlerte != null) {
                    mediatheque.inscrireAlerte(idDoc, emailAlerte);
                    out.println("INFO Alerte enregistree : vous serez prevenu par email quand "
                            + idDoc + " sera rendu");
                } else {
                    out.println("INFO Reessayez avec votre email en 3eme argument pour etre prevenu au retour");
                }
            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // methode utilitaire pour recuperer les secondes restantes
    // sans casser l'interface Document (qui ne connait pas getSecondesRestantes)
    private long getSecondesRestantes(Document doc) {
        if (doc instanceof DVD) {
            return ((DVD) doc).getSecondesRestantes();
        }
        if (doc instanceof Livre) {
            return ((Livre) doc).getSecondesRestantes();
        }
        return 0;
    }
}