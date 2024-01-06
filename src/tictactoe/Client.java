package tictactoe;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static Communicator communicator;
    static String board;

    public static void main(String[] args) {
        runClient();
    }

    private static void runClient() {
        try (Socket socket = new Socket("localhost", 1234)){
            System.out.println("Client Connected : " + socket.getInetAddress() + " : " + socket.getPort());

            communicator = new Communicator(socket);

            String userId = communicator.read();
            String[] input;
            int x, y;

            System.out.println(communicator.read());

            communicator.write(new Scanner(System.in).nextLine());

            String message = communicator.read();
            System.out.println(message);

            if (message.equals("Choose your Side : (R,S,P)")){
                communicator.write(new Scanner(System.in).nextLine());
            }

            System.out.println(communicator.read());

            while (socket.isConnected()) {
                board = communicator.read();

                System.out.println("choose the space (x y) : ");
                input = new Scanner(System.in).nextLine().strip().split(" ");
                x = Integer.parseInt(input[0]);
                y = Integer.parseInt(input[1]);

                communicator.write(userId + " " + x + " " + y);
            }
        } catch (IOException e) {
            System.err.println("Client Error : " + e);
        }
    }
}