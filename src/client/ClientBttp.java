package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientBttp {

    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            System.out.println("Usage : ClientBttp <port> [host]");
            return;
        }

        int port = Integer.parseInt(args[0]);
        String host = "localhost";
        if (args.length >= 2) {
            host = args[1];
        }

        if (port == 2000) {
            System.out.println("=== Service RESERVATION (port 2000) ===");
            System.out.println("Format : numAbonne idDoc");
        } else if (port == 2001) {
            System.out.println("=== Service EMPRUNT (port 2001) ===");
            System.out.println("Format : numAbonne idDoc");
        } else if (port == 2002) {
            System.out.println("=== Service RETOUR (port 2002) ===");
            System.out.println("Format : idDoc  ou  idDoc DEGRADE");
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("> ");
        String requete = sc.nextLine();

        Socket socket = new Socket(host, port);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out.println(requete);

        String reponse;
        while ((reponse = in.readLine()) != null) {
            System.out.println("[Serveur] " + reponse);
            if (reponse.startsWith("OK") || reponse.startsWith("ERREUR")) {
                break;
            }
        }

        socket.close();
    }
}
