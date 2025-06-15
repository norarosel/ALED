package es.upm.dit.aled.lab1;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

/**
 * Composicion de graficos para representar en una misma ventana los diagramas de las sondas del electroencefalografo
 * @author mmiguel
 *
 */
public class CompositionGraficos2D {

	public static boolean pruebas=false;
    private JFrame frame;
    private Graficos2D[] graficos;

    /**
     * Construimos la ventana como una composicion de diagramas
     * @param graficos los diagramas que representan todas las sondas
     */
    public CompositionGraficos2D(Graficos2D[] graficos) {
		this.graficos=graficos;
		for (Graficos2D grafico : graficos)
			grafico.setContainer(this);
		init();
    }
    
    public static void setPruebas(boolean p) {
    	pruebas=p;
    }
    
    /**
     * Actualiza la representacion grafica de los diagramas con un tiempo de simulacion
     * @param t tiempo de simulacion
     */
    public void show(int t) {
        draw();
        try { 
        	if (!pruebas) Thread.sleep(t); 
        } catch (InterruptedException e) { System.out.println("Error sleeping"); }
    }
    
    private void draw() {
    	for (Graficos2D grafico : graficos)
    		grafico.draw();
    	if (!pruebas)
    		frame.repaint();
    }
    
    private void init() {
    	if (!pruebas) {
	        if (this.frame != null) this.frame.setVisible(false);
	        frame = new JFrame();
	        addComponentsToPane(frame.getContentPane());
	        // frame.setContentPane(draw);
	        frame.setResizable(false);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);            // closes all windows
	        // frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);      // closes only current window
	        frame.setTitle("Standard Draw");
	        frame.setJMenuBar(createMenuBar());
	        frame.pack();
	        frame.requestFocusInWindow();
	        frame.setLocationByPlatform(true);
	        frame.setLocation(0, 0);
	        frame.setVisible(true);
    	}
    }
    
    private void addComponentsToPane(Container pane) {
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		boolean first=true;
		for (Graficos2D grafico : graficos) {
			if (!first)
				pane.add(Box.createRigidArea(new Dimension(0,2)));
			pane.add(grafico.getDraw());
			first=false;
		}
	}
    
    protected static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        return menuBar;
    }
    
    public static void main(String[] args) {
		Graficos2D graficos2D=new Graficos2D();
		Graficos2D graficos2D2=new Graficos2D(512,256);
		Graficos2D[] graficos={graficos2D,graficos2D2};
		CompositionGraficos2D container=new CompositionGraficos2D(graficos);
	    graficos2D.square(.2, .8, .1);
	    graficos2D.filledSquare(.8, .8, .2);
	    graficos2D.circle(.8, .2, .2);
	
	    graficos2D.setPenColor(Graficos2D.BOOK_RED);
	    graficos2D.setPenRadius(.02);
	    graficos2D.arc(.8, .2, .1, 200, 45);
	
	    // draw a blue diamond
	    graficos2D.setPenRadius();
	    graficos2D.setPenColor(Graficos2D.BOOK_BLUE);
	    double[] x = { .1, .2, .3, .2 };
	    double[] y = { .2, .3, .2, .1 };
	    graficos2D.filledPolygon(x, y);
	
	    // text
	    graficos2D.setPenColor(Graficos2D.BLACK);
	    graficos2D.text(0.2, 0.5, "black text");
	    graficos2D.setPenColor(Graficos2D.WHITE);
	    graficos2D.text(0.8, 0.8, "white text");
	    
	    graficos2D2.square(.1, .4, .05);
	    graficos2D2.filledSquare(.4, .4, .1);
	    graficos2D2.circle(.4, .1, .1);
	}
}
