package fr.dauphine.ja.teamdeter.flowControl.message;

import java.io.Serializable;

public abstract class Message implements Serializable {
	private int id_emetteur;
	private int m_connaissance ; 

	public Message(int emit) {
		this.id_emetteur = emit;
	}

	public void setEmit(int emit) {
		this.id_emetteur = emit;
	}
	public int getEmit() {
		return this.id_emetteur ; 
	}
	public void setConnaissance(int connai) {
		this.m_connaissance = connai ; 
		
	}
	
	public int getConnaissances() {
		return this.m_connaissance ; 
	}
}
