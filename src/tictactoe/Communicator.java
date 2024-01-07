package tictactoe;

import java.io.*;
import java.net.Socket;

/**
 *  Client와 Server를 객체로 연결하기 위해 생성한 Class
 *  @ Author 이주현
 *
 */
public class Communicator {
    private final BufferedWriter bw;
    private final BufferedReader br;
    private Socket socket;

    public String getId() {
        return Integer.toString(id);
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public Communicator(Socket socket, int id) throws IOException {
        this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.socket = socket;
        this.id = id;
    }
    public Communicator(Socket socket) throws IOException {
        this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.socket = socket;
    }

    /**
     * Client -> Server // Server -> Client 통신시 write() 할 경우 사용
     * @param message
     * @throws IOException
     */
    public void write(String message) throws IOException {
        bw.write(message);
        bw.newLine();
        bw.flush();
    }

    /**
     * 통신시 read() 메소드 필요할때 사용
     * @return
     * @throws IOException
     */

    public String read() throws IOException {
        return br.readLine();
    }
}
