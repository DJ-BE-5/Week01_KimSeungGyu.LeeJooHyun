package tictactoe2;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try(Socket socket = new Socket("localhost", 1234);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            System.out.println("서버에 연결되었습니다. " + socket.getInetAddress() + ":" + socket.getPort());

            // 가위바위보 실시 여부
            int input;
            String serverResponse;
            do {
                System.out.println("가위바위보를 통해 순서를 정하고 싶으시면 1, 접속 순으로 순서를 정하시고 싶으시면 2를 입력해주세요.");
                System.out.println("두 플레이어가 모두 동의하시면 가위바위보를 진행합니다.");
                input = sc.nextInt();
            } while(input != 1 && input != 2);

            // 가위바위보 실시
            callAPI("@1 " + input, bw, br);
            serverResponse = br.readLine();
            if(serverResponse.equals("true")) {
                System.out.println("가위바위보를 시작합니다.");
                boolean running;
                do {
                    running = false;

                    do {
                        System.out.println("가위바위보를 입력해주세요.(1: 가위, 2: 바위, 3: 보)");
                        input = sc.nextInt();
                    } while(input != 1 && input != 2 && input != 3);

                    callAPI("@2 " + input, bw, br);
                    serverResponse = br.readLine();
                    if("1". equals(serverResponse)) {
                        System.out.println("승리하셨습니다. 당신이 선공입니다.");
                    } else if("2".equals(serverResponse)) {
                        System.out.println("패배하셨습니다. 당신은 후공입니다. 순서를 기다려주세요.");
                    } else {
                        System.out.println("비기셨습니다. 다시 가위바위보를 진행합니다.");
                        running = true;
                    }
                } while(running);
            }

            // 틱택토 시작
            System.out.println("틱택토 게임을 시작합니다.");
            System.out.println("틱택토 게임은 3x3의 판에서 진행됩니다.");

            int x;
            int y;
            callAPI("@4 " + "No Message", bw, br);
            serverResponse = br.readLine();
            Boolean isMyTurn = serverResponse.equals("1");
            if(!isMyTurn) {
                System.out.println("상대방의 차례입니다. 상대방의 차례가 끝날 때까지 기다려주세요.");
            }
            while(true) {
                // 게임이 끝났는지 확인
                callAPI("@6 " + "No Message", bw, br);
                serverResponse = br.readLine();
                if(!serverResponse.equals("0")) {
                    // 내 차례가 아닌 경우 보드 출력
                    bw.write("@5 No Message");
                    bw.newLine();
                    bw.flush();
                    System.out.println(br.readLine());
                    System.out.println(br.readLine());
                    System.out.println(br.readLine());
                }
                if(serverResponse.equals("1")) {
                    System.out.println("승리하셨습니다!!!");
                    break;
                } else if(serverResponse.equals("2")) {
                    System.out.println("패배하셨습니다...");
                    break;
                } else if(serverResponse.equals("3")) {
                    System.out.println("무승부입니다!");
                    break;
                }

                // 자신의 차례인지 확인
                callAPI("@4 " + "No Message", bw, br);
                serverResponse = br.readLine();
                isMyTurn = serverResponse.equals("1");
                if(!isMyTurn) {
                    continue;
                }
                // 내 차례에 보드 출력
                bw.write("@5 No Message");
                bw.newLine();
                bw.flush();
                System.out.println(br.readLine());
                System.out.println(br.readLine());
                System.out.println(br.readLine());

                // 좌표 입력
                do {
                    System.out.println("x, y 좌표를 입력해주세요. 왼쪽 위가 0,0 오른쪽 아래가 2,2 입니다.");
                    System.out.println("입력 방식 : x y(x : 0~2, y : 0~2)");
                    x = sc.nextInt();
                    y = sc.nextInt();
                } while(x < 0 || x > 2 || y < 0 || y > 2);

                // 좌표 전송
                callAPI("@3 " + x + " " + y, bw, br);
                serverResponse = br.readLine();

                // 돌을 놓은 후 보드 출력
                bw.write("@5 No Message");
                bw.newLine();
                bw.flush();
                System.out.println(br.readLine());
                System.out.println(br.readLine());
                System.out.println(br.readLine());

                // 게임이 끝났는지 확인
                if(serverResponse.equals("1")) {
                    System.out.println("승리하셨습니다!!!");
                    break;
                } else if(serverResponse.equals("2")) {
                    System.out.println("패배하셨습니다...");
                    break;
                } else if(serverResponse.equals("3")) {
                    System.out.println("무승부입니다!");
                    break;
                } else if(serverResponse.equals("-1")) {
                    System.out.println("이미 돌이 놓인 위치이거나 잘못된 접근입니다. 다시 입력해주세요.");
                }

                // 끝나지 않은 경우 상대방의 차례를 기다린다.
                System.out.println("상대방의 차례입니다. 상대방의 차례가 끝날 때까지 기다려주세요.");
            }
        } catch(UnknownHostException e) {
            System.err.println("Client::main::Socket 생성에서 UnknownHostException 발생");
        } catch(IOException e) {
            System.err.println("Client::main::Socket 생성에서 IOException 발생");
        }
    }

    private static void callAPI(String message, BufferedWriter bw, BufferedReader br) throws IOException {
        bw.write(message);
        bw.newLine();
        bw.flush();
    }
}
