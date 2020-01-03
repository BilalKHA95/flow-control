package fr.dauphine.ja.teamdeter.flowControl.message;

import java.io.Serializable;

public abstract class Message implements Serializable {
	private int id_emetteur;

	public Message(int emit) {
		this.id_emetteur = emit;
	}

	public void setEmit(int emit) {
		this.id_emetteur = emit;
	}
}
