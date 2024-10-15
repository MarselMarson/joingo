package rt.marson.syeta.util;

import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;

@RequiredArgsConstructor
public class PasswordGeneratorUtil {
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String ALL = LOWER + UPPER + DIGITS;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generatePassword() {
        StringBuilder password = new StringBuilder();

        // Добавляем по 1 символу: цифра, заглавная буква, строчная буква
        password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        password.append(UPPER.charAt(RANDOM.nextInt(UPPER.length())));
        password.append(LOWER.charAt(RANDOM.nextInt(LOWER.length())));

        for (int i = 3; i < 8; i++) {
            password.append(ALL.charAt(RANDOM.nextInt(ALL.length())));
        }

        return shuffleString(password.toString());
    }

    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            int randomIndex = RANDOM.nextInt(characters.length);
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }
}
