package jeu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Conversation extends Thread {
    private int id; // Identifiant du client
    private Socket socket; // Socket associée à ce client
    private HashMap<Integer, Socket> listClient; // Liste de tous les clients connectés, avec leur socket
    private int targetNumber; // Le nombre à deviner

    // Constructeur de la classe Conversation
    public Conversation(int id, Socket socket, HashMap<Integer, Socket> listClient, int targetNumber) {
        this.id = id;
        this.socket = socket;
        this.listClient = listClient;
        this.targetNumber = targetNumber;
    }

    // Méthode pour envoyer un message à un autre client
    private void sendMessage(String message, Socket socket) {
        try {
            OutputStream out = socket.getOutputStream();
            PrintWriter sortie = new PrintWriter(out);
            sortie.println(message); // Envoyer le message
            sortie.flush(); // S'assurer que le message est envoyé immédiatement
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Gestion des flux d'entrée et de sortie pour la communication
            InputStream in = socket.getInputStream(); // Flux pour recevoir des données du client
            BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
            
            String msg;
            while ((msg = buffer.readLine()) != null) {
                try {
                    int prediction = Integer.parseInt(msg); // Convertir le message en nombre
                    System.out.println("Client " + id + " a proposé : " + prediction);

                    if (prediction == targetNumber) {
                        broadcastMessage("Le client " + id + " a trouvé le nombre ! Le jeu est terminé.");
                        break; // Terminer le thread car le jeu est terminé
                    } else {
                        broadcastMessage("Client " + id + " a proposé : " + prediction + ". Mauvaise prédiction !");
                    }
                } catch (NumberFormatException e) {
                    sendMessage("Veuillez entrer un nombre valide.", socket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour diffuser un message à tous les clients
    private void broadcastMessage(String message) {
        for (Socket clientSocket : listClient.values()) {
            sendMessage(message, clientSocket);
        }
    }
}

