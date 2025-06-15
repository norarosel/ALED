package es.upm.dit.aled.lab5;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class LineaMetroImpl implements LineaMetro {

	protected List<Tramo> tramos;
	private Color color;
	private int id;
	private MapaMetro mapa;

	/**
	 * Construye una linea de metro convencional
	 * @param id identificador de la linea
	 * @param color color con el que se dibuja la linea
	 * @param tramos secuencia de tramos que recorre la linea. 
	 * El destino de un tramo debe ser el origen del siguiente tramo
	 * @param mapa mapa donde se dibuja la linea
	 */
	public LineaMetroImpl(int id, Color color, List<Tramo> tramos, MapaMetro mapa) {
		this.id=id;
		this.color=color;
		this.tramos=tramos;
		this.mapa=mapa;
	}

	@Override
	public TrayectoLineaMetro getSecuenciaMovimientos(Estacion origen,Estacion destino,boolean ida) {
		if (destino != tramos.get(0).desde() && destino != tramos.get(tramos.size()-1).hasta())
			throw new IllegalArgumentException("Destino de linea de metro no existe");
		Estacion primera=null;
		Estacion segunda=null;
		for (Estacion est : getEstaciones()) {
			if (est.equals(origen))
				if (primera == null)
					primera=origen;
				else {
					segunda=origen;
					break;
				}
			if (est.equals(destino))
				if (primera == null)
					primera=destino;
				else {
					segunda=destino;
					break;
				}
		}
		if (primera == null || segunda == null)
			throw new IllegalArgumentException("Origen o destino de un trayecto no estan en la linea");
		if (primera == origen && !ida)
			throw new IllegalArgumentException("Origen y destino de un trayecto no son consistentes con el sentido");
		return new TrayectoLineaMetroImpl(this,origen,ida);
	}

	@Override
	public MapaMetro getMapa() {
		return mapa;
	}

	@Override
	public List<Estacion> getEstaciones() {
		List<Estacion> estaciones=new ArrayList<Estacion>();
		for (Tramo t : tramos) {
			if (!estaciones.contains(t.desde()))
				estaciones.add(t.desde());
			if (!estaciones.contains(t.hasta()))
				estaciones.add(t.hasta());
		}
		return estaciones;
	}

	@Override
	public List<Tramo> getTramos() {
		return tramos;
	}
	
	@Override
	public Color getColor() {
		return color;
	}
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "linea "+id;
	}
}
