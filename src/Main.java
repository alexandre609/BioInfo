import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Cette classe démarre le programme et son interface graphique.
 * <br>Il n'est donc pas étonnant d'y retrouver de nombreux éléments servant uniquement à l'interface graphique.
 * <br>J'essaie néanmoins de regrouper tout l'aspect graphique vers le bas du fichier autant que possible.
 * @author Alexandre Florentin
 *
 */
public class Main {
	public static JFrame frame;
	public static JTextField champRecherche;
	public static JProgressBar progress;
	public static JLabel avancement;
	public static JTree arborescence;
	public static JScrollPane centerPanel;
	
	public static void main(String[] args) {
		creerFenetre();
		frame.setCursor(Cursor.WAIT_CURSOR);
		Arborescence.royaumes();
		Arborescence.recupererArborescence();
		frame.setCursor(Cursor.getDefaultCursor());
		refreshArborescence();
		//Recuperation.test();
		//Sortie.sortieExcel();
	}

	/**
	 * Cette méthode va créer la fenêtre avec toutes ses composantes.
	 */
	public static void creerFenetre(){
		frame = new JFrame("BioInfo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 400);
		frame.setLocationRelativeTo(null);
		
		//Panneau du haut, qui contiendra les options de recherche.
		JPanel northPanel = new JPanel();
		frame.getContentPane().add(northPanel, BorderLayout.NORTH);
		JButton test = new JButton("OK");
		JLabel label = new JLabel("Recherche :");
		champRecherche = new JTextField("\t\t");
		champRecherche.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				arborescence.setSelectionPath(arborescence.getSelectionPath());
			}
		});
		northPanel.add(label, BorderLayout.WEST);
		northPanel.add(champRecherche, BorderLayout.CENTER);
		northPanel.add(test, BorderLayout.EAST);
		
		//Panneau central contenant l'arborescence
		DefaultMutableTreeNode root = Arborescence.getSubDirs(new File("./Kingdom/"));
		arborescence = new JTree(root,true);
		centerPanel = new JScrollPane(arborescence);
		frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerPanel.setPreferredSize(new Dimension(300,300));
		centerPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		//Panneau du bas, contenant la barre de progression
		JPanel southPanel = new JPanel();
		frame.getContentPane().add(southPanel,BorderLayout.SOUTH);
		avancement = new JLabel("");
		progress = new JProgressBar();
		progress.setVisible(false);
		southPanel.add(progress,BorderLayout.CENTER);
		southPanel.add(avancement,BorderLayout.SOUTH);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	static void refreshArborescence(){
		DefaultMutableTreeNode root = Arborescence.getSubDirs(new File("./Kingdom/"));
		arborescence = new JTree(root,true);
		centerPanel.setViewportView(arborescence);
	}
}
