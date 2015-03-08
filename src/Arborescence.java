import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.tree.DefaultMutableTreeNode;


/**
 * Cette classe sert � g�rer l'arborescence des dossiers.
 * @author Alexandre Florentin
 *
 */
public class Arborescence {
	public static void royaumes() {
		File dir = new File("Kingdom/");
		if(!dir.exists())
			dir.mkdir();
		dir = new File("Kingdom/Prokaryotes");
		if(!dir.exists())
			dir.mkdir();
		dir = new File("Kingdom/Eukaryotes");
		if(!dir.exists())
			dir.mkdir();
		dir = new File("Kingdom/Virus");
		if(!dir.exists())
			dir.mkdir();
		dir = new File("Kingdom/Plasmids");
		if(!dir.exists())
			dir.mkdir();
	}
	
	public static void recupererArborescence(){
		BufferedReader reader = null;
		try{
			URL url = new URL("http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=download&orgn=&report=proks&status=50|40|30|20|&group=--%20All%20Prokaryotes%20--&subgroup=--%20All%20Prokaryotes%20--");
			URLConnection urlConnection = url.openConnection();
			urlConnection.connect();
			reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String line = null;
	    	while ((line = reader.readLine()) != null){
	    		if(!Parse.repliconProkaryote(line).equals("-")){
	    			File dir = new File("Kingdom/Prokaryotes/" + Parse.groupeProkaryote(line));
	    			if(!dir.exists())
	    				dir.mkdir();
	    			dir = new File("Kingdom/Prokaryotes/" + Parse.groupeProkaryote(line) +"/" + Parse.subgroupeProkaryote(line));
	    			if(!dir.exists())
	    				dir.mkdir();
	    			dir = new File("Kingdom/Prokaryotes/" + Parse.groupeProkaryote(line) +"/" + Parse.subgroupeProkaryote(line) + "/" + Parse.nomEspece(line));
	    			dir.mkdir();
	    		}
	    	}
	    	
	    	url = new URL("http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=download&orgn=&report=euks&status=50|40|30|20|&group=--%20All%20Eukaryota%20--&subgroup=--%20All%20Eukaryota%20--");
	    	urlConnection = url.openConnection();
	    	reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    	line = null;
	    	while ((line = reader.readLine()) != null){
	    		if(!Parse.repliconEukaryote(line).equals("-")){
	    			File dir = new File("Kingdom/Eukaryotes/" + Parse.groupeEukaryote(line));
	    			if(!dir.exists())
	    				dir.mkdir();
	    			dir = new File("Kingdom/Eukaryotes/" + Parse.groupeEukaryote(line) +"/" + Parse.subgroupeEukaryote(line));
	    			if(!dir.exists())
	    				dir.mkdir();
	    			dir = new File("Kingdom/Eukaryotes/" + Parse.groupeEukaryote(line) +"/" + Parse.subgroupeEukaryote(line) + "/" + Parse.nomEspece(line));
	    			dir.mkdir();
	    		}
	    	}
	    	/*
	    	url = new URL("http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=download&orgn=&report=plasmids&king=All&group=All&subgroup=All");
	    	urlConnection = url.openConnection();
	    	reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    	line = null;
	    	while ((line = reader.readLine()) != null){
	    		if(!Parse.repliconPlasmid(line).equals("-")){
	    			File dir = new File("Kingdom/Plasmids/" + Parse.nomEspece(line));
	    			dir.mkdir();
	    		}else System.out.println("Plasmids : trouv� un -");
	    	}
	    	
	    	url = new URL("http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=download&orgn=&report=viruses&status=50|40|30|20|&host=All&group=--%20All%20Viruses%20--&subgroup=--%20All%20Viruses%20--");
	    	urlConnection = url.openConnection();
	    	reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    	line = null;
	    	while ((line = reader.readLine()) != null){
	    		if(!Parse.repliconVirus(line).equals("-")){
	    			File dir = new File("Kingdom/Virus/" + Parse.nomEspece(line));
	    			dir.mkdir();
	    		}else System.out.println("Virus : On a trouv� un -");
	    	}
	    	*/
		} catch (IOException ex) {
			System.out.println("Erreur URLConnection");
		}
	}
	
	
	public static DefaultMutableTreeNode getSubDirs(File root){
		DefaultMutableTreeNode racine = new DefaultMutableTreeNode(root,true);
		File[] list = root.listFiles();
		
		if ( list != null){
			for (int j = 0 ; j<list.length ; j++){
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