package ru.ddiribas.Encryption;

import org.bouncycastle.util.Arrays;
import ru.ddiribas.MainApp;

public class PasswordAuthenticator {
    public static byte[] addPasswordPrint(byte[] input) {
        byte[] result = Arrays.concatenate(input, MainApp.getMainController().requestPassword());
        return StribogBouncy.getByteHash256(result);
    }
}
