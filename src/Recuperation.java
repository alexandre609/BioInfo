import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


/**
 * Cette classe contient toutes les opérations propres à la récupération de données dans la (les) bases de données.
 * <br>Elles utilisent principalement le service web.
 * @author Alexandre Florentin
 */
public class Recuperation{
	/**
	 * Stocke la liste des espèces récupérée localement
	 */
	ArrayList<String[]> liste;
	/**
	 * Stocke la liste des espèces récupérée en ligne
	 */
	ArrayList<String[]> recupere;
	ArrayList<Espece> especes;
	Parse parseur;
	String kingdom;
	public static int avancement;
	
	public String getKingdom(){return kingdom;}
	public ArrayList<Espece> getEspeces(){return especes;}
	
	/**
	 * Récupère les données avec FastaCDS.
	 * @param sauvegarder Booléen précisant si l'on veut également enregistrer les séquences traîtées
	 */
	public void recupFastaLocal(){
		final boolean save = Main.sauvegarder();
		double taille = especes.size();
		double actu = 1.0;
		int tail = especes.size();
		int act = 1;
		Double av=0.0;
		Main.progress.setStringPainted(true);
		for(Espece espece : especes){
			av = (actu/taille)*100.0;
			Main.progressText(kingdom + " : " + act+"/"+tail);
			Main.progress(av.intValue());
			
			File log = new File("Kingdom/" + espece.getKingdom() + "/"+ espece.getGroup() +"/" + espece.getSubGroup() + "/" + espece.getOrganism());
			boolean existe = false;
			if(log.listFiles() != null){
				for(File f : log.listFiles()){
					if(f.getName().equals(espece.getBioproject()+".xls"));
						existe = true;
				}
			}
			
			if(!existe){
				Statistiques stats = new Statistiques(espece);
				for(String id:espece.getReplicons()){
					recupFasta(id, espece,save,stats);
				}
				stats.sortieExcel();
			}
			actu++;
			act++;
		}
	}

	/**
	 * Récupère la liste du réplicon désigné par la méthode FASTA_CDS.
	 * </br>Si on précise sauvegarder = false, alors on ne fait que les statistiques, si on précise sauvegarder = true, on sauvegarde en plus le fichier récupéré.
	 * @param id	Identifiant du réplicon à chercher
	 * @param sauvegarder Précise si on veut sauvegarder la séquence sur le disque dur.
	 * @return
	 */
	private static void recupFasta(String id, Espece espece, boolean sauvegarder, Statistiques stats){
		BufferedReader reader = null;
		BufferedWriter bw = null;
		File fichier=null;
		if(sauvegarder){
			fichier = new File("Kingdom/"+ espece.getKingdom() +"/" + espece.getGroup() +"/" + espece.getSubGroup() + "/" + espece.getOrganism()+"/Sequences/");
			if(!fichier.exists())
				fichier.mkdir();
			fichier = new File("Kingdom/"+ espece.getKingdom() +"/" + espece.getGroup() +"/" + espece.getSubGroup() + "/" + espece.getOrganism()+"/Sequences/"+id+".txt");
		}
		
		try{
			URL url = new URL("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=" + id + "&rettype=fasta_cds_na&retmode=text");
			URLConnection urlConnection = url.openConnection();
			urlConnection.connect();
			
			String sequence = "";
			String ligne;
			
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            if(sauvegarder && fichier!=null)
            	bw = new BufferedWriter(new FileWriter(fichier,false));
            
            boolean continuer = true;
		    while ((ligne = reader.readLine()) != null) {
		    	//Dans tous les cas, si on veut sauvegarder, on sauvegarde direct
		    	if(sauvegarder){
			    	bw.newLine();
			    	bw.write(ligne);
		    	}
		    	//Ensuite on fait les traitements à la volée
		    	if(ligne.startsWith(">")){
		    		//On vérifie le header
		    		if(verifierHeader(ligne)){
		    			//S'il est bon, on prépare la suite
						if(sequence.equals("")){
							//Toute première ligne
						}else{
							//Nouveau header, donc on traite la séquence trouvée.
							if(continuer)//Si le header est bon
								stats.analyser(sequence);
							sequence = "";
							continuer = true;
						}
						stats.setHeader(ligne);
		    		}else{
		    			//S'il est mauvais, on arrête tout
		    			stats.erreur();
		    			continuer = false;
		    		}
				}else if(ligne.equals("")){
					//EOF, donc on traite la séquence trouvée
					if(continuer)//Si elle est bonne
						stats.analyser(sequence);
					continuer = true;
				}else{
					//On concatène, si c'est juste une nouvelle ligne
					sequence += ligne;
				}
		    }
		    if(sauvegarder)
		    	bw.close();
		}catch(IOException ex){
			System.out.println("Erreur URLConnection");
		}
	}
	
	public static boolean verifierHeader(String header){
		
		return true;
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
			            		ligne[parseur.getBioProject()],
			            		ligne[parseur.getReleaseDate()],
			            		ligne[parseur.getModifDate()]));
			        }
		        }
		        br.close();
			}catch(IOException e){
				System.out.println("Erreur : Recuperation.chargerInfos() : "+ kingdom +" (BufferedReader)");
			}
		}else try {
			initialiserInfos();
			Main.refreshArborescence();
			fichier.createNewFile();
		}catch(Exception e){
			System.out.println("Erreur : Recuperation.chargerInfos() : "+ kingdom +" (!fichier.exists())");
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public int verifierInfos(){
		int nombreModifs = 0;
		this.recupere = new ArrayList<String[]> ();
		try{
			URL url =null;
			if(kingdom.equals("Prokaryotes"))
				url = new URL("http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=download&orgn=&report=proks&status=50|40|30|20|&group=--%20All%20Prokaryotes%20--&subgroup=--%20All%20Prokaryotes%20--");
			else if(kingdom.equals("Eukaryotes"))
				url = new URL("http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=download&orgn=&report=euks&status=50|40|30|20|&group=--%20All%20Eukaryota%20--&subgroup=--%20All%20Eukaryota%20--");
			else if(kingdom.equals("Virus"))
				url = new URL("http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=download&orgn=&report=viruses&status=50|40|30|20|&host=All&group=--%20All%20Viruses%20--&subgroup=--%20All%20Viruses%20--");
			else return -1;
			
			URLConnection urlConnection = url.openConnection();
			urlConnection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			
			//Première ligne = titre des colonnes
			String line = reader.readLine();
			recupere.add(line.split("\t"));
			Parse parseur = new Parse(line);
			
			Main.progressText("Vérification de : "+kingdom);
			Main.progress(true);
			
			boolean modifTrouvee = false;
	    	while ((line = reader.readLine()) != null){
	    		avancement = (avancement + 1)%100;
	    		Main.progress(avancement);
	    		String[] l = line.split("\t");
	    		boolean trouve=false;
	    		if(!l[parseur.getReplicons()].equals("-")){
	    			recupere.add(l);
	    			String creaNouv = parseur.releaseDate(line);
	    			String dateNouv = parseur.modifDate(line);
	    			String especeNouv = parseur.espece(line);
	    			String bioProjNouv = parseur.bioproject(line);
	    			//On cherche l'espèce correspondant
	    			for(Espece espece:especes){
	    				if(especeNouv.equals(espece.getOrganism()) && bioProjNouv.equals(espece.getBioproject())&& creaNouv.equals(espece.getReleaseDate())){
	    					if(!dateNouv.equals(espece.getModifyDate())){
	    						recupere.get(recupere.size()-1)[parseur.getModifDate()] = dateNouv;
	    						System.out.println(espece.getOrganism()+": On remplace : "+espece.getModifyDate()+" par "+dateNouv);
	    						espece.setModifyDate(dateNouv);
	    						File log = new File("Kingdom/" + espece.getKingdom() + "/"+ espece.getGroup() +"/" + espece.getSubGroup() + "/" + espece.getOrganism());
	    						if(log.listFiles() != null){
		    						for(File f: log.listFiles())
		    							if(f.getName().endsWith(".xls"))
		    								f.delete();
	    						}
	    						modifTrouvee = true;
	    						nombreModifs++;
	    					}
	    					trouve = true;
	    					break;
	    				}
	    			}
	    			//Si on l'a pas trouvée, on la crée
	    			if(!trouve){
	    				System.out.println("Nouvelle espèce : "+parseur.espece(line));
	    				nombreModifs++;
	    				modifTrouvee = true;
		    			File dir = new File("Kingdom/"+ kingdom +"/" + parseur.groupe(line));
		    			if(!dir.exists())
		    				dir.mkdir();
		    			dir = new File("Kingdom/"+ kingdom +"/" + parseur.groupe(line) +"/" + parseur.subgroupe(line));
		    			if(!dir.exists())
		    				dir.mkdir();
		    			dir = new File("Kingdom/"+ kingdom +"/" + parseur.groupe(line) +"/" + parseur.subgroupe(line) + "/" + parseur.espece(line));
		    			dir.mkdir();
	    			}
	    		}
	    	}
	    	if(modifTrouvee){
	    		enregistrerInfos();
	    		chargerInfos();
	    	}
			return nombreModifs;
		}catch(Exception e){
			System.err.println("Erreur");
			return 0;
		}
	}
	
	/**
	 * Crée l'arborescence avec la liste des espèce existantes
	 * @return
	 */
	public boolean initialiserInfos(){
		this.recupere = new ArrayList<String[]> ();
		File dir = new File("Kingdom/");
		if(!dir.exists())
			dir.mkdir();
		dir = new File("Kingdom/" + kingdom);
		if(!dir.exists())
			dir.mkdir();
		
		
		Main.progress(1);
		BufferedReader reader = null;
		
		int avancement = 0;
		int avMax;
		
		try{
			Main.progressText("Connexion pour : "+kingdom +"...");
			Main.progress.setStringPainted(false);
			Main.progress(false);
			URL url =null;
			if(kingdom.equals("Prokaryotes")){
				avMax = 33756;
				url = new URL("http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=download&orgn=&report=proks&status=50|40|30|20|&group=--%20All%20Prokaryotes%20--&subgroup=--%20All%20Prokaryotes%20--");
			}else if(kingdom.equals("Eukaryotes")){
				avMax = 2212;
				url = new URL("http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=download&orgn=&report=euks&status=50|40|30|20|&group=--%20All%20Eukaryota%20--&subgroup=--%20All%20Eukaryota%20--");
			}else if(kingdom.equals("Virus")){
				avMax = 4669;
				url = new URL("http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=download&orgn=&report=viruses&status=50|40|30|20|&host=All&group=--%20All%20Viruses%20--&subgroup=--%20All%20Viruses%20--");
			}else return false;
			
			URLConnection urlConnection = url.openConnection();
			urlConnection.connect();
			reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			
			//Première ligne = titre des colonnes
			String line = reader.readLine();
			recupere.add(line.split("\t"));
			Parse parseur = new Parse(line);
			
			Main.progressText("Chargement de : "+kingdom);
			Main.progress(true);
			Main.progress.setStringPainted(true);
	    	while ((line = reader.readLine()) != null){
	    		avancement++;
	    		int av = (avancement *100/avMax);
	    		if(av > 100)av=100;
	    		Main.progress(av);
	    		
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
	    		}
	    	}
	    	Main.progress(99);
	    	enregistrerInfos();
	    	Main.progress(100);
	    	Main.progressText("Chargement de l'arborescence terminée");
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
			else{
				fichier.delete();
				fichier.createNewFile();
			}
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