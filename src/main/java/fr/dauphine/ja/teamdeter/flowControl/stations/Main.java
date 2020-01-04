package fr.dauphine.ja.teamdeter.flowControl.stations;

import java.util.ArrayList;

import fr.dauphine.ja.teamdeter.flowControl.message.ApplicatifMessage;
import fr.dauphine.ja.teamdeter.flowControl.message.Token;

public class Main {
	public static void main(String[] args) {
for(int i= 0 ; i < 500 ; i++) {
			
		}
		
		Producteur t = new Producteur(5, 1, 2);
		Producteur t2 = new Producteur(15, 2, 2);
		ArrayList<Integer>aaaa = new ArrayList<Integer>() ; 
		
		
		aaaa.add(3) ; 
		aaaa.add(4);
		MasterT maestro = new MasterT(10,0,aaaa ) ; 
		Consommateurs tt = new Consommateurs(1,2) ; 
		Consommateurs ttt = new Consommateurs(1,2) ; 
		new Thread(maestro).start();
		Thread aa = new Thread(t) ; 
		Thread aaa = new Thread(t2);
		Thread aaaaa = new Thread(tt);
		Thread aaaaaa = new Thread(ttt);
		aa.start();
		aaa.start();
		aaaaa.start();
		aaaaaa.start();
		Token t1 = new Token(2) ; 
		t1.setVal(10);
		maestro.envoyer_a(0, t1) ; 
		for (int i = 0; i < 50000; i++) {
			t.produire(new ApplicatifMessage(i, Integer.toString(i)));
		}
		
	
		System.out.println(""); 
		
	}
}
