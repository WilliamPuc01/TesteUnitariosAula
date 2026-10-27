package org.example.daos;

import org.example.entities.Locacao;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO falso (test double) para os testes unitários.
 * Em vez de gravar no banco, guarda as locações em memória,
 * o que permite verificar se o serviço realmente chamou salvar().
 */
public class LocacaoDAOFake implements LocacaoDAO {

    private final List<Locacao> salvas = new ArrayList<>();

    @Override
    public void salvar(Locacao locacao) {
        salvas.add(locacao);
    }

    public List<Locacao> getSalvas() {
        return salvas;
    }

    public int quantidadeSalva() {
        return salvas.size();
    }
}