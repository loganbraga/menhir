package fr.bragabresolin.menhir.Core.Joueurs;

import java.util.*;
import fr.bragabresolin.menhir.Core.Saison;
import fr.bragabresolin.menhir.Core.Cartes.*;
import fr.bragabresolin.menhir.Core.Joueurs.Comportements.*;

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
	public void setComportementStrategy(ComportementStrategy comportementStrategy) {
		this.comportementStrategy = comportementStrategy;
	}

	public void jouer(ArrayList<Joueur> contexte, boolean partieAvancee, Saison saisonActuelle) {
		this.setChanged();
		this.notifyObservers(this.nom + " joue... ");

		ArrayList<CarteIngredient> cartesIng = new ArrayList<CarteIngredient>();
		for (Carte carte : this.cartes)
			if (!carte.getDejaJouee() && carte instanceof CarteIngredient)
				cartesIng.add((CarteIngredient) carte);
		CarteIngredient carteChoisie = this.comportementStrategy.choisirCarteIngredient(this, saisonActuelle, contexte, cartesIng);
		carteChoisie.executer(saisonActuelle);

		if (partieAvancee) {
			CarteAllie carteAllie = this.choisirJouerAllie(saisonActuelle, contexte);
			if (carteAllie != null) carteAllie.executer(saisonActuelle);
		}
	}

	protected CarteAllie choisirJouerAllie(Saison saisonActuelle, ArrayList<Joueur> contexte) {
		ArrayList<CarteAllie> cartesAllie = new ArrayList<CarteAllie>();
		for (Carte carte : this.cartes)
			if (!carte.getDejaJouee() && carte instanceof CarteAllie)
				cartesAllie.add((CarteAllie) carte);

		if (cartesAllie.size() == 0) return null;

		return this.comportementStrategy.choisirCarteAllie(this, saisonActuelle, contexte, cartesAllie);
	}

	public boolean veutPiocherCarteAllie() {
		// Pour l'instant, le choix de piocher 1 carte allié ou 2 graines est 
		// aléatoire.
		// A l'avenir, on pourra éventuellement déléguer cette décision au 
		// ComportementStrategy du joueur virtuel pour des comportements plus 
		// réfléchis.
		return (Math.random() > 0.5);
	}

	public void jouerDansTourAdverse(ArrayList<Joueur> contexte, Saison saisonActuelle) {
		// Pour l'instant, les joueurs virtuels ne jouent jamais leur taupe
		// pendant le tour des autres.
	}

	public String toString() {
		String str = super.toString();
		return this.nom + " Joueur Virtuel <" + this.comportementStrategy.toString() +"> " + str;
	}
}