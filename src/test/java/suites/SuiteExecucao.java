package suites;

import br.ce.wcaquino.servicos.Calculadora;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import services.CalculadoraTest;
import services.CalculoValorLocacaoTest;
import services.LocacaoServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CalculadoraTest.class,
        CalculoValorLocacaoTest.class,
        LocacaoServiceTest.class
})
public class SuiteExecucao {

}
