package es.upm.dit.aled.lab4;

import java.awt.Color;
import java.util.List;

public interface LineaMetro {
	
	/**
	 * Genera el camino entre el origen y el destino. Se representa como la secuencia de los tramos
	 * del camino.
	 * @param origen origen estacion de origen de la secuencia de movimientos
	 * @param destino destino final de la ruta deseada
	 * @prama ida nos indica si el sentido es de ida o de vuelta
	 * @throws lava.lang.NoSuchElementException si no se puede llegar a ese destino
	 * @throws java.lang.IllegalArgumentException si el desnino no esta en la linea o si no esta en el sentido de ida/vuelta
	 */
	TrayectoLineaMetro getSecuenciaMovimientos(Estacion origen,Estacion destino,boolean ida);
	
	/**
	 * Indica el mapa en el que se representa esta linea de metro
	 * @return el mapa representado con un grafo de estaciones y tramos
	 */
	MapaMetro getMapa();
	
	/**
	 * Devuelve la secuencia de estaciones que incluye la linea
	 * @return secuencia de estaciones
	 */
	List<Estacion> getEstaciones();
	
	/**
	 * Devuelve la secuencia de tramos que recorremos a lo largo de la linea
	 * @return secuencia de tramos
	 */
	List<Tramo> getTramos();
	
	/**
	 * Devuelve el color con el que se dibuja la linea
	 * @return color de la linea
	 */
	Color getColor();
	
	/**
	 * Devuelve el numero de la linea
	 * @return numero de linea
	 */
	int getId();
}

