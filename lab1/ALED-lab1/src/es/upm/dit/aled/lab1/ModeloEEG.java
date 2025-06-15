package es.upm.dit.aled.lab1;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Modelo de representacion de los datos que recibe un electroencefalografo.
 * Esos datos se representan graficamente
 * @author mmiguel
 *
 */
public class ModeloEEG {

	protected List<Muestra> muestras=new ArrayList<Muestra>();
	protected EEG_GUI gui;
	
	/**
	 * Construye un nuevo modelo y sus muestras a partir de los valores contenidos en un fichero
	 * @param backupFile fichero que continene las muestras
	 */
	public ModeloEEG(String backupFile) {
		try {
			leeFichero(backupFile);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Construye un nuevo modelo a partir de una coleccion de muestras
	 * @param muestras muestras que definen el modelo
	 */
	public ModeloEEG(Muestra[] muestras) {
		//this.muestras = muestras; Esto da error  
		// TODO
		for(Muestra mu: muestras) {
			this.muestras.add(mu);
		}
	}
	

	/**
	 * Construye un modelo sin ninguna muestra
	 */
	public ModeloEEG() {
	}

	/**
	 * Introduce una nueva muestra en la coleccion de muestras. 
	 * Si la representacion grafica esta inicializada, dibuja la muestra.
	 * @param muestra la muestra a incluir
	 */
    public void nuevaMuestra(Muestra muestra) {
		muestras.add(muestra);
		if (gui != null)
			gui.dibujaMuestra(muestra);
    }
    
    /**
     * Salva la collecion de muestras en un fichero, en un formato que puede leer el metodo leeFichero
     * @param fileName camino del fichero donde salvamos la coleccion de muestras
     * @throws IOException esta excepcion salta cuando no se puede escribir en el fichero
     */
    public void escribeFichero(String fileName) throws IOException {
    	
    	// TODO
    	File file=new File(fileName);
		FileOutputStream fos=new FileOutputStream(file);
		PrintStream ps=new PrintStream(fos);
		for(int n=0;n<muestras.size();n++){
			Muestra mu = muestras.get(n);
			n=n++;
			ps.append(String.valueOf(n));//anado indice al final 
    	for(int j=0;j< mu.numeroSondas();j++) {
    		//ps.append(", ");
    		//se anade al final de cada valor coma y espacio
    		float v=mu.sonda(j);
    		ps.append(String.valueOf(v));
    		if(j!= mu.numeroSondas()-1)
    			ps.print(", ");
    		}
    	ps.append("\n");//salto de linea al final de cada fila
    	n= n+1;
    	}
    	ps.close();
    }

    /**
     * Inicializamos la coleccion de muestras a partir del contenio de un fichero
     * El formato del fichero debe ser CVS, la primera columna es un indice en modulo 256 del numero de muestra.
     * Todas las lineas del fichero tienen el mismo numero de columnas
     * @param fileName nombre del fichero para recuperar las muestras
     * @throws IOException esta excepcion salta cuando no se puede leer el fichero
     */
    public void leeFichero(String fileName) throws IOException {
		File f=new File(fileName);
		FileInputStream fis=new FileInputStream(f);
		DataInput fid=new DataInputStream(fis);
    	String line;
    	while ((line=fid.readLine()) != null) {
    		if (line.startsWith("%")) continue;
    		String[] columnas=line.split(",");
    		float[] muestrasD = new float[columnas.length-1];
    		for (int i=1; i < columnas.length; i++)
    			muestrasD[i-1]=Float.parseFloat(columnas[i]);
    		nuevaMuestra(new Muestra(muestrasD));
    	}
	    fis.close();
    }
    
    /**
     * Realiza una representacion grafica de la coleccion de muestras, calculando los valores maximo y minimo
     * para cada sonda, para escalar los valores de esa sonda. Simula un tiempo representacion de 4ms (suponemos
     * que las muestras llegan con una frecuencia de 250Hz)
     */
    public void representaDatos() {
		if (muestras.size() == 0) return;
		int nmuestras=muestras.get(0).numeroSondas();
		float[] minY=new float[nmuestras];
		float[] maxY=new float[nmuestras];
		for (int i=0; i < nmuestras; i++) {
			minY[i]=muestras.get(0).sonda(i);
			maxY[i]=muestras.get(0).sonda(i);
			int j=0;
    		for (Muestra m : muestras) {
    			float y=m.sonda(i);
    			if (y > maxY[i])
    				maxY[i]=y;
    			if (y < minY[i])
    				minY[i]=y;
    			j++;
    		}
		}
		initGrafico(minY,maxY,nmuestras,4);
		for (Muestra m : muestras)
			gui.dibujaMuestra(m);
    }
    
    /**
     * Inicializaliza la representacion grafica de muestras. Para cada sonda recibe los valores maximo y minimo de la sonda
     * para escalar los valores de la sonda
     * @param minY valores minimos para escalar cada sonda
     * @param maxY valores maximos para escalar cada sonda
     * @param nmuestras numero de sondas por muestra. El tamano de minY y maxY coincide con este valor
     * @param fAnima valor para simular el tiempo de representacion de ese valor
     */
    protected void initGrafico(float[] minY,float[] maxY, int nmuestras,int fAnima) {
    	gui=new EEG_GUI(minY,maxY,nmuestras,250.0F,fAnima);
    }
    
    /**
     * Muestras de la coleccion en formato de array
     * @return muestras del modelo en forma de array
     */
    public Muestra[] getMuestras() {
    	Muestra[] am=new Muestra[muestras.size()];
    	return muestras.toArray(am);
    }
    
    private Random random=new Random();
    private final float sine_freq_Hz = 10.0f;
    
    private Muestra sintetizaMuestra(int nchan, float fs_Hz, float scale_fac_uVolts_per_count) {
    	double val_uV;
    	double[] sine_phase_rad=new double[nchan];
    	float[] curDataPacket_values=new float[nchan];
    	for (int ichan=0; ichan < nchan; ichan++) {
    	    val_uV = random.nextGaussian()*Math.sqrt(fs_Hz/2.0f); // ensures that it has amplitude of one unit per sqrt(Hz) of signal bandwidth
    	    if (ichan==0) val_uV*= 10;  //scale one channel higher

    	    if (ichan==1) {
    	        //add sine wave at 10 Hz at 10 uVrms
    	        sine_phase_rad[ichan] += 2.0f*Math.PI * sine_freq_Hz / fs_Hz;
    	        if (sine_phase_rad[ichan] > 2.0f*Math.PI) sine_phase_rad[ichan] -= 2.0f*Math.PI;
    	        val_uV += 10.0f * Math.sqrt(2.0)*Math.sin(sine_phase_rad[ichan]);
    	    } else if (ichan==2) {
    	        //50 Hz interference at 50 uVrms
    	        sine_phase_rad[ichan] += 2.0f*Math.PI * 50.0f / fs_Hz;  //60 Hz
    	        if (sine_phase_rad[ichan] > 2.0f*Math.PI) sine_phase_rad[ichan] -= 2.0f*Math.PI;
    	        val_uV += 50.0f * Math.sqrt(2.0)*Math.sin(sine_phase_rad[ichan]);    //20 uVrms
    	    } else if (ichan==3) {
    	        //60 Hz interference at 50 uVrms
    	        sine_phase_rad[ichan] += 2.0f*Math.PI * 60.0f / fs_Hz;  //50 Hz
    	        if (sine_phase_rad[ichan] > 2.0f*Math.PI) sine_phase_rad[ichan] -= 2.0f*Math.PI;
    	        val_uV += 50.0f * Math.sqrt(2.0)*Math.sin(sine_phase_rad[ichan]);  //20 uVrms
    	    }
    	    curDataPacket_values[ichan] = (int) (0.5f+ val_uV / scale_fac_uVolts_per_count); //convert to counts, the 0.5 is to ensure rounding
    	}
    	return new Muestra(curDataPacket_values);
    }

    /**
     * Sintentetiza muestras que se introducen en el modelo y se representan graficamente.
     * Simula una sintesis como si se generasen con una frecuencia de 250Hz, con 4 sondas por muestra
     * @param nmuestras numero de muestras a sintetizar
     */
    public void sintetizaDatos(int nmuestras) {
    	float[] min={-200.0f,-200.0f,-200.0f,-200.0f};
    	float[] max={200.0f,200.0f,200.0f,200.0f};
    	initGrafico(min,max,4,0);
    	for (int i=0; i < nmuestras; i++) {
    		long startTime=System.currentTimeMillis();
    		Muestra m = sintetizaMuestra(4,250,1.0f);
    		nuevaMuestra(m);
    		try {
    			long diff=1000/250-(System.currentTimeMillis()-startTime);
    			Thread.sleep(diff > 0 ? diff : 0);
    		} catch (Exception e) {}
    	}
    }
    
    /**
     * Aplica un filtro al modelo, devolviendo un nuevo modelo construido a partir del filtro
     * @param filtro que extrae muestras del modelo para crea el nuevo modelo
     * @return nuevo filtro construido
     */
    public ModeloEEG filtraModelo(FiltroEEG filtro) {
    	// TODO
		return filtro.aplicaFiltro(this);
    }
    
    public static void main(String[] args) {
    	if (args.length > 0) {
	    	ModeloEEG eeg = new ModeloEEG(args[0]);
	    	eeg.representaDatos();
	    } else {
	    	ModeloEEG eeg = new ModeloEEG();
	    	eeg.sintetizaDatos(9600);
    	}
    }
}
