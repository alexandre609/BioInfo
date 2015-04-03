import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Cette classe contient toutes les op�rations propres � la r�cup�ration de donn�es dans la (les) bases de donn�es.
 * <br>Elles utilisent principalement le service web.
 * @author Alexandre Florentin
 *
 */
public class Recuperation{
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
	
	
	public static void test(){
		BufferedReader reader = null;
		try{
			URL url = new URL("http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=download&orgn=&report=proks&status=50|40|30|20|&group=--%20All%20Prokaryotes%20--&subgroup=--%20All%20Prokaryotes%20--");
			URLConnection urlConnection = url.openConnection();
			urlConnection.connect();
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		    
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		    	File dir = new File("Kingdom/Prokaryotes/" + Parse.nomEspece(line));
				dir.mkdir();
		    }
		} catch (IOException ex) {
			System.out.println("Erreur URLConnection");
			//Logger.getLogger(HTTPLoader.class.getName()).log(Level.SEVERE, null, ex);
			//return "";
		}/* finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ex) {
				Logger.getLogger(HTTPLoader.class.getName()).log(Level.SEVERE, null, ex);
			}
		}*/
	}	
}
