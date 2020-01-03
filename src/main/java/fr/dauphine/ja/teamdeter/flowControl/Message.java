package fr.dauphine.ja.teamdeter.flowControl;

import java.io.Serializable;

public abstract class Message implements Serializable  {
	private int id_emetteur ; 
	public Message(int emit) {
		this.id_emetteur = emit ; 
		
	}
	
	
}
