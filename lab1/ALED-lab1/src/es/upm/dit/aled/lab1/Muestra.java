package es.upm.dit.aled.lab1;

public class Muestra {

	private float[] sondas;
	public Muestra(float[] muestraEEG) {
		sondas=muestraEEG;
	}
	
	public float sonda(int numMuestra) {
		return sondas[numMuestra];
	}
	
	public int numeroSondas() {
		return sondas.length;
	}
}
