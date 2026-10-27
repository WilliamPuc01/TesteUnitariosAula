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
    private SPCService spcService;
    private  EmailService emailService;

    // O DAO é injetado: em produção vem a implementação real (banco),
    // nos testes vem um DAO falso. Assim o serviço não depende do banco.
    public LocacaoService(LocacaoDAO dao) {
        this.dao = dao;
    }

    public void setLocacaoDAO(LocacaoDAO dao) {
        this.dao = dao;
    }

    public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws Exception {

        if (usuario == null) throw new Exception("Usuario vazio");
        if (filmes == null || filmes.isEmpty()) throw new Exception("Filme vazio");
        if(spcService.possuiNegativo(usuario)){
            throw new Exception("Usuario Negativado");
        }

        for (Filme filme : filmes) {
            if (filme.getEstoque() == null || filme.getEstoque() == 0) {
                throw new Exception("Filme sem estoque");
            }
        }

        Locacao locacao = new Locacao();
        locacao.setFilmes(filmes);
        locacao.setUsuario(usuario);
        locacao.setDataLocacao(new Date());
        locacao.setValor(calcularValorLocacao(filmes));

        // Entrega no dia seguinte
        Date dataEntrega = adicionarDias(new Date(), 1);
        locacao.setDataRetorno(dataEntrega);

        // Salvando a locacao...
        dao.salvar(locacao);

        return locacao;
    }

    public void setEmailService(EmailService email){
        emailService = email;
    }

    public void setSpcService(SPCService spc){
        spcService = spc;
    }

    public void notificarAtrasos(){
        List<Locacao> locacoes = dao.obterLocacoesPendentes();
        for(Locacao locacao : locacoes){
            if(locacao.getDataRetorno().before(new Date())) {
                emailService.notificarAtraso(locacao.getUsuario());
            }
        }
    }



    /**
     * Desconto progressivo por quantidade de filmes na mesma locação:
     * 1º e 2º: preço cheio | 3º: 25% | 4º: 50% | 5º: 75% | 6º em diante: grátis
     */
    private Double calcularValorLocacao(List<Filme> filmes) {
        double valorTotal = 0d;
        for (int i = 0; i < filmes.size(); i++) {
            double preco = filmes.get(i).getPrecoLocacao();
            valorTotal += preco * (1 - percentualDesconto(i + 1));
        }
        // arredonda para 2 casas para evitar lixo de ponto flutuante
        return Math.round(valorTotal * 100) / 100.0;
    }

    private double percentualDesconto(int posicaoFilme) {
        switch (posicaoFilme) {
            case 1:
            case 2:
                return 0.0;
            case 3:
                return 0.25;
            case 4:
                return 0.50;
            case 5:
                return 0.75;
            default:
                return 1.0;
        }
    }
}