package es.upm.dit.aled.lab4;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


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
		return "Tren [id=" + id + "]";
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
			LOGGER.log(Level.INFO, this.id+" llega a "+c.hasta(),params2);
			this.estacion=c.hasta();
			linea.getMapa().desembarca(this, c.hasta());
			LOGGER.log(Level.INFO, this.id+" sale de "+c.hasta(),params2);
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
		// TODO
		return null;
	}
	
	/**
	 * Espera hasta que el tren arrancado haya terminado de dar todas las vueltas.
	 * Si ya las hubiese terminado, no hay espera, y sino retornara cuando esten terminadas
	 */
	public void esperaElTrenPare() {
		// TODO
	}
	
	public static void main(String[] arg) {
		 // TODO
	}
	
    public static void main2(String[] arg) {
    	String f="mapa_metro.txt";
		try {
			MapaMetro g = new MapaMetro(f);
			g.dibuja();

			LineaMetro linea = g.getLineaMetro(9);
			Estacion colombia=linea.getEstaciones().get(2);
			Tren v1=new Tren("id000",linea,colombia);
			v1.moverElTren(linea.getEstaciones().get(linea.getEstaciones().size()-1), true, 2);
			
			linea = g.getLineaMetro(6);
			Estacion aa=linea.getEstaciones().get(1);
			Tren v2=new Tren("id001",linea,aa);
			v2.moverElTren(linea.getEstaciones().get(0), true, 1);
			
			Estacion nuevosMinisterios=linea.getEstaciones().get(0);
			Tren v3=new Tren("id002",linea,nuevosMinisterios);
			v3.moverElTren(linea.getEstaciones().get(0), false, 1);

			linea = g.getLineaMetro(10);
			Estacion pc=linea.getEstaciones().get(1);
			Tren v4=new Tren("id003",linea,pc);
			v4.moverElTren(linea.getEstaciones().get(0), false, 1);
			
			linea = g.getLineaMetro(4);
			Estacion  prospe=linea.getEstaciones().get(0);
			Tren v5=new Tren("id004",linea,prospe);
			v5.moverElTren(linea.getEstaciones().get(linea.getEstaciones().size()-1), true, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}