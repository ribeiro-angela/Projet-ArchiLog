package serveur;

import exceptions.EmpruntException;
import metier.Abonne;
import metier.Document;
import metier.Mediatheque;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServiceEmprunt implements Runnable {

    private Socket socket;
    private Mediatheque mediatheque;

    public ServiceEmprunt(Socket socket, Mediatheque mediatheque) {
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

            try {
                doc.emprunt(ab);
                out.println("OK emprunt effectue pour " + doc.idDoc());
            } catch (EmpruntException e) {
                out.println("ERREUR " + e.getMessage());
            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
