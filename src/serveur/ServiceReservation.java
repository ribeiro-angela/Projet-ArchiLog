package serveur;

import metier.Abonne;
import metier.DocumentAbstrait;
import metier.EtatDocument;
import metier.Mediatheque;
import exceptions.ReservationException;

import java.io.*;
import java.net.Socket;

/**
 * Service de réservation (port 2000).
 * Protocole :
 *   client -> "numAbonne idDoc"
 *   serveur -> "OK" ou "ERREUR message"
 *
 * Grand Chaman : si le document est réservé et qu'il reste <= 60s,
 * on attend la fin de la réservation au lieu de rejeter directement.
 */
public class ServiceReservation implements Runnable {

    private final Socket socket;
    private final Mediatheque mediatheque;

    public ServiceReservation(Socket socket, Mediatheque mediatheque) {
        this.socket = socket;
        this.mediatheque = mediatheque;
    }

    @Override
    public void run() {
        try (
            BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter   out  = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String ligne = in.readLine();
            if (ligne == null) return;

            String[] parts = ligne.trim().split(" ");
            if (parts.length < 2) {
                out.println("ERREUR Syntaxe : numAbonne idDoc");
                return;
            }

            int numAbonne;
            try {
                numAbonne = Integer.parseInt(parts[0]);
            } catch (NumberFormatException e) {
                out.println("ERREUR Numero d'abonne invalide.");
                return;
            }
            String idDoc = parts[1];

            Abonne ab = mediatheque.getAbonne(numAbonne);
            if (ab == null) {
                out.println("ERREUR Abonne #" + numAbonne + " inconnu.");
                return;
            }

            DocumentAbstrait doc = mediatheque.getDocument(idDoc);
            if (doc == null) {
                out.println("ERREUR Document \"" + idDoc + "\" inconnu.");
                return;
            }

            // --- Grand Chaman ---
            doc.verifierExpiration();
            if (doc.getEtat() == EtatDocument.RESERVE) {
                long restantes = doc.secondesRestantes();
                if (restantes > 0 && restantes <= 60) {
                    // On fait patienter le client avec musique céleste
                    out.println("ATTENTE Le document est reserve encore " + restantes
                            + "s. Veuillez patienter (musique celeste en cours)...");
                    try {
                        Thread.sleep(restantes * 1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    // Re-vérifier après l'attente
                    doc.verifierExpiration();
                }
            }
            // --- fin Grand Chaman ---

            try {
                doc.reservation(ab);
                out.println("OK Document \"" + doc.getTitre()
                        + "\" reserve. Vous avez 2h pour venir l'emprunter.");
            } catch (ReservationException e) {
                out.println("ERREUR " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("[ServiceReservation] Erreur IO : " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
