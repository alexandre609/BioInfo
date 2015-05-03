import java.util.ArrayList;

public class Espece {
	private String organism;
	private String kingdom;
	private String group;
	private String subgroup;
	private String bioproject;
	private ArrayList<String> replicons;
	private String releaseDate;
	private String modifyDate;
	
	public Espece(String nom, String royaume, String groupe, String subgroupe, String bioproject){
		this.organism=nom.replaceAll("/", "");
		this.kingdom = royaume.replaceAll("/", "");
		this.group=groupe.replaceAll("/", "");
		this.subgroup=subgroupe.replaceAll("/", "");
		this.bioproject = bioproject.replaceAll("/", "");
	}
	
	public Espece(String nom, String royaume, String groupe, String subgroupe, String replicons, String bioproject, String releaseDate, String modifyDate){
		this.organism=nom.replaceAll("/", "");
		this.kingdom = royaume.replaceAll("/", "");
		this.group=groupe.replaceAll("/", "");
		this.subgroup=subgroupe.replaceAll("/", "");
		this.releaseDate = releaseDate;
		this.modifyDate=modifyDate;
		this.replicons=new ArrayList<String>();
		this.bioproject = bioproject.replaceAll("/", "");
		
		for(String typeRep : replicons.split(";")){
			for(String replicon :typeRep.substring(typeRep.indexOf(':')+1).split("/"))
				this.replicons.add(replicon);
			
		}
	}
	public void setModifyDate(String nouv){this.modifyDate=nouv;}
	
	public String getBioproject(){return bioproject;}
	public String getOrganism(){return organism;}
	public String getKingdom(){return kingdom;}
	public String getGroup(){return group;}
	public String getSubGroup(){return subgroup;}
	public String getReleaseDate(){return releaseDate;}
	public String getModifyDate(){return modifyDate;}
	public ArrayList<String> getReplicons(){return replicons;}
	
	public String afficherReplicons(){
		String a = "";
		for(String s:replicons)a+=", "+s;
		return a;
	}
}