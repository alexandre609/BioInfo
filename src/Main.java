import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Cette classe démarre le programme et son interface graphique.
 * <br>Il n'est donc pas étonnant d'y retrouver de nombreux éléments servant uniquement à l'interface graphique.
 * <br>J'essaie néanmoins de regrouper tout l'aspect graphique vers le bas du fichier autant que possible.
 * @author Alexandre Florentin
 *
 */
public class Main {
	public static ArrayList<Recuperation> royaumes;
	public static JFrame frame;
	public static JPanel contentPane;
	public static JTextField champRecherche;
	public static JProgressBar progress;
	public static JLabel progressText;
	public static DefaultMutableTreeNode root;
	public static JTree arborescence;
	public static JScrollPane scrollPane;
	public static ArrayList<JCheckBoxMenuItem> checkKingdom;
	
	
	public static void main(String[] args) {
		initialize();
		royaumes = new ArrayList<Recuperation>();
		royaumes.add(new Recuperation("Eukaryotes"));
		royaumes.add(new Recuperation("Prokaryotes"));
		royaumes.add(new Recuperation("Virus"));
		
		//Sortie.sortieExcel();
	}

	/**
	 * Actions effectuées au clic du bouton Lancer
	 */
	public static void actionsBoutonLancer(){
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		if(checkKingdom.get(0).isSelected()){
			royaumes.get(0).recupFastaLocal();
			//Statistiques stats1 = new Statistiques(royaumes.get(0));
		}
		if(checkKingdom.get(1).isSelected()){
			royaumes.get(1).recupFastaLocal();
			//Statistiques stats2 = new Statistiques(royaumes.get(1));
		}
		if(checkKingdom.get(2).isSelected()){
			royaumes.get(2).recupFastaLocal();
			//Statistiques stats3 = new Statistiques(royaumes.get(2));
		}
		frame.setCursor(Cursor.getDefaultCursor());
	}
	
	public static void actionsBoutonVerif(){
		progressText("Vérification de l'arborescence");
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		if(checkKingdom.get(0).isSelected())	royaumes.get(0).verifierInfos();
		if(checkKingdom.get(1).isSelected())	royaumes.get(1).verifierInfos();
		if(checkKingdom.get(2).isSelected())	royaumes.get(2).verifierInfos();
		
		frame.setCursor(Cursor.getDefaultCursor());
		refreshArborescence();
	}
	
	/**
	 * Rafraîchissement de l'arborescence affichée
	 */
	static void refreshArborescence(){
		DefaultMutableTreeNode root = getSubDirs(new File("./Kingdom/"));
		arborescence = new JTree(root,true);
		arborescence.setRootVisible(false);
		scrollPane.setViewportView(arborescence);
	}
	
	/**
	 * Création de toute l'interface graphique
	 */
	static void initialize(){
		try {
            // Set System L&F
			/*UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());*/
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} 
		catch (Exception e) {
			// handle exception
		}
		frame = new JFrame();
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Fenetre.class.getResource("/javax/swing/plaf/metal/icons/ocean/hardDrive.gif")));
		frame.setTitle("BioInfo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 650, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		frame.setContentPane(contentPane);
		
		
		//DEBUT BARRE DU HAUT
		JMenuBar menuBar = new JMenuBar();
		contentPane.add(menuBar, BorderLayout.NORTH);
		
		JMenu mnFichier = new JMenu("Fichier");
		menuBar.add(mnFichier);
		
		JMenuItem mntmFermer = new JMenuItem("Fermer");
		mnFichier.add(mntmFermer);
		mntmFermer.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					System.exit(0);
				}});
		
		JMenu mnEdition = new JMenu("\u00C9dition");
		menuBar.add(mnEdition);
		
		JMenu mnArbre = new JMenu("Arbre");
		menuBar.add(mnArbre);
		
		JMenu mnAffichage = new JMenu("Affichage");
		menuBar.add(mnAffichage);	
	
		checkKingdom = new ArrayList<JCheckBoxMenuItem>();
		checkKingdom.add(new JCheckBoxMenuItem("Eukaryotes"));
		checkKingdom.get(0).setSelected(true);
		mnArbre.add(checkKingdom.get(0));
		
		checkKingdom.add(new JCheckBoxMenuItem("Prokaryotes"));
		checkKingdom.get(1).setSelected(true);
		mnArbre.add(checkKingdom.get(1));
		
		checkKingdom.add(new JCheckBoxMenuItem("Virus"));
		checkKingdom.get(2).setSelected(true);
		mnArbre.add(checkKingdom.get(2));
		
		JMenu mnOutils = new JMenu("Outils");
		menuBar.add(mnOutils);
		//FIN BARRE DU HAUT
		
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);
		
		root = getSubDirs(new File("./Kingdom/"));
		arborescence = new JTree(root,true);
		arborescence.setRootVisible(false);
		scrollPane.setViewportView(arborescence);
		
		JPanel panel = new JPanel();
		panel_2.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblRecherche = new JLabel("Recherche :");
		panel.add(lblRecherche, BorderLayout.WEST);
		
		champRecherche = new JTextField();
		panel.add(champRecherche, BorderLayout.CENTER);
		champRecherche.setColumns(10);
		champRecherche.getDocument().addDocumentListener(new DocumentListener() {
		    private void updateData() {
		    	if(champRecherche.getText().length() > 4)
		    		rechercheArbre();
		    }
		 
		    @Override
		    public void changedUpdate(DocumentEvent e) {}
		 
		    @Override
		    public void insertUpdate(DocumentEvent e) {
		    	updateData();
		    }
		 
		    @Override
		    public void removeUpdate(DocumentEvent e)  {
		        updateData();
		    }
		 
		});
		try{
			BufferedImage buttonIcon = ImageIO.read(new File("res/loupe.png"));
			JButton button = new JButton(new ImageIcon(buttonIcon));
			button.setBorder(BorderFactory.createEmptyBorder());
			button.setContentAreaFilled(false);
			button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					rechercheArbre();
				}});
			panel.add(button, BorderLayout.EAST);
		}catch(Exception e){
			
		}
		
		
		JPanel panel_1 = new JPanel();
		panel_2.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		progressText = new JLabel("");
		panel_1.add(progressText);
		
		JButton btnLancer = new JButton("Lancer");
		panel_1.add(btnLancer);
		btnLancer.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				//On va lancer la recherche en ligne
				Runnable runner = new Runnable(){
			        public void run() {
			        	actionsBoutonLancer();
			        }
			    };
			    Thread t = new Thread(runner, "Code Executer");
			    t.start();
			}});
		
		JButton btnVerif = new JButton("Verifier");
		panel_1.add(btnVerif);
		btnVerif.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				//On va lancer la recherche en ligne
				Runnable runner = new Runnable(){
			        public void run() {
			        	actionsBoutonVerif();
			        }
			    };
			    Thread t = new Thread(runner, "Code Executer");
			    t.start();
			}});

		JButton btnQuitter = new JButton("Quitter");
		panel_1.add(btnQuitter);
		btnQuitter.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}});
		
		progress = new JProgressBar();
		contentPane.add(progress, BorderLayout.SOUTH);
		
		frame.setVisible(true);
	}
	
	private static TreePath find(DefaultMutableTreeNode root, String s) {
	    @SuppressWarnings("unchecked")
	    Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
	    while (e.hasMoreElements()) {
	        DefaultMutableTreeNode node = e.nextElement();
	        if (node.toString().toLowerCase().contains(s.toLowerCase())) {
	            return new TreePath(node.getPath());
	        }
	    }
	    return null;
	}
	
	private static void rechercheArbre(){
		if(!champRecherche.getText().isEmpty()){
			TreePath path = find(root,champRecherche.getText());
			arborescence.setSelectionPath(path);
			arborescence.scrollPathToVisible(path);
		}
	}
	
	public static void progress(int valeur){progress.setValue(valeur);}
	public static void progress(boolean valeur){progress.setIndeterminate(!valeur);}
	
	public static void progressText(String texte){
		progressText.setText(texte);
		progressText.repaint();
	}
	
	public static DefaultMutableTreeNode getSubDirs(File root){
		DefaultMutableTreeNode racine = new DefaultMutableTreeNode(root,true);
		File[] list = root.listFiles();
		
		if ( list != null){
			for (int j = 0 ; j<list.length ; j++){
				DefaultMutableTreeNode file = null;
				if (list[j].isDirectory()){
					file = getSubDirs(list[j]);  
					racine.add(file);
				}
			}
		}
		return racine;
	}
}
