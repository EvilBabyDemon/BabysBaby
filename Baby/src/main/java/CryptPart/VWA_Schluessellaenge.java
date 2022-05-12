package CryptPart;

public class VWA_Schluessellaenge {

	/*
	 * probedurchlauf um zu schauen wie viele Wiederholungen vorkommen (f�r den
	 * Friedmanntest)
	 * 
	 * @param a2
	 * 
	 * @return die gr��e f�r das Array,wo die Orte der Folgen gespeichert
	 * werden.
	 */
	public static int myKasiskiProbedurchlauf(char[] a2, int arrayGroesse) {

		for (int i = 0; i < (a2.length - 2); i++) {
			for (int j = 0; j < (a2.length - 2); j++) {
				if (i == j) {
					continue;
				}
				for (int k = 0; k < a2.length; k++) {
					if ((i + k) >= a2.length || (j + k) >= a2.length) {
						k--;
						if (k > 1) {
							arrayGroesse++;
							i = i + k;
						}
						break;
					}
					if (a2[(i + k)] == a2[(j + k)]) {
					} else {
						k--;
						if (k > 1) {
							arrayGroesse++;
							i = i + k;
						}
						break;
					}
				}
			}
		}

		return (arrayGroesse);
	}

	/*
	 * richtiger durchlauf um die Wiederholungen und deren abst�nde zu speichern
	 * 
	 * @param wiederholungen
	 * 
	 */
	public static Object[] myKasiskiDurchlauf(char[][] folgen, int[] nummerFolgen, char[] a2) {

		int folgenzaehler = 0;
		int zaehler2 = 0;

		for (int i = 0; i < (a2.length - 2); i++) {
			for (int j = 0; j < (a2.length - 2); j++) {
				if (i == j) {
					continue;
				}
				for (int k = 0; k < a2.length; k++) {

					if ((i + k) >= a2.length || (j + k) >= a2.length) {
						k--;
						if (k > 1) {

							nummerFolgen[zaehler2] = i;
							zaehler2++;
							nummerFolgen[zaehler2] = j;
							zaehler2++;

							folgen[folgenzaehler] = new char[(k + 1)]; // <-- wofuer ist das gut?

							for (int b = 0; b <= k; b++) {
								folgen[folgenzaehler][b] = a2[(i + b)];
							}

							folgenzaehler++;

							i = i + k;

						}
						break;
					}

					if (a2[(i + k)] == a2[(j + k)]) {
					} else {
						k--;
						if (k > 1) {

							nummerFolgen[zaehler2] = i;
							zaehler2++;
							nummerFolgen[zaehler2] = j;
							zaehler2++;

							folgen[folgenzaehler] = new char[(k + 1)];

							for (int b = 0; b <= k; b++) {
								folgen[folgenzaehler][b] = a2[(i + b)];
							}

							folgenzaehler++;

							i = i + k;

						}
						break;
					}
				}
			}
		}

		return new Object[] { folgen, nummerFolgen };

	}

	public static char[][] richtigemArrayFolgenZuweisen(int[] xarr, char[][] folgen300, char[][] folgenRichtig) {

		for (int i = 0; i < folgenRichtig.length; i++) {
			folgenRichtig[i] = new char[xarr[i]];
		}

		for (int i = 0; i < folgenRichtig.length; i++) {
			for (int b = 0; b < folgenRichtig[i].length; b++) {
				folgenRichtig[i][b] = folgen300[i][b];
			}
		}

		return folgenRichtig;
	}

	public static Object[] FolgenDezimieren(char[][] folgen, char[][] folgen300, int[] nummerFolgen300x2,
			int[] nummerFolgen,
			int[] xarr) {

		int nummerz100 = 0;
		int[] loescher = new int[300];
		boolean loosch = false;

		for (int i = 0; i < loescher.length; i++) {
			loescher[i] = -1;
		}

		for (int i = 0; i < folgen300.length; i++) {
			int testx = 0;
			for (int e = 0; e < folgen.length; e++) {
				for (int loez = 0; loez <= i; loez++) {
					if (e == loescher[loez]) {
						loosch = true;
					}
				}
				if (loosch) {
					loosch = false;
					continue;
				}

				if (testx < folgen[e].length) {
					if (folgen[e].length > folgen300[0].length) {
						continue;
					}
					testx = folgen[e].length;
					xarr[i] = testx;
					for (int b = 0; b < folgen[e].length; b++) {
						folgen300[i][b] = folgen[e][b];
					}
					nummerFolgen300x2[nummerz100] = nummerFolgen[nummerz100];
					nummerFolgen300x2[nummerz100 + 1] = nummerFolgen[nummerz100 + 1];
					loescher[i] = e;
				}
			}
			nummerz100 += 2;
		}

		return new Object[] { folgen300, nummerFolgen300x2 };

	}

	public static Object[] zaehlungDerNenner(int[] nennerZahlen, int[] nennerZahlenAnzahl, int[] nummerFolgen300x2) {

		int abstand = 0;
		int nennerAnzahl = 0;
		boolean gleich = false;

		for (int laufvariable = 0; laufvariable < (nummerFolgen300x2.length - 1); laufvariable += 2) {

			abstand = (nummerFolgen300x2[laufvariable] - nummerFolgen300x2[(laufvariable + 1)]);

			if (abstand < 0) {
				abstand = abstand * (-1);
			}

			for (int nenner = 2; nenner <= abstand; nenner++) {
				if ((abstand % nenner) == 0) {
					gleich = false;
					for (int i = 0; i < nennerAnzahl; i++) {
						if (nennerZahlen[i] == nenner) {
							nennerZahlenAnzahl[i]++;
							abstand = abstand / nenner;
							nenner = 1;
							gleich = true;
							;
							break;
						}
					}
					if (gleich) {
						continue;
					}
					nennerZahlen[nennerAnzahl] = nenner;
					nennerZahlenAnzahl[nennerAnzahl] += 1;
					abstand = abstand / nenner;
					nenner = 1;
					nennerAnzahl++;

					if (nennerAnzahl > 999) {
						break;
					}
				}
			}
		}

		for (int i = 0; i < nennerAnzahl; i++) {
			for (int j = 1; j < nennerAnzahl; j++) {
				if (nennerZahlenAnzahl[(j - 1)] < nennerZahlenAnzahl[j]) {

					int tmp = nennerZahlenAnzahl[j];
					nennerZahlenAnzahl[j] = nennerZahlenAnzahl[(j - 1)];
					nennerZahlenAnzahl[(j - 1)] = tmp;

					tmp = nennerZahlen[j];
					nennerZahlen[j] = nennerZahlen[(j - 1)];
					nennerZahlen[(j - 1)] = tmp;
				}
			}
		}

		return new Object[] { nennerZahlen, nennerZahlenAnzahl };

	}

	public static int[] myFriedamnnTest(char[] a2, int[] Friedmannschluessel) {

		double koinzWerte[][] = new double[a2.length / 10][];

		int schluesselLaenge = 2;

		for (int i = 2; i < koinzWerte.length; i++) {
			koinzWerte[i] = new double[schluesselLaenge];
			schluesselLaenge++;
		}

		for (int i = 2; i < koinzWerte.length; i++) { // dividiert durch 10 da man unter 10 buchstaben nicht wirklich e
														// finden kann
			int[] buchstabe = new int[26];

			for (int k = 0; k < koinzWerte[i].length; k++) {

				int resteFuerLaenge = 0;
				int[] laengenFuerRechnung = new int[koinzWerte[i].length];

				resteFuerLaenge = a2.length % i;
				int laengeSchluesselTeil = (a2.length - resteFuerLaenge) / i;

				for (int j = 0; j < laengenFuerRechnung.length; j++) {
					if (resteFuerLaenge > 0) {
						laengenFuerRechnung[j] = laengeSchluesselTeil + 1;
						resteFuerLaenge--;
					} else {
						laengenFuerRechnung[j] = laengeSchluesselTeil;
					}
				}

				for (int j = 0; j < 26; j++) {

					int zaehler = 0;

					for (int lng = 0; lng < (a2.length / i) - k; lng++) {

						if ((97 + j) == a2[(lng * i) + k]) {
							zaehler++;
						}

					}
					buchstabe[j] = zaehler;

				}

				double x = 0;
				for (int j = 0; j < 26; j++) {
					x += ((buchstabe[j] * (buchstabe[j] - 1)) * 1000)
							/ (laengenFuerRechnung[k] * (laengenFuerRechnung[k] - 1));
				}

				x = x / 1000;
				koinzWerte[i][k] = x;
			}
		}

		double koinzschnitt[] = new double[koinzWerte.length];

		for (int i = 2; i < koinzWerte.length; i++) {
			double temporaer = 0;
			for (int j = 0; j < koinzWerte[i].length; j++) {
				temporaer += koinzWerte[i][j];
			}
			temporaer = temporaer / i;
			koinzschnitt[i] = temporaer;
		}

		double momentanerAbstand = 0.0;
		double momentanerAbstand2 = 0.0;
		double deutscherKoinzidenzwert = 0.76;
		int[] schluesselLaengeArray = new int[koinzschnitt.length];

		for (int i = 0; i < schluesselLaengeArray.length; i++) {
			schluesselLaengeArray[i] = i;
		}
		// Bubblesort methode
		for (int i = 0; i < koinzschnitt.length; i++) {
			for (int j = 3; j < koinzschnitt.length; j++) {
				momentanerAbstand = koinzschnitt[j - 1] - deutscherKoinzidenzwert;
				if (momentanerAbstand < 0) {
					momentanerAbstand = momentanerAbstand * -1;
				}
				momentanerAbstand2 = koinzschnitt[j] - deutscherKoinzidenzwert;
				if (momentanerAbstand2 < 0) {
					momentanerAbstand2 = momentanerAbstand2 * -1;
				}

				if (momentanerAbstand2 < momentanerAbstand) {
					double tmp = koinzschnitt[j];
					koinzschnitt[j] = koinzschnitt[(j - 1)];
					koinzschnitt[(j - 1)] = tmp;
					int tmpInt = schluesselLaengeArray[j];
					schluesselLaengeArray[j] = schluesselLaengeArray[j - 1];
					schluesselLaengeArray[j - 1] = tmpInt;
				}
			}
		}

		for (int i = 2; i < Friedmannschluessel.length + 2; i++) {
			Friedmannschluessel[i - 2] = schluesselLaengeArray[i];
		}
		return Friedmannschluessel;

	}
}