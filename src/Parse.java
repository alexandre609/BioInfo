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
			else if(place[i].equalsIgnoreCase("Downloaded"))
				setEstTelecharge(i);
		}
	}
	
	//Setteurs
	public void setOrganism(Integer info)		{emplacement[0] = info;}
	public void setBioProject(Integer info)		{emplacement[1] = info;}
	public void setGroup(Integer info)			{emplacement[2] = info;}
	public void setSubGroup(Integer info)		{emplacement[3] = info;}
	public void setReplicons(Integer info)		{emplacement[4] = info;}
	public void setModifDate(Integer info)		{emplacement[5] = info;}
	public void setEstTelecharge(Integer info)	{emplacement[9] = info;}
	
	//Getteurs servant à retrouver l'emplacement
	public Integer getOrganism()		{return emplacement[0];}
	public Integer getBioProject()		{return emplacement[1];}
	public Integer getGroup()			{return emplacement[2];}
	public Integer getSubGroup()		{return emplacement[3];}
	public Integer getReplicons()		{return emplacement[4];}
	public Integer getModifDate()		{return emplacement[5];}
	public Integer getEstTelecharge()	{return emplacement[9];}
	
	public boolean replicons(String line){
		if(line.equals("-"))
			return false;
		else return true;
	}
	
	public String modifDate(String ligne){
		String[] l = ligne.split("\t");
		return l[getModifDate()];
	}
	
	public String groupe(String ligne){
		String[] l = ligne.split("\t");
		return l[getGroup()];
	}
	
	public String subgroupe(String ligne){
		String[] l = ligne.split("\t");
		return l[getSubGroup()];
	}
	
	public String espece(String ligne){
		String[] l = ligne.split("\t");
		return l[getOrganism()];
	}
}
