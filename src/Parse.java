/**
 * Cette classe sert à filtrer le texte de différentes manières.
 * @author Alexandre Florentin
 *
 */
public class Parse {
	private Integer[] emplacement;
	
	public Parse(String motif){
		emplacement = new Integer[10];
		String[] place = motif.split("\t");
		
		for(int i =0 ; i< place.length; i++){
			if(place[i].equalsIgnoreCase("#Organism/Name"))
				setOrganism(i);
			else if(place[i].equalsIgnoreCase("bioproject"))
				setBioProject(i);
			else if(place[i].equalsIgnoreCase("Group"))
				setGroup(i);
			else if(place[i].equalsIgnoreCase("SubGroup"))
				setSubGroup(i);
			else if(place[i].equalsIgnoreCase("replicons") || place[i].equalsIgnoreCase("segmemts") || place[i].equalsIgnoreCase("segments"))
				setReplicons(i);
			else if(place[i].equalsIgnoreCase("Modify Date"))
				setModifDate(i);
		}
	}
	
	//Setteurs
	private void setOrganism(Integer info)		{emplacement[0] = info;}
	private void setBioProject(Integer info)	{emplacement[1] = info;}
	private void setGroup(Integer info)			{emplacement[2] = info;}
	private void setSubGroup(Integer info)		{emplacement[3] = info;}
	private void setReplicons(Integer info)		{emplacement[4] = info;}
	private void setModifDate(Integer info)		{emplacement[5] = info;}
	
	//Getteurs servant à retrouver l'emplacement
	private Integer getOrganism(Integer info)		{return emplacement[0];}
	private Integer getBioProject(Integer info)		{return emplacement[1];}
	private Integer getGroup(Integer info)			{return emplacement[2];}
	private Integer getSubGroup(Integer info)		{return emplacement[3];}
	private Integer getReplicons(Integer info)		{return emplacement[4];}
	private Integer getModifDate(Integer info)		{return emplacement[5];}
	
	public static String nomEspece(String ligne){
		return ligne.substring(0, ligne.indexOf("\t"));
	}
	
	public static int nthOccurrence(String str, char c, int n) {
	    int pos = str.indexOf(c, 0);
	    while (n-- > 0 && pos != -1)
	        pos = str.indexOf(c, pos+1);
	    return pos;
	}
	
	public static String groupeProkaryote(String ligne){
		int indexDebut = nthOccurrence(ligne,'\t',1)+1;
		int indexFin = ligne.indexOf("\t", indexDebut);
		return ligne.substring(indexDebut, indexFin);
	}
	
	public static String subgroupeProkaryote(String ligne){
		int indexDebut = nthOccurrence(ligne,'\t',2)+1;
		int indexFin = ligne.indexOf("\t", indexDebut);
		return ligne.substring(indexDebut, indexFin);
	}
	
	public static String repliconProkaryote(String ligne){
		int indexDebut = nthOccurrence(ligne,'\t',5)+1;
		int indexFin = ligne.indexOf("\t", indexDebut);
		return ligne.substring(indexDebut, indexFin);
	}
	
	public static String groupeEukaryote(String ligne){
		int indexDebut = nthOccurrence(ligne,'\t',1)+1;
		int indexFin = ligne.indexOf("\t", indexDebut);
		return ligne.substring(indexDebut, indexFin);
	}
	
	public static String subgroupeEukaryote(String ligne){
		int indexDebut = nthOccurrence(ligne,'\t',2)+1;
		int indexFin = ligne.indexOf("\t", indexDebut);
		return ligne.substring(indexDebut, indexFin);
	}
	
	public static String repliconEukaryote(String ligne){
		int indexDebut = nthOccurrence(ligne,'\t',6)+1;
		int indexFin = ligne.indexOf("\t", indexDebut);
		return ligne.substring(indexDebut, indexFin);
	}
	
	public static String repliconPlasmid(String ligne){
		int indexDebut = nthOccurrence(ligne,'\t',7)+1;
		int indexFin = ligne.indexOf("\t", indexDebut);
		return ligne.substring(indexDebut, indexFin);
	}
	
	public static String groupeVirus(String ligne){
		int indexDebut = nthOccurrence(ligne,'\t',1)+1;
		int indexFin = ligne.indexOf("\t", indexDebut);
		return ligne.substring(indexDebut, indexFin);
	}
	
	public static String subgroupeVirus(String ligne){
		int indexDebut = nthOccurrence(ligne,'\t',2)+1;
		int indexFin = ligne.indexOf("\t", indexDebut);
		return ligne.substring(indexDebut, indexFin);
	}
	
	public static String repliconVirus(String ligne){
		int indexDebut = nthOccurrence(ligne,'\t',6)+1;
		int indexFin = ligne.indexOf("\t", indexDebut);
		return ligne.substring(indexDebut, indexFin);
	}
}
