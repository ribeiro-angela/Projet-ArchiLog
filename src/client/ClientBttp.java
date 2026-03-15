package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client générique bttp2.0 :
 * le numéro de port est récupéré via les arguments du main.
 *
 * Usage :
 *   java client.ClientBttp 2000   -> service réservation
 *   java client.ClientBttp 2001   -> service emprunt
 *   java client.ClientBttp 2002   -> service retour
 *
 * Le client envoie une ligne lue sur stdin et affiche toutes
 * les lignes reçues jusqu'à obtenir une ligne commençant par OK ou ERREUR.
 *
 * Exemples de saisie :
 *   port 2000 -> "1 D001"          (numAbonne idDoc)
 *   port 2001 -> "1 D001"
 *   port 2002 -> "D001"  ou "D001 DEGRADE"
 */
public class ClientBttp {

    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            System.out.println("Usage : java client.ClientBttp <port> [host]");
            System.out.println("  port 2000 = reservation, 2001 = emprunt, 2002 = retour");
            return;
        }

        int    port = Integer.parseInt(args[0]);
        String host = args.length > 1 ? args[1] : "localhost";

        afficherAide(port);

        Scanner sc = new Scanner(System.in);
        System.out.print("> ");
        String requete = sc.nextLine().trim();

        try (
            Socket        socket = new Socket(host, port);
            PrintWriter   out    = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in    = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(requete);

            String ligne;
            while ((ligne = in.readLine()) != null) {
                System.out.println("[Serveur] " + ligne);
                if (ligne.startsWith("OK") || ligne.startsWith("ERREUR")) break;
            }
        }
    }

    private static void afficherAide(int port) {
        switch (port) {
            case 2000:
                System.out.println("=== Service RESERVATION (port 2000) ===");
                System.out.println("Format : <numAbonne> <idDoc>");
                break;
            case 2001:
                System.out.println("=== Service EMPRUNT (port 2001) ===");
                System.out.println("Format : <numAbonne> <idDoc>");
                break;
            case 2002:
                System.out.println("=== Service RETOUR (port 2002) ===");
                System.out.println("Format : <idDoc>  ou  <idDoc> DEGRADE");
                break;
            default:
                System.out.println("=== Service sur port " + port + " ===");
        }
    }
}
