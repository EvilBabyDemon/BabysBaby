package CryptPart;

public class VWA_HelperEntschluesseln {

	/*
	 * Leerzeichen und weitere Sonderzeichen entfernen
	 * 
	 * @param String text
	 * 
	 */
	public static String textCleaner(String text) {

		text = text.toLowerCase();
		// text = text.replace("\u00e4", "ae");
		// text = text.replace("\u00f6", "oe");
		// text = text.replace("\u00fc", "ue");
		// text = text.replace("\u00df", "ss");

		String text2 = "";
		for (int i = 0; i < text.length(); i++) {
			if (!(text.charAt(i) > 96 && text.charAt(i) < 123)) {
				continue;
			}
			text2 += text.charAt(i);
		}

		return (text2);

	}

	/*
	 * Zuweisung des Strings zu einem character array
	 * 
	 * @param text
	 */
	public static char[] myStringToArray(String text) {

		char[] a2 = new char[text.length()];

		for (int i = 0; i < a2.length; i++) {
			a2[i] = text.charAt(i);
		}

		return a2;
	}
}