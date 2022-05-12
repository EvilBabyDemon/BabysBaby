package CryptPart;

public class VWA_MainEntschluesseln {

	public static String Viginere(String text, int methodeAbfrage) {

		// Leerzeichen und weitere Sonderzeichen entfernen
		text = VWA_HelperEntschluesseln.textCleaner(text);

		// Zuweisung des Strings zu einem character array
		char[] a2 = VWA_HelperEntschluesseln.myStringToArray(text);

		int[] Friedmanschluessel = new int[10];
		boolean ersterDurchlaufFried = true;

		char[] entschlText = new char[a2.length];

		int[] nennerZahlen = new int[1000];
		int[] nennerZahlenAnzahl = new int[1000];
		int friedmanzaehler = 0;
		int kasiskiZaehler = 0;
		boolean erstenFuenfNenner = true;

		// Abfrage welche Methode man zum Entschlüsseln benützen will

		switch (methodeAbfrage) {
			// FRIEDMANN TEST
			case 1:
				if (ersterDurchlaufFried) {
					Friedmanschluessel = VWA_Schluessellaenge.myFriedamnnTest(a2, Friedmanschluessel);
					ersterDurchlaufFried = false;
				}

				Object[] ausgabeFunktion3 = VWA_Entschluesseln.myFriedmanEntschluesseln(a2, Friedmanschluessel,
						friedmanzaehler);

				char[] entschlFried = new char[(int) ausgabeFunktion3[1]];

				entschlFried = (char[]) ausgabeFunktion3[2];
				entschlText = (char[]) ausgabeFunktion3[4];
				friedmanzaehler = (int) ausgabeFunktion3[5];

				/*
				 * System.out.print("Schlüssel zum Verschlüsseln: ");
				 * System.out.println(entschlFried);
				 * System.out.print("Schlüssel zum Entschlüsseln: ");
				 * System.out.println(verschlFried);
				 */

				String done = "";
				for (int j = 0; j < entschlFried.length; j++) {
					done += entschlFried[j];
				}
				done = " \n";
				for (int j = 0; j < entschlText.length; j++) {
					done += entschlText[j];
				}
				return done;
			// KASISKI TEST
			case 2:
				// Kasiski
				int arrayGroesse = 0;
				// probedurchlauf um zu schauen wie viele Wiederholungen vorkommen
				arrayGroesse = VWA_Schluessellaenge.myKasiskiProbedurchlauf(a2, arrayGroesse);

				// Orte der Folgen, die jeweils hintereinander angegeben werden
				int[] nummerFolgen = new int[(arrayGroesse * 2)];
				// Folgen
				char[][] folgen = new char[arrayGroesse][];

				char[][] folgen300 = new char[300][(a2.length / 2)];
				int[] nummerFolgen300x2 = new int[300 * 2];
				int[] xarr = new int[300];

				char[][] folgenRichtig = new char[300][];

				// richtiger durchlauf um Wiederholungen und deren abstände zu speichern
				Object[] objKasiskiDurchlauf = VWA_Schluessellaenge.myKasiskiDurchlauf(folgen, nummerFolgen, a2);

				folgen = (char[][]) objKasiskiDurchlauf[0];
				nummerFolgen = (int[]) objKasiskiDurchlauf[1];

				// folgen auf die 300 laengsten dezimieren
				Object[] ausgabeFunktion = VWA_Schluessellaenge.FolgenDezimieren(folgen, folgen300, nummerFolgen300x2,
						nummerFolgen, xarr);
				folgen300 = (char[][]) ausgabeFunktion[0];
				nummerFolgen300x2 = (int[]) ausgabeFunktion[1];
				// xarr = (int[]) ausgabeFunktion[2];

				// Folgen dem richtigen Array zuweisen und auf 100 dezimieren
				folgenRichtig = VWA_Schluessellaenge.richtigemArrayFolgenZuweisen(xarr, folgen300, folgenRichtig);

				// Kleinste Nenner der Abstände zählen
				Object[] ausgabeFunktion2 = VWA_Schluessellaenge.zaehlungDerNenner(nennerZahlen, nennerZahlenAnzahl,
						nummerFolgen300x2);

				nennerZahlen = (int[]) ausgabeFunktion2[0];
				nennerZahlenAnzahl = (int[]) ausgabeFunktion2[1];

				// Text entschlüsseln
				Object[] ausgabeFunktionKasiski = VWA_Entschluesseln.myKasiskiEntschluesseln(a2, nennerZahlen,
						kasiskiZaehler, erstenFuenfNenner);

				String finished = "This test didnt work... Pls use the different method.";

				if (ausgabeFunktionKasiski[0] != null) {

					char[] entschlKasiski = new char[(int) ausgabeFunktionKasiski[1]];

					entschlKasiski = (char[]) ausgabeFunktionKasiski[2];
					entschlText = (char[]) ausgabeFunktionKasiski[4];

					for (int j = 0; j < entschlKasiski.length; j++) {
						finished += entschlKasiski[j];
					}

					finished = " \n";
					for (int j = 0; j < entschlText.length; j++) {
						finished += entschlText[j];
					}

				}

				return finished;
		}

		return "";
	}
}
