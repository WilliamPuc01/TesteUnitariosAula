package services;

import builders.FilmeBuilder;
import builders.LocacaoBuilder;
import builders.UsuarioBuilder;
import org.example.daos.LocacaoDAO;
import org.example.entities.Filme;
import org.example.entities.Locacao;
import org.example.entities.Usuario;
import org.example.services.EmailService;
import org.example.services.LocacaoService;
import org.example.services.SPCService;
import org.example.utils.DataUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static builders.UsuarioBuilder.umUsuario;
import static org.example.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class LocacaoServiceTest {

    private LocacaoService service;
    private LocacaoDAO dao;
    private SPCService spc;
    private EmailService email;


    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Before
    public void setup() {
        dao = mock(LocacaoDAO.class);
        spc = mock(SPCService.class);
        service = new LocacaoService(dao);
        service.setSpcService(spc);
        email = Mockito.mock(EmailService.class);
        service.setEmailService(email);
    }

    @Test
    public void deveAlugarFilme() throws Exception {
        // cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Collections.singletonList(FilmeBuilder.umFilme().comValor(5.0).agora());

        // ação
        Locacao locacao = service.alugarFilme(usuario, filmes);

        // verificação
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
    }

    @Test(expected = Exception.class)
    public void naoDeveAlugarFilmeSemEstoque_elegante() throws Exception {
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Collections.singletonList(FilmeBuilder.umFilme().semEstoque().agora());

        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void naoDeveAlugarFilmeSemEstoque_robusta() {
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Collections.singletonList(FilmeBuilder.umFilme().semEstoque().agora());

        try {
            service.alugarFilme(usuario, filmes);
            Assert.fail("Deveria lançar exceção");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Filme sem estoque"));
        }
    }

    @Test
    public void naoDeveAlugarSemUsuario() {
        List<Filme> filmes = Collections.singletonList(FilmeBuilder.umFilme().agora());

        try {
            service.alugarFilme(null, filmes);
            Assert.fail("Deveria lançar exceção");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Usuario vazio"));
        }
    }

    @Test
    public void naoDeveAlugarSemFilme() {
        Usuario usuario = umUsuario().agora();

        try {
            service.alugarFilme(usuario, Collections.<Filme>emptyList());
            Assert.fail("Deveria lançar exceção");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Filme vazio"));
        }
    }

    // ===== Experimento com DAO =====

    @Test
    public void deveSalvarLocacaoNoDao() throws Exception {
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora(), FilmeBuilder.umFilme().agora());

        Locacao locacao = service.alugarFilme(usuario, filmes);

        verify(dao, times(1)).salvar(locacao);
    }

    @Test
    public void naoDeveSalvarNoDaoQuandoFilmeSemEstoque() {
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Collections.singletonList(FilmeBuilder.umFilme().semEstoque().agora());

        try {
            service.alugarFilme(usuario, filmes);
        } catch (Exception ignored) {
        }

        verify(dao, never()).salvar(any(Locacao.class));
    }

    @Test(expected = NullPointerException.class)
    public void semDaoInjetadoDeveEstourarNullPointer() throws Exception {
        // Mostra o problema do código original: sem DAO, salvar() estoura NPE
        LocacaoService serviceSemDao = new LocacaoService(null);
        serviceSemDao.alugarFilme(umUsuario().agora(),
                Collections.singletonList(FilmeBuilder.umFilme().agora()));
    }

    @Test
    public void naoDeveAlugarFilmeParaNegativadoSPC() {
        // cenário
        Usuario usuario = umUsuario().agora();
        Usuario usuario2 = umUsuario().comNome("Usuario 2").agora();
        List<Filme> filmes = Collections.singletonList(FilmeBuilder.umFilme().agora());

        when(spc.possuiNegativo(usuario)).thenReturn(true);

        // ação
        try {
            service.alugarFilme(usuario, filmes);
            Assert.fail("Deveria lançar exceção");
        } catch (Exception e) {
            // verificação
            assertThat(e.getMessage(), is("Usuario Negativado"));
        }

        verify(spc).possuiNegativo(usuario);
    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrasadas(){
        //Cenario
        Usuario usuario = umUsuario().agora();
        Usuario usuario2 = umUsuario().comNome("Usuario em dia").agora();
        Usuario usuario3 = umUsuario().comNome("Usuario atrasado").agora();
        List<Locacao> locacoes = Arrays.asList(
                LocacaoBuilder.umaLocacao().atrasada().comUsuario(usuario).agora(),
                LocacaoBuilder.umaLocacao().comUsuario(usuario2).agora(),
                LocacaoBuilder.umaLocacao().atrasada().comUsuario(usuario3).agora());
        when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

        //Acao
        service.notificarAtrasos();

        //Verificacao
        Mockito.verify(email).notificarAtraso(usuario);
        Mockito.verify(email).notificarAtraso(usuario3);
        Mockito.verify(email, never()).notificarAtraso(usuario2);
    }
}