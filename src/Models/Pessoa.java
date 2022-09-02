/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

public class Pessoa {

    public static String query = "<operacao>;<cpf>;<nome>;<endereco>";
    private String cpf;
    private String nome;
    private String endereco;
    private boolean allocated;

    public Pessoa(String cpf, String nome, String endereco) {
        this.cpf = cpf;
        this.nome = nome;
        this.endereco = endereco;
    }

    public Pessoa() {
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public boolean isAllocated() {
        return allocated;
    }

    public void setAllocated(boolean value) {
        this.allocated = value;
    }

    public String getCpf() {
        return this.cpf;
    }

    @Override
    public String toString() {
        return cpf + ";" + nome + ";" + endereco;
    }

}
