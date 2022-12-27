package util;

public class PreprocessUtil {
    public static final String DIACRITICS = "àâäéèêëîïôùûç";
    public static final String DIACRITICS_REPLACE = "aaaeeeeiiouuc";
    public static final String TO_REMOVE = " \n\t\"+-*/=&|'()[]{},;.!?#~_";

    public static int indexOf(char c, String str) {
        for (int i = 0; i < str.length(); i++)
            if (str.charAt(i) == c)
                return i;
        return -1;
    }

    public static String preprocessLine(String line) {
        line = line.toLowerCase();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            int index = indexOf(c, DIACRITICS);
            if (index != -1) //char is not a diacritic
                builder.append(DIACRITICS_REPLACE.charAt(index));
            else if (indexOf(c, TO_REMOVE) != -1) //char has to be removed
            {
            } else if (c == 'æ')
                builder.append("ae");
            else if (c == 'œ')
                builder.append("oe");
            else //lowercase letters, digits, and "other stuff" is kept
                builder.append(c);
        }

        return builder.toString();
    }
}
