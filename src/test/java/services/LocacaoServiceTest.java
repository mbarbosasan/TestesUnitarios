package services;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.dao.LocacaoDAOFake;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.servicos.EmailService;
import br.ce.wcaquino.servicos.SPCService;
import br.ce.wcaquino.servicos.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.servicos.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import builder.FilmeBuilder;
import builder.LocacaoBuilder;
import builder.UsuarioBuilder;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.*;

import java.util.*;

import static br.ce.wcaquino.servicos.exceptions.matchers.MatchersProprios.*;
import static builder.FilmeBuilder.umFilme;
import static builder.FilmeBuilder.umFilmeSemEstoque;
import static builder.LocacaoBuilder.umLocacao;
import static builder.UsuarioBuilder.umUsuario;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class LocacaoServiceTest {
    @InjectMocks
    private LocacaoService service;
    @Mock
    private SPCService spc;
    @Mock
    private LocacaoDAO dao;
    @Mock
    private EmailService emailService;


    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void deveAlugarFilme() throws Exception {
        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
        Usuario usuario = umUsuario().agora();
        List<Filme> filme = Arrays.asList(umFilme().comValor(5.00).agora());

        Locacao locacao = service.alugarFilme(usuario, filme);

        error.checkThat(locacao.getValor(), is(equalTo(5.00)));
        error.checkThat(locacao.getDataLocacao(), ehHoje());
        error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));

    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void deveLancarExcecaoQuandoOfilmeNaoTiverEstoque() throws Exception {
        Usuario usuario = umUsuario().agora();
        List<Filme> filme = Arrays.asList(umFilmeSemEstoque().agora());

        service.alugarFilme(usuario, filme);
    }

    @Test
    public void deveLancarExcecaoQuandoOfilmeNaoTiverEstoque2() {
        Usuario usuario = umUsuario().agora();
        List<Filme> filme = Arrays.asList(umFilmeSemEstoque().agora());

        try {
            service.alugarFilme(usuario, filme);
            Assert.fail("Deveria ter lancado uma excecao");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), is("Filme sem estoque"));
        }
    }

    @Test
    public void deveLancarExcecaoQuandoOFilmeNaoTiverEstoque3() throws Exception {
        Usuario usuario = umUsuario().agora();
        List<Filme> filme = Arrays.asList(umFilmeSemEstoque().agora());

        expectedException.expect(Exception.class);
        expectedException.expectMessage("Filme sem estoque");

        service.alugarFilme(usuario, filme);

    }

    @Test
    public void deveLancarExcecaoQuandoNaoTiverUsuario() throws FilmeSemEstoqueException {
        List<Filme> filme = Arrays.asList(umFilme().agora());
        Usuario usuario = umUsuario().agora();

        try {
            service.alugarFilme(usuario, filme);
        } catch (LocadoraException e) {
            Assert.assertThat(e.getMessage(), is("Usuario vazio"));
        }

    }

    @Test
    public void deveLancarExcecaoQuandoNaoTiverFilme() throws FilmeSemEstoqueException, LocadoraException {
        Usuario usuario = umUsuario().agora();

        expectedException.expect(LocadoraException.class);
        expectedException.expectMessage("Filme vazio");

        service.alugarFilme(usuario, null);
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        Locacao retorno = service.alugarFilme(usuario, filmes);

        assertThat(retorno.getDataRetorno(), caiNumaSegunda());
    }

    @Test
    public void naoDeveAlugarFilmeParaNegativoadSPC() throws Exception {
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        Mockito.when(spc.possuiNegativacao(usuario)).thenReturn(true);

        try {
            service.alugarFilme(usuario, filmes);
            Assert.fail();
        } catch (LocadoraException e) {
            Assert.assertThat(e.getMessage(), is("Usuario negativado"));
        }


        verify(spc).possuiNegativacao(usuario);

    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrasadas() {
        Usuario usuario = umUsuario().agora();
        Usuario usuario2 = umUsuario().comNome("Usuario em dia").agora();
        Usuario usuario3 = umUsuario().comNome("Outro atrasado").agora();
        List<Locacao> locacoes = Arrays.asList(
                umLocacao().atrasado().comUsuario(usuario).agora(),
                umLocacao().comUsuario(usuario2).agora(),
                umLocacao().atrasado().comUsuario(usuario3).agora(),
                umLocacao().atrasado().comUsuario(usuario3).agora()
    );


        Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

        service.notificarAtrasos();
        verify(emailService, times(3)).notificarAtraso(Mockito.any(Usuario.class));
        verify(emailService).notificarAtraso(usuario);
        verify(emailService, Mockito.atLeastOnce()).notificarAtraso(usuario3);
        verify(emailService, Mockito.never()).notificarAtraso(usuario2);
        Mockito.verifyNoMoreInteractions(emailService);

    }

    @Test
    public void deveTratarErroNoSPC() throws Exception {
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        when(spc.possuiNegativacao(usuario)).thenThrow(new RuntimeException("Problemas com SPC, tente novamente"));

        expectedException.expect(LocadoraException.class);
        expectedException.expectMessage("Problemas com SPC, tente novamente");

        service.alugarFilme(usuario, filmes);


    }

    @Test
    public void deveProrrograrUmaLocacao() {
        Locacao locacao = umLocacao().agora();

        service.prorrogarLocacao(locacao, 3);


        ArgumentCaptor<Locacao> argumentCaptor = ArgumentCaptor.forClass(Locacao.class);
        verify(dao).salvar(argumentCaptor.capture());
        Locacao locacaoRetornada = argumentCaptor.getValue();

        error.checkThat(locacaoRetornada.getValor(), is(12.00));
        error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
        error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(3));
    }


}
