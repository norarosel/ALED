package es.upm.dit.aled.lab4;

import java.awt.Color;
import java.util.List;
import java.util.NoSuchElementException;

public class LineaMetroCircular extends LineaMetroImpl {

	/**
	 * Construye una linea de metro circular
	 * @param id identificador de la linea
	 * @param color color con el que se representa
	 * @param tramos secuencia de tramos de la linea. 
	 * El destino de un tramo debe ser el origen del siguiente tramo.
	 * En estas lineas la estacion de origen del primer tramo y el destino del ultimo tramo deben 
	 * ser la misma estacion
	 * @param mapa mapa donde se dibuja la linea
	 */
	public LineaMetroCircular(int id, Color color, List<Tramo> tramos, MapaMetro mapa) {
		super(id,color,tramos,mapa);
	}
	
	@Override
	public TrayectoLineaMetro getSecuenciaMovimientos(Estacion origen,Estacion destino,boolean ida) {
		if (destino != tramos.get(0).desde() && destino != tramos.get(tramos.size()-1).desde())
			throw new IllegalArgumentException("Destino de linea de metro no es uno de los dos extremos de la linea "+getId()+": "+destino);
		List<Estacion> estaciones=getEstaciones();
		if (!estaciones.contains(origen))
			throw new IllegalArgumentException("Origen de un trayecto no esta en la linea "+getId()+":"+origen);
		if (!estaciones.contains(destino))
			throw new IllegalArgumentException("Destino de un trayecto no estan en la linea "+getId()+":"+destino);
		return new TrayectoLineaMetroImpl(this,origen,ida);
	}
}
