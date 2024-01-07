package tictactoe;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 서버 클래서
 * @Author 이주현
 */

public class Server {
    /**
     *  TicTacToeImpl을 멤버로 유지
     */
    private TicTacToeImpl ticTacToe = new TicTacToeImpl();

    public static void main(String[] args) {
        Server server = new Server();
        server.runServer();
    }

    /**
     * 서버 작동
     */
    private void runServer(){
        /**
         *  클라이언트가 2개 필요하니 통신체 2개 생성
         */
        Communicator firstCommunicator;
        Communicator secondCommunicator;

        /**
         * 서버소켓 생성 및 클라이언트와 연결.
         * 너무 순차적이고, 조건문이 많다.
         */
        try(ServerSocket serverSocket = new ServerSocket(1234)){
            System.out.println("Server Connected");
            Socket firstSocket = serverSocket.accept();
            System.out.println("First Client Connected");

            firstCommunicator = new Communicator(firstSocket,1);
            firstCommunicator.write(firstCommunicator.getId());

            Socket secondSocket = serverSocket.accept();
            System.out.println("Second Client Connected");

            secondCommunicator = new Communicator(secondSocket,2);
            secondCommunicator.write(secondCommunicator.getId());

            firstCommunicator.write("Wanna Rock Scissor Paper? : (y/n)");
            secondCommunicator.write("Wanna Rock Scissor Paper? : (y/n)");

            // 빼서 처리
            String firstMessage = firstCommunicator.read().toLowerCase();
            String secondMessage = secondCommunicator.read().toLowerCase();

            if (firstMessage.equals("y") && secondMessage.equals("y")){
                firstCommunicator.write("Choose your Side : (R,S,P)");
                secondCommunicator.write("Choose your Side : (R,S,P)");
                
                firstMessage = firstCommunicator.read();
                secondMessage = secondCommunicator.read();
                if (!RockScissorPaper(firstMessage,secondMessage)){     // 먼저 들어온 클라이언트가 지는 경우
                    firstCommunicator.setId(2);
                    firstCommunicator.write(firstCommunicator.getId());
                    secondCommunicator.setId(1);
                    secondCommunicator.write(secondCommunicator.getId());
                }
                else{
                    firstCommunicator.write(firstCommunicator.getId()); // 비기는 경우
                    secondCommunicator.write(secondCommunicator.getId());
                }
            }
            else{ // 먼저 들어온 클라이언트가 이기는 경우
                firstCommunicator.write("We Don't Proceed RSP");
                secondCommunicator.write("We Don't Proceed RSP");
                firstCommunicator.write(firstCommunicator.getId());
                secondCommunicator.write(secondCommunicator.getId());
            }

            firstCommunicator.write("Game Start!");
            secondCommunicator.write("Game Start!");

            /**
             * 승패 처리 및 틱택토 게임 진행
             * myTurn이라는 flag를 사용해 ClientID와 비교하여 누구에게 정보를 받아야 하는지 검사
             */

            String[] messages = new String[3];
            String board;
            int myTurn = 1;
            while(firstSocket.isConnected() && secondSocket.isConnected()) {  // 소켓 연결 종료시까지 반복
                if (ticTacToe.isFinished()){
                    if (ticTacToe.getWinner() == 1 && firstCommunicator.getId().equals("1")) {
                        firstCommunicator.write("Client 1 Wins!");
                        secondCommunicator.write("Client 1 Wins!");
                    }
                    else if(ticTacToe.getWinner() == 1 && firstCommunicator.getId().equals("2")){
                        firstCommunicator.write("Client 2 Wins!");
                        secondCommunicator.write("Client 2 Wins!");
                    }
                    else if(ticTacToe.getWinner() == 2 && firstCommunicator.getId().equals("1")){
                        firstCommunicator.write("Client 1 Wins!");
                        secondCommunicator.write("Client 1 Wins!");
                    }
                    else if(ticTacToe.getWinner() == 2 && firstCommunicator.getId().equals("2")){
                        firstCommunicator.write("Client 2 Wins!");
                        secondCommunicator.write("Client 2 Wins!");
                    }
                    else if(ticTacToe.getWinner() == -1){
                        firstCommunicator.write("Draw");
                        secondCommunicator.write("Draw");
                    }else{
                        throw new RuntimeException("TicTacToe.getWinner() return error");
                    }
                    firstSocket.close();
                    secondSocket.close();
                }else{
                    board = ticTacToe.getBoard();

                    if (firstCommunicator.getId().equals(Integer.toString(myTurn))){
                        firstCommunicator.write(board + myTurn);
                        secondCommunicator.write(board + myTurn);

                        messages = firstCommunicator.read().split(" ");
                        if (myTurn == 1) myTurn = 2;
                        else myTurn = 1;

                    }else if (secondCommunicator.getId().equals(Integer.toString(myTurn))){
                        firstCommunicator.write(board + myTurn);
                        secondCommunicator.write(board + myTurn);

                        messages = secondCommunicator.read().split(" ");
                        if (myTurn == 1) myTurn = 2;
                        else myTurn = 1;
                    }
                    ticTacToe.putStone(Integer.parseInt(messages[0]), Integer.parseInt(messages[1]), Integer.parseInt(messages[2]));
                }
            }
        }catch (IOException e) {
            System.err.println("Server Error : " + e);
        }
    }

    /**
     * 간단하게 가위바위보 boolean으로 반환하는 함수
     * 비기는 상황은 먼저 들어오는 Client가 이기는것으로 간주하였다.
     * @param client1
     * @param client2
     * @return
     */
    private boolean RockScissorPaper(String client1, String client2){
        String cli1 = client1.toLowerCase().strip();
        String cli2 = client2.toLowerCase().strip();
        if (cli1.equals("r") && cli2.equals("r")) return true;
        else if (cli1.equals("r") && cli2.equals("s")) return true;
        else if (cli1.equals("r") && cli2.equals("p")) return false;
        else if (cli1.equals("s") && cli2.equals("r")) return false;
        else if (cli1.equals("s") && cli2.equals("s")) return true;
        else if (cli1.equals("s") && cli2.equals("p")) return true;
        else if (cli1.equals("p") && cli2.equals("r")) return true;
        else if (cli1.equals("p") && cli2.equals("s")) return false;
        else return true;
    }
}
