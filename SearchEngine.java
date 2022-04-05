import structure5.*;
import java.nio.file.*;
import java.io.IOException;

/**
 * This is an implementation of a search engine using TF-IDF and hash tables.
 */
class SearchEngine {
  
    protected Table t;

    public SearchEngine(Vector<String> query, Path dir, int k) throws IOException {
        t = new Table(dir);
        Vector<Association<String, Double>> topK = t.topK(query, k);

        for (int i = 0; i < topK.size(); i++) {
            Association<String, Double> result = topK.get(i);
            System.out.println("Result " + (i + 1) + ": " + result.getKey());
            System.out.println(result.getValue());
        }
    }
  
    /**
   * This main method should allow a user to call this program as follows:
   * $ java SearchEngine "<query string>" <document folder path> <k>
   *   where
   *   <query string> is a search query, potentially composed of multiple terms,
   *   <document folder path> is a string representing the location of a
   *     document collection, and
   *   <k> is a positive integer representing how many documents to return.
   *
   * For example,
   * $ java SearchEngine "dog" ~/Desktop/ufo 5
   *   will return the 5 most relevant documents in the ~/Desktop/ufo folder
   *   for documents mentioning the word "dog".
   *
   * This method should first understand the user's arguments, extract the search
   * terms from the query, compute term counts for the documents in the given
   * path, then compute the TF-IDF score for each document given the query. Finally,
   * it should generate a list of documents, sorted by their TF-IDF score, and it
   * should print out the top k most relevant documents back to the user.
   *
   * @param args The command line argument array.
   */
    public static void main(String[] args) {
        // Make sure user used it properly
        if (args.length != 3 || !Character.isDigit(args[2].charAt(0))) {
            System.out.println("Improper search terms. The Search Engine takes the following parameters:");
            System.out.println("     java SearchEngine \"<query string>\" <document folder path> <# of documents to return>");
            return;
        }

        // handle the search
        try {
            Vector<String> query = Term.toTerms(args[0]);
            Path dir = Paths.get(args[1]);
            int k = Integer.parseInt(args[2]);

            SearchEngine search = new SearchEngine(query, dir, k);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}