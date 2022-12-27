package util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static util.PreprocessUtil.preprocessLine;

public class CaesarUtil {

    public static void displayCaesarWelcomeMessage() {
        System.out.println("______________________________________");
        System.out.println("Welcome to the Caesar cryptanalysis");
        System.out.println("______________________________________");
    }

    private static void displayOptions() {
        System.out.println();
        System.out.println("would you like to cipher or decipher ?");
        System.out.println("1.cipher");
        System.out.println("2.decipher");
        System.out.println("______________________________________");
    }

    private static void displayStartingOptions() {
        System.out.println();
        System.out.println("would you like to use caesar or vigenere ?");
        System.out.println("1.caesar");
        System.out.println("2.vigenere");
        System.out.println("______________________________________");
    }

    private static void welcomeMessage() {
        System.out.println("______________________________________");
        System.out.println("Welcome to the cryptanalysis world");
        System.out.println("______________________________________");
    }

    private static boolean isNotValidOption(String option) {
        return !option.equals("1") && !option.equals("2");
    }

    public static String askOption(boolean isStartOfProgram) {
        if (isStartOfProgram) {
            welcomeMessage();
            Scanner kbd = new Scanner(System.in);
            String option = "";
            while (isNotValidOption(option)) {
                displayStartingOptions();
                option = kbd.nextLine();
                if (isNotValidOption(option)) {
                    System.out.println("please enter a valid option.");
                }
            }
            return option;
        } else {
            Scanner kbd = new Scanner(System.in);
            String option = "";
            while (isNotValidOption(option)) {
                displayOptions();
                option = kbd.nextLine();
                if (isNotValidOption(option)) {
                    System.out.println("please enter a valid option.");
                }
            }
            return option;
        }
    }

    public static String askDecipherOption() {
        Scanner kbd = new Scanner(System.in);
        String option = "";
        while (isNotValidOption(option)) {
            displayDecipherOptions();
            option = kbd.nextLine();
            if (isNotValidOption(option)) {
                System.out.println("please enter a valid option.");
            }
        }
        return option;
    }

    public static String getPreprocessedData(String input, boolean ignoreLines)
            throws FileNotFoundException, IOException {
        Scanner in = new Scanner(new FileReader(input));
        StringBuilder out = new StringBuilder();
        while (in.hasNextLine()) {
            out.append(preprocessLine(in.nextLine()));
            if (!ignoreLines)
                out.append("\n");
        }
        return out.toString();
    }

    public static String getData(Path p) throws IOException {
        return getPreprocessedData(String.valueOf(p), true);
    }

    public static Path getPath() {
        String p;
        Scanner kbd = new Scanner(System.in);
        System.out.print("enter the path to the (txt) file : ");
        p = kbd.nextLine();
        boolean isValid = false;
        while (!isValid) {
            if (!p.endsWith("txt")) {
                System.out.println("please enter a valid path.");
                System.out.println();
                System.out.print("path : ");
                p = kbd.nextLine();
            } else {
                isValid = true;
            }
        }
        return Paths.get(p);
    }

    public static int askCaesarKey() {
        Scanner kbd = new Scanner(System.in);
        System.out.print("enter a key : ");
        int key = -1;
        boolean isNegative = true;
        while (isNegative) {
            try {
                key = Integer.parseInt(kbd.nextLine());
                if (key < 0) {
                    System.out.println("\nplease enter a valid (positive) key.");
                    System.out.print("key : ");
                } else {
                    isNegative = false;
                }
            } catch (NumberFormatException e) {
                System.out.println("\nplease enter a valid (positive) key.");
                System.out.print("key : ");
            }
        }
        return key;
    }

    //inspired from : https://www.baeldung.com/
    public static String cipher(String data, int key) {
        StringBuilder cipheredText = new StringBuilder();
        for (char character : data.toCharArray()) {
            if (character != ' ') {
                int alphabetPos = character - 'a';
                int shiftedPos = (alphabetPos + key) % 26;
                char encodedChar = (char) ('a' + shiftedPos);
                cipheredText.append(encodedChar);
            } else {
                System.out.println("a");
                cipheredText.append(character);
            }
        }
        return cipheredText.toString();
    }

    public static void createCaesarCipheredFile(String cipheredData) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("caesar_ciphered.txt"), "utf-8"))) {
            writer.write(cipheredData);
            System.out.println();
            System.out.println("file successfully created.");
            System.out.println("Windows : /FrequentialAnalysis/caesar_ciphered.txt");
            System.out.println("Linux : /FrequentialAnalysis/src/main/java/caesar_ciphered.txt");
        } catch (IOException e) {
            System.out.println("an error occurred.");
            e.printStackTrace();
        }
    }

    public static String decipher(String data, int key) {
        return cipher(data, 26 - (key % 26));
    }

    public static void createCaesarDecipheredFile(String decipheredData) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("caesar_deciphered.txt"), "utf-8"))) {
            writer.write(decipheredData);
            System.out.println();
            System.out.println("file successfully created.");
            System.out.println("Windows : /FrequentialAnalysis/caesar_deciphered.txt");
            System.out.println("Linux : /FrequentialAnalysis/src/main/java/caesar_deciphered.txt");
        } catch (IOException e) {
            System.out.println("an error occurred.");
            e.printStackTrace();
        }
    }

    public static void displayDecipherOptions() {
        System.out.println();
        System.out.println("would you like to decipher with the help of a key or not ?");
        System.out.println("1.key");
        System.out.println("2.no key");
        System.out.println("__________________________________________________________");
    }

    //inspired from : http://practicalcryptography.com/cryptanalysis/text-characterisation/chi-squared-statistic/
    public static String decipherCaesarWithoutKey(String data) {
        double[] englishLettersFrequencies = getEnglishLetterFrequencies();
        double[] chiSquared = new double[26];
        String changingData;
        for (int i = 0; i < englishLettersFrequencies.length; i++) {
            changingData = decipher(data, i);
            int[] cipheredLettersFrequencies = countLetterFrequencies(changingData);
            chiSquared[i] = findChiSquared(changingData, englishLettersFrequencies, cipheredLettersFrequencies);
        }
        int key = findMostLikelyKey(chiSquared);
        return decipher(data, key);
    }

    private static int findMostLikelyKey(double[] chiSquared) {
        int key = 0;
        for (int i = 1; i < chiSquared.length; i++) {
            if (chiSquared[i] < chiSquared[key]) key = i;
        }
        return key;
    }

    private static double findChiSquared(String data, double[] englishLettersFrequencies, int[] cipheredLettersFrequencies) {
        double chiSquared = 0;
        for (int i = 0; i < englishLettersFrequencies.length; i++) {
            double expected = data.length() * englishLettersFrequencies[i];
            double countMinusExpectedSquared = Math.pow((cipheredLettersFrequencies[i] - (expected)), 2);
            chiSquared = chiSquared + countMinusExpectedSquared / expected;
        }
        return chiSquared;
    }

    private static double[] getEnglishLetterFrequencies() {
        // can always change to read from file
        double[] frequenciesInPercent = {8.2, 1.5, 2.7, 4.7, 13, 2.2, 2, 6.2, 6.9, 0.16, 0.81, 4.0, 2.7, 6.7, 7.8, 1.9,
                0.11, 5.9, 6.2, 9.6, 2.7, 0.97, 2.4, 0.15, 2, 0.078};
        return probabilityFormat(frequenciesInPercent);
    }

    private static double[] probabilityFormat(double[] frequenciesInPercent) {
        double[] probabilityFormat = new double[26];
        for (int i = 0; i < frequenciesInPercent.length; i++) {
            probabilityFormat[i] = frequenciesInPercent[i] / 100;
        }
        return probabilityFormat;
    }

    private static int[] countLetterFrequencies(String data) {
        char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z'};
        int[] observed = new int[26];
        for (char c : data.toCharArray()) {
            for (int i = 0; i < alphabet.length; i++) {
                if (c == alphabet[i]) {
                    observed[i]++;
                }
            }
        }
        return observed;
    }

}
