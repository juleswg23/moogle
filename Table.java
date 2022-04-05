import java.util.Hashtable;
import structure5.*;
import java.io.*;
import java.nio.file.*;

class Table implements Serializable {
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
        // TODO delete the printlines
        //System.out.println("foundFiles");
        int i = 0;
        for (Path file : files) {
            if (!file.toFile().isDirectory()) {
                //System.out.println("File: " + i++);
                _table.put(file.toString(), new TermFrequency(file));
            }
        }
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
            
        return Math.log(documents / (appearences + 1)) / Math.log(2);
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

    public static void main(String[] args) {
        try {
            Table t = new Table(Paths.get("ufo-test"));
            //System.out.println(t);
            Vector<String> query = Term.toTerms("ufo Reagan alien?");

            System.out.println("Built table");
            Vector<Association<String, Double>> topK = t.topK(query, 5);
            System.out.println(topK.get(0));
            System.out.println(topK.get(1));
            System.out.println(topK.get(2));
            System.out.println(topK.get(3));
            System.out.println(topK.get(4));


        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
