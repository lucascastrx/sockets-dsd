package Helpers;

import DAO.DisciplinaRepository;
import DAO.PessoaRepository;
import Models.Disciplina;
import Models.Pessoa;

import java.util.Locale;
import java.util.Map;

public class Operation {

    private final Object[] models = new Object[3];
    private Object activeModel;

    public Operation(int type) {
        this.models[0] = new Pessoa();
        this.models[1] = new Disciplina();
        this.models[2] = null; // quando a query for para linkar/deslinkar
        setActiveModel(type);
    }

    public Object getActiveModel() {
        return activeModel;
    }

    public void setActiveModel(int type) {
        this.activeModel = this.models[type - 1];
    }

    public String execute(String[] operation) {
        try {

            if (activeModel == null) {
                switch (operation[0].toUpperCase(Locale.ROOT)) {
                    case "LINK":
                        return link(operation);
                    case "UNLINK":
                        return unlink(operation);
                    default:
                        return "Erro na operacao.\n";
                }
            }

            switch (operation[0].toUpperCase(Locale.ROOT)) {
                case "INSERT":
                    return insert(operation);
                case "UPDATE":
                    return update(operation);
                case "GET":
                    return get(operation);
                case "DELETE":
                    return delete(operation);
                case "LIST":
                    return list();
                default:
                    return "Erro na operacao.\n";
            }

        } catch (Exception e) {
            return "Erro na operacao. \n";
        }
    }


    private String insert(String[] operation) throws Exception {

        if (this.activeModel.getClass() == Pessoa.class) {
            PessoaRepository.pessoas.putIfAbsent(operation[1],
                    new Pessoa(operation[1],
                            operation[2],
                            operation[3])
            );

        } else {
            Disciplina d = new Disciplina(operation[1], Integer.parseInt(operation[2]));
            DisciplinaRepository.disciplinas.putIfAbsent(operation[1], d);
        }

        return "\n";

    }

    private String update(String[] operation) throws Exception {
        if (this.activeModel.getClass() == Pessoa.class) {
            Pessoa pessoa = PessoaRepository.pessoas.get(operation[1]);
            if (pessoa != null) {
                pessoa.setNome(operation[2]);
                pessoa.setEndereco(operation[3]);
                PessoaRepository.pessoas.put(operation[1], pessoa);
                /**
                 * Segue uma logica parecida com o delete(), depois que e
                 * atualizado no mapPessoas verifica se pode ser atualizado em
                 * algum list de Disciplinas
                 */
                if (pessoa.isAllocated()) {
                    for (Disciplina d : DisciplinaRepository.disciplinas.values()) {
                        for (Pessoa p : d.getPessoas()) {
                            if (pessoa.getCpf().equalsIgnoreCase(p.getCpf())) {
                                d.getPessoas().remove(p);
                                d.getPessoas().add(pessoa);
                                return "Pessoa atualizada com sucesso\n";
                            }
                        }
                    }
                }
                return "Pessoa atualizada com sucesso\n";
            } else {
                return "Pessoa não encontrada\n";
            }
        } else {
            Disciplina d = DisciplinaRepository.disciplinas.get(operation[1]);
            if (d != null) {
                d.setNome(operation[1]);
                d.setCargaHoraria(Integer.parseInt(operation[2]));
                DisciplinaRepository.disciplinas.put(operation[1], d);
                return "Disciplina atualizada com sucesso\n";
            } else {
                return "Disciplina não encontrada\n";
            }

        }
    }

    private String delete(String[] operation) throws Exception {
        if (this.activeModel.getClass() == Pessoa.class) {
            Pessoa pessoa = PessoaRepository.pessoas.get(operation[1]);
            if (pessoa != null) {
                /**
                 * Primeiro verifica se a pessoa esta em uma Disciplina, caso esteja
                 * faz um loop no map e em cada Disciplina existente ele faz um loop no list
                 * que guarda as Pessoas, verifica se o cpf é igual a algum existente no list
                 * da Disciplina e caso seja chama o metodo unlink pra q seja desfeita essa relacao
                 * e por fim remove a Pessoa do mapPessoas. Se a pessoa nao estiver em nenhuma disciplina
                 * ela pode ser removida sem problema nenhum.
                 */
                if (pessoa.isAllocated()) {
                    for (Disciplina d : DisciplinaRepository.disciplinas.values()) {
                        for (Pessoa p : d.getPessoas()) {
                            if (pessoa.getCpf().equalsIgnoreCase(p.getCpf())) {
                                String[] arr = {"UNLINK", p.getCpf(), d.getNome()};
                                unlink(arr);
                                PessoaRepository.pessoas.remove(operation[1]);
                                return "Pessoa removida com sucesso\n";
                            }
                        }
                    }
                }

                PessoaRepository.pessoas.remove(operation[1]);
                return "Pessoa removida com sucesso\n";
            } else if (PessoaRepository.pessoas.size() != 0) {
                return "Pessoa não encontrada\n";
            } else {
                return "Sem pessoas cadastradas\n";
            }

        } else {
            /**
             * Pra remover uma Disciplina a tarefa e mais facil.
             * Basta apenas fazer um loop no seu list de Pessoas e
             * dar um setAllocated(false), logo as pessoas nao estarao
             * em nenhuma Disciplina e ela pode ser deletada sem problemas ja que
             * o seu list sumira junto
             */
            Disciplina disciplina = DisciplinaRepository.disciplinas.get(operation[1]);
            if (disciplina != null) {
                disciplina.getPessoas().forEach(p -> {
                    p.setAllocated(false);
                });
                DisciplinaRepository.disciplinas.remove(operation[1]);
                return "Disciplina removida com sucesso\n";
            } else if (DisciplinaRepository.disciplinas.size() != 0) {
                return "Disciplina não encontrada\n";
            } else {
                return "Sem disciplinas cadastradas\n";
            }
        }
    }

    private String get(String[] operation) throws Exception {

        if (this.activeModel.getClass() == Pessoa.class) {
            Pessoa p = PessoaRepository.pessoas.get(operation[1]);
            if (p != null) {
                return p + "\n";
            } else if (PessoaRepository.pessoas.size() != 0) {
                return "Pessoa nao encontrada\n";
            }
            return "Sem pessoas cadastradas\n";

        } else {
            Disciplina d = DisciplinaRepository.disciplinas.get(operation[1]);
            if (d != null) {
                String txt = d.toString();
                for (Pessoa pessoa : d.getPessoas()) {
                    txt += "\n - " + pessoa.toString();
                }
                txt += "\n";
                return txt;
            } else if (DisciplinaRepository.disciplinas.size() != 0) {
                return "Disciplina não encontrada\n";
            }
            return "Sem disciplinas cadastradas\n";

        }
    }

    private String list() throws Exception {
        StringBuilder retorno;
        if (this.activeModel.getClass() == Pessoa.class) {
            Map<String, Pessoa> map = PessoaRepository.pessoas;
            retorno = new StringBuilder(String.format("%02d", map.size()) + "\n");

            for (String key : map.keySet()) {
                retorno.append(map.get(key)).append("\n");
            }

        } else {
            Map<String, Disciplina> map = DisciplinaRepository.disciplinas;
            retorno = new StringBuilder(String.format("%02d", map.size()) + "\n");

            for (String key : map.keySet()) {
                retorno.append(map.get(key)).append("\n");
                if (map.get(key).getPessoas().size() > 0) {
                    retorno.append("Pessoas matriculadas em " + map.get(key).getNome())
                            .append("\n");
                }
                for (Pessoa p : map.get(key).getPessoas()) {
                    retorno.append(" - ").append(p.toString()).append("\n");
                }
            }

        }
        return retorno.toString();
    }


    private String link(String[] operation) throws Exception {
        Pessoa p = PessoaRepository.pessoas.get(operation[1]);
        Disciplina d = DisciplinaRepository.disciplinas.get(operation[2]);
        if (p != null) {
            if (!p.isAllocated()) {
                if (d != null) {
                    d.addPessoa(p);
                    p.setAllocated(true);
                    PessoaRepository.pessoas.put(operation[1], p);
                    DisciplinaRepository.disciplinas.put(operation[2], d);
                    return "A pessoa foi inserida na disciplina\n";
                } else {
                    return "A disciplina selecionada nao existe\n";
                }
            } else {
                return "A pessoa ja esta inserida em uma disciplina\n";
            }
        } else {
            return "A pessoa selecionada nao existe\n";
        }
    }

    private String unlink(String[] operation) throws Exception {
        Pessoa p = PessoaRepository.pessoas.get(operation[1]);
        Disciplina d = DisciplinaRepository.disciplinas.get(operation[2]);
        if (p != null) {
            if (p.isAllocated()) {
                if (d != null) {
                    d.removePessoa(p);
                    p.setAllocated(false);
                    PessoaRepository.pessoas.put(operation[1], p);
                    DisciplinaRepository.disciplinas.put(operation[2], d);
                    return "A pessoa foi removida na disciplina\n";
                } else {
                    return "A disciplina selecionada nao existe\n";
                }
            } else {
                return "A pessoa nao esta inserida em uma disciplina\n";
            }
        } else {
            return "A pessoa selecionada nao existe\n";
        }
    }

}
