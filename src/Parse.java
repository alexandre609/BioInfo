
/**
 * Cette classe sert à filtrer le texte de différentes manières.
 * @author Alexandre Florentin
 *
 */
public class Parse {
	public static String nomEspece(String ligne){
		return ligne.substring(0, ligne.indexOf("\t"));
	}
}
