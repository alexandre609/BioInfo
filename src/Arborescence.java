import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.tree.DefaultMutableTreeNode;


/**
 * Cette classe sert à gérer l'arborescence des dossiers.
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
	}// http://www.ncbi.nlm.nih.gov/sviewer/viewer.cgi?tool=portal&sendto=on&log$=seqview&db=nuccore&dopt=fasta&sort=&val=672789134&from=begin&to=end&maxplex=1
	
	public static void recupererArborescence(){
		BufferedReader reader = null;
		try{
			//LECTURE DES PROKARYOTES
			URL url = new URL("http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=download&orgn=&report=proks&status=50|40|30|20|&group=--%20All%20Prokaryotes%20--&subgroup=--%20All%20Prokaryotes%20--");
			URLConnection urlConnection = url.openConnection();
			urlConnection.connect();
			reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			//Première ligne = titre des colonnes
			String line = reader.readLine(); // Il faudra probablement parser cette ligne pour avoir les numéros de colonne de chaque information
			Parse parseur = new Parse(line);
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
	    			//Chopper les id et les lire avec ça :  http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=JPKY01000541.1&rettype=fasta_cds_na&retmode=text
	    			//										"http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=" + replicons id + "&rettype=fasta_cds_na&retmode=text";
	    		}
	    	}
	    	
	    	// LECTURE DES EUKARYOTES
	    	url = new URL("http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=download&orgn=&report=euks&status=50|40|30|20|&group=--%20All%20Eukaryota%20--&subgroup=--%20All%20Eukaryota%20--");
	    	urlConnection = url.openConnection();
	    	reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    	
	    	line = reader.readLine(); // Il faudra probablement parser cette ligne pour avoir les numéros de colonne de chaque information
	    	
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
	    	
	    	
	    	//LECTURE DES VIRUS
	    	url = new URL("http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=download&orgn=&report=viruses&status=50|40|30|20|&host=All&group=--%20All%20Viruses%20--&subgroup=--%20All%20Viruses%20--");
	    	urlConnection = url.openConnection();
	    	reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    	
	    	line = reader.readLine(); // Il faudra probablement parser cette ligne pour avoir les numéros de colonne de chaque information
	    	
	    	while ((line = reader.readLine()) != null){
	    		if(!Parse.repliconVirus(line).equals("-")){
	    			File dir = new File("Kingdom/Virus/" + Parse.groupeVirus(line));
	    			if(!dir.exists())
	    				dir.mkdir();
	    			dir = new File("Kingdom/Virus/" + Parse.groupeVirus(line) +"/" + Parse.subgroupeVirus(line));
	    			if(!dir.exists())
	    				dir.mkdir();
	    			dir = new File("Kingdom/Virus/" + Parse.groupeVirus(line) +"/" + Parse.subgroupeVirus(line) + "/" + Parse.nomEspece(line));
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
	    		}else System.out.println("Plasmids : trouvé un -");
	    	}
	    	
	    	url = new URL("http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=download&orgn=&report=viruses&status=50|40|30|20|&host=All&group=--%20All%20Viruses%20--&subgroup=--%20All%20Viruses%20--");
	    	urlConnection = url.openConnection();
	    	reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    	line = null;
	    	while ((line = reader.readLine()) != null){
	    		if(!Parse.repliconVirus(line).equals("-")){
	    			File dir = new File("Kingdom/Virus/" + Parse.nomEspece(line));
	    			dir.mkdir();
	    		}else System.out.println("Virus : On a trouvé un -");
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