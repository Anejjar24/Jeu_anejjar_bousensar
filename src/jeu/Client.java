package jeu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try {
            // Établir la connexion avec le serveur
            InetAddress adr = InetAddress.getByName("localhost");
            Socket socket = new Socket(adr, 1500);
            System.out.println("Connexion établie avec succès!");

            // Gestion des flux d'entrée et de sortie
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            //Démarrer un thread pour écouter les messages reçus du serveur
            new Thread(new Message(in)).start();

            // Utilisation d'un scanner pour lire les prédictions saisies par l'utilisateur
            Scanner sc = new Scanner(System.in);
            PrintWriter sortie = new PrintWriter(out, true);  // true pour auto-flush
            
            // Boucle pour envoyer les prédictions au serveur
            while (true) {
                System.out.print("Entrez votre prédiction (1-100) : ");
                String message = sc.nextLine();
                sortie.println(message);  // Envoyer la prédiction au serveur
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Classe pour gérer la réception des messages du serveur
class Message implements Runnable {
    private BufferedReader in;// Buffer pour lire les messages reçus
 // Constructeur pour initialiser le flux d'entrée
    public Message(InputStream inStream) {
        this.in = new BufferedReader(new InputStreamReader(inStream));
    }

    @Override
    public void run() {
        try {
            String message;
            // Boucle pour lire et afficher les messages envoyés par le serveur
            while ((message = in.readLine()) != null) {
                System.out.println("Message reçu : " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
