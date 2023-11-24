import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String regNum = scanner.nextLine(); // a valid or invalid registration number

        /* write your code here */


        check(regNum);


    }


    public static boolean check(String regNum) {

        String regex = "[ABEKMHOPCTYX]\\d{3}[ABEKMHOPCTYX]{2}";
        boolean matches = regNum.matches(regex);
        System.out.println(matches);
        return matches;
    }
}