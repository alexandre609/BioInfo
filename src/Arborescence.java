import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;


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
	
	public static DefaultMutableTreeNode getSubDirs(File root){
		DefaultMutableTreeNode racine = new DefaultMutableTreeNode(root,true);
		File[] list = root.listFiles();
		
		if ( list != null){
			for (int j = 1 ; j<list.length ; j++){
				DefaultMutableTreeNode file = null;
				if (list[j].isDirectory()){
					file = getSubDirs(list[j]);  
					racine.add(file);
				}
			}
		}
		return racine;
	}
}
