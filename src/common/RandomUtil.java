package common;

import java.util.Random;

public class RandomUtil {

	private String characters;

	public RandomUtil() {
		this.characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	}

	public RandomUtil(String characters) {
		this.characters = characters;
	}

	public char[] generateCharacters(int length) {
		char[] randomAlphabet = new char[length];
		Random r = new Random();
		for (int i = 0; i < length; i++) {
			randomAlphabet[i] = (characters.charAt(r.nextInt(characters.length())));
		}
		return randomAlphabet;
	}

	public String[] generateStrings(int length) {
		String[] randomAlphabet = new String[length];
		Random r = new Random();
		for (int i = 0; i < length; i++) {
			randomAlphabet[i] = Character
					.toString((characters.charAt(r.nextInt(characters.length()))));
		}
		return randomAlphabet;
	}

	public char generateCharacter() {
		Random r = new Random();
		return (characters.charAt(r.nextInt(characters.length())));
	}

	public String generateString() {
		Random r = new Random();
		return Character.toString((characters.charAt(r.nextInt(characters.length()))));
	}
}
