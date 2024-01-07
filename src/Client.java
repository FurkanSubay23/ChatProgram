import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String name;

    public Client(Socket socket, String name) {
        try {
            this.socket = socket;
            this.name = name;
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException e) {
            closeAll(socket, reader, writer);
        }
    }

    // method to send messages using thread
    public void sendMessage() {
        try {
            writer.write(name);
            writer.newLine();
            writer.flush();

            Scanner sc = new Scanner(System.in);

            while (socket.isConnected()) {
                String messageToSende = sc.nextLine();
                writer.write(name + " : " + messageToSende);
                writer.newLine();
                writer.flush();
            }

        } catch (IOException e) {
            closeAll(socket, reader, writer);
        }
    }

    // method to read messages using thread
    public void readMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String readMessage;
                    while (socket.isConnected()) {
                        readMessage = reader.readLine();
                        System.out.println(readMessage);
                    }
                } catch (IOException e) {
                    closeAll(socket, reader, writer);
                }
            }
        }).
                start();
    }

    // method to close everything in the socket
    public void closeAll(Socket socket, BufferedReader reader, BufferedWriter writer) {
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

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Your Name : ");
        String name = sc.nextLine();
        Socket socket1 = new Socket("localhost", 1234);
        Client client = new Client(socket1, name);
        client.readMessage();
        client.sendMessage();
    }
}
