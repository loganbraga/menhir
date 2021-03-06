package fr.bragabresolin.menhir.Core.Message;

/**
 * Enumération représentant les différents types de messages qu'un producteur de 
 * message peut vouloir envouyer à ses observateurs.
 *
 * @author  Logan Braga
 * @author  Simon Bresolin
 * @see fr.bragabresolin.menhir.Core.Message.Message
 */
public enum MessageType {
	// Messages de la partie
	DEBUT_PARTIE, FIN_PARTIE, DEBUT_MANCHE, FIN_MANCHE, DEBUT_SAISON, FIN_SAISON,

	// Messages des cartes
	TAS_MELANGE, CARTE_EXEC, CARTE_SET_CIBLE,
	
	// Messages des joueurs
	JOUEUR_DEBUT_TOUR, JOUEUR_FIN_TOUR, JOUEUR_PIOCHE_CARTE, JOUEUR_PIOCHE_GRAINE, JOUEUR_JOUE_CARTE, JOUEUR_CHOIX_JOUER_ING, JOUEUR_CHOIX_JOUER_ALLIE, JOUEUR_CHOIX_PIOCHER_ALLIE,
	JOUEUR_PERDS_GRAINES, JOUEUR_GAGNE_MENHIRS, JOUEUR_PERDS_MENHIR,
	JOUEUR_RESET_CHAMP, JOUEUR_REND_CARTES
}
