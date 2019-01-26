package http.models;

import java.util.Base64;
import java.util.Map;
import java.util.Set;

public class User {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String age;
    private final String colorR;
    private final String colorB;
    private final String colorG;
    public User(String base64String) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        String decode = new String(decodedBytes);
        String[] parseArr = new String[3];
        int found = 0;
        StringBuilder tempString = new StringBuilder();
        for (int i = 0; i < decode.length() && found < parseArr.length; i++) {
            char c = decode.charAt(i);
            if (Character.isLetter(c) || Character.isDigit(c)) {
                tempString.append(c);
            } else if (tempString.length() > 0) {
                parseArr[found++] = tempString.toString();
                tempString = new StringBuilder();
            }

        }
        this.id = parseArr[0];
        this.firstName = parseArr[1];
        this.lastName = parseArr[2];
        this.age = "" + decodedBytes[decodedBytes.length - 6];
        this.colorR = "" + decodedBytes[decodedBytes.length - 4];
        this.colorB = "" + decodedBytes[decodedBytes.length - 3];
        this.colorG = "" + decodedBytes[decodedBytes.length - 2];

    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age='" + age + '\'' +
                ", colorR='" + colorR + '\'' +
                ", colorB='" + colorB + '\'' +
                ", colorG='" + colorG + '\'' +
                '}';
    }

}
