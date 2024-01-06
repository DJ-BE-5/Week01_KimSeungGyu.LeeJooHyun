package tictactoe;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    static boolean isFinished = true;
    static private TicTacToeImpl ticTacToe = new TicTacToeImpl();

    public static void main(String[] args) {
        runServer();
    }
    public static void runServer(){
        Communicator firstCommunicator;
        Communicator secondCommunicator;

        try(ServerSocket serverSocket = new ServerSocket(1234)){
            System.out.println("Server Connected");
            Socket firstSocket = serverSocket.accept();
            System.out.println("First Client Connected");

            firstCommunicator = new Communicator(firstSocket);
            firstCommunicator.write(setId(1));

            Socket secondSocket = serverSocket.accept();
            System.out.println("Second Client Connected");

            secondCommunicator = new Communicator(secondSocket);
            firstCommunicator.write(setId(2));

            firstCommunicator.write("Wanna Rock Scissor Paper? : (y/n)");
            secondCommunicator.write("Wanna Rock Scissor Paper? : (y/n)");

            if (firstCommunicator.read().strip().toLowerCase().equals("y") || secondCommunicator.read().strip().toLowerCase().equals("y")){
                firstCommunicator.write("Choose your Side : (R,S,P)");
                secondCommunicator.write("Choose your Side : (R,S,P)");

                String firstMessage = firstCommunicator.read().strip().toLowerCase();
                String secondMessage = secondCommunicator.read().strip().toLowerCase();
                if (!RockScissorPaper(firstMessage,secondMessage)){
                    firstCommunicator.write(setId(2));
                    secondCommunicator.write(setId(1));
                }
            }else{
                firstCommunicator.write("We Don't Proceed RSP");
                secondCommunicator.write("We Don't Proceed RSP");
            }

            firstCommunicator.write("Game Start!");
            secondCommunicator.write("Game Start!");

            String[] message;
            String board;
            while(firstSocket.isConnected() && secondSocket.isConnected()) {
                board = ticTacToe.getBoard();
                firstCommunicator.write(board);
                secondCommunicator.write(board);
                if (ticTacToe.getWinner() == 1) {
                    firstCommunicator.write("Client 1 Wins!");
                    secondCommunicator.write("Client 1 Wins!");
                    firstSocket.close();
                    secondSocket.close();
                }else if(ticTacToe.getWinner() == 2){
                    firstCommunicator.write("Client 2 Wins!");
                    secondCommunicator.write("Client 2 Wins!");
                    firstSocket.close();
                    secondSocket.close();
                }else{
                    if(isFinished){
                        message = firstCommunicator.read().split(" ");
                        isFinished = false;
                    }else {
                        message = secondCommunicator.read().split(" ");
                        isFinished = true;
                    }
                    ticTacToe.putStone(Integer.parseInt(message[0]), Integer.parseInt(message[1]), Integer.parseInt(message[2]));
                }
            }
        }catch (IOException e) {
            System.err.println("Server Error : " + e);
        }

    }
    public static String setId(int id){
        return Integer.toString(id);
    }
    public static boolean RockScissorPaper(String client1, String client2){
        if (client1.equals("r") && client2.equals("r")) return true;
        else if (client1.equals("r") && client2.equals("s")) return true;
        else if (client1.equals("r") && client2.equals("p")) return false;
        else if (client1.equals("s") && client2.equals("r")) return false;
        else if (client1.equals("s") && client2.equals("s")) return true;
        else if (client1.equals("s") && client2.equals("p")) return true;
        else if (client1.equals("p") && client2.equals("r")) return true;
        else if (client1.equals("p") && client2.equals("s")) return false;
        else return true;
    }
}
