package Models;

import java.util.ArrayList;
import java.util.List;

public class Disciplina {

    private String nome;
    private int cargaHoraria;
    private List<Pessoa> pessoas = new ArrayList<>();
    public static String query = "<operacao>;<nome da disciplina>;<carga horaria;";

    public Disciplina(String nome, int cargaHoraria){
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
    }

    public Disciplina() { }

    public String getNome(){ return nome; }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCargaHoraria(int cargaHoraria) { this.cargaHoraria = cargaHoraria; }

    public void addPessoa(Pessoa p ){ pessoas.add(p); }

    public void removePessoa(Pessoa p ){ pessoas.remove(p); }

    public List<Pessoa> getPessoas(){ return pessoas; }

    @Override
    public String toString() { return nome + ";" + cargaHoraria; }
}
