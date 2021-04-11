package CryptPart;

public class VWA_Entschluesseln {

	public static Object[] myFriedmanEntschluesseln(char[] a2, int [] Friedmannschluessel, int friedmannzaehler) {

		int schluessel = 0;
		
		if(friedmannzaehler<Friedmannschluessel.length) {
			schluessel = Friedmannschluessel[friedmannzaehler];
		} else {
			System.out.println("Der Friedmann Test hat nicht funktioniert. Wählen sie eine andere Methode für diesen Text aus");
			System.exit(0);
		}
		friedmannzaehler++;
		
		int[] verschl = new int[schluessel];
		
		for (int schwied = 0; schwied < schluessel; schwied++) {

			int[] haufigk = new int[26];

			for (int bchst = 0; bchst < 26; bchst++) {
				for (int i = schwied; i < a2.length; i += schluessel) {
					if ((bchst + 97) == ((int) a2[i])) {
						haufigk[bchst]++;
					}
				}
			}

			int sorti = 0;
			int sORTi = 0;

			for (int groes = 0; groes < haufigk.length; groes++) {
				if (sorti < haufigk[groes]) {
					sorti = haufigk[groes];
					sORTi = groes;
				}
			}
			verschl[schwied] = sORTi;
		}

		for (int i = 0; i < schluessel; i++) {
			verschl[i] += 93;
			if (verschl[i] < 97) {
				verschl[i] += 26;
			}
		}

		int entschl[] = new int[verschl.length];

		for (int i = 0; i < schluessel; i++) {
			int x = 0;
			x = (220 - verschl[i]);
			if (x > 122) {
				x -= 26;
			}
			entschl[i] = x;
		}

		int[] entschlang = new int[a2.length];

		for (int i = 0; i < a2.length; i++) {
			if (entschl.length != 0) {
				entschlang[i] = entschl[i % entschl.length] - 97;
			} else {
				System.out.print("Fehler bei entschl!");
			}
		}

		char[] entschlText = new char[a2.length];

		for (int i = 0; i < a2.length; i++) {
			entschlText[i] = (char) (entschlang[i] + (int) a2[i]);
			if (entschlText[i] > 122) {
				entschlText[i] = (char) (-26 + entschlText[i]);
			}
		}

		char[] verschlChar = new char[verschl.length];
		char[] entschlChar = new char[entschl.length];

		for (int k = 0; k < verschlChar.length; k++) {
			verschlChar[k] = (char) verschl[k];
			entschlChar[k] = (char) entschl[k];
		}

		int verschlLaenge = verschlChar.length;
		int entschlLaenge = entschlChar.length;

		return new Object[] { verschlLaenge, entschlLaenge, verschlChar, entschlChar, entschlText, friedmannzaehler };
	}

	public static Object[] myKasiskiEntschluesseln(char[] a2, int[] nennerZahlen, int kasiskiZaehler, boolean erstenFuenfNenner) {

		int schluessel = 0;
		
		if(erstenFuenfNenner) {
			schluessel = nennerZahlen[kasiskiZaehler];
			kasiskiZaehler++;
			if(kasiskiZaehler>4) {
				erstenFuenfNenner = false;
				kasiskiZaehler = 0;
			}
		} else {
			schluessel = nennerZahlen[kasiskiZaehler]*nennerZahlen[kasiskiZaehler+1];
			kasiskiZaehler++;
		}
		
		if (kasiskiZaehler < 20) {
			return new Object[] { null, null, null, null, null };
		}
		
		if (schluessel == 0) {
			System.out.println("Schluessel ist 0...");
			System.exit(0);
		}

		int[] verschl = new int[schluessel];

		for (int schwied = 0; schwied < schluessel; schwied++) {

			int[] haufigk = new int[26];

			for (int bchst = 0; bchst < 26; bchst++) {
				for (int i = schwied; i < a2.length; i += schluessel) {
					if ((bchst + 97) == ((int) a2[i])) {
						haufigk[bchst]++;
					}
				}
			}

			int sorti = 0;
			int sORTi = 0;

			for (int groes = 0; groes < haufigk.length; groes++) {
				if (sorti < haufigk[groes]) {
					sorti = haufigk[groes];
					sORTi = groes;
				}
			}
			verschl[schwied] = sORTi;
		}


		for (int i = 0; i < schluessel; i++) {
			verschl[i] += 93;
			if (verschl[i] < 97) {
				verschl[i] += 26;
			}
		}

		int entschl[] = new int[verschl.length];

		for (int i = 0; i < schluessel; i++) {
			int x = 0;
			x = (220 - verschl[i]);
			if (x > 122) {
				x -= 26;
			}
			entschl[i] = x;
		}

		int[] entschlang = new int[a2.length];

		for (int i = 0; i < a2.length; i++) {
			entschlang[i] = entschl[i % entschl.length] - 97;
		}

		char[] entschlText = new char[a2.length];

		for (int i = 0; i < a2.length; i++) {
			entschlText[i] = (char) (entschlang[i] + (int) a2[i]);
			if (entschlText[i] > 122) {
				entschlText[i] = (char) (-26 + entschlText[i]);
			}
		}

		char[] verschlChar = new char[verschl.length];
		char[] entschlChar = new char[entschl.length];

		for (int k = 0; k < verschlChar.length; k++) {
			verschlChar[k] = (char) verschl[k];
			entschlChar[k] = (char) entschl[k];
		}

		int verschlLaenge = verschlChar.length;
		int entschlLaenge = entschlChar.length;
		return new Object[] { verschlLaenge, entschlLaenge, verschlChar, entschlChar, entschlText };
	}


	public static Object[] myBruteForceEntschluesseln(char[] a2, boolean bruteFunktionierte, int bruteForce) {

		bruteFunktionierte = false;
		String[] Suche = new String[11];
		Suche[0] = "der";
		Suche[1] = "die";
		Suche[2] = "das";
		Suche[3] = "und";
		Suche[4] = "sein";
		Suche[5] = "ein";
		Suche[6] = "von";
		Suche[7] = "haben";
		Suche[8] = "werden";
		Suche[9] = "mit";
		Suche[10] = "auf";

		for (; bruteForce < (int) (a2.length / 6); bruteForce++) { // a2.length/6 erklaeren

			int schluessel = bruteForce;
			int[] verschl = new int[schluessel];

			for (int schwied = 0; schwied < schluessel; schwied++) {

				int[] haufigk = new int[26];

				for (int bchst = 0; bchst < 26; bchst++) {
					for (int i = schwied; i < a2.length; i += schluessel) {
						if ((bchst + 97) == ((int) a2[i])) {
							haufigk[bchst]++;
						}
					}
				}

				int sorti = 0;
				int sORTi = 0;

				for (int groes = 0; groes < haufigk.length; groes++) {
					if (sorti < haufigk[groes]) {
						sorti = haufigk[groes];
						sORTi = groes;
					}
				}
				verschl[schwied] = sORTi;
			}


			for (int i = 0; i < schluessel; i++) {
				verschl[i] += 93;
				if (verschl[i] < 97) {
					verschl[i] += 26;
				}
			}

			int entschl[] = new int[verschl.length];

			for (int i = 0; i < schluessel; i++) {
				int x = 0;
				x = (220 - verschl[i]);
				if (x > 122) {
					x -= 26;
				}
				entschl[i] = x;
			}

			int[] entschlang = new int[a2.length];

			for (int i = 0; i < a2.length; i++) {
				entschlang[i] = entschl[i % entschl.length] - 97;
			}

			char[] entschlText = new char[a2.length];

			for (int i = 0; i < a2.length; i++) {
				entschlText[i] = (char) (entschlang[i] + (int) a2[i]);
				if (entschlText[i] > 122) {
					entschlText[i] = (char) (-26 + entschlText[i]);
				}
			}

			String Woertersuche = new String(entschlText);

			for (int i = 0; i < Suche.length - 1; i++) {
				for (int j = (i + 1); j < Suche.length; j++) {
					if (Woertersuche.indexOf(Suche[i]) != -1 && Woertersuche.indexOf(Suche[j]) != -1) {

						char[] verschlChar = new char[verschl.length];
						char[] entschlChar = new char[entschl.length];

						for (int k = 0; k < verschlChar.length; k++) {
							verschlChar[k] = (char) verschl[k];
							entschlChar[k] = (char) entschl[k];
						}

						int verschlLaenge = verschlChar.length;
						int entschlLaenge = entschlChar.length;
						bruteFunktionierte = true;
						return new Object[] { bruteFunktionierte, verschlLaenge, entschlLaenge,
								verschlChar, entschlChar, entschlText };
					}
				}
			}
		}
		String funktionierteNicht = new String("Brute Force funktionierte nicht");
		return new Object[] { bruteFunktionierte, funktionierteNicht };
	}
}