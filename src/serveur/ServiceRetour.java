package serveur;

import exceptions.RetourException;
import metier.Document;
import metier.DVD;
import metier.Livre;
import metier.Mediatheque;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServiceRetour implements Runnable {

    private Socket socket;
    private Mediatheque mediatheque;

    public ServiceRetour(Socket socket, Mediatheque mediatheque) {
        this.socket = socket;
        this.mediatheque = mediatheque;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String ligne = in.readLine();
            String[] tab = ligne.split(" ");

            String idDoc = tab[0];
            boolean degrade = tab.length >= 2 && tab[1].equals("DEGRADE");

            Document doc = mediatheque.getDocument(idDoc);
            if (doc == null) {
                out.println("ERREUR document " + idDoc + " introuvable");
                socket.close();
                return;
            }

            try {
                if (degrade) {
                    // retour avec degradation -> bannissement (certification geronimo)
                    if (doc instanceof DVD) {
                        ((DVD) doc).retourDegrade();
                    } else if (doc instanceof Livre) {
                        ((Livre) doc).retourDegrade();
                    }
                    out.println("OK retour enregistre (document degrade, abonne banni 1 mois)");
                } else {
                    doc.retour();
                    out.println("OK retour enregistre");
                }

                // certification sitting bull : on previent les gens en liste d'attente
                if (mediatheque.yaDesGensQuiAttendent(idDoc)) {
                    mediatheque.notifierAttente(idDoc);
                    out.println("INFO des abonnes en attente ont ete notifies par email");
                }

            } catch (RetourException e) {
                out.println("ERREUR " + e.getMessage());
            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}