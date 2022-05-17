package it.polimi.ingsw;

import it.polimi.ingsw.Constants.Constants;
import it.polimi.ingsw.Constants.MessageType;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private final Socket client;
    private final BufferedReader in;
    private final PrintWriter out;
    private static final ArrayList<ClientHandler> clients = new ArrayList<>();
    private static int id;
    private String nickName;
    private boolean isActive;
    private String latestMessage;
    private boolean latestMessageUsed;
    private boolean yourTurn;

    /**
     *  Constructor which instantiates channel dedicated to the handling of each client
     * @param clientSocket to connect to the specific client
     * @throws IOException
     */
    public ClientHandler(Socket clientSocket) throws IOException {
        this.client = clientSocket;
        this.isActive = true;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
        clients.add(this);

        latestMessageUsed = false;
        yourTurn = false;
    }

    /**
     *  The run-method first asks for general info about the client and keep listening on the client's channel elaborating his request depending on what info the controller needs.
     *  <p></p>Requests which starts with "/" have an higher priority and are handled at server-level (these aren't passed to the game controller)
     */
    @Override
    public void run() {
        try {
            try {
                out.println(MessageType.EASY_MESSAGE.getType() + "\n[SERVER] Welcome! You are the player " + clients.size() + "\nEND OF MESSAGE");

                setUp();

                while (isActive) {

                    String request;

                    request = in.readLine();

                    if (request.startsWith("/say")) {
                        int firstSpace = request.indexOf(" ");
                        if (firstSpace != -1) {
                            outToAll(request.substring(firstSpace + 1) + "\nEND OF MESSAGE");
                        }
                    }
                    if (request.startsWith("/quit")) {
                        isActive = false;
                        out.println(MessageType.EASY_MESSAGE.getType() + "\nDisconnected!\nEND OF MESSAGE");
                        out.close();
                        in.close();
                        client.close();
                        System.out.println("Client " + clients.indexOf(this) + " disconnected!");
                        clients.remove(this);
                    }
                    if (yourTurn){
                        latestMessage = request;
                        latestMessageUsed = true;
                    }
                    else {
                        out.println(MessageType.EASY_MESSAGE.getType() + "\nit's not your turn\nEND OF MESSAGE");
                    }
                }
            } catch (SocketException socketException) {
                System.out.println("[SERVER] Client disconnected");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * SetUp is the first method used to map all the clients in indexToNick e nickToIndex
     * @throws IOException
     * @throws InterruptedException
     */
    private void setUp() throws IOException, InterruptedException {
        String request;

        out.println(MessageType.EASY_MESSAGE.getType() + "\nInsert your nickname: \nEND OF MESSAGE");
        request = in.readLine();
        nickName = request;

        if (clients.size() == 1) {
            do {
                out.println(MessageType.EASY_MESSAGE.getType() + "\nSelect Game Mode: 0 = easy/ 1 = hard\nEND OF MESSAGE");
                request = in.readLine();
            } while (!request.equals("0") && !request.equals("1"));
            if (request.equals("0")) {
                Constants.setGameMode(false);
            } else {
                Constants.setGameMode(true);
            }
            do {
                out.println(MessageType.EASY_MESSAGE.getType() + "\nSelect Number of Players: 2 / 3\nEND OF MESSAGE");
                request = in.readLine();
            } while (!request.equals("2") && !request.equals("3"));
            if (request.equals("2")) {
                Constants.setNumPlayers(2);
            } else {
                Constants.setNumPlayers(3);
            }
            out.println(MessageType.EASY_MESSAGE.getType() + "\n[SERVER] Waiting for other players to join...\nEND OF MESSAGE");
        }
        if (clients.size() == Constants.getNumPlayers()) {
            new GameHandler(Constants.getNumPlayers(), Constants.isGameMode(), clients);
        }
    }

    private void outToAll(String substring) throws IOException {
        for (ClientHandler client : clients) {
            client.out.println(MessageType.EASY_MESSAGE.getType() + "\n" + substring);
        }
    }

    public void sendMessage(String msg) {
        out.println(MessageType.EASY_MESSAGE.getType() + "\n" + msg + "\nEND OF MESSAGE");
    }

    public String getNickName() {
        return nickName;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessageUsed(boolean latestMessageUsed) {
        this.latestMessageUsed = latestMessageUsed;
    }

    public boolean isLatestMessageUsed() {
        return latestMessageUsed;
    }

    public void setYourTurn(boolean yourTurn) {
        this.yourTurn = yourTurn;
    }
}
