package org.example.daos;

import org.example.entities.Locacao;

import java.util.List;

public interface LocacaoDAO {

    public void salvar(Locacao locacao);

    List<Locacao> obterLocacoesPendentes();
}
