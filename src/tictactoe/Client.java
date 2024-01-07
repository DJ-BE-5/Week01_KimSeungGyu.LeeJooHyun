package tictactoe;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * 클라이언트 구현 클래스
 *
 * @Author 이주현
 */

public class Client {
    private Communicator communicator;
    private String[] board = new String[4];

    public static void main(String[] args) {
        Client client = new Client();
        client.runClient();
    }

    /**
     *  소켓 생성 후 서버와 연결.
     *  서버 종속성이 높고, 순차적으로 작성되어서 객체지향과 거리가 멀게됨 (아쉬움)
     */
    private void runClient() {
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

            if (communicator.getId() != userId) {
                userId = communicator.read();
                System.out.println(communicator.read());
            }

            while (socket.isConnected()) {
                board = communicator.read().split("-");
                for (int i = 0; i < 3; i++) {
                    System.out.println(board[i]);
                }


                if (board[3].equals(userId)){
                    System.out.println("choose the space (x y) : ");
                    input = new Scanner(System.in).nextLine().strip().split(" ");
                    x = Integer.parseInt(input[0]);
                    y = Integer.parseInt(input[1]);

                    communicator.write(userId + " " + x + " " + y);
                }
            }
        } catch (IOException e) {
            System.err.println("Client Error : " + e);
        }
    }
}