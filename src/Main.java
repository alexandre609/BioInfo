import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


/**
 * Cette classe d�marre le programme et son interface graphique.
 * <br>Il n'est donc pas �tonnant d'y retrouver de nombreux �l�ments servant uniquement � l'interface graphique.
 * <br>J'essaie n�anmoins de regrouper tout l'aspect graphique vers le bas du fichier autant que possible.
 * @author Alexandre Florentin
 *
 */
public class Main {
	public static JFrame frame;
	public static JPanel panel;
	public static JTextField texte;
	
	
	public static void main(String[] args) {
		//Arborescence.truc();
		//
		//Recuperation.test();
		//Sortie.sortieExcel();
		creerFenetre();
	}

	/**
	 * Cette m�thode va cr�er la fen�tre avec toutes ses composantes.
	 */
	public static void creerFenetre(){
		frame = new JFrame("BioInfo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new JPanel();
	
		JLabel label = new JLabel("Recherche :");
		panel.add(label);
		
		texte = new JTextField("");
		panel.add(texte);
		
		
		
		JButton test = new JButton("OK");
		panel.add(test);
		
		
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
}
