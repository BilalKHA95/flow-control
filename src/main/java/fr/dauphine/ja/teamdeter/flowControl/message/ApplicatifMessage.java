package fr.dauphine.ja.teamdeter.flowControl.message;

public class ApplicatifMessage extends Message {
	String m_value;

	public ApplicatifMessage(int emit, String value) {
		super(emit);
		this.m_value = value;
	}

	private static final long serialVersionUID = -4173362345380560910L;
}
