package fr.bragabresolin.menhir;

import java.util.*;

public class JoueurVirtuel extends Joueur {

	/*
	 * (non-javadoc)
	 */
	private ComportementStrategy comportementStrategy = null;

	/**
	 * Getter of the property <tt>comportementStrategy</tt>
	 * 
	 * @return Returns the comportementStrategy.
	 * 
	 */

	public ComportementStrategy getComportementStrategy() {
		return comportementStrategy;
	}

	public JoueurVirtuel() {
		super();
		int n = 1 + (int) (Math.random() * ((3 - 1) + 1));
		switch (n) {
			case 1:
				this.comportementStrategy = new TransformateurStrategy();
				break;
			case 2:
				this.comportementStrategy = new VoleurStrategy();
				break;
			case 3:
				this.comportementStrategy = new StockeurStrategy();
				break;
		}
	}

	public JoueurVirtuel(String nom, int age) {
		this();
		this.nom = nom;
		this.age = age;
	}

	/**
	 * Setter of the property <tt>comportementStrategy</tt>
	 * 
	 * @param comportementStrategy
	 *            The comportementStrategy to set.
	 * 
	 */
	public void setComportementStrategy(
			ComportementStrategy comportementStrategy) {
		this.comportementStrategy = comportementStrategy;
			}

	public void jouer(ArrayList<Joueur> contexte, Saison saisonActuelle) {

	}

	protected CarteAllie choisirJouerAllie() {
		return null;
	}

	public boolean veutPiocherCarteAllie() {
		if (Math.random() > 0.5) return true;
		return false;
	}

	public String toString() {
		String str = super.toString();
		return this.nom + " Joueur Virtuel <" + this.comportementStrategy.toString() +"> " + str;
	}
}
