import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public class Statistiques {
	String header;
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
	
	ArrayList<Trinucl> trinucleotide;
	static ArrayList<String> trinucleotides;
	//Recuperation royaume;
	Espece espece;
	
	public Statistiques(Espece espece){//Recuperation royaume){
		initialiserTrinucl();
		String[] tri = {"AAA","AAC","AAG","AAT","ACA","ACC","ACG","ACT","AGA","AGC","AGG","AGT","ATA","ATC","ATG","ATT",
				"CAA","CAC","CAG","CAT","CCA","CCC","CCG","CCT","CGA","CGC","CGG","CGT","CTA","CTC","CTG","CTT",
				"GAA","GAC","GAG","GAT","GCA","GCC","GCG","GCT","GGA","GGC","GGG","GGT","GTA","GTC","GTG","GTT",
				"TAA","TAC","TAG","TAT","TCA","TCC","TCG","TCT","TGA","TGC","TGG","TGT","TTA","TTC","TTG","TTT"};
		trinucleotides = new ArrayList<String>(Arrays.asList(tri));
		//final ExecutorService service = Executors.newFixedThreadPool(100);
		//this.royaume = royaume;
		this.espece = espece;
		this.nbCds = 0;
		this.nbTrinu = 0;
		this.nbErrCds = 0;
		Main.progress.setStringPainted(true);
		Main.progressText("Calcul des statistiques de : " + espece.getOrganism());
	}
	
	public void erreur(){nbErrCds++;}
	
	public void setHeader(String header){
		this.header = header;
	}
	
	/**
	 * Compte les trinucléotides d'une séquence donnée.
	 * <br>On lira pour les trois phases.
	 * @param sequence La séquence à analyser
	 */
	public boolean analyser(String sequence){
		int i=0;
		while(i < (sequence.length() -3)){
			int index = trinucleotides.indexOf(sequence.substring(i, i+3));
			if(index == -1){
				System.err.println("Mauvais trinucléotide : "+sequence.substring(i, i+3));
				return false;
			}else
				trinucleotide.get(index).phase[i%3] += 1;
			i++;
			nbTrinu++;
		}
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
	
	public void sortieExcel(){
		try{
			Workbook wb = new HSSFWorkbook();
		    //Workbook wb = new XSSFWorkbook();
		    CreationHelper createHelper = wb.getCreationHelper();
		    Sheet sheet = wb.createSheet("new sheet");
		    CellStyle style = wb.createCellStyle();
	    	style.setAlignment(CellStyle.ALIGN_CENTER);

		    // Create a row and put some cells in it. Rows are 0 based.
		    Row row = sheet.createRow((short)0);
		    // Create a cell and put a value in it.
		    
		    //cell.setCellValue("Nom");

		    // Or do it on one line.
		    row.createCell(0).setCellValue("Nom");
		    row.createCell(1).setCellValue(espece.getOrganism());
		    
		    row = sheet.createRow((short)1);
		    row.createCell(0).setCellValue("Chemin");
		    row.createCell(1).setCellValue(espece.getKingdom());
		    row.createCell(2).setCellValue(espece.getGroup());
		    row.createCell(3).setCellValue(espece.getSubGroup());
		    row.createCell(4).setCellValue(espece.getOrganism());
		    
		    row = sheet.createRow((short)2);
		    row.createCell(0).setCellValue("Nb CDS");
		    Cell cell = row.createCell(1);
		    cell.setCellValue(nbCds);	//Compter les CDS des fichiers texte (ou le nombre de fichier texte ?)
		    cell.setCellStyle(style);
		    
		    row = sheet.createRow((short)3);
		    row.createCell(0).setCellValue("Nb trinucléoitides");
		    cell = row.createCell(1);
		    cell.setCellValue(nbTrinu/3);	//Compter les trinucléotides des fichiers texte
		    cell.setCellStyle(style);
		    
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
		    
		    // Write the output to a file
		    String output = "Kingdom/" + espece.getKingdom() + "/"+ espece.getGroup() +"/" + espece.getSubGroup() + "/" + espece.getOrganism()+"/";
		    File fichier = new File(output +espece.getOrganism() + ".xls");
		    FileOutputStream fileOut = new FileOutputStream(fichier);
		    wb.write(fileOut);
		    fileOut.close();
			wb.close();
			
			fichier = new File(output+"log.txt");
			fichier.createNewFile();
			
			Main.progressText(output +" : Fichier créé");
			System.out.println(output +" : Fichier créé");
		}catch (IOException io){
			System.out.println("IOException");
		}
	}
	
	void initialiserTrinucl(){
		trinucleotide = new ArrayList<>();
		char nuc[] = {'A','C','G','T'};
		for(char n1 : nuc){
			for(char n2 : nuc){
				for(char n3 : nuc){
					trinucleotide.add(new Trinucl( String.valueOf(n1) + String.valueOf(n2) + String.valueOf(n3)));
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
