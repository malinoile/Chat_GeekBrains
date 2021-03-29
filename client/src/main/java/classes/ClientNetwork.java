package classes;

import interfaces.Callback;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ClientNetwork {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private Callback<String> callOnMessageReceived;
    private Callback<String> callOnChangeClientList;
    private Callback<String> callOnError;
    private Callback<String> callOnLogIn;
    private Callback<Boolean> callOnClose;

    public ClientNetwork() {
        connect();
    }

    private boolean isTimeOver = false;
    private boolean isAuthorize = false;

    public void connect() {
        try {
            this.socket = new Socket("localhost", 8098);
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            Long startTime = System.currentTimeMillis();

            new Thread(() -> {
                String message = null;
                boolean isChatting = true;
                try {
                    String user = null;
                    while (!isAuthorize && isChatting) {
                        message = in.readUTF();
                        if (message.startsWith("@login_ok")) {
                            callOnLogIn.callback("@login_ok");
                            user = message.split("\\s", 2)[1];
                            isAuthorize = true;
                        } else if (message.equalsIgnoreCase("@end")) {
                            isChatting = false;
                        } else if (message.startsWith("@error ")) {
                            callOnError.callback(message.split("\\s", 2)[1]);
                        }
                    }

                    if(isAuthorize && isChatting) {
                        try(HistoryManager historyManager = new HistoryManager(user)) {

                            List<String> oldMessages = historyManager.readFromFile();
                            if(oldMessages.size() > 0) {
                                for(String oldMessage : oldMessages) {
                                    callOnMessageReceived.callback(oldMessage);
                                }
                            }

                            while (isChatting) {
                                message = in.readUTF();
                                if (message.equalsIgnoreCase("@end")) {
                                    isChatting = false;
                                } else if (message.startsWith("@clients ")) {
                                    callOnChangeClientList.callback(message.substring(9));
                                } else if (message.startsWith("@change ")) {
                                    user = message.split("\\s", 2)[1];
                                    historyManager.updateFilename(user);
                                } else if (message.startsWith("@error ")) {
                                    callOnError.callback(message.split("\\s", 2)[1]);
                                } else {
                                    callOnMessageReceived.callback(message);
                                    historyManager.writeToFile(message);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    disconnect();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean sendMessage(String message) {
        try {
            out.writeUTF(message);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCallOnMessageReceived(Callback<String> callOnMessageReceived) {
        this.callOnMessageReceived = callOnMessageReceived;
    }

    public void setCallOnChangeClientList(Callback<String> callOnChangeClientList) {
        this.callOnChangeClientList = callOnChangeClientList;
    }

    public void setCallOnError(Callback<String> callOnError) {
        this.callOnError = callOnError;
    }

    public void setCallOnLogIn(Callback<String> callOnLogIn) {
        this.callOnLogIn = callOnLogIn;
    }

    public void setCallOnClose(Callback<Boolean> callOnClose) {
        this.callOnClose = callOnClose;
    }
}
