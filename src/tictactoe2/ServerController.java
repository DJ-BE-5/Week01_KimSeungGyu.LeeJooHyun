package tictactoe2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerController implements Runnable {
    private final Thread thread;
    private boolean running = true;
    private BufferedReader br;
    private BufferedWriter bw;
    private int id;
    private ServerService serverService = ServerService.getInstance();

    public ServerController(Socket socket, int id) {
        this.thread = new Thread(this, "Server");
        this.id = id;
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch(IOException e) {
            System.err.println("Server 생성에서 IOException 발생");
            throw new RuntimeException(e);
        }
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        System.out.println(thread.getName() + " 스레드 종료");
        running = false;
        try {
            bw.close();
            br.close();
        } catch(IOException e) {
            System.err.println("Server::stop::Buffered**::close에서 IOException 발생");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while(running) {
            try {
                String call = br.readLine();
                if(call == null) {
                    System.out.println("클라이언트가 연결을 종료했습니다.");
                    stop();
                    break;
                }

                String[] split = call.split(" ", 2);
                if(split.length != 2) {
                    System.err.println("잘못된 메시지 형식입니다.");
                    continue;
                }

                String method = split[0];
                String message = split[1];

                switch(method) {
                    case "@1":      // 가위바위보 진행 여부 판단하는 api
                        System.out.println("@1 input" + id + " : " + message + " my turn: " + serverService.getNowTurn());
                        boolean b = serverService.shouldPlayRSP(id, message);
                        System.out.println("@1 output" + id + " : " + b + " my turn: " + serverService.getNowTurn());
                        bw.write(String.valueOf(b));
                        bw.newLine();
                        bw.flush();
                        break;
                    case "@2":      // 가위바위보 결과를 반환하는 api
                        System.out.println("@2 input" + id + " : " + message + " my turn: " + serverService.getNowTurn());
                        int i = serverService.playRSP(id, message);
                        System.out.println("@2 output" + id + " : " + i + " my turn: " + serverService.getNowTurn());
                        bw.write(String.valueOf(i));
                        bw.newLine();
                        bw.flush();
                        break;
                    case "@3":      // 틱택토 게임 진행 api    1: 승리, 2: 패배, 3: 무승부, 0: 진행중
                        System.out.println("@3 input" + id + " : " + message + " my turn: " + serverService.getNowTurn());
                        if(!serverService.playTicTacToe(id, message)) {
                            System.out.println("@3 output" + id + " : " + "-1" + " my turn: " + serverService.getNowTurn());
                            bw.write("-1");
                            bw.newLine();
                            bw.flush();
                            break;
                        }
                        String s = serverService.isFinished(id);
                        System.out.println("@3 output" + id + " : " + s + " my turn: " + serverService.getNowTurn());
                        bw.write(s);
                        bw.newLine();
                        bw.flush();
                        break;
                    case "@4":          // 현재 내 차례인지 반환하는 api
                        String s1 = serverService.getNowTurn() == id ? "1" : "0";
                        bw.write(s1);
                        bw.newLine();
                        bw.flush();
                        break;
                    case "@5":          // 보드 출력
                        System.out.println("@5 input" + id + " : " + message + " my turn: " + serverService.getNowTurn());
                        String board = serverService.getBoard();
                        bw.write(board);
                        bw.flush();
                        break;
                    case "@6":          // 게임이 끝났는 지 확인
                        String s2 = serverService.isFinished(id);
                        bw.write(s2);
                        bw.newLine();
                        bw.flush();
                        break;
                    default:
                        System.err.println("잘못된 메소드 호출입니다.");
                        break;
                }


            } catch(IOException e) {
                System.err.println("Server::run::BufferedReader::readLine에서 IOException 발생");
                stop();
            }
        }
    }

    public static void main(String[] args) {
        List<ServerController> serverList = new ArrayList<>();

        try(ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("서버 소켓 생성");

            int id = 0;
            while(!serverSocket.isClosed() && serverList.size() < 2) {
                Socket socket = serverSocket.accept();
                System.out.println("클라이언트 연결 : " + socket.getInetAddress() + ":" + socket.getPort());

                ServerController server = new ServerController(socket, id++);
                server.start();

                serverList.add(server);
            }

        } catch(IOException e) {
            System.err.println("Server::main::ServerSocket 생성에서 IOException 발생");
        }
    }
}
