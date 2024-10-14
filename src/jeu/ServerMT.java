package jeu;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ServerMT extends Thread {

   
    private static Map<Integer, ClientHandler> clientHandlers = new HashMap<>(); // Map pour stocker les handlers de clients
    private static int numero_a_trouver; // Le numéro que les clients doivent deviner
    private static boolean finJeu = false; // Indicateur pour savoir si le jeu est terminé
    private static int comp = 0; // Compteur pour attribuer un identifiant unique à chaque client
    
    @Override
    public void run() {
        try {
            int port = 1500; // Port sur lequel le serveur écoute les connexions
            ServerSocket server = new ServerSocket(port);//Création du serveur socket
            System.out.println("Le serveur est en attente de connexions sur le port " + port + "...");

         // Génération d'un numéro aléatoire entre 1 et 100 que les clients devront deviner
            Random random = new Random();
            numero_a_trouver = random.nextInt(100) + 1;
            System.out.println("Le numéro à deviner est : " + numero_a_trouver); // Pour le débogage, à supprimer en production
            // Boucle principale pour accepter les connexions des clients tant que le jeu n'est pas terminé
            while (!finJeu) {
                Socket socket = server.accept(); // Attente d'une nouvelle connexion client
                int id = ++comp; // Incrémentation de l'identifiant du client
                System.out.println("Client " + id + " connecté.");

             // Création d'un nouveau gestionnaire (handler) pour le client connecté
                ClientHandler clientHandler = new ClientHandler(id, socket);
                clientHandlers.put(id, clientHandler); // Ajout du handler à la map des clients
                clientHandler.start();// Démarrage du thread pour gérer les interactions avec ce client
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

 // Méthode pour traiter la prédiction d'un client
    public static void handlePrediction(int id, int nombreDeviner) {
    	  // Vérification si la prédiction correspond au numéro cible
        if (nombreDeviner == numero_a_trouver) {
            finJeu = true;// Si la prédiction est correcte, le jeu est terminé
            broadcastMessage("Le client " + id + " a trouvé le numéro ! Le jeu est terminé.");
        } else {
        	  // Si la prédiction est incorrecte, notifier tous les clients
            broadcastMessage("Client " + id + " a prédit : " + nombreDeviner + ". Mauvaise prédiction !");
        }
    }

    // Méthode pour envoyer un message à tous les clients connectés
    public static void broadcastMessage(String message) {
    	 // Parcourir tous les handlers de clients pour envoyer le message
    	for (Map.Entry<Integer, ClientHandler> entry : clientHandlers.entrySet()) {
            entry.getValue().sendMessage(message);// Envoi du message au client via son handler
        }
    }

    public static void main(String[] args) {
    	 // Création d'une instance du serveur et démarrage du thread principal
        ServerMT server = new ServerMT();
        server.start(); // Lancer le serveur
    }
}
