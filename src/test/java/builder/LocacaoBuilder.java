package builder;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

import java.util.Arrays;
import java.util.Date;

import static builder.FilmeBuilder.umFilme;
import static builder.UsuarioBuilder.umUsuario;

public class LocacaoBuilder {

    private Locacao elemento;
    private LocacaoBuilder(){}

    public static LocacaoBuilder umLocacao() {
        LocacaoBuilder builder = new LocacaoBuilder();
        inicializarDadosPadroes(builder);
        return builder;
    }


    public static void inicializarDadosPadroes(LocacaoBuilder builder) {
        builder.elemento = new Locacao();
        Locacao elemento = builder.elemento;

        elemento.setUsuario(umUsuario().agora());
        elemento.setFilme(Arrays.asList(umFilme().agora()));
        elemento.setDataLocacao(new Date());
        elemento.setDataRetorno(DataUtils.obterDataComDiferencaDias(1));
        elemento.setValor(4.00);
    }

    public LocacaoBuilder comUsuario(Usuario param) {
        elemento.setUsuario(param);
        return this;
    }

    public LocacaoBuilder comListaFilme(Filme... params) {
        elemento.setFilme(Arrays.asList(params));
        return this;
    }

    public LocacaoBuilder comDataLocacao(Date param) {
        elemento.setDataLocacao(param);
        return this;
    }

    public LocacaoBuilder comDataRetorno(Date param) {
        elemento.setDataRetorno(param);
        return this;
    }

    public LocacaoBuilder comValor(Double valor) {
        elemento.setValor(valor);
        return this;
    }

    public LocacaoBuilder atrasado() {
        elemento.setDataLocacao(DataUtils.obterDataComDiferencaDias(-4));
        elemento.setDataRetorno(DataUtils.obterDataComDiferencaDias(-2));
        return this;
    }

    public Locacao agora() {
        return elemento;
    }

}
