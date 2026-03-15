package serveur;

import metier.DocumentAbstrait;
import metier.Mediatheque;
import exceptions.RetourException;

import java.io.*;
import java.net.Socket;

/**
 * Service de retour (port 2002).
 * Protocole :
 *   client -> "idDoc" (pas besoin de numAbonne)
 *   client -> "idDoc DEGRADE" (Geronimo : signalement de dégradation)
 *   serveur -> "OK" ou "ERREUR message"
 */
public class ServiceRetour implements Runnable {

    private final Socket socket;
    private final Mediatheque mediatheque;

    public ServiceRetour(Socket socket, Mediatheque mediatheque) {
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
            String idDoc = parts[0];
            boolean degrade = parts.length >= 2 && parts[1].equalsIgnoreCase("DEGRADE");

            DocumentAbstrait doc = mediatheque.getDocument(idDoc);
            if (doc == null) {
                out.println("ERREUR Document \"" + idDoc + "\" inconnu.");
                return;
            }

            try {
                if (degrade) {
                    doc.retourDegrade();
                    out.println("OK Document rendu. Degradation enregistree (abonne banni 1 mois).");
                } else {
                    doc.retour();
                    out.println("OK Document \"" + doc.getTitre() + "\" rendu. Merci !");
                }
            } catch (RetourException e) {
                out.println("ERREUR " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("[ServiceRetour] Erreur IO : " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
