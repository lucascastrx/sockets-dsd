package Applications;

import Helpers.Operation;
import Helpers.SocketAdapter;
import Helpers.Util;
import Models.Disciplina;
import Models.Pessoa;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final String[] queries = new String[3];
    private int type;
    private boolean fluxControl;

    public Server() {
        this.queries[0] = Pessoa.query;
        this.queries[1] = Disciplina.query;
        this.queries[2] = "<operacao>;<cpf da pessoa>;<nome da disciplina>";
        this.fluxControl = true;
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException, InterruptedException {

        System.out.println("Iniciando servidor.");
        Util.populateDatabase();

        ServerSocket server = new ServerSocket(80);

        while (true) {
            fluxControl = true;
            server.setReuseAddress(true);

            while (fluxControl) {
                System.out.println("\nAguardando nova conexao.");

                try (Socket conn = server.accept()) {
                    SocketAdapter adapter = new SocketAdapter(conn);

                    System.out.println("Client conectado. (" + conn.getInetAddress().getHostAddress() + ")");

                    Util.sendOptionsToClient(adapter);
                    String stringType = adapterReader(adapter);
                    boolean run = stringType.equalsIgnoreCase("1") || stringType.equalsIgnoreCase("2") ||
                            stringType.equalsIgnoreCase("3");
                    if (run) {
                        type = Integer.parseInt(stringType);

                        Operation op = new Operation(type);

                        Util.sendQueryHelperToClient(this.queries[type - 1], adapter);


                        String b = adapterReader(adapter);
                        String[] comando = Util.convert(b);
                        adapter.write(op.execute(comando));
                    } else {
                        adapter.write("end");
                    }

                    adapterReader(adapter);
                }
            }
        }
    }


    public String adapterReader(SocketAdapter socketAdapter) {
        String msg;
        String output = null;
        try {
            while ((msg = socketAdapter.read()) != null) {
                if (msg.equalsIgnoreCase("y")) {
                    System.out.println("\n\nClient desconectou.");
                    this.fluxControl = false;
                    socketAdapter.close();
                    break;
                }
                if (msg == null || msg.length() == 0) {
                    break;
                }
                output = msg;
                System.out.printf("\n%s", output);
            }
        } finally {
            return output;
        }
    }


}
