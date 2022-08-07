package services;

import br.ce.wcaquino.servicos.Calculadora;
import br.ce.wcaquino.servicos.EmailService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraMockTest {

    @Mock
    private Calculadora calculadoraMock;
    @Spy
    private Calculadora calculadoraSpy;
    @Spy
    private EmailService emailService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void devoMostrarDiferencaEntreMockSpy() {
        Mockito.when(calculadoraMock.somar(1, 6)).thenReturn(8);
        Mockito.when(calculadoraSpy.somar(1,6)).thenReturn(8);

        Mockito.doReturn(5).when(calculadoraSpy).somar(1, 2);
        Mockito.doNothing().when(calculadoraSpy).imprime();


        System.out.println("Mock: " + calculadoraMock.somar(1, 2));
        calculadoraMock.imprime();
        System.out.println("Spy: " + calculadoraSpy.somar(1, 2));
        calculadoraSpy.imprime();
    }
}
