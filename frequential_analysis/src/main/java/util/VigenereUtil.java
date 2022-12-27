package util;

import java.io.*;
import java.util.*;

public class VigenereUtil {

    public static void displayVigenereWelcomeMessage() {
        System.out.println("______________________________________");
        System.out.println("Welcome to the Vigenere cryptanalysis");
        System.out.println("______________________________________");
    }

    public static String askVigenereKey() {
        Scanner kbd = new Scanner(System.in);
        System.out.print("enter a key : ");
        String key = "";
        while (isNotOnlyAlphabet(key)) {
            key = kbd.nextLine().toLowerCase();
            if (isNotOnlyAlphabet(key)) {
                System.out.println("\nplease enter a valid (alphabet only) key.");
                System.out.print("key : ");
            }
        }
        return key;
    }

    private static boolean isNotOnlyAlphabet(String key) {
        return !key.matches("[a-zA-Z]+");
    }

    //inspired from : https://www.sanfoundry.com/
    public static String cipher(String data, String key) {
        StringBuilder res = new StringBuilder();
        for (int i = 0, j = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            if (c < 'a' || c > 'z')
                continue;
            res.append((char) ((c + key.charAt(j) - 2 * 'a') % 26 + 'a'));
            j = ++j % key.length();
        }
        return res.toString();
    }

    public static void createVigenereCipheredFile(String cipheredData) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("vigenere_ciphered.txt"), "utf-8"))) {
            writer.write(cipheredData);
            System.out.println();
            System.out.println("file successfully created.");
            System.out.println("Windows : /FrequentialAnalysis/vigenere_ciphered.txt");
            System.out.println("Linux : /FrequentialAnalysis/src/main/java/vigenere_ciphered.txt");
        } catch (IOException e) {
            System.out.println("an error occurred.");
            e.printStackTrace();
        }
    }

    public static String decipher(String data, String key) {
        StringBuilder res = new StringBuilder();
        for (int i = 0, j = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            if (c < 'a' || c > 'z')
                continue;
            res.append((char) ((c - key.charAt(j) + 26) % 26 + 'a'));
            j = ++j % key.length();
        }
        return res.toString();
    }

    public static void createVigenereDecipheredFile(String decipheredData) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("vigenere_deciphered.txt"), "utf-8"))) {
            writer.write(decipheredData);
            System.out.println();
            System.out.println("file successfully created.");
            System.out.println("Windows : /FrequentialAnalysis/vigenere_deciphered.txt");
            System.out.println("Linux : /FrequentialAnalysis/src/main/java/vigenere_deciphered.txt");
        } catch (IOException e) {
            System.out.println("an error occurred.");
            e.printStackTrace();
        }
    }

    //inspired from : https://crypto.interactive-maths.com/kasiski-analysis-breaking-the-code.html#encrypt
    public static String decipherVigenereWithoutKey(String data) {
        int keyLength = findKeyLength(data);
        String[] sequences = getSequences(data, keyLength);
        double[] englishLettersFrequencies = getEnglishLetterFrequencies();
        double[] chiSquared = new double[26];
        String changingData;
        StringBuilder key = new StringBuilder();
        for (String sequence : sequences) {
            for (int j = 0; j < englishLettersFrequencies.length; j++) {
                changingData = CaesarUtil.decipher(sequence, j);
                int[] cipheredLettersFrequencies = countLetterFrequencies(changingData);
                chiSquared[j] = findChiSquared(changingData, englishLettersFrequencies, cipheredLettersFrequencies);
            }
            int caesarKey = findMostLikelyKey(chiSquared);
            key.append(getLetter(caesarKey));
        }
        return decipher(data, String.valueOf(key));
    }

    private static char getLetter(int i) {
        char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
                's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        return alphabet[i];
    }

    private static Integer findKeyLength(String data) {
        ArrayList<Map<String, Integer>> list = new ArrayList<>();
        char[] chars = data.toCharArray();
        for (int size = 3; size <= 3; size++) {
            int beginPattern = 0;
            Map<String, Integer> map = new HashMap<String, Integer>();
            while (beginPattern < chars.length - size + 1) {
                StringBuilder pattern = new StringBuilder();
                for (int shift = 0; shift < size; shift++) {
                    pattern.append(chars[beginPattern + shift]);
                }
                Integer population = map.get(pattern.toString());
                if (population == null) {
                    population = 1;
                } else {
                    population++;
                }
                map.put(pattern.toString(), population);
                beginPattern++;
            }
            list.add(map);
        }
        ArrayList<String> repeatingTrigrams = findRepeatingTrigrams(list);
        ArrayList<ArrayList<Integer>> indexesOfRepeatedTrigrams = new ArrayList<>();
        ArrayList<Integer> listOfGcd = new ArrayList<>();
        ArrayList<ArrayList<Integer>> distancesOfRepetition = findDistancesOfRepetition(data, repeatingTrigrams,
                indexesOfRepeatedTrigrams, listOfGcd);
        int[] array = new int[listOfGcd.size()];
        for (int i = 0; i < listOfGcd.size(); i++) array[i] = listOfGcd.get(i);
        Map<Integer, Integer> map = new HashMap<>();
        for (int i : array) {
            Integer count = map.get(i);
            map.put(i, count != null ? count + 1 : 1);
        }
        Integer frequentGcd = Collections.max(map.entrySet(), Map.Entry.comparingByValue()).getKey();
        ArrayList<ArrayList<Integer>> listOfSets = new ArrayList<>();
        Integer[] primeArray = findPrimeFactors(frequentGcd).toArray(new Integer[0]);
        findPowerSet(primeArray, listOfSets);
        ArrayList<Integer> best = maxSet(listOfSets);
        return product(best);
    }

    public static ArrayList<Integer> maxSet(ArrayList<ArrayList<Integer>> sets) {
        int max = 0;
        int maxindex = -1;
        for (int i = 0; i < sets.size(); i++) {
            int prod = product(sets.get(i));
            if (prod > max && prod <= 26) {
                max = prod;
                maxindex = i;
            }
        }
        if (maxindex == -1) {
            return new ArrayList<>();
        } else {
            return sets.get(maxindex);
        }
    }

    public static int product(ArrayList<Integer> set) {
        int res = 1;
        if (set.size() == 0) {
            res = 0;
        } else {
            for (int elem : set) {
                res = res * elem;
            }
        }
        return res;
    }

    public static void printPowerSet(Integer[] S, ArrayList<Integer> out, int i, ArrayList<ArrayList<Integer>> listofsets) {
        // if all elements are processed, print the current subset
        if (i < 0) {
            ArrayList<Integer> newList = new ArrayList<>(out);
            listofsets.add(newList);
            return;
        }
        out.add(S[i]); // include the current element in the current subset and recur
        printPowerSet(S, out, i - 1, listofsets); // backtrack: exclude the current element from the current subset
        out.remove(out.size() - 1); // remove adjacent duplicate elements
        while (i > 0 && Objects.equals(S[i], S[i - 1])) {
            i--;
        }
        printPowerSet(S, out, i - 1, listofsets); // exclude the current element from the current subset and recur
    }

    // wrapper over "printPowerSet()" function
    public static void findPowerSet(Integer[] S, ArrayList<ArrayList<Integer>> listofsets) {
        Arrays.sort(S); // sort the set
        ArrayList<Integer> out = new ArrayList<>(); // create an empty list to store elements of a subset
        printPowerSet(S, out, S.length - 1, listofsets);
    }

    public static ArrayList<Integer> findPrimeFactors(int nb) {
        ArrayList<Integer> res = new ArrayList<>();
        int number = nb;
        for (int i = 2; i < number; i++) {
            while (number % i == 0) {
                res.add(i);
                number = number / i;
            }
        }
        if (number > 2) {
            res.add(number);
        }
        return res;
    }

    private static ArrayList<ArrayList<Integer>> findDistancesOfRepetition(String data, ArrayList<String> repeatingTrigrams, ArrayList<ArrayList<Integer>> indexesOfRepeatedTrigrams, ArrayList<Integer> listOfGcd) {
        ArrayList<ArrayList<Integer>> distancesOfRepetition = new ArrayList<>();
        for (String repeatingTrigram : repeatingTrigrams) {
            ArrayList<Integer> indexPat = findIndexes(repeatingTrigram, data);
            indexesOfRepeatedTrigrams.add(indexPat);
            distancesOfRepetition.add(calculateDistances(indexPat));
        }
        for (ArrayList<Integer> distancesOfItem : distancesOfRepetition) {
            int gcd = findAllGcd(distancesOfItem);
            listOfGcd.add(gcd);
        }
        return distancesOfRepetition;
    }

    private static ArrayList<String> findRepeatingTrigrams(ArrayList<Map<String, Integer>> list) {
        ArrayList<String> repeatingTrigrams = new ArrayList<>();
        for (Map<String, Integer> map : list) {
            for (String key : map.keySet()) {
                if (map.get(key) > 1) {
                    repeatingTrigrams.add(key);
                }
            }
        }
        return repeatingTrigrams;
    }

    public static int gcd(int a, int b) {
        int first = a;
        int second = b;
        while (second != 0) {
            int temp = second;
            second = first % second;
            first = temp;
        }
        return first;
    }

    public static int findAllGcd(ArrayList<Integer> distances) {
        int res = distances.get(0);
        for (int element : distances) {
            res = gcd(res, element);
            if (res == 1) {
                return res;
            }
        }
        return res;
    }

    public static ArrayList<Integer> findIndexes(String trigram, String data) {
        ArrayList<Integer> trigramOccurences = new ArrayList<>();
        int index = data.indexOf(trigram);
        while (index >= 0) {
            trigramOccurences.add(index);
            index = data.indexOf(trigram, index + 1);
        }
        return trigramOccurences;
    }

    public static ArrayList<Integer> calculateDistances(ArrayList<Integer> indexesOfPattern) {
        ArrayList<Integer> distances = new ArrayList<>();
        int nbOccurences = indexesOfPattern.size();
        for (int i = 0; i < nbOccurences - 1; i++) {
            int distance = indexesOfPattern.get(i + 1) - indexesOfPattern.get(i);
            distances.add(distance);
        }
        return distances;
    }

    private static String[] getSequences(String data, int nbSequences) {
        String[] sequences = new String[nbSequences];
        for (int i = 0; i < nbSequences; i++) {
            sequences[i] = (getSequence(data, i, nbSequences));
        }
        return sequences;
    }

    private static String getSequence(String data, int begin, int skip) {
        StringBuilder sequence = new StringBuilder("");
        for (int i = begin; i < data.length(); i = i + skip) {
            sequence.append(data.charAt(i));
        }
        return sequence.toString();
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
        double[] frequenciesInPercent = {8.2, 1.5, 2.7, 4.7, 13, 2.2, 2, 6.2, 6.9, 0.16, 0.81, 4.0, 2.7, 6.7, 7.8, 1.9,
                0.11, 5.9, 6.2, 9.6, 2.7, 0.97, 2.4, 0.15, 2, 0.078}; // can always change to read from file
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
