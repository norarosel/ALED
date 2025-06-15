package es.upm.dit.aled.lab2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase para mejorar la generacion de laberintos que se resuelven en Laberinto
 * 
 * @author Miguel A. de Miguel
 *
 * es.upm.dit.aled.lab2.Laberinto
 */
public class LaberintoTodoAccesible extends Laberinto {
    
    /**
     * Constructor para crear un nuevo laberinto. Este constructor inicializa el laberinto y lo genera
     * 
     * @param n numero de filas y columnas del laberinto cuadrado
     */
	public LaberintoTodoAccesible(int n) {
		super(n);
	}

	/**
	 * Comprueba si se pueden alcanzar todas las posiciones del laberinto
	 * @return true cuando todo es alcanzable
	 */
   

	
	public boolean compruebaAccesibilidad() {
    	
		boolean [][] resultado = new boolean [N+2][N+2]; //Inicializo una variable donde guardo todo el tablero
		
    	laberintoAccesible (1,1,resultado); //Guardo todos los datos de las posiciones del tablero con el metodo laberintoAccesible en resultado

        for (int i=1; i<=N; i++)
            for (int j=1; j<=N; j++ )
                if (resultado[i][j]==false) //Comprobar en todas las posiciones si estan aisladas
                    return false;
        return true;
        
    }
	
    public void laberintoAccesible(int x, int y, boolean [][] resultado) {
    	//Metodo auxiliar para comprobar la accesibilidad
    	resultado [x][y] = true; 
    	//NORTE ACCESIBLE
    	if((!norte[x][y])&& (!resultado[x][y+1]))
    		laberintoAccesible (x, y+1,resultado);
    	//SUR ACCESIBLE
        if((!sur[x][y])&& (!resultado[x][y-1]))
        	laberintoAccesible (x, y-1,resultado);
      //ESTE ACCESIBLE
        if((!este[x][y])&& (!resultado[x+1][y]))
        	laberintoAccesible (x+1, y,resultado);;
      //OESTE ACCESIBLE
        if((!oeste[x][y])&& (!resultado[x-1][y]))
        	laberintoAccesible (x-1, y,resultado);
        
    }
    
	/**
	 * Aisla por norte sur este y oeste una posicion
	 * @param x coordenada x
	 * @param y coordenada y
	 */
    protected void aisla(int x, int y) {
    	// TODO
    	 //Pared Norte
        norte[x][y] = true;
        sur [x][y+1] = true;
        //Pared Sur
        sur [x][y] = true;
        norte [x][y-1] = true;
        //Pared este
        este [x][y] = true;
        oeste [x+1][y] = true;
        //Pared Oeste
        oeste [x][y] = true;
        este [x-1][y] = true;
    }
    
    /**
     * Ejecuta la busqueda con control de accesibilidad
     * @param numFilasColumnas numero de fila/columnas del laberinto
     * @return true cuando todo es accesible
     */
    public static boolean ejecutaConControlAccesibilidad(int numFilasColumnas) {
        LaberintoTodoAccesible laberinto = new LaberintoTodoAccesible(numFilasColumnas);
        StdDraw.show(0);
        laberinto.dibuja();
        if (!laberinto.compruebaAccesibilidad())
        		return false;
        laberinto.resolver();
        return true;
    }
    
    /**
     * Aisla una posicion y ejecuta la busqueda con control de accesibilidad
     * @param numFilasColumnas numero de fila/columnas del laberinto
     * @return true cuando todo es accesible
     */
    public static boolean ejecutaConControlAccesibilidadYAislado(int numFilasColumnas) {
        LaberintoTodoAccesible laberinto = new LaberintoTodoAccesible(numFilasColumnas);
        laberinto.aisla(numFilasColumnas/2,numFilasColumnas/2);
        StdDraw.show(0);
        laberinto.dibuja();
        if (!laberinto.compruebaAccesibilidad())
        		return false;
        laberinto.resolver();
        return true;
    }

    /**
     * metodo main
     * @param args argumentos de main
     */
    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);
        if (LaberintoTodoAccesible.ejecutaConControlAccesibilidad(N))
        	System.out.println("Todas las posiciones son accesibles");
        else System.out.println("Hay posiciones no accesibles");
        /*
        if (LaberintoTodoAccesible.ejecutaConControlAccesibilidadYAislado(N))
        	System.out.println("Todas las posiciones son accesibles");
        else System.out.println("Hay posiciones no accesibles");
        */
    }
}