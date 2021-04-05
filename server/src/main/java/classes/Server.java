package classes;

import interfaces.AuthService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Server {

    private static final Logger logger = LogManager.getLogManager().getLogger(Server.class.getName());
    private List<ClientHandler> clients;
    private AuthService authService = new DBAuthService();

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {
        this.clients = new ArrayList<>();
        try(
                ServerSocket serverSocket = new ServerSocket(8098);
                DBHelper dbHelper = DBHelper.getInstance()
        ) {
            logger.log(Level.INFO, "Server wait for port 8098");
            for(;;) {
                Socket socket = serverSocket.accept();
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "ServerSocket не отвечает");
            e.printStackTrace();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Возникла непредвиденная ошибка");
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message) {
        for(ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void broadcastUsers() {
        StringBuilder sb = new StringBuilder();
        sb.append("@clients ");
        for(ClientHandler client : clients) {
            sb.append(client.getUsername()).append("/");
        }
        broadcastMessage(sb.toString());
    }

    public void subscribe(ClientHandler client) {
        logger.log(Level.INFO, "Подключился пользователь " + client.getUsername());
        clients.add(client);
        broadcastUsers();
    }

    public void unsubscribe(ClientHandler client) {
        logger.log(Level.INFO, "Пользователь " + client.getUsername() + " отключился");
        clients.remove(client);
        broadcastUsers();
    }

}
