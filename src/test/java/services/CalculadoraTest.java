package services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CalculadoraTest {

    private Calculador calc;

    @Before
    public void setup(){
        calc = new Calculador();
    }

    @Test
    public void deveSomarDoisValores(){

        //cenario
        int a = 5;
        int b = 3;
       // Calculador calc = new Calculador();

        //acao
        int resultado = calc.somar(a, b);

        //verificacao
        Assert.assertEquals(8,resultado);
    }

    @Test
    public void deveSubtrairDoisValores(){
        //cenario
        int a = 5;
        int b = 3;

        //Calculador calc = new Calculador();

        //acao
        int resultado = calc.subtrair(a,b);


        //verificacao
        Assert.assertEquals(2, resultado);
    }

    @Test
    public void deveDividirDoisValores(){
        //cenario
        int a = 6;
        int b = 3;

        //Calculador calc = new Calculador();

        //acao
        int resultado = calc.dividir(a,b);

        //verificacao
        Assert.assertEquals(2, resultado);
    }
}
