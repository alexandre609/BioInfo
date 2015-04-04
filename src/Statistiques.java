import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public class Statistiques {
	static double avancement;
	final static String[] trinucleotides = {"AAA","AAC","AAG","AAT","ACA","ACC","ACG","ACT","AGA","AGC","AGG","AGT","ATA","ATC","ATG","ATT",
		"CAA","CAC","CAG","CAT","CCA","CCC","CCG","CCT","CGA","CGC","CGG","CGT","CTA","CTC","CTG","CTT",
		"GAA","GAC","GAG","GAT","GCA","GCC","GCG","GCT","GGA","GGC","GGG","GGT","GTA","GTC","GTG","GTT",
		"TAA","TAC","TAG","TAT","TCA","TCC","TCG","TCT","TGA","TGC","TGG","TGT","TTA","TTC","TTG","TTT"};
	Recuperation royaume;
	
	public Statistiques(Recuperation royaume){
		final ExecutorService service = Executors.newFixedThreadPool(100);
		this.royaume = royaume;
		Main.progress.setStringPainted(true);
		Main.progressText(this.royaume.getKingdom() +" : Génération de fichiers Excel");
		
		avancement=1;
		double max = this.royaume.getEspeces().size();
		for(Espece espece : this.royaume.getEspeces()){
			service.execute(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					sortieExcel(espece,"");
					Double prog = (avancement/max)*100.0;
					Main.progress(prog.intValue());
					avancement++;
				}
			});
		}
		Main.progressText(this.royaume.getKingdom() +" : Tâche terminée");
	}
	
	private void calculer(Workbook wb, Sheet sheet, String infos){
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
		for(String tri : trinucleotides){
			row = sheet.createRow(ligne);
			
	    	cell = row.createCell(0);
	    	cell.setCellValue(tri);
	    	cell.setCellStyle(style);
			
			//Pour chaque phase
			for(int i=0;i<3;i++){
				cell = row.createCell((2*i)+1);
				cell.setCellValue(0);
				cell.setCellStyle(styleNat);
				cell = row.createCell((2*i)+2);
				cell.setCellValue(0.000000001);
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
	
	private void sortieExcel(Espece espece, String infos){
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
		    cell.setCellValue(0);	//Compter les CDS des fichiers texte (ou le nombre de fichier texte ?)
		    cell.setCellStyle(style);
		    
		    row = sheet.createRow((short)3);
		    row.createCell(0).setCellValue("Nb trinucléoitides");
		    cell = row.createCell(1);
		    cell.setCellValue(0);	//Compter les trinucléotides des fichiers texte
		    cell.setCellStyle(style);
		    
		    row = sheet.createRow((short)4);
		    row.createCell(0).setCellValue("Nb CDS non traités");
		    cell = row.createCell(1);
		    cell.setCellValue(0);	//Compter les CDS des fichiers texte pas pris en compte
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
		    


		    calculer(wb, sheet, infos);
		    for(int i=0;i<7;i++)
		    	sheet.autoSizeColumn(i);
		    
		    // Write the output to a file
		    File fichier = new File("Kingdom/" + espece.getKingdom() + "/"+ espece.getGroup() +"/" + espece.getSubGroup() + "/" + espece.getOrganism()+"/"+espece.getOrganism() + ".xls");
		    FileOutputStream fileOut = new FileOutputStream(fichier);
		    wb.write(fileOut);
		    fileOut.close();
			wb.close();
		}catch (IOException io){
			System.out.println("IOException");
		}
	}
}
