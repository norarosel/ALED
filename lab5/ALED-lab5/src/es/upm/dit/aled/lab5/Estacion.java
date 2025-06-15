package es.upm.dit.aled.lab5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Estacion {

	private int id;
	private Vector posicion;
	private String nombre;
	private double tiempo;
	private boolean[] andenOcupado=new boolean[] {false,false};
	private Map<Boolean,List<Tren>> esperando=new HashMap<Boolean,List<Tren>>();

	/**
	 * Crea una nueva estacion en el mapa
	 * @param id identificador de la estacion
	 * @param posicion coordenadas de la estacion en el mapa
	 * @param tiempo tiempo que tardan los trenes en desembarcar/embarcar pasajeros
	 * @param nombre Nombre de la estacion
	 */
	public Estacion(int id, Vector posicion, double tiempo, String nombre) {
		this.id=id;
		this.posicion=posicion;
		this.nombre=nombre;
		this.tiempo=tiempo;
		esperando.put(true, new ArrayList<Tren>());
		esperando.put(false, new ArrayList<Tren>());
	}
	
	/**
	 * Devuelve el identificador de la estacion
	 * @return el identificador
	 */
	public int getId() {
		return id;
	}

	/**
	 * Devuelve las coordenadas de la estacion en el mapa
	 * @return posicion de la estacion
	 */
	public Vector getPosicion() {
		return posicion;
	}

	/**
	 * Devueve el nombre de la estacion
	 * @return nombre de la estacion
	 */
	public String getNombre() {
		return nombre;
	}
	
	/**
	 * Devuelve el tiempo que estan los trenes en esta estacion
	 * @return tiempo de embarque/desembarque
	 */
	public double getTiempo() {
		return tiempo;
	}
	
	@Override
	public boolean equals(Object otra) {
		if (!(otra instanceof Estacion))
			return false;
		else
		return id == ((Estacion) otra).id;
	}
	
	public synchronized void entrarEnAnden(Tren tren, boolean ida) {
		if(ida==true) {
			List<Tren> listIda = esperando.get(true);
			listIda.add(tren);//anado tren a la lista de ida
			esperando.put(true,listIda);
			//while(andenOcupado[0]==true || listIda.get(0)!=tren ) {
				try {
					while(andenOcupado[0]==true || listIda.get(0)!=tren ) 
					wait();
					} catch (InterruptedException e) {
					e.printStackTrace();
					}
			listIda.remove(tren);
			esperando.put(true, listIda);
			andenOcupado[0]=true;
		}
		
		if(!ida) {
			List<Tren> listVuelta = esperando.get(false);
			listVuelta.add(tren);
			 esperando.put(false, listVuelta);

			//while(andenOcupado[1]==true || listVuelta.get(0)!=tren) {
			try {
				while(andenOcupado[1]==true || listVuelta.get(0)!=tren)
			      wait();
			} catch (InterruptedException e) {
			e.printStackTrace();
			}
			listVuelta.remove(tren);
			 esperando.put(false, listVuelta);
			 andenOcupado[1]=true;
	         }
	     }
	public synchronized void salirDelAnden(Tren tren, boolean ida) {
		if(ida==true) //o if(ida)
			andenOcupado[0]=false;
		if(ida==false) 
			andenOcupado[1]=false;
		notifyAll();
	}
	
    public String toString() {
			return nombre;
		}
	
	@Override
	public String toString() {
		return nombre;
	}
}
