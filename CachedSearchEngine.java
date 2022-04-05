import structure5.*;
import java.nio.file.*;
import java.io.*;

/**
 * This is an implementation of a search engine using TF-IDF and hash tables.
 * It will use a saved file if it has been used previously on the same corpus.
 */
class CachedSearchEngine extends SearchEngine {
    
    /* See SearchEngine for implementation details */
    public CachedSearchEngine(Vector<String> query, Table t, int k) {
        super(query, t, k);
    }

    /**
     * Conduct the search, the write the file to a text file
     * of the form "dir-table.txt"
     */
    public CachedSearchEngine(Vector<String> query, Path dir, int k) throws IOException {
        super(query, dir, k);
        System.out.println("Writing file...");
        
        // then write the file to the filename in the standardized form.
        String tableName = dir + "-table.txt";
        FileOutputStream fileWriter = new FileOutputStream(tableName);
        ObjectOutputStream objectWriter = new ObjectOutputStream(fileWriter);
        objectWriter.writeObject(table);
        objectWriter.flush();
        objectWriter.close();
        System.out.println("File written");
    }

    /**
     * Parse the text as in SearchEngine, and conduct the search
     * If the cached table is found, read it in, else conduct search normally
     * 
     * @param args of same format as SearchEngine
     */
    public static void main(String[] args) {
        if (args.length != 3 || !Term.isNumber(args[2])) {
            System.out.println("Improper search terms. The Cached Search Engine takes the following parameters:");
            System.out.println("     java CachedSearchEngine \"<query string>\" <document folder path> <# of documents to return>");
            System.exit(1);
        }

        // get the search components
        Vector<String> query = Term.toTerms(args[0]);
        Path dir = Paths.get(args[1]);
        int k = Integer.parseInt(args[2]);

        // if a table exists, bypass the search engine
        if (new File(dir + "-table.txt").exists()) {
            // try to read from file
            try { 
                FileInputStream fileReader = new FileInputStream(dir + "-table.txt");
                ObjectInputStream objectReader = new ObjectInputStream(fileReader);
                System.out.println("Found cached Table \nThis will take about 15 seconds");
                Table fromFile = (Table) objectReader.readObject();
                objectReader.close();
                System.out.println("Successfully read in Table from txt file.\n");

                SearchEngine s = new CachedSearchEngine(query, fromFile, k);
                System.exit(1);  
            // if reading file fails, move on to normal search.
            } catch (Exception e) {
                System.out.println("File existed but could not be determined to be a Table");
                System.out.println("You are either running the program for the first time, or the txt file was edited");
                System.out.println("The program will now run a normal search engine and cache the Table.\n");
            }
        }

        // if no table exists, handle the search normally
        // creating the table afterwards in the the constructor
        try {
            SearchEngine s = new CachedSearchEngine(query, dir, k);  
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

