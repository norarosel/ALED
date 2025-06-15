package es.upm.dit.aled.lab1;

/**
 * Filtro para extraer 
 * @author mmiguel
 *
 */
public class FiltraPeriodo implements FiltroEEG {
	private int max;
	private int min;

	/**
	 * Al aplicar el filtro devolvera un nuevo modelo que incluye el rango [min..max] de muestras que devuelve el metodo getMuestras de ModeloEEG.
	 * Donde min y max son indices del array que devuelve getMuestras. min debe ser menor que max, y los dos son menores que el tamano del array 
	 * que devuelve getMuestras. Ambos indices estran incluido en el modelo generado.
	 * @param min indice menor del rango a filtrar
	 * @param max indice mayor del rango a filtrar
	 */
	public FiltraPeriodo(int min, int max) {
		// TODO
		this.max=max;
		this.min=min;
		
	}
	
	@Override
	public ModeloEEG aplicaFiltro(ModeloEEG eeg) {
		// TODO
		ModeloEEG nuevoEeg = new ModeloEEG();
		for(int t=min; t<=max;t++) {
		Muestra myMuestra=eeg.muestras.get(t);	
		nuevoEeg.muestras.add(myMuestra);
		}
		return nuevoEeg;
	}
}
