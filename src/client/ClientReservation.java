package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client de réservation (port 2000).
 * Usage : java client.ClientReservation [host] [port]
 * Par défaut : localhost 2000
 */
public class ClientReservation {

    public static void main(String[] args) throws IOException {
        String host = args.length > 0 ? args[0] : "localhost";
        int    port = args.length > 1 ? Integer.parseInt(args[1]) : 2000;

        Scanner sc = new Scanner(System.in);

        System.out.print("Votre numero d'abonne : ");
        String numAbonne = sc.nextLine().trim();

        System.out.print("Identifiant du document a reserver : ");
        String idDoc = sc.nextLine().trim();

        try (
            Socket       socket = new Socket(host, port);
            PrintWriter  out    = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in   = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(numAbonne + " " + idDoc);

            // On peut recevoir une ligne ATTENTE, puis OK ou ERREUR
            String ligne;
            while ((ligne = in.readLine()) != null) {
                System.out.println("[Serveur] " + ligne);
                if (ligne.startsWith("OK") || ligne.startsWith("ERREUR")) break;
            }
        }
    }
}
