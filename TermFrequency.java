import java.util.Hashtable;
import java.util.Set;
import structure5.*;
import java.io.Serializable;
import java.nio.file.*;
import java.util.Scanner;

/**
 * A class that tracks term frequencies (counts) for a single document.
 */
class TermFrequency implements Serializable {
    private Hashtable<String,Integer> _counts;

    // Storing this makes the calls to tf a lot faster
    //private Association<String, Integer> mostFrequentTerm = new Association<>("", 0); 
    private int mostFrequent = 0;   
    
    /**
      * Opens the given file, and for each word in the file, converts
      * it to a normalized term, and counts it.
      * 
      * @param file Path to a document.
      */
    public TermFrequency(Path file) {
        _counts = new Hashtable<>();
        Scanner text = new Scanner(new FileStream(file.toString()));

        //faster
        String term;
        while (text.hasNext()) {
            term = Term.normalize(text.next());
            if (!term.equals(""))
                incrementCount(term);
        }
       
        // while (text.hasNextLine()) {
        //     for (String word : Term.toTerms(text.nextLine())) {
        //         incrementCount(word);
        //     }
        // }
        text.close();

    }

    // Helper method for building the _counts hashtable.
    private void incrementCount(String term) {
        if (_counts.containsKey(term)) {
            _counts.put(term, _counts.get(term) + 1);
        } else {
            _counts.put(term, 1);
        }

        // update mostFrequentTerm if needed
        if (_counts.get(term) > mostFrequent)
            mostFrequent = _counts.get(term);
            //mostFrequentTerm = new Association<String,Integer>(term, _counts.get(term));
    }
    
    public boolean isEmpty() {
        return _counts.size() == 0;
    }
    
    /**
     * Computes the term frequency (TF_i) for term i in this document.
     *
     * @param term A string term.
     */
    public double tf(String term) {
        return (double) getCount(term) / mostFrequent; //has no parens
    }
    
    /**
     * Returns an association containing the most frequent term
     * along with its count.
     */
    // public Association<String,Integer> mostFrequentTerm() {        
    //     int largestValue = 0;
    //     Association<String,Integer> mostFrequent = null;
    //     Set<Association<String, Integer>> allElements = _counts.entrySet();

    //     for (Association<String, Integer> curElement : allElements) {
    //         if (curElement.getValue() > largestValue) {
    //             mostFrequent = curElement;
    //             largestValue = mostFrequent.getValue();
    //         }
    //     }

    //     return mostFrequent;
    // }
    
    /**
     * Returns the count for a given term.
     *
     * @param term The given term.
     */
    public int getCount(String term) {
        Integer count = _counts.get(term);
        if (count != null) return count;
        else return 0;
    }
    
    /**
     * Returns all of the stored terms as a set.
     */
    public Set<String> terms() {
        return _counts.keySet();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 300; i++) {
            TermFrequency t = new TermFrequency(Paths.get("ufo-test", "aus_gov.txt"));
        }
        //System.out.println(t.terms());
        
    }
}