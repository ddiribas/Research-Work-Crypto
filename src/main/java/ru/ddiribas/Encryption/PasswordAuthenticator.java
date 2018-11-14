package ru.ddiribas.Encryption;

public class PasswordAuthenticator {
    public static byte[] addPasswordPrint(byte[] input) {
        String key = new String(input);

        //TODO: форма для ввода пароля вызывается отсюда
        StringBuilder result = new StringBuilder();
        result.append(key);
        return StribogBouncy.getByteHash256(result.toString().getBytes());

    }
}
