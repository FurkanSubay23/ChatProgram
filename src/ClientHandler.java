import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

    public static List<ClientHandler> clientHandlers = new ArrayList<>();
    public Socket socket;
    public BufferedReader reader;
    public BufferedWriter writer;
    public String name;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.name = this.reader.readLine();
            clientHandlers.add(this);
            broadCastMessage("SERVER " + name + " has entered in the room");

        } catch (IOException e) {
            closeAll(socket, reader, writer);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        try {
            while (socket.isConnected()) {
                messageFromClient = reader.readLine();
                broadCastMessage(messageFromClient);

            }
        } catch (IOException e) {
            closeAll(socket, reader, writer);
        }
    }


    public void broadCastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.name.equals(name)) {
                    clientHandler.writer.write(messageToSend);  // Write params
                    clientHandler.writer.newLine();             // Separate A new line
                    clientHandler.writer.flush();               // Send data now
                }
            } catch (IOException e) {
                closeAll(socket, reader, writer);
            }
        }
    }

    // notify if the user left the chat
    public void removeClientHandlers() {
        clientHandlers.remove(this);
        broadCastMessage("Server " + name + " has gone !!!");
    }

    public void closeAll(Socket socket, BufferedReader reader, BufferedWriter writer) {
        // handle the removeClient function
        removeClientHandlers();
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
