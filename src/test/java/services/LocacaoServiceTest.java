package services;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.servicos.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.servicos.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.util.*;

import static br.ce.wcaquino.servicos.exceptions.matchers.MatchersProprios.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class LocacaoServiceTest {
    private LocacaoService service;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        service = new LocacaoService();
    }


    @Test
    public void deveAlugarFilme() throws Exception {
        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
        Usuario usuario = new Usuario("Joao");
        List<Filme> filme = Arrays.asList(new Filme("Zumbilandia 1", 1, 4.0));

        Locacao locacao = service.alugarFilme(usuario, filme);

        error.checkThat(locacao.getValor(), is(equalTo(4.00)));
        error.checkThat(locacao.getDataLocacao(), ehHoje());
        error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));

    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void deveLancarExcecaoQuandoOfilmeNaoTiverEstoque() throws Exception {
        Usuario usuario = new Usuario("Joao");
        List<Filme> filme = Arrays.asList(new Filme("Zumbilandia 1", 0, 4.0));

        service.alugarFilme(usuario, filme);
    }

    @Test
    public void deveLancarExcecaoQuandoOfilmeNaoTiverEstoque2() {
        Usuario usuario = new Usuario("Joao");
        List<Filme> filme = Arrays.asList(new Filme("Zumbilandia 1", 0, 4.0));

        try {
            service.alugarFilme(usuario, filme);
            Assert.fail("Deveria ter lancado uma excecao");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), is("Filme sem estoque"));
        }
    }

    @Test
    public void deveLancarExcecaoQuandoOFilmeNaoTiverEstoque3() throws Exception {
        Usuario usuario = new Usuario("Joao");
        List<Filme> filme = Arrays.asList(new Filme("Zumbilandia 1", 0, 4.0));

        expectedException.expect(Exception.class);
        expectedException.expectMessage("Filme sem estoque");

        service.alugarFilme(usuario, filme);

    }

    @Test
    public void deveLancarExcecaoQuandoNaoTiverUsuario() throws FilmeSemEstoqueException {
        List<Filme> filme = Arrays.asList(new Filme("Zumbilandia 1", 1, 4.0));
        Usuario usuario = new Usuario("Usuario 1");

        try {
            service.alugarFilme(usuario, filme);
        } catch (LocadoraException e) {
            Assert.assertThat(e.getMessage(), is("Usuario vazio"));
        }

    }

    @Test
    public void deveLancarExcecaoQuandoNaoTiverFilme() throws FilmeSemEstoqueException, LocadoraException {
        Usuario usuario = new Usuario("Usuario 1");

        expectedException.expect(LocadoraException.class);
        expectedException.expectMessage("Filme vazio");

        service.alugarFilme(usuario, null);
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.00));

        Locacao retorno = service.alugarFilme(usuario, filmes);

        assertThat(retorno.getDataRetorno(), caiNumaSegunda());
    }


}
