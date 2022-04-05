import structure5.*;
import java.util.Scanner;

/**
 * A class that contains some static helper methods for working
 * with terms.
 */
class Term {
    
    /**
     * Converts a query string into a normalized term array.
     *
     * @param query Query string.
     */
    public static Vector<String> toTerms(String query) {
        Vector<String> terms = new Vector<>();
        Scanner sc = new Scanner(query);
        String curWord;

        while (sc.hasNext()) {
            curWord = normalize(sc.next());
            if (!curWord.equals("")) terms.add(curWord);
        }
        sc.close();
        return terms;
    }
    
    /**
     * Returns a normalized a word by making the given word 
     * lowercase and by removing all punctuation.
     *
     * @param word An unprocessed word.
     */
    public static String normalize(String word) {
        String str = "";
        for (int i = 0; i < word.length(); i++) 
            if (Character.isLetter(word.charAt(i))) str += word.charAt(i);
        return str.toLowerCase();
    }

    /**
     * Returns true if String is made up of all digits
     *
     * @param a String.
     */
    public static boolean isNumber(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i)))
                return false;
        }
        return true;
    }


}