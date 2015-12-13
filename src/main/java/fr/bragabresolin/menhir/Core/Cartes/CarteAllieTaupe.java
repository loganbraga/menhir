package fr.bragabresolin.menhir.Core.Cartes;

import fr.bragabresolin.menhir.Core.Saison;

public class CarteAllieTaupe extends CarteAllie {

	public void executer(Saison saisonActuelle) {
		int forceEffet = this.matriceForces.get(saisonActuelle);
		int nombreMenhirsEnleves = this.cible.diminuerMenhirs(forceEffet);
		this.setChanged();
		this.notifyObservers(this.origine.getNom() + " lance une taupe qui détruit " + nombreMenhirsEnleves + " menhirs au joueur " + this.cible.getNom() + ".");

		super.executer(saisonActuelle);
	}

	public String toString() {
		String template = super.toString();
		template = template.replace("{{_}}", "Taupe");

		return template;
	}
}