package es.upm.dit.aled.lab2;

import java.awt.Font;

public class Laberinto {
    protected int N; // Tamano alto y ancho del laberinto
    // norte, sur este y oeste nos dicen cuando hay paredes o no alrededor de una posicion
    protected boolean[][] norte;
    protected boolean[][] este;
    protected boolean[][] sur;
    protected boolean[][] oeste;
    // visitado nos dice cuando hemos pasado por una posicion
    protected boolean[][] visitado;

    // encontrado: hemos llegado al punto central
    protected boolean encontrado = false;
    protected double radio=StdDraw.getPenRadius();

    /**
     * Crea un nuevo laberinto
     * 
     * @param N tamano del laberinto
     */
    public Laberinto(int N) {
        this.N = N;
        StdDraw.setCanvasSize(700,700);
        StdDraw.setXscale(0, N+2);
        StdDraw.setYscale(0, N+2);
        init();
        generar();
    }

    private void init() {
        // todas las posiciones del marco exterior marcadas como visitadas
        visitado = new boolean[N+2][N+2];
        for (int x = 0; x < N+2; x++) visitado[x][0] = visitado[x][N+1] = true;
        for (int y = 0; y < N+2; y++) visitado[0][y] = visitado[N+1][y] = true;


        // todas las paredes levantadas
        norte = new boolean[N+2][N+2];
        este  = new boolean[N+2][N+2];
        sur = new boolean[N+2][N+2];
        oeste  = new boolean[N+2][N+2];
        for (int x = 0; x < N+2; x++)
            for (int y = 0; y < N+2; y++)
                norte[x][y] = este[x][y] = sur[x][y] = oeste[x][y] = true;
    }

    // generar el laberinto
    private void generar(int x, int y) {
        visitado[x][y] = true;

        // mientras tengamos una posicion junto a esta que no este visitada
        while (!visitado[x][y+1] || !visitado[x+1][y] || !visitado[x][y-1] || !visitado[x-1][y]) {

            // elegimos una posicion cualquiera
            while (true) {
                double r = Math.random();
                if (r < 0.25 && !visitado[x][y+1]) {
                    norte[x][y] = sur[x][y+1] = false;
                    generar(x, y + 1);
                    break;
                }
                else if (r >= 0.25 && r < 0.50 && !visitado[x+1][y]) {
                    este[x][y] = oeste[x+1][y] = false;
                    generar(x+1, y);
                    break;
                }
                else if (r >= 0.5 && r < 0.75 && !visitado[x][y-1]) {
                    sur[x][y] = norte[x][y-1] = false;
                    generar(x, y-1);
                    break;
                }
                else if (r >= 0.75 && r < 1.00 && !visitado[x-1][y]) {
                    oeste[x][y] = este[x-1][y] = false;
                    generar(x-1, y);
                    break;
                }
            }
        }
    }

    /**
     * Generamos un laberinto, empezamos a generar por la posicion izquierda y abajo
     */
    protected void generar() {
        generar(1, 1);
    }

    // resolvemos buscado la primera solucion que encontremos
    private int resolver (int x, int y, int pasos){
    /**
     * Resolver el laberinto empezando en la primera posicion: abajo a la izquierda
     * @return numero de pasos dados
     */
    	
    	if (visitado[x][y] == true) 
    		return 0;
    	
    	if (encontrado)
    		return pasos;
    	
    	visitado[x][y] = true;
    	// marcamos la posicion de azul
    	StdDraw.setPenColor(StdDraw.BLUE);
    	StdDraw.filledCircle(x + 0.5, y + 0.5, 0.25);
    	//StdDraw.show(0);
    	
    	if((x==Math.round(N/2.0)&&y==N/2.0)) {
    		encontrado = true;
    		return pasos;
		}
    	
    	int pasosDevueltos = 0;
    	
    	if (norte[x][y]==false) {
    		pasosDevueltos = resolver(x, y + 1, pasos+1);
    				if (encontrado == true)
    					return pasosDevueltos;
    	}
    	
    	if(este[x][y]==false) {
    		pasosDevueltos = resolver(x+1, y, pasos+1);
    				if (encontrado == true)
    					return pasosDevueltos;
    	}
    	
    	if(sur[x][y]==false) {
    		pasosDevueltos = resolver(x, y - 1, pasos+1);
    				if (encontrado == true)
    					return pasosDevueltos;
    	}
    	
    	if(oeste[x][y]==false) {
    		pasosDevueltos = resolver(x - 1, y, pasos+1);
    				if (encontrado == true)
    					return pasosDevueltos;
    	}
    	
    	// marcamos la posicion de gris, si no he llegado a la solucion
    	StdDraw.setPenColor(StdDraw.GRAY);
    	StdDraw.filledCircle(x + 0.5, y + 0.5, 0.25);
    	//StdDraw.show(0);
    	return 0;
    
    }
    
    
    
    public int resolver() {
    	long t=System.currentTimeMillis();
    	for (int x = 1; x <= N; x++)
            for (int y = 1; y <= N; y++)
                visitado[x][y] = false;
        encontrado = false;
        int pasos=resolver(1, 1, 1);
        StdDraw.show(0);
        System.out.println("Tiempo total "+(System.currentTimeMillis()-t));
        return pasos;
    }
    
    /**
     * Dibuja el laberinto
     */
    public void dibuja() {
    	StdDraw.clear();
    	StdDraw.setPenRadius(radio);
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.filledCircle(Math.round(N/2.0) + 0.5, Math.round(N/2.0) + 0.5, 0.375);
        StdDraw.filledCircle(1.5, 1.5, 0.375);

        StdDraw.setPenColor(StdDraw.BLACK);
        Font f = StdDraw.getFont();
        int size=300/N;
        StdDraw.setFont(new Font(null,0, size > 12 ? 16 : size));
        for (int x = 1; x <= N; x++)
        	StdDraw.text(x+0.5, 0, new Integer(x).toString());
        for (int y = 1; y <= N; y++)
        	StdDraw.text(0, y+0.5, new Integer(y).toString());
        StdDraw.setFont(f);
        for (int x = 1; x <= N; x++) {
            for (int y = 1; y <= N; y++) {
                if (sur[x][y]) StdDraw.line(x, y, x + 1, y);
                if (norte[x][y]) StdDraw.line(x, y + 1, x + 1, y + 1);
                if (oeste[x][y])  StdDraw.line(x, y, x, y + 1);
                if (este[x][y])  StdDraw.line(x + 1, y, x + 1, y + 1);
            }
        }
        StdDraw.show(0);
    }

    /**
     * Punto de entrada al programa. Incluye una prueba
     * @param args debe incluir un argumento con el tamano del laberinto
     */
    public static void main(String[] args) {
    	int N;
    	if (args.length == 0)
    		N=50;
    	else
    		N = Integer.parseInt(args[0]);
        Laberinto laberinto = new Laberinto(100);
        StdDraw.show(0);
        laberinto.dibuja();
        System.out.println("Pasos hasta solucion: "+laberinto.resolver());
    }

}

