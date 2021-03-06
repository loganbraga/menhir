package fr.bragabresolin.menhir.Core.Message;

import fr.bragabresolin.menhir.Core.Cartes.Carte;

/**
 * Classe servant de tampon (buffer) de stockage d'une carte de jeu,
 * généralement pour échanger des informations entre le joueur et le coeur du 
 * jeu.
 * Cette classe fonctionne à la manière d'une "boîte aux lettres" synchronisée.
 * Cette classe implémente le patron de conception Singleton.
 *
 * @author  Logan Braga
 * @author  Simon Bresolin
 */
public class TamponCarte {

	/**
	 * Représente la carte stockée dans le tampon.
	 * Elle peut être lue et écrite de manière synchrone.
	 *
	 * Une valeur nulle signifie qu'aucune donnée n'est disponible.
	 * 
	 * @see fr.bragabresolin.menhir.Core.Cartes.Carte
	 */
	private Carte carte;

	/**
	 * Attribut utilisé pour indiquer que la présence ou non d'une carte dans le 
	 * tampon peut être ignorée.
	 * Cet attribut est utilisé pour indiquer la non disponibilité définitive 
	 * d'une donnée pour un besoin particulier, étant donné que la valeur nulle 
	 * pour l'attribut carte signifie qu'une donnée n'est pas encore disponible,
	 * mais peut l'être prochainement.
	 */
	private boolean ignorer;
	
	/**
	 * Attribut statique contenant l'unique instance de la classe (patron de 
	 * conception Singleton).
	 *
	 * Une valeur nulle est possible et signifie que la classe n'a pas encore 
	 * été utilisée, mais la nullité de cette valeur n'est jamais apparente à 
	 * l'extérieur de la classe.
	 */
	private static TamponCarte instance;
	
	/**
	 * Point d'accès à l'instance unique (patron Singleton) du tampon.
	 *
	 * @return L'unique instance du TamponCarte
	 */
	public static TamponCarte getInstance() {
		if (TamponCarte.instance == null)
			TamponCarte.instance = new TamponCarte();
		return TamponCarte.instance;
	}
	
	/**
	 * Constructeur privé (patron Singleton).
	 *
	 * Crée le TamponBooleen en initialisant l'attribut de données comme étant 
	 * vide.
	 * @see fr.bragabresolin.menhir.Core.Cartes.Carte
	 */
	private TamponCarte() {
		this.carte = null;
	}
	
	/**
	 * Mutateur pour le mode ignorer du tampon.
	 *
	 * Un tampon en mode ignorer ne se synchronise pas et renvoit ses données, 
	 * même vides.
	 */
	public synchronized void setIgnorer(boolean ignorer) {
		this.ignorer = ignorer;
		this.notifyAll();
	}
	
	/**
	 * Renvoit la carte stockée dans le tampon.
	 *
	 * Le thread appelant est mis en attente si nécessaire jusqu'à ce qu'une 
	 * donnée soit disponible dans le tampon, auquel cas elle est renvoyée et le 
	 * tampon est nettoyé.
	 * Si le tampon a été mis en mode ignorer, la carte est retournée telle 
	 * qu'elle (très probablement null).
	 *
	 * @return La carte stockée
	 * @see fr.bragabresolin.menhir.Core.Cartes.Carte
	 */
	public synchronized Carte recupererCarte() {
		while (this.carte == null && !this.ignorer) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Carte carteAJouer = this.carte;
		carte = null;
		this.ignorer = false;
		
		return carteAJouer;
	}
	
	/**
	 * Stocke une carte dans le tampon.
	 *
	 * Le thread appelant est mis en attente si nécessaire jusqu'à ce que le 
	 * tampon soit vide, auquel cas les threads en attente du tampon sont 
	 * notifiés.
	 *
	 * @param carte La carte à enregistrer
	 * @see fr.bragabresolin.menhir.Core.Cartes.Carte
	 */
	public synchronized void deposerCarte(Carte carte) {
		while (this.carte != null) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		this.carte = carte;
		this.notifyAll();
	}
}
