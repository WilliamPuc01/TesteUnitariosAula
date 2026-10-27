package services;

import builders.FilmeBuilder;
import builders.UsuarioBuilder;
import org.example.daos.LocacaoDAO;
import org.example.daos.LocacaoDAOFake;
import org.example.entities.Filme;
import org.example.entities.Locacao;
import org.example.services.LocacaoService;
import org.example.services.SPCService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Testa o desconto progressivo com filmes de R$ 4,00:
 * 3º = 25% | 4º = 50% | 5º = 75% | 6º+ = 100%
 */
@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

    @Parameter(0)
    public int quantidadeFilmes;

    @Parameter(1)
    public Double valorEsperado;

    @Parameter(2)
    public String cenario;

    private LocacaoService service;

    @Before
    public void setup() {
        service = new LocacaoService(new LocacaoDAOFake());
        LocacaoDAO dao = Mockito.mock(LocacaoDAO.class);
        service.setLocacaoDAO(dao);
        SPCService spc = Mockito.mock(SPCService.class);
        service.setSpcService(spc);
    }

    @Parameters(name = "{2}")
    public static Collection<Object[]> dados() {
        return Arrays.asList(new Object[][]{
                {1, 4.0, "1 filme: sem desconto"},
                {2, 8.0, "2 filmes: sem desconto"},
                {3, 11.0, "3 filmes: 25% no 3º"},
                {4, 13.0, "4 filmes: 50% no 4º"},
                {5, 14.0, "5 filmes: 75% no 5º"},
                {6, 14.0, "6 filmes: 6º grátis"},
                {7, 14.0, "7 filmes: 7º também grátis"},
        });
    }

    @Test
    public void deveCalcularValorLocacaoConsiderandoDescontos() throws Exception {
        List<Filme> filmes = new ArrayList<>();
        for (int i = 1; i <= quantidadeFilmes; i++) {
            filmes.add(FilmeBuilder.umFilme().comNome("Filme " + i).agora());
        }

        Locacao resultado = service.alugarFilme(UsuarioBuilder.umUsuario().agora(), filmes);

        assertThat(resultado.getValor(), is(valorEsperado));
    }
}