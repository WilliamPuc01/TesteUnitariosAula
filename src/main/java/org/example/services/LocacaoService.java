package org.example.services;

import org.example.daos.LocacaoDAO;
import org.example.entities.Filme;
import org.example.entities.Locacao;
import org.example.entities.Usuario;

import java.util.Date;
import java.util.List;

import static org.example.utils.DataUtils.adicionarDias;

public class LocacaoService {

    private LocacaoDAO dao;



    public Locacao alugarFilme(Usuario usuario, List<Filme> filmes)throws Exception {

        if(usuario == null)throw new Exception("Usuario vazio");

        for(Filme filme : filmes){
            if(filme.getEstoque() == 0){
                throw new Exception("Filme sem estoque");
            }
        }

        Locacao locacao = new Locacao();
        locacao.setFilmes(filmes);
        locacao.setUsuario(usuario);
        locacao.setDataLocacao(new Date());

        Double valorTotal = 0d;
        for (Filme filme : filmes){
          valorTotal += filme.getPrecoLocacao();
        }
        locacao.setValor(valorTotal);


        //Entrega no dia seguinte
        Date dataEntrega = new Date();
        dataEntrega = adicionarDias(dataEntrega, 1);
        locacao.setDataRetorno(dataEntrega);

        //Salvando a locacao...
       dao.salvar(locacao);

        return locacao;
    }



}