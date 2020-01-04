package fr.dauphine.ja.teamdeter.flowControl.stations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import fr.dauphine.ja.teamdeter.flowControl.message.ApplicatifMessage;

public class Main {
	public static int nbProd = 5;
	private static int nbCons = 20;
	public static long timeProcessConsommateurs = 1;
	public static long timeProcessProducteur = 100;
	private static int tTamponSize = 2;
	private static int prodTampon = 5;
	private static int consTampon = 2;
	private static int nbMaxTache = 20;
	private static long timeBetweenTaskLauncher = 10;

	public static void main(String[] args) {
		java.util.Random mon_rand = new Random();
		ArrayList<Integer> conso = new ArrayList<Integer>();
		ArrayList<Producteur> prods = new ArrayList<Producteur>();
		int idMaestro = nbProd + nbCons;
		for (int i = 0; i < Main.nbProd; i++) {
			if (i + 1 < Main.nbProd) {
				Producteur a = new Producteur(prodTampon, i + 1, idMaestro);
				prods.add(a);
				new Thread(a).start();
			} else {
				Producteur a = new Producteur(prodTampon, idMaestro, idMaestro);
				prods.add(a);
				new Thread(a).start();
			}
		}
		for (int i = 0; i < Main.nbCons; i++) {
			Consommateurs a = new Consommateurs(consTampon, idMaestro);
			new Thread(a).start();
			conso.add(a.getId());
		}
		MasterT maestro = new MasterT(tTamponSize, 0, conso);
		new Thread(maestro).start();
		File file = new File("ressource\\test.txt");
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String st;
			try {
				while ((st = br.readLine()) != null) {
					String[] tab = st.split(" ") ; 
					for (String mot : tab) {
						prods.get(0).produire(new ApplicatifMessage(prods.get(0).getId(), mot));
						
					}
					System.out.println(); 

					
				
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * while (true) { int index = mon_rand.nextInt(prods.size()); int nbProd =
		 * mon_rand.nextInt(nbMaxTache); for (int i = 0; i < nbProd; i++) {
		 * prods.get(index).produire(new ApplicatifMessage(prods.get(index).getId(),
		 * "test")); } try { Thread.sleep(timeBetweenTaskLauncher); } catch
		 * (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 */
	}
}
