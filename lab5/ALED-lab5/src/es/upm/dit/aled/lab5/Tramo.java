package es.upm.dit.aled.lab5;

/**
 * Este es un tramo entre dos estaciones del mapa
 * 
 * @author mmiguel
 *
 */
public class Tramo {
	private Estacion desde;
	private Estacion hasta;
	private double tiempo;
	private LineaMetro linea;
	
	/**
	 * Este es un constructor de tramo, desde una estacion hasta otra estacion  
	 * @param desde estacion de origen en sentido ida
	 * @param hasta estacion destino en sentido ida
	 * @param tiempo tiempo que un tren tarda en recorrer el tramo
	 * @param linea linea en la que se encuentra el tramo
	 */
	public Tramo(Estacion desde, Estacion hasta, double tiempo, LineaMetro linea) {
		if (desde == null || hasta == null || Double.isNaN(tiempo) || tiempo < 0)
			throw new IllegalArgumentException();
		this.desde=desde;
		this.hasta=hasta;
		this.tiempo=tiempo;
		this.linea=linea;
	}
	
	/**
	 * Devuelve el origen del tramo en sentido ida
	 * @return Origen del tramo
	 */
	public Estacion desde() {
		return desde;
	}
	
	/**
	 * Devuelve el destino del tramo en sentido ida
	 * @return Destino del tramo
	 */
	public Estacion hasta() {
		return hasta;
	}
	
	/**
	 * Devuelve cuanto tiempo tarda un tren en recorrer este tramo
	 * @return longitud del tramo
	 */
	public double tiempo() {
		return tiempo;
	}
	
	/**
	 * Linea de metro en la que se encuentra el tramo
	 * @return linea de metro
	 */
	public LineaMetro getLineaMetro() {
		return linea;
	}
	
	/**
	 * Actualiza la linea de metro del tramo
	 * @param linea linea de metro
	 */
	public void setLineaMetro(LineaMetro linea) {
		this.linea=linea;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tramo))
			return false;
		Tramo other=(Tramo) o;
		return this.desde().equals(other.desde()) && this.hasta().equals(other.hasta()) && this.tiempo == other.tiempo;
	}
	
	@Override
	public String toString() {
		return "{"+desde.toString()+"-"+tiempo+"->"+hasta.toString()+"}";
	}
}
