package CryptPart;

public class VWA_Verschluesseln {

	public static String encrypter(String t, String textcode) {

		t = t.toLowerCase();
		t = t.replace("\u00e4", "ae");
		t = t.replace("\u00f6", "oe");
		t = t.replace("\u00fc", "ue");
		t = t.replace("\u00df", "ss");

		char[] cde = new char[textcode.length()];

		int[] code = new int[textcode.length()];

		for (int i = 0; i < cde.length; i++) {
			cde[i] = textcode.charAt(i);
		}

		for (int i = 0; i < cde.length; i++) { // Umwandlung der Buchstaben des Codes zu Zahlen
			code[i] = ((int) cde[i]) - 97;
		}

		char[] a = new char[t.length()];

		for (int i = 0; i < a.length; i++) {
			a[i] = t.charAt(i);
		}

		int[] neuCode = new int[a.length];

		for (int i = 0; i < a.length; i++) { // Code wird auf die L�nge des Textes verl�ngert indem er wiederholt
												// wird
			neuCode[i] = code[i % code.length];
		}

		int x = 0;

		for (int i = 0; i < a.length; i++) { // Schleife um den Text zu verschluesseln

			if (!(a[i] > 96 && a[i] < 123)) {
				continue;
			}

			a[i] = (char) (neuCode[x] + a[i]); // Buchstabe + Verschiebung (Zahl des Codes)
			if (a[i] > 122) {
				a[i] = (char) (-26 + a[i]);
			}
			x++;
		}

		String encrypted = "";
		for (int i = 0; i < a.length; i++) {
			encrypted += a[i];
		}

		return encrypted;

	}

}
