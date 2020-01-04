package fr.dauphine.ja.teamdeter.flowControl.stations;

import fr.dauphine.ja.teamdeter.flowControl.message.ApplicatifMessage;
import fr.dauphine.ja.teamdeter.flowControl.message.Token;

public class Main {
	public static void main(String[] args) {
		
		Producteur t = new Producteur(5, 1, 2);
		Producteur t2 = new Producteur(15, 2, 2);
		MasterT maestro = new MasterT(100,0, null) ; 
		new Thread(maestro).start();
		Thread aa = new Thread(t) ; 
		Thread aaa = new Thread(t2);
		aa.start();
		aaa.start();
		Token t1 = new Token(1) ; 
		t1.setVal(1000);
		t2.envoyer_a(0, t1) ; 
		for (int i = 0; i < 100; i++) {
			t.produire(new ApplicatifMessage(i, Integer.toString(i)));
		}
		
	
		System.out.println(""); 
		
	}
}
