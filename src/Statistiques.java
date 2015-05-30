import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public class Statistiques {
	String header;
	String outputSpecific;
	/**
	 * Compte le nombre de CDS rencontrés au total
	 */
	int nbCds;
	
	/**
	 * Compte le nombre de CDS erronés et donc skippés
	 */
	int nbErrCds;
	int nbTrinu;
	static double avancement;

	String[] init = {"ATG","CTG","TTG","GTG","ATA","ATC","ATT","TTA"};
	String[] stop = {"TAA","TAG","TGA"};
	String[] tri = {"AAA","AAC","AAG","AAT","ACA","ACC","ACG","ACT","AGA","AGC","AGG","AGT","ATA","ATC","ATG","ATT",
			"CAA","CAC","CAG","CAT","CCA","CCC","CCG","CCT","CGA","CGC","CGG","CGT","CTA","CTC","CTG","CTT",
			"GAA","GAC","GAG","GAT","GCA","GCC","GCG","GCT","GGA","GGC","GGG","GGT","GTA","GTC","GTG","GTT",
			"TAA","TAC","TAG","TAT","TCA","TCC","TCG","TCT","TGA","TGC","TGG","TGT","TTA","TTC","TTG","TTT"};
	ArrayList<Trinucl> trinucleotide;
	static ArrayList<String> trinucleotides;
	Espece espece;
	
	public Statistiques(Espece espece){
		initialiserTrinucl();
		
		trinucleotides = new ArrayList<String>(Arrays.asList(tri));
		this.espece = espece;
		this.nbCds = 0;
		this.nbTrinu = 0;
		this.nbErrCds = 0;
		this.outputSpecific = "";
		Main.progress.setStringPainted(true);
		Main.progressText(espece.getKingdom() +" : " + espece.getOrganism());
	}
	
	public Statistiques(){
		initialiserTrinucl();
		
		trinucleotides = new ArrayList<String>(Arrays.asList(tri));
		this.espece = null;
		this.nbCds = 0;
		this.nbTrinu = 0;
		this.nbErrCds = 0;
		Main.progress.setStringPainted(true);
		Main.progressText("Combinaison des statistiques");
	}
	
	public void erreur(){nbErrCds++;}
	
	public void setHeader(String header){
		this.header = header;
	}
	
	/**
	 * Vérifie si la séquence donnée commence bien par un codon init et termine par un codon stop.
	 * @param sequence La séquence à vérifier.
	 * @return <b>true</b> si les codons init et stop sont bons, <b>false</b> sinon.
	 */
	public boolean codonsConformes(String sequence){
		if((sequence == null) || (sequence.length() < 3))
			return false;
		
		String codonInit = sequence.substring(0, 3);
		String codonStop = sequence.substring(sequence.length()-3, sequence.length());
		ArrayList<String> listeCodonsInit = new ArrayList<String>(Arrays.asList(init));
		ArrayList<String> listeCodonsStop = new ArrayList<String>(Arrays.asList(stop));
		
		if(listeCodonsInit.contains(codonInit)){
			if(listeCodonsStop.contains(codonStop))
				return true;
			else
				System.err.println("Codon stop erroné : "+ codonStop);
		}else System.err.println("Codon init erroné : "+ codonInit);
			
		return false;
	}
	
	/**
	 * Compte les trinucléotides d'une séquence donnée.
	 * <br>On lira pour les trois phases.
	 * @param sequence La séquence à analyser
	 */
	public boolean analyser(String sequence){
		//Si le codon init et le codon stop ne sont pas conformes, on ne fait rien du tout
		if(!codonsConformes(sequence))
			return false;
		
		//Tableau "buffer", au cas où on lit une erreur
		ArrayList<Trinucl> trinucleotideBuffer = new ArrayList<Trinucl>();
		int nbTrinuBuffer = 0;
		char nuc[] = {'A','C','G','T'};
		for(char n1 : nuc){
			for(char n2 : nuc){
				for(char n3 : nuc){
					trinucleotideBuffer.add(new Trinucl( String.valueOf(n1) + String.valueOf(n2) + String.valueOf(n3)));
				}
			}
		}
		
		//Lecture de la séquence en buffer
		int i=0;
		int compteTrinu[] = new int[3];for(int c=0;c<3; compteTrinu[c++]=0);
		while(i< (sequence.length() -3)){
			int index = trinucleotides.indexOf(sequence.substring(i, i+3));
			if(index == -1){
				System.err.println("Mauvais trinucléotide : "+sequence.substring(i, i+3));
				nbErrCds++;
				return false;
			}else{
				trinucleotideBuffer.get(index).phase[i%3] += 1;
				compteTrinu[i%3]++;
				nbTrinuBuffer++;
			}
			i++;
		}
		//Vérification de la somme des trinucléotides pour chaque phase.
		if((compteTrinu[0] != compteTrinu[1]) || (compteTrinu[1] != compteTrinu[2])){
			System.err.println("Pas le même nombre de trinucléotide sur chaque phase.");
			return false;
		}
		
		//Si la lecture n'a pas fait d'erreur, on garde les données
		for(i=0;i<64;i++){
			trinucleotide.get(i).phase[0] += trinucleotideBuffer.get(i).phase[0];
			trinucleotide.get(i).phase[1] += trinucleotideBuffer.get(i).phase[1];
			trinucleotide.get(i).phase[2] += trinucleotideBuffer.get(i).phase[2];
		}
		
		nbTrinu += nbTrinuBuffer;
		nbCds++;
		return true;
	}
	
	
	private void calculer(Workbook wb, Sheet sheet){
		Row row;
		Cell cell;
		CellStyle style = wb.createCellStyle();
    	style.setAlignment(CellStyle.ALIGN_CENTER);
		CellStyle styleNat = wb.createCellStyle();
		styleNat.setAlignment(CellStyle.ALIGN_CENTER);
		CellStyle styleDec = wb.createCellStyle();
		styleDec.setDataFormat(wb.createDataFormat().getFormat("0.00"));
		styleDec.setAlignment(CellStyle.ALIGN_CENTER);
		short ligne = 7;
		//Pour chaque Trinucléotide
		for(Trinucl t : trinucleotide){
			row = sheet.createRow(ligne);
			
	    	cell = row.createCell(0);
	    	cell.setCellValue(t.getNom());
	    	cell.setCellStyle(style);
			
			//Pour chaque phase
			for(int i=0;i<3;i++){
				//On écrit le résultat du comptage
				cell = row.createCell((2*i)+1);
				cell.setCellValue(t.getPhase(i));
				cell.setCellStyle(styleNat);
				
				//On écrit la formule qui va bien pour chaque phase
				cell = row.createCell((2*i)+2);
				cell.setCellType(Cell.CELL_TYPE_FORMULA);
			    if(i==0)cell.setCellFormula("(B"+ (ligne + 1) +"/B72)*100");
			    else if(i==1)cell.setCellFormula("(D"+ (ligne + 1) +"/D72)*100");
			    else if(i==2)cell.setCellFormula("(F"+ (ligne + 1) +"/F72)*100");
				cell.setCellStyle(styleDec);
			}
			ligne++;
		}
	    CellStyle style1 = wb.createCellStyle();
    	style1.setAlignment(CellStyle.ALIGN_CENTER);
	    Font font = wb.createFont();
	    font.setBold(true);
	    style1.setFont(font);
	    
	    row = sheet.createRow(ligne);
	    cell = row.createCell(0);
	    cell.setCellStyle(style1);
	    cell.setCellValue("Total");
	    //TOTAUX
	    cell = row.createCell(1);
	    cell.setCellType(Cell.CELL_TYPE_FORMULA);
	    cell.setCellFormula("SUM(B8:B71)");
	    cell = row.createCell(2);
	    cell.setCellType(Cell.CELL_TYPE_FORMULA);
	    cell.setCellFormula("SUM(C8:C71)");
	    cell = row.createCell(3);
	    cell.setCellType(Cell.CELL_TYPE_FORMULA);
	    cell.setCellFormula("SUM(D8:D71)");
	    cell = row.createCell(4);
	    cell.setCellType(Cell.CELL_TYPE_FORMULA);
	    cell.setCellFormula("SUM(E8:E71)");
	    cell = row.createCell(5);
	    cell.setCellType(Cell.CELL_TYPE_FORMULA);
	    cell.setCellFormula("SUM(F8:F71)");
	    cell = row.createCell(6);
	    cell.setCellType(Cell.CELL_TYPE_FORMULA);
	    cell.setCellFormula("SUM(G8:G71)");
	}
	
	public boolean sortieExcel(){
		if(nbTrinu != 0){
			try{
				Workbook wb = new HSSFWorkbook();
			    Sheet sheet = wb.createSheet("Statistiques");
			    CellStyle style = wb.createCellStyle();
		    	style.setAlignment(CellStyle.ALIGN_CENTER);
	
			    Row row = sheet.createRow((short)0);
			    row.createCell(0).setCellValue("Nom");
			    
			    if(espece != null){
				    row.createCell(1).setCellValue(espece.getOrganism());
				    
				    row = sheet.createRow((short)1);
				    row.createCell(0).setCellValue("Chemin");
				    row.createCell(1).setCellValue(espece.getKingdom());
				    row.createCell(2).setCellValue(espece.getGroup());
				    row.createCell(3).setCellValue(espece.getSubGroup());
				    row.createCell(4).setCellValue(espece.getOrganism());
			    }
			    
			    row = sheet.createRow((short)2);
			    row.createCell(0).setCellValue("Nb CDS");
			    Cell cell = row.createCell(1);
			    cell.setCellValue(nbCds);	//Compter les CDS des fichiers texte (ou le nombre de fichier texte ?)
			    cell.setCellStyle(style);
			    
			    //Nb trinucléotides
			    if(!outputSpecific.equals("")){//espece == null){
			    	/*row = sheet.createRow((short)3);
				    row.createCell(0).setCellValue("Nb trinucléotides");
				    cell = row.createCell(1);
				    cell.setCellType(Cell.CELL_TYPE_FORMULA);
				    cell.setCellFormula("(B72 + D72 + F72)/3");
				    cell.setCellStyle(style);
			    	*/
			    	int count = 0;
			    	for(Trinucl t : this.trinucleotide){
			    		count += t.getPhase(0);
			    	}
			    	nbTrinu = count;
			    	row = sheet.createRow((short)3);
				    row.createCell(0).setCellValue("Nb trinucléotides");
				    cell = row.createCell(1);
				    cell.setCellValue(nbTrinu);	//Compter les trinucléotides des fichiers texte
				    cell.setCellStyle(style);
			    }else{
				    row = sheet.createRow((short)3);
				    row.createCell(0).setCellValue("Nb trinucléotides");
				    cell = row.createCell(1);
				    cell.setCellValue(nbTrinu/3);	//Compter les trinucléotides des fichiers texte
				    cell.setCellStyle(style);
			    }
			    
			    row = sheet.createRow((short)4);
			    row.createCell(0).setCellValue("Nb CDS non traités");
			    cell = row.createCell(1);
			    cell.setCellValue(nbErrCds);	//Compter les CDS des fichiers texte pas pris en compte
			    cell.setCellStyle(style);
			    
			    
			    row = sheet.createRow((short)6);
			    cell = row.createCell(0);
			    cell.setCellValue("Trinucléotides");
			    cell.setCellStyle(style);
			    cell = row.createCell(1);
			    cell.setCellValue("Nb Ph0");
			    cell.setCellStyle(style);
			    cell = row.createCell(2);
			    cell.setCellValue("Pb Ph0");
			    cell.setCellStyle(style);
			    cell = row.createCell(3);
			    cell.setCellValue("Nb Ph1");
			    cell.setCellStyle(style);
			    cell = row.createCell(4);
			    cell.setCellValue("Pb Ph1");
			    cell.setCellStyle(style);
			    cell = row.createCell(5);
			    cell.setCellValue("Nb Ph2");
			    cell.setCellStyle(style);
			    cell = row.createCell(6);
			    cell.setCellValue("Pb Ph2");
			    cell.setCellStyle(style);
			    
			    calculer(wb, sheet);
			    for(int i=0;i<7;i++)
			    	sheet.autoSizeColumn(i);
			    
			    File fichier;
			    
			    //Si c'est une espèce, on écrit dans le sous-dossier
			    if(outputSpecific == ""){
			    	outputSpecific = "Kingdom/" + espece.getKingdom() + "/"+ espece.getGroup() +"/" + espece.getSubGroup() + "/" + espece.getOrganism()+"/";
			    	fichier = new File(outputSpecific);
			    	if(!fichier.exists())
				    	fichier.mkdir();
				    fichier = new File(outputSpecific +espece.getBioproject() + ".xls");
			    }
			    //Sinon on écrit dans le dossier
			    else{
			    	if(outputSpecific == null){
			    		wb.close();
			    		return false;
			    	}else
			    		fichier = new File(outputSpecific + ".xls");
			    }
			    
			    FileOutputStream fileOut = new FileOutputStream(fichier);
			    wb.write(fileOut);
			    fileOut.close();
				wb.close();
				
				Main.progressText(outputSpecific +" : Fichier créé");
			}catch (IOException io){
				io.printStackTrace();
			}
			return true;
		}else
			return false;
	}
	
	void initialiserTrinucl(){
		trinucleotide = new ArrayList<Trinucl>();
		char nuc[] = {'A','C','G','T'};
		for(char n1 : nuc){
			for(char n2 : nuc){
				for(char n3 : nuc){
					trinucleotide.add(new Trinucl( String.valueOf(n1) + String.valueOf(n2) + String.valueOf(n3)));
				}
			}
		}
	}
	
	/**
	 * Disons que dossier est un royaume
	 * @param dossier
	 */
	public static void hierarchicalStats(File dossier, boolean fine, final boolean multiThread){
		//Si c'est bien un dossier et qu'il n'est pas vide
		if(dossier.isDirectory() && (!dossier.getName().equals("Sequences")) && dossier.listFiles() != null){
			//On prend tous les groupes pour faire l'opération
			if(multiThread){
				final boolean f = fine;
				final File d = dossier;
				final File[] sousDossier = dossier.listFiles();
				ExecutorService execute = Executors.newFixedThreadPool(10);
				execute.execute(new Runnable(){
					@Override
					public void run(){
						for(File fichier : sousDossier){
							hierarchicalStats(fichier, f, multiThread);
						}
						Statistiques stats = new Statistiques();
						stats.combinerStats(d, f);
						stats.sortieExcel();
					}
				});
				execute.shutdown();
				try {
					execute.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else{
				for(File fichier : dossier.listFiles()){
					hierarchicalStats(fichier, fine, multiThread);
				}
				Statistiques stats = new Statistiques();
				stats.combinerStats(dossier, fine);
				stats.sortieExcel();
			}
		}
		if(Main.avancementMax != 100)
			Main.progress(((++Main.avancement)*100)/Main.avancementMax);
		else
			Main.progress((++Main.avancement)%100);
	}
	
	/**
	 * Combine les statistiques trouvées dans les fichiers Excel du dossier mentionné.
	 * <br>Ces statistiques sont additionnées et gardées dans l'objet Statistiques courant.
	 * @param dossier Dossier dans lequel figurent les fichiers dont on veut récupérer les données.
	 */
	public void combinerStats(File dossier, boolean fine){
		if((dossier != null) && (dossier.isDirectory()) && (dossier.listFiles() != null)){
			for(File fichier : dossier.listFiles()){
				if(fichier.getName().endsWith(".xls")){
					try{
						FileInputStream file = new FileInputStream(fichier);
						 
						Workbook workbook = new HSSFWorkbook(file);
						Sheet sheet = workbook.getSheetAt(0);
						Cell cell = null;
						
						//On lit les cellules trinucléotides
						for(int i=7 ; i<71; i++){
							for(int j=0;j<3;j++){
								cell = sheet.getRow(i).getCell(1+(j*2));											
								trinucleotide.get(i-7).phase[j] += cell.getNumericCellValue();
							}
						}
						//On lit nbCds
						cell = sheet.getRow(2).getCell(1);
						nbCds += cell.getNumericCellValue();
						
						//On lit nbTrinu
						cell = sheet.getRow(3).getCell(1);
						//nbTrinu += (cell.getNumericCellValue() * 3);
						nbTrinu += (cell.getNumericCellValue());
						
						//On lit nbErrCds
						cell = sheet.getRow(4).getCell(1);
						nbErrCds += cell.getNumericCellValue();
						
						if(this.espece == null){
							String kingdom = "";
							String group = "";
							String subGroup = "";
							String organism = "";
							
							String[] pathString = dossier.getPath().split("/");
							if(pathString.length > 2) kingdom = pathString[2];
							if(pathString.length > 3) group = pathString[3];
							if(pathString.length > 4) subGroup = pathString[4];
							if(pathString.length > 5) organism = pathString[5];
							
							
							this.espece = new Espece(organism, kingdom, group, subGroup, organism);
							this.outputSpecific = dossier.getPath();
							System.out.println(outputSpecific);
						}
							
						workbook.close();
						file.close();
						
						//Si on fait des statistiques grossières
						if(!fine){
							//Si le fichier est un dossier vide ou un 
							if(fichier.listFiles() == null)
								break;
						}
					}catch(IOException e){
						e.printStackTrace();
					}
				}
			}
		}
	}
}

class Trinucl{
	private String nom;
	public int phase[];

	public Trinucl(String nom){
		this.nom = nom;
		this.phase = new int[3];
		
		//On initialise les phases à 0
		for(int i=0; i<3 ; this.phase[i]=0, i++);
	}
	
	public int getPhase(int i){return phase[i];}
	public String getNom(){return this.nom;}
}