package services;

import builders.FilmeBuilder;
import builders.UsuarioBuilder;
import org.example.daos.LocacaoDAOFake;
import org.example.entities.Filme;
import org.example.entities.Locacao;
import org.example.entities.Usuario;
import org.example.services.LocacaoService;
import org.example.utils.DataUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class LocacaoServiceTest {

    private LocacaoService service;
    private LocacaoDAOFake dao;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Before
    public void setup() {
        dao = new LocacaoDAOFake();
        service = new LocacaoService(dao);
    }

    @Test
    public void deveAlugarFilme() throws Exception {
        // cenário
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Collections.singletonList(FilmeBuilder.umFilme().comValor(5.0).agora());

        // ação
        Locacao locacao = service.alugarFilme(usuario, filmes);

        // verificação
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true));
    }

    @Test(expected = Exception.class)
    public void naoDeveAlugarFilmeSemEstoque_elegante() throws Exception {
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Collections.singletonList(FilmeBuilder.umFilme().semEstoque().agora());

        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void naoDeveAlugarFilmeSemEstoque_robusta() {
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
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
        Usuario usuario = UsuarioBuilder.umUsuario().agora();

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
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora(), FilmeBuilder.umFilme().agora());

        Locacao locacao = service.alugarFilme(usuario, filmes);

        assertThat(dao.quantidadeSalva(), is(1));
        assertThat(dao.getSalvas().get(0), is(sameInstance(locacao)));
    }

    @Test
    public void naoDeveSalvarNoDaoQuandoFilmeSemEstoque() {
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Collections.singletonList(FilmeBuilder.umFilme().semEstoque().agora());

        try {
            service.alugarFilme(usuario, filmes);
        } catch (Exception ignored) {
        }

        assertThat(dao.quantidadeSalva(), is(0));
    }

    @Test(expected = NullPointerException.class)
    public void semDaoInjetadoDeveEstourarNullPointer() throws Exception {
        // Mostra o problema do código original: sem DAO, salvar() estoura NPE
        LocacaoService serviceSemDao = new LocacaoService(null);
        serviceSemDao.alugarFilme(UsuarioBuilder.umUsuario().agora(),
                Collections.singletonList(FilmeBuilder.umFilme().agora()));
    }
}