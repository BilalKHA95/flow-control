package fr.dauphine.ja.teamdeter.flowControl.message;

public class Requests extends Message {
	
	private static final long serialVersionUID = 529590426157751951L;
	private RequestEnum m_statut ; 
	
	public Requests(int emit) {
		super(emit) ; 
		this.m_statut = RequestEnum.req ; 
	}
	public RequestEnum getStatus() {
		return this.m_statut ; 
	}
	public void setStatus(RequestEnum value) {
		this.m_statut = value ; 
	}
	
	
	
}
