import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Cette classe démarre le programme et son interface graphique.
 * <br>Il n'est donc pas étonnant d'y retrouver de nombreux éléments servant uniquement à l'interface graphique.
 * <br>J'essaie néanmoins de regrouper tout l'aspect graphique vers le bas du fichier autant que possible.
 * @author Alexandre Florentin
 */
public class Main {
	public static Integer avancement = 0;
	public static Integer avancementMax = 100;
	public static JRadioButton radio0;
	public static JRadioButton radio1;
	public static JRadioButton radio2;
	static Thread t;
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
	public static ArrayList<StayOpenCBItem> checkKingdom;
	public static StayOpenCBItem sauvegarder;
	public static StayOpenCBItem checkThread;
	public static ExecutorService service;
	public static Main m;
	private static String OS;
	
	public Main(){
	}
	
	public static String detectOs(){
		String OS = System.getProperty("os.name").toLowerCase();
		if(OS.indexOf("win") >= 0) return "Windows";
		else if(OS.indexOf("mac") >= 0) return "Mac";
		else return "Linux";
	}
	
	public static void main(String[] args) {
		OS = detectOs();
		m=new Main();
		initialize();
		royaumes = new ArrayList<Recuperation>(3);
		royaumes.add(new Recuperation("Eukaryotes"));
		royaumes.add(new Recuperation("Prokaryotes"));
		royaumes.add(new Recuperation("Virus"));
	}
	
	public static void ouvrirDossier(){
		if(arborescence.getLastSelectedPathComponent() != null){
			String path = arborescence.getLastSelectedPathComponent().toString();
			if(OS.equals("Windows"))path.replaceAll("/", "\\");
			File dir = new File(path);
			try{
				if(dir.isDirectory()){
					if(new File(path + ".xls").exists())
						Desktop.getDesktop().open(new File(path + ".xls"));
					else if(dir.getName().equals("Kingdom")){
						Desktop.getDesktop().open(dir);
					}
				}else{
					Desktop.getDesktop().open(dir);
				}
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	
	public static boolean sauvegarder(){
		return sauvegarder.getState();
	}
	
	/**
	 * Indique si on a coché "Multithread" dans l'UI
	 * @return <b>true</b> si Multithread est coché, <b>false</b> sinon.
	 */
	public static boolean multiThread(){
		return checkThread.getState();
	}
	
	/**
	 * Lance le programme de façon séquentielle
	 */
	public static void runThreaded(){
		service.execute(new Runnable(){
			@Override
			public void run(){
				if(checkKingdom.get(0).isSelected()){
					if(radio0.isSelected())
						royaumes.get(0).recupFastaLocal();
					else
						Statistiques.hierarchicalStats(new File("./Kingdom/Eukaryotes"),radio2.isSelected(), Main.multiThread());
				}
			}
		});
		service.execute(new Runnable(){
			@Override
			public void run(){
				if(checkKingdom.get(1).isSelected()){
					if(radio0.isSelected())
						royaumes.get(1).recupFastaLocal();
					else
						Statistiques.hierarchicalStats(new File("./Kingdom/Prokaryotes"),radio2.isSelected(), Main.multiThread());
				}
			}
		});
		
		service.execute(new Runnable(){
			@Override
			public void run(){
				if(checkKingdom.get(2).isSelected()){
					if(radio0.isSelected())
						royaumes.get(2).recupFastaLocal();
					else
						Statistiques.hierarchicalStats(new File("./Kingdom/Virus"),radio2.isSelected(), Main.multiThread());
				}
			}
		});
		
		service.shutdown();
		try {
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void runMono(){
		if(checkKingdom.get(0).isSelected()){
			if(radio0.isSelected())
				royaumes.get(0).recupFastaLocal();
			else{
				avancement = 0;
				avancementMax = 2212;
				//Statistiques.hierarchicalStats(new File("./Kingdom/Eukaryotes"),radio2.isSelected(), Main.multiThread());
				Statistiques.hierarchicalStats(new File("./Kingdom"),radio2.isSelected(), Main.multiThread());
			}
		}
	
		if(checkKingdom.get(1).isSelected()){
			if(radio0.isSelected())
				royaumes.get(1).recupFastaLocal();
			else{
				avancement = 0;
				avancementMax = 33756;
				//Statistiques.hierarchicalStats(new File("./Kingdom/Prokaryotes"),radio2.isSelected(), Main.multiThread());
			}
		}

		if(checkKingdom.get(2).isSelected()){
			if(radio0.isSelected())
				royaumes.get(2).recupFastaLocal();
			else{
				avancement = 0;
				avancementMax = 4669;
				//Statistiques.hierarchicalStats(new File("./Kingdom/Virus"),radio2.isSelected(), Main.multiThread());
			}
		}
		avancementMax=100;
	}
	
	/**
	 * Actions effectuées au clic du bouton Lancer
	 */
	public static void actionsBoutonLancer(){
		btnVerif.setEnabled(false);
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		service = Executors.newFixedThreadPool(10);
		if(multiThread()){
			runThreaded();
		}else{
			runMono();
		}
		
		
		frame.setCursor(Cursor.getDefaultCursor());
		btnVerif.setEnabled(true);
		refreshArborescence();
		progress(100);
		progressText("Statistiques terminées !");
		btnLancer.setText("Lancer");
	}
	
	public static void actionsBoutonVerif(){
		btnLancer.setEnabled(false);
		btnVerif.setEnabled(false);
		progressText("Vérification de l'arborescence");
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		
		int modifications=0;
		if(checkKingdom.get(0).isSelected()){
			modifications += royaumes.get(0).verifierInfos();
		}
		if(checkKingdom.get(1).isSelected()){
			modifications += royaumes.get(1).verifierInfos();
		}
		if(checkKingdom.get(2).isSelected()){
			modifications += royaumes.get(2).verifierInfos();
		}
		
		frame.setCursor(Cursor.getDefaultCursor());
		btnVerif.setEnabled(true);
		btnLancer.setEnabled(true);
		refreshArborescence();
		Main.progressText("Votre arborescence est à jour: "  +modifications+" modifications trouvées");
		Main.progress(100);
	}
	
	/**
	 * Rafraîchissement de l'arborescence affichée
	 */
	static void refreshArborescence(){
		root = getSubDirs(new File("./Kingdom/"));
		arborescence = new JTree(root,true);
		arborescence.setRootVisible(true);
		
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
	
	/**
	 * 
	 * @param root
	 * @return
	 */
	public static DefaultMutableTreeNode getSubDirs(File root){
		DefaultMutableTreeNode racine = new DefaultMutableTreeNode(root,true);
		File[] list = root.listFiles();
		if(list!= null){
			for (int j = 0 ; j<list.length ; j++){
				DefaultMutableTreeNode file = null;
				if (list[j].isDirectory()){
					file = getSubDirs(list[j]);  
					racine.add(file);
				}else if(list[j].isFile()){
					if(list[j].getName().equals("content.txt")){
						//Skip si on tombe sur ce fichier.
					}else if(list[j].getName().endsWith(".xls")){
						
					}else{
						file = new DefaultMutableTreeNode(list[j]);
						file.setAllowsChildren(false);
						racine.add(file);
					}
				}
			}
		}
		return racine;
	}
	
	
	/**
	 * Création de toute l'interface graphique.
	 * <br>On la laisse tout en bas, car elle est assez énorme.
	 */
	static void initialize(){
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} 
		catch (Exception e) {
			// Si on arrive pas à charger le thème, au pire c'est pas grave.
			e.printStackTrace();
		}
		frame = new JFrame();
		try{
			
			File file = new File("res/icone.png");
			if(file.exists()){
				frame.setIconImage(ImageIO.read(file));
			}else{
				frame.setIconImage(ImageIO.read(m.getClass().getResource("icone.png")));
			}
		}catch(IOException e){
			System.err.println("Icône introuvable");
		}
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
		
		JMenuItem compresser = new JMenuItem("Compresser le dossier Kingdom");
		mnFichier.add(compresser);
		compresser.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					try{
						new ZipDirectory(new File("./Kingdom/"));
						JOptionPane.showMessageDialog(null, "Le dossier Kingdom a bien été compressé", "Compression réussie", JOptionPane.INFORMATION_MESSAGE);
					}catch(Exception e){
						JOptionPane.showMessageDialog(null, "Le dossier Kingdom n'a pas pu être compresser", "Erreur de compression", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}});
		
		JMenuItem mntmFermer = new JMenuItem("Fermer");
		mnFichier.add(mntmFermer);
		mntmFermer.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					System.exit(0);
				}});
		
		JMenu parametres = new JMenu("Paramètres");
		menuBar.add(parametres);
		
		checkThread = new StayOpenCBItem("Multi-thread");
		checkThread.setSelected(false);
		parametres.add(checkThread);
		
		JMenu mnArbre = new JMenu("Royaumes concernés");
		parametres.add(mnArbre);
		
		JMenu mnAffichage = new JMenu("Affichage");
		//menuBar.add(mnAffichage);
	
		checkKingdom = new ArrayList<StayOpenCBItem>();
		checkKingdom.add(new StayOpenCBItem("Eukaryotes"));
		checkKingdom.get(0).setSelected(true);
		mnArbre.add(checkKingdom.get(0));
		
		checkKingdom.add(new StayOpenCBItem("Prokaryotes"));
		checkKingdom.get(1).setSelected(true);
		mnArbre.add(checkKingdom.get(1));
		
		checkKingdom.add(new StayOpenCBItem("Virus"));
		checkKingdom.get(2).setSelected(true);
		mnArbre.add(checkKingdom.get(2));
	
		sauvegarder = new StayOpenCBItem("Sauvegarder les séquences");
		sauvegarder.setSelected(false);
		parametres.add(sauvegarder);
		
		JMenu stats = new JMenu("Statistiques");
		radio0 = new JRadioButton("Organisme uniquement");
		radio0.setSelected(true);
		radio1 = new JRadioButton("Statistique fine");
		radio2 = new JRadioButton("Statistique massive");
		ButtonGroup btnGrp = new ButtonGroup();
		btnGrp.add(radio0);
		btnGrp.add(radio1);
		btnGrp.add(radio2);
		parametres.addSeparator();
		
		stats.add(radio0);
		stats.add(radio1);
		stats.add(radio2);
		parametres.add(stats);
		
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
			BufferedImage buttonIcon;
			File file = new File("res/loupe.png");
			if(file.exists())
				buttonIcon = ImageIO.read(file);
			else
				buttonIcon = ImageIO.read(m.getClass().getResource("loupe.png"));
			
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
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent arg0) {
				if(btnLancer.getText().equals("Lancer")){
					if(!multiThread())
						btnLancer.setText("Interrompre");
					//On va lancer la recherche en ligne
					Runnable runner = new Runnable(){
				        public void run() {
				        	actionsBoutonLancer();
				        }
				    };
				    t = new Thread(runner, "Code Executer");
				    t.start();
				}else{
					t.stop();
					frame.setCursor(Cursor.getDefaultCursor());
					progressText("Tâche interrompue");
					progress(100);
					btnLancer.setText("Lancer");
				}
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
}

/**
 * Cette classe, dont la source figure ci-dessous, sert à évider que le menu se ferme quand on coche une case.
 * <br>Par soucis d'honnêteté elle est laissée intacte et telle qu'elle est trouvée sur le site d'origine.
 * @author florentin
 * @see <a href="http://stackoverflow.com/questions/3759379/how-to-prevent-jpopupmenu-disappearing-when-checking-checkboxes-in-it">Origine du code</a>
 */
@SuppressWarnings("serial")
class StayOpenCBItem extends JCheckBoxMenuItem {
    private static MenuElement[] path;

    {
        getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (getModel().isArmed() && isShowing()) {
                    path = MenuSelectionManager.defaultManager().getSelectedPath();
                }
            }
        });
    }

    public StayOpenCBItem(String text) {
        super(text);
    }

    @Override
    public void doClick(int pressTime) {
        super.doClick(pressTime);
        MenuSelectionManager.defaultManager().setSelectedPath(path);
    }
}