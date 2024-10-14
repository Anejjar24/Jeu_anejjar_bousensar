package jeu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    private int clientId; // ID unique pour le client
    private Socket socket; // Socket pour la connexion
    private PrintWriter out; // Pour envoyer des messages au client
    private BufferedReader in; // Pour lire les messages du client

    public ClientHandler(int clientId, Socket socket) throws IOException {
        this.clientId = clientId;
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true); // Auto-flush
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // Envoyer un message au client pour commencer le jeu
        out.println("Bienvenue ! Essayez de deviner le numéro choisi par le serveur (entre 1 et 100).");
    }

    @Override
    public void run() {
        try {
            String message;
            // Lire les messages envoyés par le client
            while ((message = in.readLine()) != null) {
                try {
                    int prediction = Integer.parseInt(message); // Essayer de convertir le message en entier
                    ServerMT.handlePrediction(clientId, prediction); // Traiter la prédiction
                } catch (NumberFormatException e) {
                    out.println("Veuillez entrer un nombre valide."); // Message d'erreur pour une entrée invalide
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close(); // Fermer la connexion
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Méthode pour envoyer un message à ce client
    public void sendMessage(String message) {
        out.println(message); // Envoyer le message
    }
}
