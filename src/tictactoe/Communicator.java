package tictactoe;

import java.io.*;
import java.net.Socket;

public class Communicator {
    private final BufferedWriter bw;
    private final BufferedReader br;
    private Socket socket;

    public Communicator(Socket socket) throws IOException {
        this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.socket = socket;
    }

    public void write(String message) throws IOException {
        bw.write(message);
        bw.newLine();
        bw.flush();
    }

    public String read() throws IOException {
        return br.readLine();
    }
}
