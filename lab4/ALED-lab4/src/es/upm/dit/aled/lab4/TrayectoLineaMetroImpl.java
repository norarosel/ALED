package es.upm.dit.aled.lab4;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class TrayectoLineaMetroImpl implements TrayectoLineaMetro {

	protected ListIterator<Tramo> movimientos;
	protected LineaMetro linea;
	
	/**
	 * Construye un trayecto de tramos a partir de la linea de meto y de estacion de donde 
	 * iniciamos el trayecto y el sentido de ida o vuelta
	 * @param linea linea de metro donde se encuentra el trayecto
	 * @param origen estacion donde empieza el trayecto
	 * @param sentidoIda sentido ida o vuelta, si es ida el final del trayecto sera el ultimo tramo de la linea,
	 * si es vuelta (false) los tramos a recorrer van en sentido inverso y el ultimo tramo es el primero de la linea
	 */
	public TrayectoLineaMetroImpl(LineaMetro linea,Estacion origen,boolean sentidoIda) {
		if (sentidoIda) {
			movimientos=linea.getTramos().listIterator(0);
		} else {
			List<Tramo> vueltas=new ArrayList<Tramo>();
			for (ListIterator<Tramo> iterator=linea.getTramos().listIterator(linea.getTramos().size());
				iterator.hasPrevious();) {
				Tramo t=iterator.previous();
				Tramo vuelta=new Tramo(t.hasta(),t.desde(),t.tiempo(),t.getLineaMetro());
				vueltas.add(vuelta);
			}
			movimientos=vueltas.listIterator(0);
		}
		for (; movimientos.hasNext() && !(movimientos.next()).desde().equals(origen););
		movimientos.previous();
		this.linea=linea;
	}

	@Override
	public boolean finMovimiento() {
		if (movimientos == null)
			throw new NoSuchElementException();
		return !movimientos.hasNext();
	}
	
	@Override
	public Tramo siguienteMovimiento() {
		if (movimientos == null)
			throw new NoSuchElementException();
		return movimientos.next();
	}
}
