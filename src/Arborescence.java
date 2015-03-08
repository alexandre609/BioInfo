import java.io.File;


/**
 * Cette classe sert à gérer l'arborescence des dossiers.
 * @author Alexandre Florentin
 *
 */
public class Arborescence {
	public static void truc() {
		File dir = new File("Kingdom/Prokaryotes");
		dir.mkdir();
		dir = new File("Kingdom/Eukaryotes");
		dir.mkdir();
		dir = new File("Kingdom/Virus");
		dir.mkdir();
		dir = new File("Kingdom/Plasmids");
		dir.mkdir();
	}
	
	
}
