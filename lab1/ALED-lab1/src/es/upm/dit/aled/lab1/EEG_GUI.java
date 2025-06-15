package es.upm.dit.aled.lab1;

import java.awt.Color;
import java.awt.Font;
/**
 * Clase de representacion grafica de las muestras que llegan al electroencefalografo
 * @author mmiguel
 *
 */
public class EEG_GUI {

	private Graficos2D[] graficos;
	final private int X,Y;
	private float[] x_actual;
	private float[] y_actual;
	private float[] maxY,minY;
	private int fAnima;
	private float t=0.0f;
	CompositionGraficos2D container;
	private float periodo;
	
	/**
	 * Construye el soporte de representacion grafica, teniendo en cuenta que cada diagrama se escala segun sus valores maximo/minimo
	 * En un grafico se representan 1250/frecuencia muestras. Cuando el grafico se llena, se borra y vuelve a comenzar desde el principio
	 * @param minY valores minimos para escalado de cada sonda
	 * @param maxY valores maximos para escalado de cada sonda
	 * @param nmuestras numero de sondas de cada muestra. Este valor debe coindidir con el tamano de minY,maxY
	 * @param frecuencia frecuencia con la que se generan las muestras, y es la frecuencia con la que llegan las muestras
	 * @param fAnima valor para simular los tiempo de representacion grafica de las muestras
	 */
	public EEG_GUI(float[] minY, float[] maxY, int nmuestras, float frecuencia, int fAnima) {
		this.Y=600/nmuestras;
		this.X=1250;
		graficos=new Graficos2D[nmuestras];
		y_actual=new float[nmuestras];
		x_actual=new float[nmuestras];
		for (int i=0; i < nmuestras; i++)
			graficos[i]=new Graficos2D(X,Y);
		this.periodo=X/frecuencia;
		this.maxY=maxY;
		this.minY=minY;
		this.fAnima=fAnima;
		for (int i=0; i < graficos.length; i++) {
	        graficos[i].setFont(new Font(null,0, 12));
	        graficos[i].setXscale(0, X);
	        if (Math.abs(maxY[i]-minY[i]) == 0.0)
	        		if (maxY[i] != 0.0)
	        			graficos[i].setYscale(minY[i], maxY[i]*1.1);
	        		else;
	        else if (maxY[i] != 0.0)
						graficos[i].setYscale(minY[i], maxY[i]);
	        initCanvas(i,0);
	        y_actual[i]=(minY[i]+maxY[i])/2.0F;
	        x_actual[i]=0.0F;
		}
		container=new CompositionGraficos2D(graficos);
        container.show(fAnima);
	}
	
	/**
	 * Devuelve un grafico para cada sonda que se representa graficamente
	 * @return grafico que representan las sondas
	 */
	public Graficos2D[] getGraficos() {
		return graficos;
	}
	
	private void initCanvas(int i, float t) {
        graficos[i].setPenColor(Color.RED);
        graficos[i].line(0,(minY[i]+maxY[i])/2.0F,X,(minY[i]+maxY[i])/2.0F);
        graficos[i].text(-10, (minY[i]+maxY[i])/2.0F, "["+i+"]");
        float y=0.0f;
        if (Math.abs(minY[i]) > 0.1f)
        		y=(2*minY[i]+maxY[i])/3.0F;
        else
        		y=(minY[i]+maxY[i])/4.0F;
        graficos[i].text(-10, y, ""+t);
        graficos[i].setPenColor(Color.BLACK);
	}
	
	/**
	 * Dibuja una muestra, actualizando el grafico de cada sonda
	 * @param m muestra a representar graficamente
	 */
	public void dibujaMuestra(Muestra m) {
		boolean b=false;
		for (int i=0; i < graficos.length && i < m.numeroSondas(); i++) {
			float y=m.sonda(i);
			if (y > maxY[i])
				y=maxY[i];
			if (y < minY[i])
				y=minY[i];
					
			graficos[i].line(x_actual[i], y_actual[i], x_actual[i]=x_actual[i]+periodo, y_actual[i]=y);
			if (x_actual[i] > X) {
				x_actual[i]=0.0f;
				graficos[i].clear();
				if (!b)
					t=t+X/periodo;
				b=true;
				initCanvas(i,t);
			}
		}
		container.show(fAnima);
	}
}
