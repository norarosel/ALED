package es.upm.dit.aled.lab1;

/**
 * Interfaz que soportan los filtros de muestras de los modelos de EEG
 * @author mmiguel
 *
 */
public interface FiltroEEG {

	/**
	 * Aplica el filtro sobre un modelo de EEG, para crear un nuevo modelo
	 * @param eeg modelo sobre el que aplicar el filtro
	 * @return nuevo modelo creado con el filtro
	 */
	
	public ModeloEEG aplicaFiltro(ModeloEEG eeg);
}
