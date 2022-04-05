import java.util.Hashtable;
import structure5.*;
import java.io.Serializable;
import java.nio.file.*;
import java.util.Scanner;
import org.jsoup.nodes.*;

/**
 * A class that tracks term frequencies (counts) for a single document.
 * Since it is serializable, the Hashtable is from java.util, not structure5.
 */
class TermFrequency implements Serializable {
    /* a hashtable representation of the appearances of each word in the document */
    private Hashtable<String,Integer> _counts;
    
    /**
      * Given a scanner, populate the table with the counts
      * of each word found.
      * 
      * @param text Scanner of the document to be added.
      */
    public TermFrequency(Scanner text) {
        _counts = new Hashtable<>();
        String term;

        while (text.hasNext()) {
            term = Term.normalize(text.next());
            if (!term.equals(""))
                incrementCount(term);
        }
        text.close();
    }

    /**
      * Opens the given file, and for each word in the file, converts
      * it to a normalized term, and counts it.
      * 
      * @param file Path to a document.
      */
    public TermFrequency(Path file) {
        this(new Scanner(new FileStream(file.toString())));
    }

    /**
      * Create a TermFrequency object for a document
      * 
      * @param doc an org.jsoup.nodes Document
      */
    public TermFrequency(Document doc) {
        this(new Scanner(doc.normalise().text()));  
    }

    /* Helper method for building the _counts hashtable. */
    private void incrementCount(String term) {
        // If _counts has the term add one to the value,
        // else put the term int _counts and set value to one.
        if (_counts.containsKey(term)) {
            _counts.put(term, _counts.get(term) + 1);
        } else {
            _counts.put(term, 1);
        }
    }
    
    /**
     * Computes the term frequency (TF_i) for term i in this document.
     *
     * @param term A string term.
     */
    public double tf(String term) {
        return (double) getCount(term) / mostFrequentTerm().getValue();
    }
    
    /**
     * Returns an association containing the most frequent term
     * along with its count.
     */
    public Association<String,Integer> mostFrequentTerm() {        
        Association<String,Integer> mostFrequent = new Association<>("", 0);
        for (String key : _counts.keySet()) {
            if (_counts.get(key) > mostFrequent.getValue()) {
                mostFrequent = new Association<>(key, _counts.get(key));
            }
        }
        return mostFrequent;
    }
    
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
     * Since it's going from the java Set to a structure5 SetVector,
     * the adding has to be manual.
     */
    public Set<String> terms() {
        Set<String> terms = new SetVector<>();
        for (String key : _counts.keySet()) {
            terms.add(key);
        }
        return terms;
    }

}