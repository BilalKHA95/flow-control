package fr.dauphine.ja.teamdeter.flowControl.message;

public class Token extends Message {
	private static final long serialVersionUID = 856550991161403161L;
	private int val ; 
	public Token(int emit) {
		super(emit) ; 
		this.val = 0 ; 
	}
	public int getVal() {
		return this.val ; 
	}
	public void setVal(int value) {
		this.val = value ; 
	}
}
