
/**
 * Cette classe sert � filtrer le texte de diff�rentes mani�res.
 * @author Alexandre Florentin
 *
 */
public class Parse {
	public static String nomEspece(String ligne){
		return ligne.substring(0, ligne.indexOf("\t"));
	}
}
