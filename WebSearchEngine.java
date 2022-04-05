import structure5.*;
import java.net.URL;

/**
 * A SearchEngine for the Web, starting from a seed URL.
 */
public class WebSearchEngine extends SearchEngine {

    /**
     * Search according to the SearchEngine with a table from the seed URL.
     * The crawling occurs in the Table constructor and Table method webCrawler.
     * 
     * @param query         normalized vector of search terms
     * @param startingURL   seed for the search
     * @param k             int of results to return
     * @param depth         int of how many pages to index
     */
    public WebSearchEngine(Vector<String> query, URL startingURL, int k, int depth) {
        super(query, new Table(startingURL, depth), k);
    }

    /**
     * Conduct the search from the command line.
     * @param args four parameters given by the third line in the main method.
     */
    public static void main(String[] args) {
        // ensure user input is accurate
        if (args.length != 4 || !Term.isNumber(args[2]) || !Term.isNumber(args[3])) {
            System.out.println("Improper search terms. The Search Engine takes the following parameters:");
            System.out.println("     java WebSearchEngine \"<query string>\" <url> <# of pages to return> <depth of search>");
            System.exit(1);
        }

        // handle the search terms
        Vector<String> query = Term.toTerms(args[0]);
        int k = Integer.parseInt(args[2]);
        int depth = Integer.parseInt(args[3]);
        URL startingPage = null;

        // It may not like the given URL.
        try {    
            startingPage = new URL(args[1]);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("URL was not a valid url. Shutting down.");
            System.exit(1);
        }

        // execute the search
        SearchEngine s = new WebSearchEngine(query, startingPage, k, depth);

    }

}