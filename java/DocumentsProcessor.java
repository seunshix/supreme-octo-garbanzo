import java.io.*;
import java.util.*;


/**
 * @author Ijeoma Okoye
 */
public class DocumentsProcessor implements IDocumentsProcessor {
    /** an instance of DocumentIterator. */
    private DocumentIterator documentIterator;

    /**
     * We write ngrams sequentially in the file. they are separated by
     * a space
     *
     * @param directoryPath          - the path to the directory of files
     *                              we want to break into ngrams
     * @param sequenceFile - path to the file we will be writing to
     * @param n -  the size of n-gram to use
     * @return a list of file and size (in byte) of character written in file
     *         path
     */
    public List<Tuple<String, Integer>> processAndStore(String directoryPath,
                                                        String sequenceFile, int n) {
        List<Tuple<String, Integer>> storedNGrams = new ArrayList<>();
        String fileName;

        try {
            File fileDir = new File(directoryPath);
            File[] fileList = fileDir.listFiles();
            if (fileList == null || fileList.length == 0) {
                System.out.println("No files found in the directory.");
                return storedNGrams; // No files to process
            }

            // Sort the fileList alphabetically
            Arrays.sort(fileList, Comparator.comparing(File::getName));

            File outputFile = new File(sequenceFile);
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            // append set to false so it overwrites existing content
            try (Writer fileWriter = new FileWriter(outputFile, false)) {
                BufferedWriter writer = new BufferedWriter(fileWriter);
                for (File file : fileList) {

                    if (file.toString().contains(".DS_Store")) {
                        continue;
                    }

                    int bytes = 0;


                    try (Reader reader = new BufferedReader(new FileReader(file))) {

                        documentIterator = new DocumentIterator(reader, n);
                        int ngramsNumber = 0;
                        while (documentIterator.hasNext()) {
                            String nGram = documentIterator.next();
                            writer.write(nGram + " ");
                            bytes = bytes + nGram.length();
                            ngramsNumber++;
                        }

                        writer.newLine();
                        int lastSlash = String.valueOf(file).lastIndexOf("/");
                        fileName = String.valueOf(file).substring(lastSlash + 1,
                                String.valueOf(file).length());
                        bytes = bytes + ngramsNumber;
                        storedNGrams.add(new Tuple<>(fileName, bytes));

                    }
                }
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("Trouble reading/writing files");
        }

        return storedNGrams;
    }


    /**
     * @param directoryPath - the path to the directory
     * @param n             - the size of n-gram to use
     * @return collection of files with n-grams in each
     */
    @Override
    public Map<String, List<String>> processDocuments(String directoryPath, int n) {

        Map<String, List<String>> nGramsMap = new HashMap<>();
        String fileName;
//        List<String> n_gramsList = new ArrayList<>();
        try {

            File fileDir = new File(directoryPath);
            File[] fileList = fileDir.listFiles();
            if (fileList == null || fileList.length == 0) {
                System.out.println("No files found in the directory.");
                return nGramsMap; // No files to process
            }


            for (File file : fileList) {
                if (file.toString().contains(".DS_Store")) {
                    continue;
                }

                List<String> nGramsList = new ArrayList<>();
                try {


                    Reader reader = new BufferedReader(new FileReader(file));
//                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    documentIterator = new DocumentIterator(reader, n);
                    while (documentIterator.hasNext()) {

                        nGramsList.add(documentIterator.next());
                    }

                    int lastSlash = String.valueOf(file).lastIndexOf("/");
                    fileName = String.valueOf(file).substring(lastSlash + 1,
                            String.valueOf(file).length());
                    nGramsMap.put(fileName, nGramsList);

                } catch (IOException e) {

                    System.out.println("Trouble reading. File may not exist " +
                            "or check directory Path"); // log the appropriate exception
                }
//            break;
            }
        } catch (NullPointerException e) {
            System.out.println("File is null, it does not seem to exist");
        }
        return nGramsMap;
    }

    /**
     * We write ngrams sequentially in the file. they are separated by
     * a space
     *
     * @param docs          - map of string with list of all ngrams
     * @param nwordFilePath of the file to store the ngrams
     * @return a list of file and size (in byte) of character
     * written in file path
     */
    @Override
    public List<Tuple<String, Integer>> storeNGrams(Map<String, List<String>> docs,
                                                    String nwordFilePath) {
        List<Tuple<String, Integer>> storedNGrams = new ArrayList<>();
//        int bytes = 0;
        try {

            if (docs != null) {
                File file = new File(nwordFilePath);
                file.createNewFile();
                // append set to false so it overwrites existing content
                Writer fileWriter = new FileWriter(file, false);
                BufferedWriter writer = new BufferedWriter(fileWriter);

                for (Map.Entry<String, List<String>> entry : docs.entrySet()) {
                    int bytes = 0;
                    // Handle null values in the lis
                    if (entry.getValue() != null) {
                        String key = entry.getKey();
                        for (int i = 0; i < entry.getValue().size(); i++) {
                            bytes = bytes + entry.getValue().get(i).length();
                            writer.write(entry.getValue().get(i) + " ");
                        }
                        writer.newLine();
                        bytes = bytes + entry.getValue().size();
                        storedNGrams.add(new Tuple<>(key, bytes));
                    }
                }
                writer.flush();
                writer.close();

            } else {
                System.out.println("Ngrams map is null");
            }

        } catch (IOException e) {
            System.out.println("File not created, file path may not exist");
        }

        return storedNGrams;
    }


    /**
     * @param nwordFilePath of the file to store the n-grams
     * @param fileindex     - a list of tuples representing each file and its size
     *                      in nwordFile
     * @return a TreeSet of file similarities. Each Similarities instance
     *         encapsulates the files (two) and the number of n-grams
     *         they have in common
     */
    @Override
    public TreeSet<Similarities> computeSimilarities(String nwordFilePath,
                                                     List<Tuple<String, Integer>> fileindex) {
        TreeSet<Similarities> similaritiesTreeSet = new TreeSet<>();
        // change this to a set of strings??
        Map<String, Set<String>> extractNgrams;

        extractNgrams = extractNgrams(nwordFilePath, fileindex);

        // Iterate through file index to extract files ngram
        for (Map.Entry<String, Set<String>> entry : extractNgrams.entrySet()) {

            String currentDocumentName = entry.getKey();

            Set<String> currentNgrams = entry.getValue();


            // Calculates 2nd documents ngrams
            for (Tuple<String, Integer> stringIntegerTuple : fileindex) {
                String otherDocumentName = stringIntegerTuple.getLeft();
                // To ensure that it does not do the same pair of documents twice
                if (currentDocumentName.compareTo(otherDocumentName) > 0) {
                    continue;
                }
                // possibly remove this line, i suspect it's taking up heap space
                Set<String> otherNgrams = extractNgrams.get(stringIntegerTuple.getLeft());

                if (!otherDocumentName.equals(currentDocumentName)) {
                    int commonNGrams = calculateCommonNgramsCount(currentNgrams, otherNgrams);

                    if (commonNGrams > 0) {

                        // Check if the TreeSet already contains a Similarities object
                        // with the same pair of document names
                        Similarities existingSimilarities = findSimilarities(similaritiesTreeSet,
                                currentDocumentName, otherDocumentName);

                        if (existingSimilarities != null) {
                            // Update the existing Similarities object in the TreeSet
                            existingSimilarities.setCount(commonNGrams);
                        } else {
                            // Create new Similarities object
                            Similarities similarities = new Similarities(currentDocumentName,
                                    otherDocumentName);
                            similarities.setCount(commonNGrams);

                            // Add the new Similarities object to the TreeSet
                            similaritiesTreeSet.add(similarities);
                        }
                    }

                }
            }
        }

        return similaritiesTreeSet;
    }

    private Similarities findSimilarities(TreeSet<Similarities> similaritiesTreeSet,
                                          String currentDocumentName, String otherDocumentName) {
        for (Similarities similarities : similaritiesTreeSet) {
            if (similarities.containsDocuments(currentDocumentName, otherDocumentName)) {
                return similarities;
            }
        }
        return null;
    }

    /**
     * @param nwordFilePath of the file to store the n-grams
     * @param fileindex     - a list of tuples representing each file and its size
     *                      in nwordFile
     * @return  - a  map containing the file name as the key and the ngrams in
     * that file as the value in a set of strings
     */
    public Map<String, Set<String>>  extractNgrams(String nwordFilePath,
                                                  List<Tuple<String, Integer>> fileindex) {
        // Implement the preprocessing logic to create an index mapping each
        // n-gram to the documents that contain it
        File file = new File(nwordFilePath);
        Map<String, Set<String>> extractedNgramsMap = new HashMap<>();



        try (RandomAccessFile fileReader = new RandomAccessFile(file, "r")) {
            long endPosition = 0;

            // Iterate through file index to extract files ngram
            for (int i = 0; i < fileindex.size(); i++) {
                int fileSize = fileindex.get(i).getRight();
                if (i == 0) {
                    fileReader.seek(endPosition);
                } else {
                    fileReader.seek(endPosition + 1);
                }

                byte[] buffer = new byte[fileSize];
                fileReader.readFully(buffer);
                endPosition = fileReader.getFilePointer();

                // Convert byte array to string and split into ngrams
                String content = new String(buffer);
//                String content = fileReader.readLine();
                String[] ngrams2 = content.split("\\s+"); // ngrams are separated by whitespace
//                List<String> ngrams21 = List.of(ngrams2);
//                List<String> ngramsTwo = List.of(ngrams2);
                List<String> ngramsTwo = new ArrayList<>(Arrays.asList(ngrams2));
//                for (String ngram : ngrams2) {
//                    ngramsTwo.add(ngram);
//                }



                Set<String> ngrams = new HashSet<>(ngramsTwo);
//                Set<String> ngrams = new HashSet<>(List.of(ngrams2));

                extractedNgramsMap.put(fileindex.get(i).getLeft(), ngrams);


            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found, check file path again");
        } catch (IOException e) {
            System.out.println("Trouble moving through file, check file path again");
        }

        return extractedNgramsMap;
    }

    /** This method takes the extracted ngrams from each file and counts how
     * many ngrams they have in common.
     *  @param ngramsFile1      - extracted ngrams from a file from the output file
     *  @param ngramsFile2 - extracted ngrams from a file from the output file
     * @return    commonNGramsCount - number of shared ngrams between the two files.
     * */
    public int calculateCommonNgramsCount(Set<String> ngramsFile1, Set<String> ngramsFile2) {
        int commonNGramsCount = 0;
        if (ngramsFile1.size() < ngramsFile2.size()) {
            for (String otherNGram : ngramsFile1) {
                if (ngramsFile2.contains(otherNGram)) {
                    commonNGramsCount++;
                }
            }
        } else {
            for (String otherNGram : ngramsFile2) {
                if (ngramsFile1.contains(otherNGram)) {
                    commonNGramsCount++;
                }
            }
        }
        return commonNGramsCount;
    }


    /**
     * @param sims      - the TreeSet of Similarities
     * @param threshold - only Similarities with a count greater than threshold
     *                  are printed
     */
    @Override
    public void printSimilarities(TreeSet<Similarities> sims,  int threshold) {
        for (Similarities similarity : sims) {
            if (similarity.getCount() > threshold) {
                System.out.println(similarity.getFile1() + " and " + similarity.getFile2() +
                        " have " + similarity.getCount() + " shared n-grams");
            }
        }
    }

    public static void main(String[] args) {
        DocumentsProcessor d = new DocumentsProcessor();
        List<Tuple<String, Integer>> storedNGrams = new ArrayList<>();
        Map<String, List<String>> nGramsMap = new HashMap<>();
        Map<String, Set<String>> nGramsMap2 = new HashMap<>();
//        n_gramsMap = d.processDocuments("ij", 3);
//        n_gramsMap = d.processDocuments("/Users/ijeomaokoye/IdeaProjects/hw1-plagiarist-students/test_files", 3);
////        n_gramsMap = d.processDocuments("/Users/ijeomaokoye/IdeaProjects/hw1-plagiarist-students/sm_doc_set", 4);
//
//        storedNGrams = d.storeNGrams(n_gramsMap, "/Users/ijeomaokoye/IdeaProjects/hw1-plagiarist-students/new_file/output_test_file.txt");
//        storedNGrams = d.storeNGrams(n_gramsMap, "oust_file.txt");


//
        storedNGrams = d.processAndStore("src/test_folder/test_files", "src/test_folder/new_file/output_test_file.txt", 3);
//          storedNGrams = d.processAndStore("s", "t", 3);
//        storedNGrams = d.processAndStore("n", "uj", 3);
//        System.out.println(n_gramsMap);
        for (Tuple<String, Integer> tuple : storedNGrams) {
            System.out.println(tuple.getLeft());
            System.out.println(tuple.getRight());

        }
        nGramsMap2 = d.extractNgrams("src/test_folder/new_file/output_test_file.txt", storedNGrams);
        System.out.println(nGramsMap2);


        TreeSet<Similarities> treesetSim = d.computeSimilarities("src/test_folder/new_file/output_test_file.txt", storedNGrams);
        System.out.println(treesetSim.size());
        d.printSimilarities(treesetSim, 1);

    }

}
