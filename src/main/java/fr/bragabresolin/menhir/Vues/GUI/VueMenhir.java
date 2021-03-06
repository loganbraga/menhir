package fr.bragabresolin.menhir.Vues.GUI;

import fr.bragabresolin.menhir.Core.JeuMenhir;
import fr.bragabresolin.menhir.Core.JeuMenhirThread;
import fr.bragabresolin.menhir.Core.Cartes.CarteAllie;
import fr.bragabresolin.menhir.Core.Cartes.CarteAllieChien;
import fr.bragabresolin.menhir.Core.Cartes.CarteAllieTaupe;
import fr.bragabresolin.menhir.Core.Cartes.CarteIngredient;
import fr.bragabresolin.menhir.Core.Message.Message;
import fr.bragabresolin.menhir.Core.Partie.Manche;
import fr.bragabresolin.menhir.Core.Joueurs.*;
import fr.bragabresolin.menhir.Vues.Vue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

/**
 * Interface graphique complète pour le jeu du Menhir.
 * Cette vue graphique encapsule un ensemble d'autres composants graphiques du 
 * même paquet afin d'afficher indépendamment les informations des joueurs, des 
 * cartes et le déroulement du jeu.
 * Cette classe utilise de façon extensive le layout Swing non officiel 
 * MigLayout.
 *
 *
 * @author  Logan Braga
 * @author  Simon Bresolin
 * @see fr.bragabresolin.menhir.Core.JeuMenhir
 * @see fr.bragabresolin.menhir.Vues.Vue
 * @see fr.bragabresolin.menhir.Vues.GUI.VueCarteAllie
 * @see fr.bragabresolin.menhir.Vues.GUI.VueCarteIngredient
 * @see fr.bragabresolin.menhir.Vues.GUI.VueJoueur
 * @see fr.bragabresolin.menhir.Vues.GUI.VueMainJoueur
 * @see fr.bragabresolin.menhir.Vues.GUI.BlackTheme
 */
public class VueMenhir implements Vue, BlackTheme {
	
	/**
	 * Référence vers le jeu que la vue observe.
	 * 
	 * Une valeur nulle signifie que le jeu n'a pas encore été créé ; c'est 
	 * typiquement le cas au début de l'application, lorsque l'on demande 
	 * d'abord les informations nécessaires aux réglages du jeu à l'utilisateur.
	 * 
	 * @see fr.bragabresolin.menhir.Core.JeuMenhir
	 */
	private JeuMenhir jeu;
	
	/**
	 * Référence vers la frame principale de la fenêtre.
	 */
	private JFrame frame;

	/**
	 * Référence vers le panneau contenant les vues des joueurs de la partie.
	 */
	private JPanel panelJoueurs;

	/**
	 * Liste contenant toutes les vues des joueurs ajoutées au panneau des 
	 * joueurs.
	 * 
	 * Cette référence est nécessaire notamment afin de pouvoir 
	 * ajouter/supprimer les observateurs des joueurs avant la re-création de 
	 * nouvelles vues de joueurs pour éviter d'avoir des observateurs fantômes.
	 * 
	 * @see fr.bragabresolin.menhir.Vues.GUI.VueJoueur
	 */
	private LinkedList<VueJoueur> vuesJoueurs;

	/**
	 * Référence vers le panneau contenant les vues de la main du joueur.
	 * 
	 * @see fr.bragabresolin.menhir.Vues.GUI.VueMainJoueur
	 */
	private JPanel panelCartes;

	/**
	 * Référence vers le panneau contenant les informations de suivi des actions 
	 * de la partie.
	 *
	 * TODO: pas besoin de le garder en attribut
	 */
	private JPanel panelSuiviEffets;
	
	/**
	 * Label affiché contenant les dernières actions effectuées 
	 * (par exemple, "Joueur X gagne Y graines") par les joueurs.
	 */
	private JLabel lblSuiviEffets;

	/**
	 * Label affiché contenant le numéro de manche active.
	 */
	private JLabel lblManche;

	/**
	 * Label affiché contenant le nom de la saison en cours de jeu.
	 */
	private JLabel lblSaisonencours;

	/**
	 * Label affiché contenant les informations générales de la partie 
	 * (changement de manche/saison, tour de tel ou tel joueur, fin de partie, 
	 * etc...)
	 */
	private JLabel lblInformations;
	
	/**
	 * Représente le numéro de manche actuellement jouée.
	 * 
	 * TODO: à déprécier ; devrait être gardé dans le jeu
	 */
	private int mancheActuelle;


	/**
	 * Constructeur de la vue principale du jeu.
	 * 
	 * On règle uniquement la fenêtre (taille, icône, thème, etc...).
	 */
	public VueMenhir() {
		this.mancheActuelle = 0;
		
		this.vuesJoueurs = new LinkedList<VueJoueur>();

		frame = new JFrame();
		frame.setIconImage(frame.getToolkit().getImage(getClass().getResource("/images/ico.png")));
		frame.setTitle("Jeu du Menhir - Braga & Bresolin");
		frame.setResizable(true);
		frame.getContentPane().setBackground(DARK_BG);
		frame.setBackground(DARK_BG);
		frame.setBounds(100, 100, WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		UIManager.put("OptionPane.background", DARK_BG);
		UIManager.put("Panel.background",DARK_BG);
		UIManager.put("OptionPane.messageForeground",LIGHT_FG);
    }

	/**
	 * Initialise globalement la vue.
	 * Les panels sont réinitialisés puis créés.
	 *
	 * Les joueurs et les cartes se voient attacher des écouteurs d'événement 
	 * supplémentaires afin de disposer d'informations à afficher dans les 
	 * barres de statut de l'interface.
	 * 
	 * Cette méthode réinitialise l'état de la vue à chaque appel ; elle peut 
	 * donc être utilisée pour lancer une nouvelle partie après une partie.
	 * 
	 * @see fr.bragabresolin.menhir.Vues.GUI.VueCarteAllie
	 * @see fr.bragabresolin.menhir.Vues.GUI.VueCarteIngredient
	 * @see fr.bragabresolin.menhir.Vues.GUI.VueJoueur
	 * @see fr.bragabresolin.menhir.Vues.GUI.VueMainJoueur
	 * @see fr.bragabresolin.menhir.Vues.GUI.BlackTheme
	 */
	private void initialize() {
		frame.getContentPane().removeAll();
		frame.getContentPane().setLayout(new MigLayout("", "0[100%,grow]0", "0[50px,fill]0[35px,fill]0[40%-20px,grow]0[60%-65px,grow]0"));

		this.mancheActuelle = 0;
		
		JPanel panel = new JPanel();
		panel.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));
		panel.setBackground(ACCENT_1);
		frame.getContentPane().add(panel, "cell 0 0,growx,aligny top");
		panel.setLayout(new MigLayout("", "20[10%]10[80%]10[10%]20", "[100%]"));

		this.lblSaisonencours = new JLabel("Saison".toUpperCase());
		this.lblSaisonencours.setToolTipText("Saison en cours");
		this.lblSaisonencours.setFont(TITLE_FONT);
		this.lblSaisonencours.setForeground(LIGHT_FG);
		panel.add(this.lblSaisonencours, "cell 0 0,alignx left,growy");

		this.lblInformations = new JLabel("<...>");
		this.lblInformations.setFont(UIManager.getFont("Table.font"));
		this.lblInformations.setForeground(LIGHT_FG);
		panel.add(this.lblInformations, "cell 1 0,alignx center,aligny center");

		this.lblManche = new JLabel("MANCHE x");
		this.lblManche.setToolTipText("Manche en cours");
		this.lblManche.setForeground(LIGHT_FG);
		this.lblManche.setFont(TITLE_FONT);
		panel.add(this.lblManche, "cell 2 0,alignx right,aligny center");
		
		this.panelJoueurs = new JPanel();
		this.panelJoueurs.setBackground(DARK_BG);
		frame.getContentPane().add(this.panelJoueurs, "cell 0 2,grow");
		this.panelJoueurs.setLayout(new GridLayout(1, 6, 0, 0));
		this.remplirPanelJoueurs();
		
		this.panelSuiviEffets = new JPanel();
		this.panelSuiviEffets.setBackground(ACCENT_2);
		this.panelSuiviEffets.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));
		frame.getContentPane().add(this.panelSuiviEffets, "cell 0 1,grow");
		this.lblSuiviEffets = new JLabel("En attente d'actions à afficher");
		this.lblSuiviEffets.setForeground(ACCENT_FG);
		this.lblSuiviEffets.setFont(DEFAULT_FONT);
		this.lblSuiviEffets.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblSuiviEffets.setBorder(new MatteBorder(4, 0, 0, 0, ACCENT_2)); // padding
		this.panelSuiviEffets.add(this.lblSuiviEffets);

		this.panelCartes = new VueMainJoueur(this.jeu);
		this.frame.getContentPane().add(this.panelCartes, "cell 0 3,grow");
		
		// On surveille le jeu afin de savoir quand démarrer/finir l'affichage 
		// selon le début/fin de partie.
		this.jeu.addObserver(new Observer() {
			public void update(Observable o, Object message) {
				switch (((Message) message).getType()) {
				case DEBUT_PARTIE:
					VueMenhir.this.initTopPanel();
					break;
				case FIN_PARTIE:
					VueMenhir.this.afficherClassement();
					break;
				default:
					break;
				}
			}
		});
		
		// On surveille les cartes ingrédient pour savoir quand elles sont 
		// exécutées afin d'afficher leur effet dans le panneau d'informations
		Iterator<CarteIngredient> itc = this.jeu.getTasIng().iterator();
		while (itc.hasNext()) {
			CarteIngredient carte = itc.next();
			carte.addObserver(new Observer() {
				public void update(Observable o, Object message) {
					if (message instanceof Message) {
						Message mes = (Message) message;
						switch (mes.getType()) {
						case CARTE_EXEC:
							CarteIngredient c = (CarteIngredient) o;
							switch (c.getAction()) {
							case GEANT:
								VueMenhir.this.lblSuiviEffets.setText(c.getOrigine().getNom() + " récupère " + (Integer) mes.getBody() + " graines.");
								break;
							case ENGRAIS:
								VueMenhir.this.lblSuiviEffets.setText(c.getOrigine().getNom() + " fait pousser " + (Integer) mes.getBody() + " menhirs.");
								break;
							case FARFADET:
								VueMenhir.this.lblSuiviEffets.setText(c.getOrigine().getNom() + " vole " + (Integer) mes.getBody() + " graines à " + c.getCible().getNom() + ".");
								break;
							}
							break;
						default:
							break;
						}
					}
				}
			});
		}
		
		// On surveille les cartes allié afin de savoir quand elles sont 
		// exécutées pour afficher leur effet dans le panneau d'informations
		Iterator<CarteAllie> itca = this.jeu.getTasAllie().iterator();
		while(itca.hasNext()) {
			CarteAllie carte = itca.next();
			carte.addObserver(new Observer() {
				public void update(Observable o, Object message) {
					if (message instanceof Message) {
						Message mes = (Message) message;
						switch (mes.getType()) {
						case CARTE_EXEC:
							CarteAllie c = (CarteAllie) o;
							if (c instanceof CarteAllieChien) {
								VueMenhir.this.lblSuiviEffets.setText(c.getOrigine().getNom() + " appelle un chien qui protège " + (Integer) mes.getBody() + " graines.");
							} else if (c instanceof CarteAllieTaupe) {
								VueMenhir.this.lblSuiviEffets.setText(c.getOrigine().getNom() + " appelle une taupe géante qui détruit " + (Integer) mes.getBody() + " menhirs de " + c.getCible().getNom());
							}
						default:
							break;
						}
					}
				}
			});
		}
		
		// On surveille les joueurs afin de mettre à jour le panneau 
		// d'informations lorsqu'un joueur doit jouer
		Iterator<Joueur> itj = this.jeu.getJoueurs().iterator();
		while (itj.hasNext()) {
			itj.next().addObserver(new Observer() {
				public void update(Observable o, Object message) {
					if (message instanceof Message) {
						switch (((Message) message).getType()) {
						case JOUEUR_DEBUT_TOUR:
							Joueur j = (Joueur) o;
							VueMenhir.this.lblInformations.setText("Tour de " + j.getNom());
							break;
						default:
							break;
						}
					}
				}
			});
		}
		
		this.frame.pack();
	}
	
	/**
	 * Affiche le classement des joueurs en fin de partie.
	 * 
	 * Cette méthode supprime les informations inutiles en fin de partie, et 
	 * affiche les joueurs dans l'ordre de fin de partie fourni par le jeu.
	 * Un bouton permettant de Rejouer est inséré. Il permet, au clic, de 
	 * relancer une nouvelle partie avec de nouveaux réglages.
	 */
	private void afficherClassement() {
		this.panelCartes.removeAll();
		this.panelCartes.setLayout(new BorderLayout());
		
		this.lblManche.setText("");
		this.lblSaisonencours.setText("");
		this.lblInformations.setText("Partie terminée !");
		this.lblSuiviEffets.setText("Voici le classement :");
		
		this.remplirPanelJoueurs();
		
		JButton boutonRejouer = new JButton("REJOUER");
		boutonRejouer.setBackground(DARK_BG);
		boutonRejouer.setForeground(LIGHT_FG);
		boutonRejouer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				VueMenhir.this.demarrerJeu();
			}
		});
		this.panelCartes.add(boutonRejouer, BorderLayout.CENTER);
		
		this.frame.pack();
	}
	
	/**
	 * Initialise l'affichage de la barre d'informations (haut de la fenêtre).
	 * 
	 * Cette méthode est appelée lorsque la partie a été initialisée et démarrée
	 * car on a besoin de récupérer les informations de la manche.
	 */
	private void initTopPanel() {
		lblSaisonencours.setText(this.jeu.getMancheEnCours().getSaisonEnCours().toString());
		this.jeu.getMancheEnCours().addObserver(new Observer() {
			public void update(Observable o, Object message) {
				Message mes = (Message) message;
				switch (mes.getType()) {
				case DEBUT_SAISON:
					lblSaisonencours.setText(((Manche) o).getSaisonEnCours().toString());
					lblInformations.setText("Changement de saison !");
					break;
				case DEBUT_MANCHE:
					remplirPanelJoueurs();
					mancheActuelle++;
					lblManche.setText("Manche " + mancheActuelle);
					lblInformations.setText("Changement de manche !");
				default:
				}
			}
		});
	}
	
	/**
	 * Remplit le panel des joueurs avec les joueurs de la partie.
	 * 
	 * Cette méthode est appelée une fois que le jeu a terminé d'initialiser les 
	 * joueurs. On ajoute successivement, dans l'ordre fourni par le jeu, les 
	 * joueurs au panel.
	 * 
	 * @see fr.bragabresolin.menhir.Core.Joueurs.Joueur
	 * @see fr.bragabresolin.menhir.Core.Vues.GUI.VueJoueur
	 */
	private void remplirPanelJoueurs() {
		Iterator<VueJoueur> itv = this.vuesJoueurs.iterator();
		while (itv.hasNext()) {
			itv.next().nettoyer();
		}
		this.panelJoueurs.removeAll();
		Iterator<Joueur> it = this.jeu.getJoueurs().iterator();
		while (it.hasNext()) {
			Joueur j = it.next();
			VueJoueur vueJoueur = new VueJoueur(j);
			this.vuesJoueurs.add(vueJoueur);
			if (it.hasNext()) {
				vueJoueur.setBorder(new MatteBorder(0, 0, 0, 1, (Color) BORDER_COLOR));
			}
			this.panelJoueurs.add(vueJoueur);
		}
	}
	
	/**
	 * Demande à l'utilisateur de confirmer le démarrage du jeu.
	 * Choisir "Annuler" ferme le jeu.
	 */
	private void confirmerDemarrage() {
		String texte = "Bienvenue dans le Jeu du Menhir !" + "\n" +
					   "La partie va démarrer !";
		int result = JOptionPane.showConfirmDialog(this.frame, texte, "Bienvenue - Jeu du Menhir", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if (result != 0) {
			System.exit(0);
		}
	}
	
	/**
	 * Demande à l'utilisateur son nom.
	 * 
	 * Tant que le résultat saisi est vide, la question est re-posée. Choisir 
	 * "Annuler" ferme le jeu.
	 * 
	 * @return La chaîne lue
	 */
	private String demanderNom() {
		String nomJoueur = null;
		while (nomJoueur == null || (nomJoueur != null && nomJoueur.equals(""))) {
			nomJoueur = (String) JOptionPane.showInputDialog(
	                this.frame,
	                "Quel est votre nom ?",
	                "Choix du nom - Menhir",
	                JOptionPane.PLAIN_MESSAGE,
	                null,
	                null,
	                null);
			if (nomJoueur == null) System.exit(0);
		}
		return nomJoueur;
	}
	
	/**
	 * Demande à l'utilisateur s'il veut jouer en partie avancée ou simple.
	 * 
	 * @return Vrai si l'utilisateur veut jouer en mode avancé
	 */
	private boolean demanderModePartie() {
		String texte = "Voulez-vous jouer en partie avancée ?";
		int result = JOptionPane.showConfirmDialog(this.frame, texte, "Choix du mode de jeu - Menhir", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
		return result == 0;
	}
	
	/**
	 * Demande à l'utilisateur son âge.
	 * 
	 * Tant que le résultat est incorrect, la question est re-posée. Choisir 
	 * "Annuler" ferme le jeu.
	 * Si l'âge donné est en dehors des bornes autorisées par les règles, le jeu 
	 * se ferme avec une fenêtre d'erreur.
	 * 
	 * @return L'entier lu de l'âge
	 */
	private int demanderAge() {
		int age = 0;
		while (age < 1) {
			String reponse = (String) JOptionPane.showInputDialog(
	                this.frame,
	                "Quel âge avez-vous ?",
	                "Choix de l'âge - Menhir",
	                JOptionPane.PLAIN_MESSAGE,
	                null,
	                null,
	                null);
			if (reponse == null) System.exit(0);
			try {
				age = Integer.parseInt(reponse, 10);
			} catch (NumberFormatException e) {
				age = 0;
			}
		}
		if (age < 8) {
			JOptionPane.showMessageDialog(this.frame, "Vous êtes trop jeune pour jouer (- de 8 ans) !", "Joueur trop jeune", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		return age;
	}
	
	/**
	 * Demande à l'utilisateur le nombre de joueurs virtuels avec qui il veut 
	 * jouer.
	 * 
	 * Tant que le résultat est incorrect, la question est re-posée. Choisir 
	 * "Annuler" ferme le jeu.
	 * 
	 * @return L'entier lu
	 */
	private int demanderNombreJoueurs() {
		int nombreJoueurs = 0;
		while (nombreJoueurs < 1 || nombreJoueurs > 5) {
			String reponse = (String) JOptionPane.showInputDialog(
	                this.frame,
	                "Avec combien de joueurs voulez-vous jouer ?" + "\n" + "Choisissez un nombre entre 1 et 5 compris.",
	                "Choix du nombre de joueurs - Menhir",
	                JOptionPane.PLAIN_MESSAGE,
	                null,
	                null,
	                null);
			if (reponse == null) System.exit(0);
			try {
				nombreJoueurs = Integer.parseInt(reponse, 10);
			} catch (NumberFormatException e) {
				nombreJoueurs = 0;
			}
		}
		return nombreJoueurs;
	}
	
	/**
	 * Démarre le jeu en posant toutes les questions de configuration à 
	 * l'utilisateur pour initialiser l'interface graphique et la partie.
	 * 
	 * On lance le jeu du Menhir dans un thread séparé.
	 * 
	 * @see fr.bragabresolin.menhir.Core.JeuMenhirThread
	 */
	private void demarrerJeu() {
		String nomJoueur = this.demanderNom();
		int ageJoueur = this.demanderAge();
		int nombreJoueurs = this.demanderNombreJoueurs();
		boolean partieAvancee = this.demanderModePartie();
		this.jeu = new JeuMenhirThread(nombreJoueurs, nomJoueur, ageJoueur, partieAvancee);
		this.initialize();
		this.jeu.lancerPartie();
	}
	
	/**
	 * Affiche le splashscreen (écran de garde) du jeu du Menhir.
	 * 
	 * On enlève tout contenu déjà présent dans la fenêtre, afin d'afficher 
	 * uniquement l'image de garde du jeu, au centre de la fenêtre.
	 * La taille précédente n'est pas changée.
	 */
	private void afficherSplashScreen() {
		frame.getContentPane().removeAll();
		JLabel splash = new JLabel("");
		splash.setIcon(new ImageIcon(VueMenhir.class.getResource("/images/splash.png")));
		frame.getContentPane().add(splash, BorderLayout.CENTER);
	}

	/**
	 * Lance et affiche l'interface graphique du jeu.
	 * 
	 * On affiche le splashscreen, puis on démarre.
	 */
	public void lancer() {
		this.afficherSplashScreen();
		this.frame.setVisible(true);
		
		this.confirmerDemarrage();
		this.demarrerJeu();
	}
}
