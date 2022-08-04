package services;

import br.ce.wcaquino.servicos.Calculadora;
import br.ce.wcaquino.servicos.exceptions.NaoPodeDividirPorZeroException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CalculadoraTest {

    private Calculadora calculadora;

    @Before
    public void setup() {
        calculadora = new Calculadora();
    }
    @Test
    public void deveSomarDoisValores() {
        //cenario
        int a = 5;
        int b = 3;
        //acao
        int resultado = calculadora.somar(a, b);

        //verificacao
        Assert.assertEquals(8, resultado);

    }

    @Test
    public void deveSubtrairDoisValores() {
        int a = 8;
        int b = 5;

        int resultado = calculadora.subtrair(a, b);

        Assert.assertEquals(3, resultado);
    }

    @Test
    public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {
        int a = 6;
        int b = 3;

        int resultado = calculadora.dividir(a, b);

        Assert.assertEquals(2, resultado);
    }

    @Test(expected = NaoPodeDividirPorZeroException.class)
    public void deveLancarExcecacaoDivdirPorZero() throws NaoPodeDividirPorZeroException {
        int a = 10;
        int b = 0;

        calculadora.dividir(a, b);
    }

}
