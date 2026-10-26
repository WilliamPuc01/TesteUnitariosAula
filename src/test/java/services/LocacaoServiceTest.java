package services;

import builders.FilmeBuilder;
import builders.UsuarioBuilder;
import org.example.entities.Filme;
import org.example.entities.Locacao;
import org.example.entities.Usuario;
import org.example.services.LocacaoService;
import org.example.utils.DataUtils;
import org.junit.*;
import org.junit.rules.ErrorCollector;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LocacaoServiceTest {

    private LocacaoService service;

   private static int i = 0;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Before
    public void setup(){
        System.out.println("Before");
        service = new LocacaoService();
        i++;
        System.out.println(i);
    }


    @Test
    public void testeLocacao() throws Exception {
        // cenário

        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Collections.singletonList(FilmeBuilder.umFilme().agora());
        System.out.println("Teste!");

        // ação


        Locacao locacao = service.alugarFilme(usuario, filmes);

        // verificação
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true));
    }
    @Test(expected = Exception.class)
    public void testeLocacao_filmeSemEstoque() throws Exception {
        // cenário
       // LocacaoService service = new LocacaoService();
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 5.0));

        // ação


        Locacao locacao = service.alugarFilme(usuario, filmes);

        // verificação
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true));
    }

    @Test
    public void testeLocacao_filmeSemEstoque_2() throws Exception {
        // cenário
       // LocacaoService service = new LocacaoService();
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Collections.singletonList(FilmeBuilder.umFilme().agora());

        // ação

        Locacao locacao;

        try{
            locacao = service.alugarFilme(usuario, filmes);
            Assert.fail("Deveria Lanca excecao");

        }catch (Exception e){
            assertThat(e.getMessage(), is("Filme sem estoque"));

        }


    }
}