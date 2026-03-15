package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client d'emprunt (port 2001).
 * Usage : java client.ClientEmprunt [host] [port]
 * Par défaut : localhost 2001
 */
public class ClientEmprunt {

    public static void main(String[] args) throws IOException {
        String host = args.length > 0 ? args[0] : "localhost";
        int    port = args.length > 1 ? Integer.parseInt(args[1]) : 2001;

        Scanner sc = new Scanner(System.in);

        System.out.print("Votre numero d'abonne : ");
        String numAbonne = sc.nextLine().trim();

        System.out.print("Identifiant du document a emprunter : ");
        String idDoc = sc.nextLine().trim();

        try (
            Socket       socket = new Socket(host, port);
            PrintWriter  out    = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in   = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(numAbonne + " " + idDoc);
            System.out.println("[Serveur] " + in.readLine());
        }
    }
}
