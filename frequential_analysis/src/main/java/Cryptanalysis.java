import java.io.IOException;

import static util.CaesarUtil.*;
import static util.VigenereUtil.*;

public class Cryptanalysis {
    public static void main(String[] args) throws IOException {
        int choice = Integer.parseInt(askOption(true));
        if (choice == 1) {
            displayCaesarWelcomeMessage();
            int option = Integer.parseInt(askOption(false));
            if (option == 1) {
                String data = getData(getPath());
                int key = askCaesarKey();
                String cipheredData = cipher(data, key);
                createCaesarCipheredFile(cipheredData);
            } else {
                int decipherOption = Integer.parseInt(askDecipherOption());
                if (decipherOption == 1) {
                    String data = getData(getPath());
                    int key = askCaesarKey();
                    String decipheredData = decipher(data, key);
                    createCaesarDecipheredFile(decipheredData);
                } else {
                    String data = getData(getPath());
                    String decipheredData = decipherCaesarWithoutKey(data);
                    createCaesarDecipheredFile(decipheredData);
                }
            }
        } else {
            displayVigenereWelcomeMessage();
            int option = Integer.parseInt(askOption(false));
            if (option == 1) {
                String data = getData(getPath());
                String key = askVigenereKey();
                String cipheredData = cipher(data, key);
                createVigenereCipheredFile(cipheredData);
            } else {
                int decipherOption = Integer.parseInt(askDecipherOption());
                if (decipherOption == 1) {
                    String data = getData(getPath());
                    String key = askVigenereKey();
                    String decipheredData = decipher(data, key);
                    createVigenereDecipheredFile(decipheredData);
                } else {
                    String data = getData(getPath());
                    String decipheredData = decipherVigenereWithoutKey(data);
                    createVigenereDecipheredFile(decipheredData);
                }
            }
        }
    }
}
