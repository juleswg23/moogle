import java.util.Hashtable;
import structure5.*;
import java.io.*;
import java.nio.file.*;
import org.jsoup.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.net.URL;

/**
 * A class that indexes and stores TermFrequencies across
 * a collection of documents (files or webpages).
 * It can compute tf_idf and ctf_idf scores for collections
 * of normalized terms.
 */
class Table implements Serializable {

    /* A java.util hashtable of documents and their corresponding TF's */
    private Hashtable<String, TermFrequency> _table;

    /**
     * Build term frequency table for all documents in path. Searches for
     * documents recursively.
     *
     * @param dir Document path.
     */
    public Table(Path dir) throws IOException {
        _table = new Hashtable<>();
        Path[] files = Files.walk(dir).toArray(Path[]::new);
        
        for (Path file : files) {
            if (!file.toFile().isDirectory()) {
                _table.put(file.toString(), new TermFrequency(file));
            }
        }
    }

     /**
     * Build term frequency table for all webpages starting at seed URL. 
     * Searches for webpages with a breadth-first search.
     *
     * @param startingURL seed URL to start at.
     * @param depth int of how many unique webpages to look at.
     */
    public Table(URL startingURL, int depth) {
        _table = new Hashtable<>();
        int pagesSeen = webCrawl(depth, startingURL);
        System.out.println("Collected " + pagesSeen);
    }

    /**
     * Web Crawler helper method that conducts a breadth-first search.
     * As it crawls, it add new pages to the _table.
     * 
     * @param curpage String URL to start at.
     * @param depth int of how many unique webpages to look at.
     * 
     * @return number of webpages accessed
     */
    private int webCrawl(int depth, URL startingURL) {
        // initialize variables that will be used in during search
        int pagesSeen = 0;
        String curPage = "";

        // Breadth first traversal of the web starts with a queue
        // Since we know the queue size will never exceed the depth, we can use an array.
        Queue<String> toCrawl = new QueueArray<>(depth + 1);
        toCrawl.add(startingURL.toString());
        
        // Try-catch for connecting to and adding the first page to the _table.
        try {
            _table.put(startingURL.toString(), new TermFrequency(Jsoup.connect(startingURL.toString()).get()));
        } catch (HttpStatusException e) {
            System.out.println("Couldn't connect to first page");
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unknown failure");
            System.exit(1);
        }
        
        // while the queue has more to look at, crawl
        while (!toCrawl.isEmpty()) {
            // dequeue and get the neighbors
            curPage = toCrawl.dequeue();
            // outer try-catch will never catch anything, but is required to get neighbors of the page.
            try {
                Elements neighbors = Jsoup.connect(curPage).get().select("a[href]");
                System.out.println("Got neighbors of " + curPage);

                // for each neighbor, add the neighbor to _table and enqueue it if not in _table
                for (Element page : neighbors) {
                    curPage = page.attr("abs:href");
                    if (!_table.containsKey(curPage)) {
                        // inner try-catch will catch pages that Jsoup fails to access.
                        try {
                            _table.put(curPage, new TermFrequency(Jsoup.connect(curPage).get()));
                            toCrawl.enqueue(curPage);
                            pagesSeen++;
                            System.out.println("Gathering... " + pagesSeen + " pages." );

                        // innter try-catch will tell the user what went wrong, but won't stop the program.
                        } catch (HttpStatusException e) { 
                            System.out.println(curPage + " was unreachable, but we will continue...");
                        } catch (IllegalArgumentException e) {
                            System.out.println("An empty url was found, but we will continue... " + curPage);
                        } catch (IOException e) {
                            System.out.print("An unknown failure occured on " + curPage + "   ");
                            System.err.println(e.getMessage());
                        }
                    }
                    if (pagesSeen >= depth) return pagesSeen;
                }

            //outer try-catch should never get anything, so we exit if it does.
            } catch (Exception e) {
                System.out.println("An unknown failure occured");
                e.printStackTrace();
                System.exit(1);
            }
        }
        return pagesSeen;
    }

    /**
     * Compute inverse document frequency (IDF) for term across a corpus.
     *
     * @param term A string term.
     */
    public double idf(String term) {
        double appearences = 0.0;
        double documents = 0.0;
        for (TermFrequency freq : _table.values()) {
            documents++;
            if (freq.getCount(term) != 0) appearences++;
        }
        // compute idf
        return (Math.log(documents) / Math.log(2)) - (Math.log(appearences + 1) / Math.log(2));
    }

    /**
     * Compute the TF-IDF score for a collection of documents and
     * a given search term.
     *
     * @param term A search term.
     */
    public Hashtable<String, Double> tfidf(String term) {
        Hashtable<String, Double> _result = new Hashtable<>();
        double idf = idf(term);
        for (String doc : _table.keySet()) {
            _result.put(doc, _table.get(doc).tf(term) * idf);
        }
        return _result;
    }

    /**
     * Computes the cumulative TF-IDF score for each document with
     * respect to a given query.
     *
     * @param query A vector of search terms.
     */
    public Hashtable<String, Double> score(Vector<String> query) {
        Hashtable<String, Double> _result = new Hashtable<>();
        Hashtable<String, Double> _tfdif;

        for (String term : query) {
            _tfdif = tfidf(term);
            for (String doc : _tfdif.keySet()) {
                if (_result.containsKey(doc)) {
                    _result.put(doc, _tfdif.get(doc) + _result.get(doc));
                } else {
                    _result.put(doc, _tfdif.get(doc));
                }
            }
        }
        return _result;
    }

    /**
     * Returns the top K documents.
     *
     * @param doc_tfidf A map from documents to their cumulative TF-IDF score.
     * @param k The number of documents to return.
     */
    public Vector<Association<String, Double>> topK(Vector<String> query, int k) {
        Vector<Association<String, Double>> result = new Vector<>();
        Hashtable<String, Double> _score = score(query);

        for (String doc : _score.keySet()) {
            double score = _score.get(doc);
            for (int i = 0; i < k; i++) {
                if (result.get(i) == null || score > result.get(i).getValue()) {
                    result.add(i, new Association<String, Double>(doc, score));
                    break;

                // The else if is here for the webpage search... if two scores are
                // exactly the same, the document is very like to have been the same, which
                // happens when the same webpage is indexed twice by the crawler. So
                // we are avoiding listing the same result twice.
                } else if (doc.startsWith("http") && score == result.get(i).getValue()) {
                    break;
                }
            }
        }
        if (result.size() > k)
            result.setSize(k);
        return result;
    }

    /**
     * Outputs frequency table in CSV format.  Useful for
     * debuggging.
     *
     * @param table A frequency table for the entire corpus.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String doc : _table.keySet()) {
            TermFrequency docFreqs = _table.get(doc);
            for (String term : docFreqs.terms()) {
                sb.append("\"" + doc + "\",");
                sb.append("\"" + term + "\",");
                sb.append(docFreqs.getCount(term));
                sb.append("\n");
            }
        }
        return sb.toString();
    }

}
