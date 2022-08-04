package services;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.servicos.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.servicos.exceptions.LocadoraException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

    private LocacaoService service;
    @Parameterized.Parameter
    public List<Filme> filmes;
    @Parameterized.Parameter(value = 1)
    public Double valorLocacao;

    @Parameterized.Parameter(value = 2)
    public String cenario;

    @Before
    public void setup() {
        service = new LocacaoService();

    }

    private static Filme filme1 = new Filme("Zumbilandia", 2, 4.00);
    private static Filme filme2 = new Filme("Odin", 2, 4.00);
    private static Filme filme3 = new Filme("Questao de Tempo", 2, 4.00);
    private static Filme filme4 = new Filme("Asgard", 2, 4.00);
    private static Filme filme5 = new Filme("Frigga", 2, 4.00);
    private static Filme filme6 = new Filme("Sutur", 2, 4.00);

    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object> getParametros() {
        return Arrays.asList(new Object[][] {
            {Arrays.asList(filme1, filme2), 8.00, "2 filmes: Sem desconto"},
            {Arrays.asList(filme1, filme2, filme3), 11.00, "3 filmes: 25%"},
            {Arrays.asList(filme1, filme2, filme3, filme4), 13.00, "4 filmes: 50%"},
            {Arrays.asList(filme1, filme2, filme3, filme4,filme5), 14.00, "5 filmes: 75%"},
            {Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.00, "6 filmes: 100%"},
            {Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme6), 18.00, "7 filmes: Sem desconto"},
        });
    }

    @Test
    public void deveCalcularValorLocacaoComDescontosProgressivos() throws FilmeSemEstoqueException, LocadoraException {
        Usuario usuario = new Usuario("Usuario");

        Locacao resultado = service.alugarFilme(usuario, filmes);

        Assert.assertThat(resultado.getValor(), is(valorLocacao));
    }


}
