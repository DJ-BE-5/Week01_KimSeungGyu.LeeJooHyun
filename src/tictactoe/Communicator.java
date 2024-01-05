package tictactoe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class Communicator {
    private final BufferedWriter bw;
    private final BufferedReader br;

    public Communicator(BufferedWriter bw, BufferedReader br) {
        this.bw = bw;
        this.br = br;
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
