import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


/**
 * Cette classe contient toutes les op�rations propres � la r�cup�ration de donn�es dans la (les) bases de donn�es.
 * <br>Elles utilisent principalement le service web.
 * @author Alexandre Florentin
 */
public class Recuperation{
	ArrayList<String[]> liste;
	ArrayList<String[]> recupere;
	ArrayList<Espece> especes;
	Parse parseur;
	String kingdom;
	
	public String getKingdom(){return kingdom;}
	public ArrayList<Espece> getEspeces(){return especes;}
	
	/**
	 * Lance la r�cup�ration de r�plicons d'apr�s la liste pr�sent�e.
	 * @param liste Liste des r�plicons au format brut, r�cup�r� apr�s parsage de la liste d'Eukaryotes, Prokaryotes ou Virus.
	 */
	public static void truc(String liste){
		String[] listeReplicons = liste.substring(liste.indexOf(':')+1, liste.indexOf(';')).split("/");
		for(String id:listeReplicons){
			recupFasta(id);
		}
	}
	
	public void recupFastaLocal(){
		int taille = especes.size();
		int actu = 1;
		for(Espece espece : especes){
			Main.progressText(kingdom + " : " + actu+"/"+taille);
			Main.progress((actu/taille)*100);
			for(String id:espece.getReplicons()){
				recupFasta(id);
			}
			actu++;
		}
	}

	/**
	 * R�cup�re la liste du r�plicon d�sign� par la m�thode FASTA_CDS
	 * @param id	Identifiant du r�plicon � chercher
	 * @return
	 */
	public static String recupFasta(String id){
		String resultat = "";
		BufferedReader reader = null;
		try{
			URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=" + id + "&rettype=fasta_cds_na&retmode=text");
			
			URLConnection urlConnection = url.openConnection();
			urlConnection.connect();
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		    
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		    	//resultat += line +"\n";
		    	//System.out.println(line);
		    }
		} catch (IOException ex) {
			System.out.println("Erreur URLConnection");
		}
		return resultat;
	}
	
	public Recuperation(){
		this.kingdom = "";
		this.liste = new ArrayList<String[]> ();
		this.recupere = new ArrayList<String[]> ();
		this.especes = new ArrayList<Espece> ();
	}
	
	public Recuperation(String kingdom){
		this.kingdom = kingdom;
		this.liste = new ArrayList<String[]> ();
		this.recupere = new ArrayList<String[]> ();
		this.especes = new ArrayList<Espece> ();
		chargerInfos();
	}
	
	
	private void chargerInfos(){
		File fichier = new File("Kingdom/" + kingdom + "/content.txt");
		especes = new ArrayList<Espece>();
		if(fichier.exists()){
			try{
				BufferedReader br = new BufferedReader(new FileReader(fichier));
		        String espece = br.readLine();
		        if(!espece.isEmpty()){
		        	parseur = new Parse(espece);
		        	while ((espece = br.readLine()) != null) {
			            String[] ligne = espece.split("\t");
			            liste.add(ligne);
			            especes.add(new Espece(
			            		ligne[parseur.getOrganism()],
			            		kingdom,
			            		ligne[parseur.getGroup()],
			            		ligne[parseur.getSubGroup()],
			            		ligne[parseur.getReplicons()],
			            		ligne[parseur.getModifDate()]));
			        }
		        }
		        br.close();
			}catch(Exception e){
				System.out.println("Erreur : Recuperation.chargerInfos() : "+ kingdom +" (BufferedReader)");
			}
		}else try {
			verifierInfos();
			Main.refreshArborescence();
			fichier.createNewFile();
		}catch(Exception e){
			System.out.println("Erreur : Recuperation.chargerInfos() : "+ kingdom +" (!fichier.exists())");
		}
	}
	
	public boolean verifierInfos(){
		File dir = new File("Kingdom/");
		if(!dir.exists())
			dir.mkdir();
		dir = new File("Kingdom/" + kingdom);
		if(!dir.exists())
			dir.mkdir();
		
		Main.progress(1);
		BufferedReader reader = null;
		
		int avancement = 0;
		
		try{
			Main.progressText("Connexion pour : "+kingdom +"...");
			Main.progress(false);
			URL url =null;
			if(kingdom.equals("Prokaryotes"))
				url = new URL("http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=download&orgn=&report=proks&status=50|40|30|20|&group=--%20All%20Prokaryotes%20--&subgroup=--%20All%20Prokaryotes%20--");
			else if(kingdom.equals("Eukaryotes"))
				url = new URL("http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=download&orgn=&report=euks&status=50|40|30|20|&group=--%20All%20Eukaryota%20--&subgroup=--%20All%20Eukaryota%20--");
			else if(kingdom.equals("Virus"))
				url = new URL("http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=download&orgn=&report=viruses&status=50|40|30|20|&host=All&group=--%20All%20Viruses%20--&subgroup=--%20All%20Viruses%20--");
			else return false;
			
			URLConnection urlConnection = url.openConnection();
			urlConnection.connect();
			reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			
			//Premi�re ligne = titre des colonnes
			String line = reader.readLine();
			recupere.add(line.split("\t"));
			Parse parseur = new Parse(line);
			
			Main.progressText("Chargement de : "+kingdom);
			Main.progress(true);
	    	while ((line = reader.readLine()) != null){
	    		avancement = (avancement + 1)%100;
	    		Main.progress(avancement);
	    		String[] l = line.split("\t");
	    		if(!l[parseur.getReplicons()].equals("-")){
	    			recupere.add(l);
	    			
	    			dir = new File("Kingdom/"+ kingdom +"/" + parseur.groupe(line));
	    			if(!dir.exists())
	    				dir.mkdir();
	    			dir = new File("Kingdom/"+ kingdom +"/" + parseur.groupe(line) +"/" + parseur.subgroupe(line));
	    			if(!dir.exists())
	    				dir.mkdir();
	    			dir = new File("Kingdom/"+ kingdom +"/" + parseur.groupe(line) +"/" + parseur.subgroupe(line) + "/" + parseur.espece(line));
	    			dir.mkdir();
	    			dir = new File("Kingdom/"+ kingdom +"/" + parseur.groupe(line) +"/" + parseur.subgroupe(line) + "/" + parseur.espece(line)+"/Genome");
	    			if(!dir.exists())
	    				dir.mkdir();
	    			dir = new File("Kingdom/"+ kingdom +"/" + parseur.groupe(line) +"/" + parseur.subgroupe(line) + "/" + parseur.espece(line)+"/Gene");
	    			if(!dir.exists())
	    				dir.mkdir();
	    			
	    			//Chopper les id et les lire avec �a :  http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=JPKY01000541.1&rettype=fasta_cds_na&retmode=text
	    			//										"http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=" + replicons id + "&rettype=fasta_cds_na&retmode=text";
	    		}
	    	}
	    	Main.progress(99);
	    	enregistrerInfos();
	    	Main.progress(100);
	    	Main.progressText("DONE");
	    	enregistrerInfos();
	    	chargerInfos();
	    	return true;
		}catch(Exception e){
			Main.progress(0);
			return false;
		}
	}
	
	
	public void afficherInfos(){
		if(liste.isEmpty())System.out.println("Recuperation.afficherInfos() : " + kingdom + " (liste vide)");
		for(String[] espece : liste){
			System.out.println(espece[parseur.getOrganism()] + " : "+espece[parseur.getReplicons()]);
			
		}
	}
	
	private void enregistrerInfos(){
		File fichier = new File("Kingdom/" + kingdom + "/content.txt");
		
		try{
			if(!fichier.exists())
				fichier.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(fichier,false));
			for(String[] espece : recupere){
				for(int i=0; i<espece.length ; i++){
					bw.write(espece[i]);
					if(i< (espece.length -1))
						bw.write('\t');
				}
				bw.newLine();
			}
			bw.close();
		}catch(Exception e){
			
		}
	}
}
