import java.util.ArrayList;


public class Espece {
	private String organism;
	private String kingdom;
	private String group;
	private String subgroup;
	private ArrayList<String> replicons;
	private String modifyDate;
	
	public Espece(String nom, String royaume, String groupe, String subgroupe, String replicons, String modifyDate){
		this.organism=nom;
		this.kingdom = royaume;
		this.group=groupe;
		this.subgroup=subgroupe;
		this.modifyDate=modifyDate;
		this.replicons=new ArrayList<String>();
		
		for(String typeRep : replicons.split(";")){
			for(String replicon :typeRep.substring(typeRep.indexOf(':')+1).split("/"))
				this.replicons.add(replicon);
			
		}
	}
	
	public String getOrganism(){return organism;}
	public String getKingdom(){return kingdom;}
	public String getGroup(){return group;}
	public String getSubGroup(){return subgroup;}
	public ArrayList<String> getReplicons(){return replicons;}
	
	public String afficherReplicons(){
		String a = "";
		for(String s:replicons)a+=", "+s;
		return a;
	}
}
