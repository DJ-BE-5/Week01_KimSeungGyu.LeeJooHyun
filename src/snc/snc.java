package snc;

import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Scanner;

public class snc {
    private static String serverIp;
    private static int port;

    public static void main(String[] args) {
        if(valid(args)) {
            if(serverIp == null) {
                runServer();
            } else {
                runClient(serverIp, port);
            }
        }
    }

    /**
     * 입력받은 인자가 유효한지 검사
     * &#064;Author  : 김승규
     * @param args
     * @return 유효하면 true, 아니면 false
     */
    private static boolean valid(String[] args) {
        String errorMessage = """   
                Usage: snc [option] [hostname] [port]
                "Options : "
                "-l\t<port>\tserver로 동작시 입력 받은 port로 listen""";
        if(args.length != 3) {
            System.out.println(errorMessage);
            return false;
        }

        try {
            if(args[1].equals("-l")) {      // 서버
                port = Integer.parseInt(args[2]);
            } else {                        // 클라이언트
                serverIp = args[1];
                port = Integer.parseInt(args[2]);
            }
        } catch(NumberFormatException e) {
            System.out.println(errorMessage);
            return false;
        }
        return true;
    }

    /**
     * 리스너 서버 생성 및 실행
     * @Author  : 이주현
     */
    private static void runServer() {
        boolean running = true;
        while (running){
            try (ServerSocket serverSocket = new ServerSocket(port)){
                System.out.println("Server Connected");
                Socket socket = serverSocket.accept();
                System.out.println("Client Connected");

                String message = "";

                BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
                BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());

                byte[] buffer = new byte[256];
                int length = input.read(buffer, 0, buffer.length);

                message = new String(Arrays.copyOf(buffer,length));
                System.out.println(message);

                Scanner sc = new Scanner(System.in);
                message = sc.nextLine();
                message += "\n";

                output.write(message.getBytes(),0,message.length());
                output.flush();

            } catch (IOException e) {
                running = false;
                System.err.println("Server Error : " + e);
            }
        }

    }


    /**
     * 리스너 서버에 데이터를 전송하고 받는 클라이언트 실행
     * &#064;Author  : 김승규
     * @param serverIp 서버 IP
     * @param port 서버 포트
     */
    private static void runClient(String serverIp, int port) {
        boolean running = true;
        Scanner sc = new Scanner(System.in);
        while(running) {
            try(Socket socket = new Socket(serverIp, port);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                System.out.println("서버에 연결되었습니다. : " + socket.getInetAddress() + ":" + socket.getPort());

                System.out.println("메시지를 입력해 주세요.");
                bw.write(sc.nextLine());
                bw.newLine();
                bw.flush();

                System.out.println("서버로부터 받은 메시지 : " + br.readLine());
            } catch(IOException e) {
                running = false;
                System.err.println("Client Error : " + e);
            }
        }
    }
}
