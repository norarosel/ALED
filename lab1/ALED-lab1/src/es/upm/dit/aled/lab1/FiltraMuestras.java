package es.upm.dit.aled.lab1;

/**
 * Clase que filtra un modelo, extrayendo algunas sondas de cada muestra.
 * Equivale a un nuevo modelo del que solo quedan las sondas seleccionadas
 * @author mmiguel
 *
 */
public class FiltraMuestras implements FiltroEEG {

	private int[] validas;//sondas que quiero coger para el filtro

	/**
	 * Construye el filtro indicando que sondas tienen informacion util
	 * @param sondasValidas sondas de las que se quiere recuperar los datos
	 */
	public FiltraMuestras(int[] sondasValidas) {
		this.validas=sondasValidas;
		//for(int k=0; k<sondasValidas.length; k++) {
			//float sonda;	
		}
		
	@Override
	public ModeloEEG aplicaFiltro(ModeloEEG eeg) {
		// TODO
		ModeloEEG nuevoModelo=new ModeloEEG();
		float[] sondasElegidas; //array de floats
		for(int i=0;i<eeg.muestras.size();i++) {
			sondasElegidas=new float [validas.length]	;
			   Muestra myMuestra=eeg.muestras.get(i);
			
		 for(int j=0;j<validas.length;j++) {
		    sondasElegidas[j]=myMuestra.sonda(j);
				nuevoModelo.muestras.add(new Muestra(sondasElegidas));	
			}
			
		}
		return nuevoModelo;
	}

}
