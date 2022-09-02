package Helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketAdapter {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;


    public SocketAdapter(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );
        this.out = new PrintWriter(socket.getOutputStream(), true);

    }

    public String read() {
        String a = null;
        try {
            a = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return a;
    }

    public boolean write(String msg) {
        out.println(msg);

        return !out.checkError();
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
