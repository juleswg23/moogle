import structure5.*;
import java.nio.file.*;
import java.io.*;

class CachedSearchEngine extends SearchEngine {
    
    public CachedSearchEngine(Vector<String> query, Path dir, int k) throws IOException {
        super(query, dir, k);
        String tableName = dir + "-table.txt";
        FileOutputStream fileWriter = new FileOutputStream(tableName);
        ObjectOutputStream objectWriter = new ObjectOutputStream(fileWriter);
        objectWriter.writeObject(t);
        objectWriter.flush();
        objectWriter.close();

    }

    public static void main(String[] args) {
        if (args.length != 3 || !Character.isDigit(args[2].charAt(0))) {
            System.out.println("Improper search terms. The Cached Search Engine takes the following parameters:");
            System.out.println("     java CachedSearchEngine \"<query string>\" <document folder path> <# of documents to return>");
            return;
        }
        Path dir = Paths.get(args[1]);

        // if a table exists, bypass the search engine
        if (new File(dir + "-table.txt").exists()) {
            System.out.println("found file");
            try { 
                FileInputStream fileReader = new FileInputStream(dir + "-table.txt");
                ObjectInputStream objectReader = new ObjectInputStream(fileReader);
                Table t = (Table) objectReader.readObject();
                objectReader.close();

                Vector<String> query = Term.toTerms(args[0]);
                int k = Integer.parseInt(args[2]);
                Vector<Association<String, Double>> topK = t.topK(query, k);

                for (int i = 0; i < topK.size(); i++) {
                    Association<String, Double> result = topK.get(i);
                    System.out.println("Result " + (i + 1) + ": " + result.getKey());
                    System.out.println(result.getValue());
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        // if no table exists, handle the search normally
        try {
            Vector<String> query = Term.toTerms(args[0]);
            int k = Integer.parseInt(args[2]);
            new CachedSearchEngine(query, dir, k);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // public static Table buildTableFromFile(Path file) {
        
    // }

}

