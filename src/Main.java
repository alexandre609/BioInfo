import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Cette classe démarre le programme et son interface graphique.
 * <br>Il n'est donc pas étonnant d'y retrouver de nombreux éléments servant uniquement à l'interface graphique.
 * <br>J'essaie néanmoins de regrouper tout l'aspect graphique vers le bas du fichier autant que possible.
 * @author Alexandre Florentin
 *
 */
public class Main {
	static String source = "";
	public static ArrayList<Recuperation> royaumes;
	public static JFrame frame;
	public static JButton btnVerif;
	public static JButton btnLancer;
	public static JPanel contentPane;
	public static JTextField champRecherche;
	public static JProgressBar progress;
	public static JLabel progressText;
	public static DefaultMutableTreeNode root;
	public static JTree arborescence;
	public static JScrollPane scrollPane;
	public static JPopupMenu menuContextuel;
	public static ArrayList<JCheckBoxMenuItem> checkKingdom;
	public static JCheckBoxMenuItem sauvegarder;
	
	public static void main(String[] args) {
		initialize();
		royaumes = new ArrayList<Recuperation>();
		royaumes.add(new Recuperation("Eukaryotes"));
		royaumes.add(new Recuperation("Prokaryotes"));
		royaumes.add(new Recuperation("Virus"));
	}
	
	public static void ouvrirDossier(){
		if(arborescence.getLastSelectedPathComponent() != null){
			File dir = new File(arborescence.getLastSelectedPathComponent().toString());
			try{
				Desktop.getDesktop().open(dir);
			}catch(Exception ex){
				System.out.println("ça marche pas");
			}
		}
	}
	
	/**
	 * Actions effectuées au clic du bouton Lancer
	 */
	public static void actionsBoutonLancer(){
		btnLancer.setEnabled(false);
		btnVerif.setEnabled(false);
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		if(checkKingdom.get(0).isSelected()){
			royaumes.get(0).recupFastaLocal(sauvegarder.isSelected());
		}
		if(checkKingdom.get(1).isSelected()){
			royaumes.get(1).recupFastaLocal(sauvegarder.isSelected());
		}
		if(checkKingdom.get(2).isSelected()){
			royaumes.get(2).recupFastaLocal(sauvegarder.isSelected());
		}
		frame.setCursor(Cursor.getDefaultCursor());
		btnVerif.setEnabled(true);
		btnLancer.setEnabled(true);
		refreshArborescence();
		progressText("Statistiques terminées !");
	}
	
	public static void actionsBoutonVerif(){
		btnLancer.setEnabled(false);
		btnVerif.setEnabled(false);
		progressText("Vérification de l'arborescence");
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		int modifications=0;
		if(checkKingdom.get(0).isSelected())	modifications += royaumes.get(0).verifierInfos();
		if(checkKingdom.get(1).isSelected())	modifications += royaumes.get(1).verifierInfos();
		if(checkKingdom.get(2).isSelected())	modifications += royaumes.get(2).verifierInfos();
		
		frame.setCursor(Cursor.getDefaultCursor());
		btnVerif.setEnabled(true);
		btnLancer.setEnabled(true);
		refreshArborescence();
		Main.progressText("Vérification effectuée. "+modifications+" modifications trouvées");
		Main.progress(100);
	}
	
	/**
	 * Rafraîchissement de l'arborescence affichée
	 */
	static void refreshArborescence(){
		root = getSubDirs(new File("./Kingdom/"));
		arborescence = new JTree(root,true);
		arborescence.setRootVisible(false);
		arborescence.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1){
					if(e.getClickCount() == 2)
						ouvrirDossier();
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});
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
		
		
		menuContextuel = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("ACTION 1" );
		menuContextuel.add(menuItem);
		
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
	
		sauvegarder = new JCheckBoxMenuItem("Sauvegarder les séquences");
		sauvegarder.setSelected(false);
		mnArbre.add(sauvegarder);
		
		JMenu mnOutils = new JMenu("Outils");
		menuBar.add(mnOutils);
		//FIN BARRE DU HAUT
		
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);
		
		refreshArborescence();
		
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
		
		btnLancer = new JButton("Lancer");
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
		
		btnVerif = new JButton("Verifier");
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
		boolean hasLog=false;
		if(list!= null){
			for (int j = 0 ; j<list.length ; j++){
				DefaultMutableTreeNode file = null;
				if (list[j].isDirectory()){
					file = getSubDirs(list[j]);  
					racine.add(file);
				}else if(list[j].isFile()){
					if(list[j].getName().equals("log.txt"))
						hasLog = true;
					else if(list[j].getName().equals("content.txt")){
						//Skip
					}else{
						file = new DefaultMutableTreeNode(list[j]);
						file.setAllowsChildren(false);
						racine.add(file);
					}
				}
			}
		}
		if(!racine.isLeaf()){
			if(racine.getFirstChild().isLeaf() && !hasLog){
			racine.remove(0);
			}
		}
		return racine;
	}
}

