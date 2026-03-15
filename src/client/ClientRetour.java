package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client de retour (port 2002).
 * Usage : java client.ClientRetour [host] [port]
 * Par défaut : localhost 2002
 *
 * Geronimo : l'utilisateur peut signaler une dégradation en tapant "DEGRADE" après l'id.
 */
public class ClientRetour {

    public static void main(String[] args) throws IOException {
        String host = args.length > 0 ? args[0] : "localhost";
        int    port = args.length > 1 ? Integer.parseInt(args[1]) : 2002;

        Scanner sc = new Scanner(System.in);

        System.out.print("Identifiant du document a rendre : ");
        String idDoc = sc.nextLine().trim();

        System.out.print("Le document est-il degrade ? (o/n) : ");
        String rep = sc.nextLine().trim().toLowerCase();
        boolean degrade = rep.equals("o") || rep.equals("oui");

        try (
            Socket        socket = new Socket(host, port);
            PrintWriter   out    = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in    = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(degrade ? idDoc + " DEGRADE" : idDoc);
            System.out.println("[Serveur] " + in.readLine());
        }
    }
}
