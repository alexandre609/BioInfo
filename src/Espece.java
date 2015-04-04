import java.util.ArrayList;


public class Espece {
	private String nom;
	private String groupe;
	private String subgroupe;
	private ArrayList<String> replicons;
	private String modifyDate;
	
	public Espece(String nom, String groupe, String subgroupe, String replicons, String modifyDate){
		this.nom=nom;
		this.groupe=groupe;
		this.subgroupe=subgroupe;
		this.modifyDate=modifyDate;
		this.replicons=new ArrayList<String>();
		
		for(String typeRep : replicons.split(";")){
			for(String replicon :typeRep.substring(typeRep.indexOf(':')+1).split("/"))
				this.replicons.add(replicon);
			
		}
	}
	
	public String getNom(){return nom;}
	public ArrayList<String> getReplicons(){return replicons;}
	
	public String afficherReplicons(){
		String a = "";
		for(String s:replicons)a+=", "+s;
		return a;
	}
}
