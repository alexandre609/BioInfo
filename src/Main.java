import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Cette classe d�marre le programme et son interface graphique.
 * <br>Il n'est donc pas �tonnant d'y retrouver de nombreux �l�ments servant uniquement � l'interface graphique.
 * <br>J'essaie n�anmoins de regrouper tout l'aspect graphique vers le bas du fichier autant que possible.
 * @author Alexandre Florentin
 *
 */
public class Main {
	public static JFrame frame;
	public static JPanel contentPane;
	public static JTextField champRecherche;
	public static JProgressBar progress;
	public static JLabel progressText;
	public static DefaultMutableTreeNode root;
	public static JTree arborescence;
	public static JScrollPane scrollPane;
	public static JCheckBoxMenuItem chckbxmntmEukaryotes;
	public static JCheckBoxMenuItem chckbxmntmProkaryotes;
	public static JCheckBoxMenuItem chckbxmntmVirus;
	
	public static void main(String[] args) {
		Recuperation eukaryotes = new Recuperation("Eukaryotes");
		eukaryotes.chargerInfos();
		eukaryotes.afficherInfos();
		Recuperation prokaryotes = new Recuperation("Prokaryotes");
		prokaryotes.chargerInfos();
		prokaryotes.afficherInfos();
		Recuperation virus = new Recuperation("Virus");
		virus.chargerInfos();
		virus.afficherInfos();
		initialize();
		//Recuperation.test();
		//Sortie.sortieExcel();
	}

	/**
	 * Actions effectu�es au clic du bouton Lancer
	 */
	public static void actionsBoutonLancer(){
		progress.setIndeterminate(true);
		progressText.setText("Chargement de l'arborescence");
		progressText.repaint();
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Arborescence.royaumes();
		Arborescence.recupererArborescence();
		frame.setCursor(Cursor.getDefaultCursor());
		refreshArborescence();
		progressText.setText("");
		progressText.repaint();
		progress.setIndeterminate(false);
	}
	
	/**
	 * Rafra�chissement de l'arborescence affich�e
	 */
	static void refreshArborescence(){
		DefaultMutableTreeNode root = Arborescence.getSubDirs(new File("./Kingdom/"));
		arborescence = new JTree(root,true);
		scrollPane.setViewportView(arborescence);
	}
	
	/**
	 * Cr�ation de toute l'interface graphique
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
		
		JMenu mnEdition = new JMenu("\u00C9dition");
		menuBar.add(mnEdition);
		
		JMenu mnAffichage = new JMenu("Affichage");
		menuBar.add(mnAffichage);
		
		JMenu mnArbre = new JMenu("Arbre");
		mnAffichage.add(mnArbre);
		
		chckbxmntmEukaryotes = new JCheckBoxMenuItem("Eukaryotes");
		chckbxmntmEukaryotes.setSelected(true);
		mnArbre.add(chckbxmntmEukaryotes);
		
		chckbxmntmProkaryotes = new JCheckBoxMenuItem("Prokaryotes");
		chckbxmntmProkaryotes.setSelected(true);
		mnArbre.add(chckbxmntmProkaryotes);
		
		chckbxmntmVirus = new JCheckBoxMenuItem("Virus");
		chckbxmntmVirus.setSelected(true);
		mnArbre.add(chckbxmntmVirus);
		
		JMenu mnOutils = new JMenu("Outils");
		menuBar.add(mnOutils);
		//FIN BARRE DU HAUT
		
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);
		
		root = Arborescence.getSubDirs(new File("./Kingdom/"));
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
				actionsBoutonLancer();
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
	
	public static void progress(int valeur){
		progress.setValue(valeur);
	}
	
	public static void progressText(String texte){
		progressText.setText(texte);
	}
}
