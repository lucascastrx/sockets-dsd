package Applications;

import Helpers.SocketAdapter;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Scanner;

public class Client {
    private final Scanner scan;
    private String ADDRESS = "127.0.0.1";
    private SocketAdapter socketAdapter;
    private boolean next;

    public Client() {
        this.scan = new Scanner(System.in);
    }

    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.ipconfig();
            client.start();

        } catch (IOException | URISyntaxException e) {

            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        boolean fluxControl = true;
        while (fluxControl) {
            this.socketAdapter = new SocketAdapter(new Socket(ADDRESS, 80));
            next = true;
            System.out.println("Conectado.");
            adapterReader();
            adapterWriter();
            adapterReader();
            if (next) {
                adapterWriter();
                adapterReader();
            }
            System.out.println("\n\nDeseja finalizar o programa? [y/N]");
            if (scan.nextLine().equalsIgnoreCase("y")) {
                fluxControl = false;
                socketAdapter.write("y");
                socketAdapter.close();
                System.out.println("A aplicacao foi finalizada.");
            } else {
                socketAdapter.write("N\n");
                socketAdapter.close();
            }
        }
    }

    private void adapterReader() {
        String msg;
        while ((msg = socketAdapter.read()) != null) {
            if (msg == null || msg.length() == 0) {
                break;
            }
            if (msg.equalsIgnoreCase("end")) {
                next = false;
                msg = "";
                System.out.println("Seleção invalida");
                break;
            }
            System.out.printf("%s \n", msg);
        }
    }

    private void adapterWriter() {
        System.out.println("\nEscreva sua mensagem");
        String msg = scan.nextLine();
        if (msg == null || msg.length() == 0) {
            next = false;
            msg = "end";
        }
        msg += "\n";
        socketAdapter.write(msg);
    }

    private void ipconfig() throws IOException, URISyntaxException {

        File arq = new File("ip.txt");
        ADDRESS = new String(Files.readAllBytes(arq.toPath()));

        System.out.println("Deseja continuar com o ultimo ip (" + ADDRESS + ")? [Y/n]");
        if (scan.nextLine().equalsIgnoreCase("n")) {
            System.out.println("Digite o novo ip do server:");
            ADDRESS = scan.nextLine();
            Files.write(arq.toPath(), ADDRESS.getBytes());
        }
    }

}
