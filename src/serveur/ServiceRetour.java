package serveur;

import exceptions.RetourException;
import metier.DVD;
import metier.Document;
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
                    if (doc instanceof DVD) {
                        ((DVD) doc).retourDegrade();
                    } else if (doc instanceof Livre) {
                        ((Livre) doc).retourDegrade();
                    }
                    out.println("OK retour avec degradation enregistre");
                } else {
                    doc.retour();
                    out.println("OK retour effectue");
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
