package Helpers;

public class Util {

    public static void sendOptionsToClient(SocketAdapter adapter) {
        String mensagem = "Digite o numero da opcao: \n"
                + "1: para executar operacoes com pessoas \n"
                + "2: para executar operacoes com disciplinas \n"
                + "3: para linkar/deslinkar uma pessoa a uma disciplina \n";
        adapter.write(mensagem);
        System.out.println("Opcoes de comando enviadas!");
    }

    public static void sendQueryHelperToClient(String query, SocketAdapter adapter) {
        String mensagem = "Monte a query seguindo o padrao abaixo: \n"
                + query + "\n";
        adapter.write(mensagem);
    }

    public static String[] convert(String mensagem) {
        return mensagem.split(";");
    }

    public static void populateDatabase() {

        //popular pessoas
        for (int i = 0; i < 10; i++) {
            Operation o = new Operation(1);
            int a = 1;
            a += i;
            String n = "" + a;
            if (i % 2 == 0) {
                String[] c = {"INSERT", "444444" + n, "LUCAS " + n, "RUA " + n};
                o.execute(c);
            } else {
                String[] c = {"INSERT", "555555" + n, "EDUARDO " + n, "AVENIDA " + n};
                o.execute(c);
            }
        }

        //popular disciplinas
        String[] nomes = {"Matematica", "Geografia", "Ciencias"};
        for (int i = 0; i < 3; i++) {
            Operation o = new Operation(2);
            String[] c = {"INSERT", nomes[i], i + 100 + ""};
            o.execute(c);
        }

        // adicionar pessoas em disciplinas
        Operation o = new Operation(3);
        String[] c = {"LINK", "4444441", "Matematica"};
        o.execute(c);

        String[] d = {"LINK", "4444447", "Matematica"};
        o.execute(d);

        String[] e = {"LINK", "5555558", "Ciencias"};
        o.execute(e);
    }
}
