package fr.dauphine.ja.teamdeter.flowControl.stations;

import fr.dauphine.ja.teamdeter.flowControl.message.ApplicatifMessage;

public class MasterT extends Station {
	private ApplicatifMessage[] tampon;
	private int m_nbcell;//nbr de mess envoy�s � un consommateur entre 2 passages du jeton 
	private int m_nbmess;//nbr mess stock�s dans tampon pas encore envoy�s � un consommateur 
	private int m_in;
	private int m_out;
	
	public void run() {
		
		
	}
	





}
