package es.upm.dit.aled.lab5;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import es.upm.dit.aled.lab4.Estacion;
import es.upm.dit.aled.lab4.LineaMetro;
import es.upm.dit.aled.lab4.MapaMetro;
import es.upm.dit.aled.lab4.Tren;


public class Tren {
	private String id;
	private Estacion estacion;
	private LineaMetro linea;
	private Thread hebra;
	
	protected static final Logger LOGGER = Logger.getLogger(Tren.class.getName());
	
	// El tratamiento por defecto de las trazas sale por la consola y el nivel es FINEST
	static {
		 Handler handler = new ConsoleHandler(); 
		 setLogger(handler,Level.FINEST);
	}
	
	/**
	 * Permite fijar como se tratan las trazas que generan las ejecuciones mediante un manejador de logs y
	 * fija el nivel de log a partir del cual se generan trazas
	 * @param manejador tratamiento que se quiere dar a los log
	 * @param nivel nivel de salida de los log
	 */
	public static void setLogger(Handler manejador, Level nivel) {
		 LOGGER.setUseParentHandlers(false); 
		 for (Handler h_actual : LOGGER.getHandlers())
			 LOGGER.removeHandler(h_actual);
		 manejador.setLevel(nivel);
		 LOGGER.addHandler(manejador); 
		 LOGGER.setLevel(nivel);
	}
	
	/**
	 * Devuelve la estacion en la que esta el tren
	 * @return la estacion
	 */
	public Estacion getEstacion() {
		return estacion;
	}
	
	/**
	 * Devuelve la linea en la que se mueve el tren
	 * @return la linea de metro
	 */
	public LineaMetro getLinea() {
		return linea;
	}
	
	/**
	 * Construye un nuevo tren a partir de linea y estación en la que se encuentra y de un identificador de tren 
	 * @param id identificador del tren
	 * @param linea linea de metro por la que se mueve el tren
	 * @param estacion estacion de la linea en la que se encuentra
	 */
	public Tren (String id, LineaMetro linea, Estacion estacion) {
		this.id = id;
		this.estacion=estacion;
		this.linea=linea;
	}
	
	/**
	 * Devuelve el identificador del tren
	 * @return identificador del tren
	 */
	public String getId() {
		return id;
	}

	/**
	 * Actualiza el identificador
	 * @param id valor del nuevo identificador
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	
	@Override
	public String toString() {
		return "Vehiculo [id=" + id + "]";
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Tren))
			return false;
		Tren other = (Tren) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	

	/**
	 * Servicio para dirigir el movimento del tren desde la estacion origen
	 * a la estacion destino en dirección ida o vuelta. Cada linea tiene una primera y ultima estacion,
	 * si va en direccion a la ultima y la linea no es circular la direccion ida debe ser true. Si va en 
	 * direccion a la primera debe ser false. Si la linea es circular el destino debe ser la primera 
	 * (0, Nuevos Ministerios) o la ultima (numero de estaciones de la linea 6 -1, Arguelles). Si la direccion
	 * es ida irá en sentido de las agujas del reloj, y sino en sentido contrario.
	 * @param origen estacion desde la que se produce el movimiento
	 * @param destino El destino del tren, cuando llegue alli se para
	 * @param ida idica si el sentido del movimiento es ida o vuelta
	 * @return Una lista con la secuencia de las estaciones recorridas
	 * @throws IllegalStateException si el tren no se encuentra en una linea de metro
	 * @throws IllegalArgumentException si el destino no es ninguno de los extremos de la linea de metro 
	 * donde esta el tren, o el sentido ida, origen y destino no son consistentes
	 */
	public List<Estacion> irA(Estacion origen,Estacion destino,boolean ida) {
		if (linea == null)
			throw new IllegalStateException();
		List<Estacion> pasos=new ArrayList<Estacion>();
		TrayectoLineaMetro tr=linea.getSecuenciaMovimientos(origen,destino,ida);
		if (tr == null)
			throw new IllegalArgumentException();
		pasos.add(estacion);
		while (!tr.finMovimiento()) {
			Tramo c=tr.siguienteMovimiento();
			Object[] params= {this.id,c};
			LOGGER.log(Level.INFO, this.id+" entra en tramo "+c,params);
			linea.getMapa().mueve(this, c);
			pasos.add(c.hasta());
			Object[] params2= {this.id,c.hasta()};
			LOGGER.log(Level.INFO, this.id+" llega a la estacion "+c.hasta(),params2);
			this.estacion=c.hasta();
			this.estacion.entrarEnAnden(this,ida);
			LOGGER.log(Level.INFO, this.id+" entra en la estacion "+c.hasta(),params2);
			linea.getMapa().entraEnEstacion(this, c);
			linea.getMapa().desembarca(this, c.hasta());
			this.estacion.salirDelAnden(this,ida);
			LOGGER.log(Level.INFO, this.id+" sale de la estacion "+c.hasta(),params2);
		}
		if (!pasos.contains(destino))
			pasos.add(destino);
		return pasos;
	}
	
	/**
	 * Mueve el tren en su línea de metro. Si la linea no es circular, el vehiculo estara iendo y viniendo
	 * desde su posicion actual hasta el destino, y de ahi va al otro extremo de la linea. Empieza partiendo 
	 * de la estacion en la que se encuentra. Hace esto el numero de vueltas indicado, utilizando el metodo 
	 * irA para hacer cada trayecto. Si la línea es circular, ira desde lamposicion en la que se encuentra
	 * hasta el destino, y una vez alli seguira dando vueltas en el mismo sentido (vueltas-1 veces). Si ida es true
	 * el sentido son las agujas del reloj, sino el sentido contrario.
	 * @param destino destino a que vamos y volvemos desde la posicion actual
	 * @param ida el sentido del movimiento del tren (ida o vuelta)
	 * @param vueltas numero de veces que vamos/volvemos. Por ejemplo, un 2 nos dice que vamos y despues volvemos 
	 * al otro extermo de la línea y el método termina
	 */
	public void moverElTren(Estacion destino, boolean ida, int vueltas) {
		// TODO
		Estacion origen;
		if(ida) {
			origen=linea.getEstaciones().get(0);
		}
		else 
			origen=linea.getEstaciones().get(linea.getEstaciones().size()-1);
		Estacion extra;
		for(int i =0;i< vueltas;i++) {
			this.irA(estacion, destino, ida);
			//extra=destino;
			//destino=origen;
			//origen=extra;
			extra=origen;
			origen=destino;
			destino=extra;
			if(linea.getId()!=6)
				ida=!ida;
		}
		
	}
	
	/**
	 * Crea una hebra independiente que representa el movimiento del tren. La hebra mueve el tren según la 
	 * implementación del método moverEltren.
	 * @param destino destino a que vamos y volvemos desde la posicion actual
	 * @param ida el sentido del movimiento del tren (ida o vuelta)
	 * @param vueltas numero de veces que vamos/volvemos. Por ejemplo un 2 nos dice que vamos y despues volvemos 
	 * al otro extremos de la línea y la hebra termina
	 * @return devuelve la hebra creada
	 */
	public Thread arrancaTren(Estacion destino, boolean ida, int vueltas) {
		hebra=new Thread() {
			public void run() {
			Tren.this.moverElTren(destino,ida,vueltas);	//se utiliza un this porque la hebra es un objeto de la clase Tren y moverElTrEN TAMBN
			}
			};//cierra sentencia de creacion de la hebra que es objeto de la clase Tren
			hebra.start();
			return hebra;
	}
	
	/**
	 * Espera hasta que el tren arrancado haya terminado de dar todas las vueltas.
	 * Si ya las hubiese terminado, no hay espera, y sino retornara cuando esten terminadas
	 */
	public void esperaElTrenPare() {
		// TODO
		try {
			hebra.join();//espera a que la hebra muera, puedo hacerlo con el metofo isAlive y decirle que while hebra isAlive wait
			 }
		catch(InterruptedException e) {};
			
	}
	
	public static void main(String[] arg) {
		 // TODO
		String f="mapa_metro.txt";
		MapaMetro g = new MapaMetro("mapa_metro.txt");
		 g.dibuja();
		 LineaMetro linea = g.getLineaMetro(9);
		 List<Estacion> estaciones = linea.getEstaciones();
		 Estacion colombia=estaciones.get(2); // Colombia es la 3a estaci�n de la linea
		 Tren v1=new Tren("id000",linea,colombia);
		 v1.moverElTren(estaciones.get(estaciones.size()-1),true,2); // direccion Estrella
	}
}