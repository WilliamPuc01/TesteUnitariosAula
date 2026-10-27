package builders;

import org.example.entities.Filme;
import org.example.entities.Locacao;
import org.example.entities.Usuario;
import org.example.utils.DataUtils;

import java.util.Arrays;
import java.util.Date;

public class LocacaoBuilder {

    private Locacao locacao;

    private LocacaoBuilder(){}

    // Locação padrão válida: 1 usuário, 1 filme, alugada hoje, devolução amanhã
    public static LocacaoBuilder umaLocacao(){
        LocacaoBuilder builder = new LocacaoBuilder();
        builder.locacao = new Locacao();
        builder.locacao.setUsuario(UsuarioBuilder.umUsuario().agora());
        builder.locacao.setFilmes(Arrays.asList(FilmeBuilder.umFilme().agora()));
        builder.locacao.setDataLocacao(new Date());
        builder.locacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(1));
        builder.locacao.setValor(4.0);
        return builder;
    }

    public LocacaoBuilder comUsuario(Usuario usuario){
        locacao.setUsuario(usuario);
        return this;
    }

    public LocacaoBuilder comFilmes(Filme... filmes){
        locacao.setFilmes(Arrays.asList(filmes));
        return this;
    }

    public LocacaoBuilder comDataLocacao(Date data){
        locacao.setDataLocacao(data);
        return this;
    }

    public LocacaoBuilder comDataRetorno(Date data){
        locacao.setDataRetorno(data);
        return this;
    }

    public LocacaoBuilder comValor(Double valor){
        locacao.setValor(valor);
        return this;
    }

    // Locação alugada há 4 dias com devolução prevista há 2 dias
    public LocacaoBuilder atrasada(){
        locacao.setDataLocacao(DataUtils.obterDataComDiferencaDias(-4));
        locacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(-2));
        return this;
    }

    public Locacao agora(){
        return locacao;
    }
}