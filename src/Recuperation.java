import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Cette classe contient toutes les opérations propres à la récupération de données dans la (les) bases de données.
 * <br>Elles utilisent principalement le service web.
 * @author Alexandre Florentin
 *
 */
public class Recuperation{
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
