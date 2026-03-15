package serveur;

import metier.Abonne;
import metier.DocumentAbstrait;
import metier.Mediatheque;
import exceptions.EmpruntException;

import java.io.*;
import java.net.Socket;

/**
 * Service d'emprunt (port 2001).
 * Protocole :
 *   client -> "numAbonne idDoc"
 *   serveur -> "OK" ou "ERREUR message"
 */
public class ServiceEmprunt implements Runnable {

    private final Socket socket;
    private final Mediatheque mediatheque;

    public ServiceEmprunt(Socket socket, Mediatheque mediatheque) {
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

            try {
                doc.emprunt(ab);
                out.println("OK Bon emprunt de \"" + doc.getTitre() + "\" !");
            } catch (EmpruntException e) {
                out.println("ERREUR " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("[ServiceEmprunt] Erreur IO : " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
