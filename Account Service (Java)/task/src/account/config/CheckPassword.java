package account.config;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

public class CheckPassword {


    private static List<String> hackerDatabase;


    private PasswordEncoder passwordEncoder;


    static {

        hackerDatabase = new ArrayList<>();


        hackerDatabase.addAll(List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
                "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
                "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"));

    }


    public static boolean checkPassWords(String userPassword, PasswordEncoder passwordEncoder) {


        for (var password : hackerDatabase) {

            var encoded = passwordEncoder.encode(password);
            if (passwordEncoder.matches(userPassword, encoded)) {
                return true;
            }
        }

        return false;
    }

}
