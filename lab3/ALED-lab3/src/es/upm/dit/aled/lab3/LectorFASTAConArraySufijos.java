package es.upm.dit.aled.lab3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class LectorFASTAConArraySufijos extends LectorFASTA {
    protected int[] sufijos;
    
    private int compara(int index1, int index2) {
    	int len=Math.min(bytesValidos-index1, bytesValidos-index2);
    	for (int i=0; i < len; i++) {
    		if (contenido[index1+i] < contenido[index2+i]) return -1;
    		if (contenido[index1+i] > contenido[index2+i]) return 1;
    	}
    	return index2 - index1;
    }
    
    public void ordena() {
    	class Sufijo implements Comparable<Sufijo> {
    		public int sufijo;
    		public Sufijo(int value) {
    			sufijo=value;
    		}
			@Override
			public int compareTo(Sufijo o) {
				return compara(sufijo,o.sufijo);
			}
    	}
    	Sufijo[] suf=new Sufijo[sufijos.length];
    	for (int i=0; i < sufijos.length; i++)
    		suf[i]=new Sufijo(sufijos[i]);
    	Arrays.sort(suf);
    	for (int i=0; i < sufijos.length; i++)
    		sufijos[i]=suf[i].sufijo;
    }
    
    public LectorFASTAConArraySufijos(byte[] text, int bytesValidos) {
        super(text,bytesValidos);
        
    	int n=0;
    	for (int i=0; i < bytesValidos; i++) 
    		if ((char) contenido[i] == 'N')
    			n++;
        this.sufijos = new int[bytesValidos-n];
        int j=0;
        for (int i = 0; i < bytesValidos; i++)
        	if ((char) contenido[i] != 'N')
        		sufijos[j++] = i;

        ordena();
    }

    // length of string
    public int length() { return sufijos.length; }

    // indice del contenido al que apunta la posicion i de la tabla de sufijos
    public int index(int i) { return sufijos[i]; }


    // tamano maximo de de prefijo comun de sufijos(i) y sufijos(i-1)
    public int lcp(int i) {
        return lcp(sufijos[i], sufijos[i-1]);
    }

    // maximo comun prefijo de s y t
    private int lcp(int s, int t) {
        int N = Math.min(bytesValidos-s, bytesValidos-t);
        for (int i = 0; i < N; i++) {
            if (contenido[s+i] != contenido[t+i]) return i;
        }
        return N;
    }

    // compara buscado y el string que referencia sufijo
    private int compara(String query, int sufijo) {
    	int indiceSufijo=sufijos[sufijo];
        int N = Math.min(query.length(), bytesValidos-indiceSufijo);
        for (int i = 0; i < N; i++) {
            if (query.charAt(i) < contenido[indiceSufijo+i]) return -1;
            if (query.charAt(i) > contenido[indiceSufijo+i]) return +1;
        }
        return query.length() - bytesValidos-indiceSufijo;
    }
    
	public void imprime() {
        System.out.println("  i ind lcp select");
        System.out.println("---------------------------");

        for (int i = 0; i < sufijos.length; i++) {
            int index = index(i);
            String ith = "\"" + new String(contenido,index, Math.min(50, bytesValidos-index)) + "\"";
            if (i == 0) {
            	System.out.printf("%3d %3d %3s %s\n", i, index, "-", ith);
            }
            else {
                int lcp  = lcp(i);
                System.out.printf("%3d %3d %3d  %s\n", i, index, lcp, ith);
            }
        }
	}
	
    // crea una array de suficjos a partir del nombre fichero que lo continene. 
    public LectorFASTAConArraySufijos(String ficheroCromosoma) {
    	super(ficheroCromosoma);
    	int n=0;
    	for (int i=0; i < bytesValidos; i++) 
    		if ((char) contenido[i] == 'N')
    			n++;
        this.sufijos = new int[bytesValidos-n];
        int j=0;
        for (int i = 0; i < bytesValidos; i++)
        	if ((char) contenido[i] != 'N')
        		sufijos[j++] = i;
        ordena();
    }
    
    @Override
    public List<Integer> buscar(byte[] patron) {
    	// TODO
    	return null;
    }
    
    @Override
    public List<Integer> buscarSNV(byte[] patron) {
	    // TODO
    	return null;
    }
    
    public static void main(String[] args) {
    	byte[] patron=null;
    	if (args.length > 1)
    		patron=args[1].getBytes();
    	LectorFASTA cr = new LectorFASTAConArraySufijos(args[0]);
    	// cr.imprime();
    	if (patron != null) {
	    	List<Integer> posiciones=cr.buscar(patron);
	    	if (posiciones.size() > 0)
		    	for (Integer pos : posiciones)
		    		System.out.println("Encontrado "+args[1]+" en "+pos+" : "+new String(cr.contenido, pos, args[1].length()));
	    	else System.out.println("No he encontrado : "+args[1]+" en ningun sitio");
    	}
    }
}
