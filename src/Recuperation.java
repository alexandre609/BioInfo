import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Cette classe contient toutes les op�rations propres � la r�cup�ration de donn�es dans la (les) bases de donn�es.
 * <br>Elles utilisent principalement le service web.
 * @author Alexandre Florentin
 *
 */
public class Recuperation{
	ArrayList<String[]> liste;
	String kingdom;
	
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
		    	resultat += line +"\n";
		    }
		} catch (IOException ex) {
			System.out.println("Erreur URLConnection");
		}
		return resultat;
	}
	
	public Recuperation(){
		this.kingdom = "";
		this.liste = new ArrayList<String[]> ();
	}
	
	public Recuperation(String kingdom){
		this.kingdom = kingdom;
		this.liste = new ArrayList<String[]> ();
	}
	
	
	public void chargerInfos(){
		File fichier = new File("Kingdom/" + kingdom + "/content.csv");
		if(fichier.exists()){
			try{
				BufferedReader br = new BufferedReader(new FileReader(fichier));
		        String espece;
	
		        while ((espece = br.readLine()) != null) {
		            String[] ligne = espece.split(";");
		            liste.add(ligne);
		        }
		        br.close();
			}catch(Exception e){
				System.out.println("Erreur : Recuperation.chargerInfos() : "+ kingdom +" (BufferedReader)");
			}
		}else try {
			fichier.createNewFile();
		}catch(Exception e){
			System.out.println("Erreur : Recuperation.chargerInfos() : "+ kingdom +" (!fichier.exists())");
		}
	}
	
	public void afficherInfos(){
		if(liste.isEmpty())System.out.println("Recuperation.afficherInfos() : " + kingdom + " (liste vide)");
		for(String[] espece : liste){
			for(int i=0 ; i<espece.length ; i++)
				System.out.println("TEST" + espece[i] + " ");
		}
	}
	
	public void enregistrerInfos(){
		File fichier = new File("Kingdom/" + kingdom + "/content.csv");
		
		try{
			if(!fichier.exists())
				fichier.createNewFile();
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(fichier));
			for(String[] espece : liste){
				for(int i=0; i<espece.length ; i++){
					bw.write(espece[i]);
					if(i< (espece.length -1))
						bw.write(';');
				}
				bw.newLine();
			}
			bw.close();
		}catch(Exception e){
			
		}
	}
}
