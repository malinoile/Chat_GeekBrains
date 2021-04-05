package classes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ClientHandler {

    private String username;
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private static final Logger logger = LogManager.getLogManager().getLogger(DBHelper.class.getName());

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                boolean isChatting = true;
                String msg = null;
                boolean isAuthorize = false;
                try {
                    while (!isAuthorize && isChatting) {
                        msg = in.readUTF();

                        if (msg.equalsIgnoreCase("@end")) {
                            sendMessage(msg);
                            isChatting = false;
                        }

                        if (msg.startsWith("@login ")) {
                            String tokens[] = msg.split("\\s");
                            username = server.getAuthService().getUsernameByLoginAndPassword(tokens[1], tokens[2]);

                            if (username != null) {
                                isAuthorize = true;
                                sendMessage("@login_ok " + username);
                                server.subscribe(this);
                            } else {
                                sendMessage("@error Данные для входа указаны некорректно");
                            }
                        }
                    }

                    while (isChatting) {
                        msg = in.readUTF();
                        System.out.println(msg);
                        if (msg.startsWith("@")) {
                            if (msg.equalsIgnoreCase("@end")) {
                                isChatting = false;
                                sendMessage(msg);
                            } else if (msg.startsWith("@change")) {
                                String arr[] = msg.split("\\s", 2);
                                if(server.getAuthService().changeUsername(username, arr[1])) {
                                    username = arr[1];
                                    server.broadcastUsers();
                                    server.broadcastMessage("@change "+username);
                                } else {
                                    sendMessage("@error Изменить имя пользователя не удалось.\nУкажите другое имя пользователя или попробуйте позднее");
                                }
                            }
                        } else {
                            server.broadcastMessage(username + ": " + msg);
                        }
                    }
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Ошибка при чтении сообщений с сервера/отправке сообщений на сервер");
                    e.printStackTrace();
                } finally {
                    disconnect();
                }
            });
            executorService.shutdown();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка в инициализации потока ввода/вывода");
            e.printStackTrace();
        }
    }



    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при отправке сообщения на сервер");
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    private void disconnect() {
        server.unsubscribe(this);
        try {
            in.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при закрытии потока записи");
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при закрытии потока чтения");
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при закрытии сокета");
            e.printStackTrace();
        }
    }
}
