import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
	public static JPanel contentPane;
	public static JTextField textField;
	public static JTextField champRecherche;
	public static JProgressBar progress;
	public static JLabel progressText;
	public static JLabel avancement;
	public static JTree arborescence;
	public static JScrollPane scrollPane;
	
	public static void main(String[] args) {
		
		initialize();
		//Recuperation.test();
		//Sortie.sortieExcel();
	}

	/**
	 * Actions effectuées au clic du bouton Lancer
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
	 * Rafraîchissement de l'arborescence affichée
	 */
	static void refreshArborescence(){
		DefaultMutableTreeNode root = Arborescence.getSubDirs(new File("./Kingdom/"));
		arborescence = new JTree(root,true);
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
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);
		
		DefaultMutableTreeNode root = Arborescence.getSubDirs(new File("./Kingdom/"));
		arborescence = new JTree(root,true);
		arborescence.setRootVisible(false);
		scrollPane.setViewportView(arborescence);
		
		JPanel panel = new JPanel();
		panel_2.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblRecherche = new JLabel("Recherche :");
		panel.add(lblRecherche, BorderLayout.WEST);
		
		textField = new JTextField();
		panel.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
		
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
		
		
		
		JMenuBar menuBar = new JMenuBar();
		contentPane.add(menuBar, BorderLayout.NORTH);
		
		JMenu mnFichier = new JMenu("Fichier");
		menuBar.add(mnFichier);
		
		JMenu mnEdition = new JMenu("\u00C9dition");
		menuBar.add(mnEdition);
		
		JMenu mnFentre = new JMenu("Fen\u00EAtre");
		menuBar.add(mnFentre);
		
		JMenu mnAide = new JMenu("Aide");
		menuBar.add(mnAide);
		
		progress = new JProgressBar();
		contentPane.add(progress, BorderLayout.SOUTH);
		
		frame.setVisible(true);
	}
}
