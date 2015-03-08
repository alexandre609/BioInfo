
/**
 * Cette classe sert à filtrer le texte de différentes manières.
 * @author Alexandre Florentin
 *
 */
public class Parse {
	public static String nomEspece(String ligne){
		return ligne.substring(0, ligne.indexOf("\t"));
	}
	
	public static int nthOccurrence(String str, char c, int n) {
	    int pos = str.indexOf(c, 0);
	    while (n-- > 0 && pos != -1)
	        pos = str.indexOf(c, pos+1);
	    return pos;
	}
	
	public static String repliconProkaryote(String ligne){
		int indexDebut = nthOccurrence(ligne,'\t',5)+1;
		int indexFin = ligne.indexOf("\t", indexDebut);
		return ligne.substring(indexDebut, indexFin);
	}
	
	public static String repliconEukaryote(String ligne){
		int indexDebut = nthOccurrence(ligne,'\t',6)+1;
		int indexFin = ligne.indexOf("\t", indexDebut);
		return ligne.substring(indexDebut, indexFin);
	}
	
	public static String repliconPlasmid(String ligne){
		int indexDebut = nthOccurrence(ligne,'\t',7)+1;
		int indexFin = ligne.indexOf("\t", indexDebut);
		return ligne.substring(indexDebut, indexFin);
	}
	
	public static String repliconVirus(String ligne){
		int indexDebut = nthOccurrence(ligne,'\t',8)+1;
		int indexFin = ligne.indexOf("\t", indexDebut);
		return ligne.substring(indexDebut, indexFin);
	}
}
