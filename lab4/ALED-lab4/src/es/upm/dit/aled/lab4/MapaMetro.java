package es.upm.dit.aled.lab4;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

/**
 * Esta clase implementa un mapa que representa plas estaciones del mapa y los tramos de lineas que las conectan
 * @author mmiguel
 *
 */
public class MapaMetro {

    private List<LineaMetro> lineas=new ArrayList<LineaMetro>();
    private Set<Estacion> estaciones=new HashSet<Estacion>();
    private boolean dibujando=false;

	/**
	 * Construye un mapa inicialmente vacio en el que habra que anadir tramos
	 */
	public MapaMetro() {
	}
	
	/**
	 * Construye un mapa leyedo de un fichero
	 * @param f camino del fichero que contiene el mapa
	 */
	public MapaMetro(String f) {
		this();
        if (f == null) throw new IllegalArgumentException("Scanner de entrada null");
		Locale def = Locale.getDefault();
		Locale.setDefault(new Locale("en", "US"));
		Scanner in=null;
        try {
        	in=new Scanner(new FileInputStream(f));
        	leeFichero(in);
        } catch (FileNotFoundException e) {
        	throw new IllegalArgumentException("nombre de fichero erroneo", e);
		} finally {
        	Locale.setDefault(def);
        	if (in != null) in.close();
        }
	}
	
	/**
	 * Construye el mapa a partir de un Scanner. El lenguaje del empleado en la lectura debe ser US
	 * @param in scanner de entrada que describe el mapa
	 */
	public MapaMetro(Scanner in) {
		this();
        if (in == null) throw new IllegalArgumentException("Scanner de entrada null");
        in.useLocale(Locale.ENGLISH);
        leeFichero(in);
	}
	
	private void leeFichero(Scanner in) {
        try {
            while (in.hasNext()) {
            	String nodeId=in.next();
            	if (nodeId.equals("."))
            		break;
            	int id=Integer.parseInt(nodeId);
            	if (!in.next().equals("(")) throw new IllegalArgumentException("formato de entrada erroneo");
                double x = in.nextDouble();
            	double y = in.nextDouble();
            	Vector p=new Vector(x,y);
            	if (!in.next().equals(")")) throw new IllegalArgumentException("formato de entrada erroneo");
            	double tiempo = in.nextDouble();
            	in.skip(" ");
            	String nombre=in.nextLine();
            	Estacion e=new Estacion(id,p,tiempo,nombre);
            	estaciones.add(e);
            }
        	while (in.hasNext()) {
        		int lineaId = in.nextInt();
        		int r=in.nextInt();
        		int g=in.nextInt();
        		int b=in.nextInt();
        		List<Tramo> tramos=new ArrayList<Tramo>();
        		Estacion primera=null;
        		Estacion ultima=null;
        		while (in.hasNext()) {
                	String nodeId=in.next();
                	if (nodeId.equals("."))
                		break;
                    int ids = Integer.parseInt(nodeId);
                	int idd = in.nextInt();
                	double tiempo = in.nextDouble();
                	Estacion origen = getEstacion(ids);
                	Estacion destino = getEstacion(idd);
                	if (primera == null)
                		primera=origen;
                	ultima=destino;
                	if (origen == null || destino == null)
                		throw new IllegalArgumentException("formato de entrada erroneo");
                    Tramo t = new Tramo(origen,destino,tiempo,null);
                    tramos.add(t);
        		}
        		LineaMetro linea;
        		if (primera == ultima)
        			linea=new LineaMetroCircular(lineaId,new Color(r,g,b),tramos,this);
        		else
        			linea=new LineaMetroImpl(lineaId,new Color(r,g,b),tramos,this);
        		for (Tramo t : tramos)
        			t.setLineaMetro(linea);
        		lineas.add(linea);
            }
        }   
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("formato de entrada erroneo", e);
        }
	}

	private Estacion getEstacion(int id) {
		for (Estacion e : estaciones)
			if (e.getId() == id)
				return e;
		return null;
	}
    
    /**
     * Devuelve una linea de metro a partir de su identificador
     * @return linea de metro buscada, o null si no se encuentra
     */
    public LineaMetro getLineaMetro(int id) {
    	for (LineaMetro lm : lineas)
    		if (lm.getId() == id)
    			return lm;
    		
    	return null;
    }
    
    private double scl=1.0;
    private double radio=scl/1.8;
    private static final int PASOS = 10;
    
    /**
     * Este método gestiona la representación gráfica y animación del movimiento
     * de un tren en el mapa
     */
    public synchronized void dibuja() {
    	double max=Double.NEGATIVE_INFINITY;
    	double min=Double.POSITIVE_INFINITY;
    	for (Estacion v : estaciones) {
    		if (max < v.getPosicion().getX())
    			max=v.getPosicion().getX();
    		if (max < v.getPosicion().getY())
    			max=v.getPosicion().getY();
    		if (min > v.getPosicion().getX())
    			min=v.getPosicion().getX();
    		if (min > v.getPosicion().getY())
    			min=v.getPosicion().getY();
    	}
    	
    	if (max - min < 0.0001) {
    		max=max+10;
    		min=min-10;
    	}
    	StdDraw.setCanvasSize(720,720);
    	StdDraw.setScale(min, max);
    	scl=(max-min)*0.03;
    	radio=scl/1.8;
		StdDraw.clear();
		StdDraw.show(0);
		
	    StdDraw.setPenColor(StdDraw.BLACK);
    	StdDraw.setPenRadius(StdDraw.getPenRadius()*5);
	    for (LineaMetro lm : lineas) {
	    	Estacion primera=lm.getEstaciones().get(0);
	    	Estacion destino=lm.getEstaciones().get(lm.getEstaciones().size()-1);
	    	TrayectoLineaMetro tr=lm.getSecuenciaMovimientos(primera,destino,true);
	    	while (!tr.finMovimiento()) {
	    		Tramo c=tr.siguienteMovimiento();
	    		tramo(c.desde().getPosicion().getX(),c.desde().getPosicion().getY(),c.hasta().getPosicion().getX(),c.hasta().getPosicion().getY(),lm.getColor());
	    		if (c.hasta() == primera)
	    			break;
	    	}
	    }
		for (Estacion p : estaciones) {
	        StdDraw.setPenColor(StdDraw.BLACK);
	        StdDraw.circle(p.getPosicion().getX(),p.getPosicion().getY(),radio);
	    	StdDraw.text(p.getPosicion().getX()-radio*2,p.getPosicion().getY()-radio*2,p.getNombre());
	    }
	    StdDraw.show(0);
	    dibujando=true;
	}
    
    private void tramo(double x1, double y1, double x2, double y2,Color color) {
    	StdDraw.setPenColor(color);
        StdDraw.line(x1, y1, x2, y2);
        StdDraw.setPenColor(StdDraw.BLACK);
    }
    
    private Map<Tren,Tramo> dibujadoEn=new HashMap<Tren,Tramo>();
    private Map<Tren,Vector> dibujadoEnPos=new HashMap<Tren,Vector>();
    private Map<Tren,Color> color=new HashMap<Tren,Color>();
    private int ultimo=0;
    private final Color[] ultimoc= {Color.BLACK,Color.GREEN,Color.RED,Color.BLUE,Color.CYAN,Color.DARK_GRAY,Color.GRAY,Color.LIGHT_GRAY,Color.MAGENTA,Color.ORANGE,Color.PINK,Color.YELLOW};
    /**
     * Representa la animacion del movimiento de un tren por un tramo en el mapa
     * @param v tren que se mueve
     * @param c tramo por el que nos movemos
     */
    public void mueve(Tren v, Tramo c) {
    	if (!dibujando)
			try {
				Thread.sleep(Math.round(c.tiempo()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				return;
			}
    	synchronized(this) {
	    	if (dibujadoEnPos.get(v) != null)
	    		borraVehiculo(dibujadoEnPos.get(v));
	    	Tramo cc;
	    	if ((cc=dibujadoEn.get(v)) != null) {
	    		tramo(cc.desde().getPosicion().getX(),cc.desde().getPosicion().getY(),cc.hasta().getPosicion().getX(),cc.hasta().getPosicion().getY(),c.getLineaMetro().getColor());
    			StdDraw.circle(c.desde().getPosicion().getX(),c.desde().getPosicion().getY(),radio);
    			StdDraw.text(c.desde().getPosicion().getX()-radio*2,c.desde().getPosicion().getY()-radio*2,c.desde().getNombre());
       			StdDraw.circle(c.hasta().getPosicion().getX(),c.hasta().getPosicion().getY(),radio);
    			StdDraw.text(c.hasta().getPosicion().getX()-radio*2,c.hasta().getPosicion().getY()-radio*2,c.hasta().getNombre());
	    	}
	    	Vector pos;
	    	if ((pos=dibujadoEnPos.get(v)) != null)
	    	    for (LineaMetro lm : lineas) {
	    	    	Estacion ultima=null;
	    	    	Estacion primera=lm.getEstaciones().get(0);
	    	    	for (Estacion est : lm.getEstaciones()) {
	    	    		if (ultima == null) {
	    	    			ultima=est;
		    	    		if (ultima.getPosicion().equals(pos))
		    	    			synchronized(this) {
		    	    				StdDraw.circle(pos.getX(),pos.getY(),radio);
		    	        			StdDraw.text(ultima.getPosicion().getX()-radio*2,ultima.getPosicion().getY()-radio*2,ultima.getNombre());
		    	    			}
	    	    			continue;
	    	    		}
	    	    		tramo(ultima.getPosicion().getX(),ultima.getPosicion().getY(),est.getPosicion().getX(),est.getPosicion().getY(),lm.getColor());
	    	    		if (est == primera)
	    	    			break;
	    	    		ultima=est;
	    	    		if (ultima.getPosicion().equals(pos))
	    	    			synchronized(this) {
	    	    				StdDraw.circle(pos.getX(),pos.getY(),radio);
	    	    				StdDraw.text(ultima.getPosicion().getX()-radio*2,ultima.getPosicion().getY()-radio*2,ultima.getNombre());
	    	    			}
	    	    	}
	    	    }
	    	if (color.get(v) == null) {
	    		color.put(v, ultimoc[ultimo]);
	    		ultimo=(ultimo+1)%ultimoc.length;
	    	}
    	}
    	for (double i=1; i < PASOS-1; i++) {
    		Vector inter=intermedio(c,i/PASOS);
			synchronized(this) {
	    		dibujaVehiculo(inter,color.get(v));
	    		dibujadoEnPos.put(v, inter);
			}
			try {
				Thread.sleep(Math.round(c.tiempo() / PASOS));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized(this) {
	    		borraVehiculo(inter);
	        	for (Tren unV : dibujadoEnPos.keySet())
	        		if (!unV.equals(v) && dibujadoEnPos.get(unV).equals(inter))
	        			dibujaVehiculo(inter,color.get(unV));
	    		tramo(c.desde().getPosicion().getX(),c.desde().getPosicion().getY(),c.hasta().getPosicion().getX(),c.hasta().getPosicion().getY(),c.getLineaMetro().getColor());
			}
    	}
    		
    	dibujaVehiculo(c.hasta().getPosicion(),color.get(v));
    	synchronized(this) {
    		dibujadoEn.put(v, c);
    		dibujadoEnPos.put(v,c.hasta().getPosicion());
    	}
		try {
			Thread.sleep(Math.round(c.tiempo() / PASOS));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public void desembarca(Tren t, Estacion e) {
    	if (!dibujando)
			try {
				Thread.sleep(Math.round(e.getTiempo()));
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			} finally {
				return;
			}
		synchronized(this) {
	    	if (dibujadoEnPos.get(t) != null)
	    		borraVehiculo(dibujadoEnPos.get(t));
        	Tramo c=dibujadoEn.get(t);
        	if (c != null)
        		tramo(c.desde().getPosicion().getX(),c.desde().getPosicion().getY(),c.hasta().getPosicion().getX(),c.hasta().getPosicion().getY(),c.getLineaMetro().getColor());
		}
    	dibujaVehiculo(e.getPosicion(),color.get(t));
    	synchronized(this) {
    		dibujadoEn.remove(t);
    		dibujadoEnPos.put(t,e.getPosicion());
    	}
		try {
			Thread.sleep(Math.round(e.getTiempo()));
		} catch (InterruptedException ee) {
			ee.printStackTrace();
		}
    }
    
    private static Vector intermedio(Tramo c, double i) {
    	return new Vector(c.desde().getPosicion().getX()+(c.hasta().getPosicion().getX()-c.desde().getPosicion().getX())*i,c.desde().getPosicion().getY()+(c.hasta().getPosicion().getY()-c.desde().getPosicion().getY())*i);
    }
    
    private synchronized void borraVehiculo(Vector v) {
    	StdDraw.setPenColor(StdDraw.WHITE);
    	StdDraw.filledRectangle(v.getX(), v.getY(), 0.7, 0.7);
    	StdDraw.setPenColor(StdDraw.BLACK);
    	StdDraw.show(0);
    }
    
    private synchronized void dibujaVehiculo(Vector v,Color c) {
    	StdDraw.setPenColor(c != null ? c : StdDraw.BLACK);
    	StdDraw.filledRectangle(v.getX(), v.getY(), 0.6, 0.6);
    	StdDraw.setPenColor(StdDraw.BLACK);
    	StdDraw.show(0);
    }
}
