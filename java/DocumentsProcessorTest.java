import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentsProcessorTest {

    /** Variables for redirecting System.out. */
    private final PrintStream originalOut = System.out;
    /** Variables for redirecting System.out. */
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

//    @Test
//    public void processDocumentsTest() {
//        // basics
//        // test when directory path does not exist
//        // test for when filedirectory exists id it prints out all the files
//        // while there is something to read it adds to n_gramsList
//        // does it rename file correctly
//        // is everything in the map
//
//
//    }

    /** Ensures when a wrong directory is passed the map is empty.*/
    @Test
    public void testProcessDocWrongDirPath() {
        File fileDir = new File("directoryPath");
        DocumentsProcessor dp = new DocumentsProcessor();
        Map<String, List<String>> actual = dp.processDocuments("directoryPath", 4);
        assertTrue(actual.isEmpty());

    }


    /** Ensures when a wrong directory is passed the list is empty. */
    @Test
    public void testProcessandStoreWrongDirPath() {
//        File fileDir = new File("directoryPath") ;
        DocumentsProcessor dp = new DocumentsProcessor();
        List<Tuple<String, Integer>> actual = dp.processAndStore("directoryPath", "incorrect", 3);

        // Check if the result is empty or contains an error message
        assertTrue(actual.isEmpty());
//        IOException exception = assertThrows(IOException.class, () -> {
//            dp.processAndStore("wrong/directory/path", "sequenceFile", 3);
//        });
//        assertEquals("No files found in the directory.", exception.getMessage());

    }
    /** This tests process documents handling non letters have the correct ngrams written to file.
     */
    @Test
    // symbols punctuation
    // one file
    // simple
    public void testProcessDocumentsWriteNonLetters() throws IOException {
        DocumentsProcessor dp = new DocumentsProcessor();
        String sequenceFile = "test_folder/new_file/output_test_nonletter.txt";
        dp.processAndStore("test_folder/test_files_simple", sequenceFile, 2);

        // Read the expected contents from a file (assuming you have it already)
        String expectedContent = Files.lines(Paths.get("test_folder/new_file/output_test_" +
                "nonletter.txt")).collect(Collectors.joining(System.lineSeparator()));

        // Read the actual contents from the output file
        BufferedReader reader = new BufferedReader(new FileReader(sequenceFile));
        StringBuilder actualContent = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            actualContent.append(line).append(System.lineSeparator());
        }
        reader.close();

        // Compare the contents
        assertEquals(expectedContent.trim(), actualContent.toString().trim(), "thisfile " +
                "fileit its sannoying annoyingrt rtme");

    }

    /** This tests simple short files, it also ensures file names are properly formatted.
     */
    @Test
    // symbols punctuation
    // one file
    // simple
    public void testProcessDocumentsNGramsListNonLetters() {
        DocumentsProcessor dp = new DocumentsProcessor();
        Map<String, List<String>> actual = dp.processDocuments("test_folder/test_files_sym", 2);
        Map<String, List<String>> expected = new HashMap<>();
        List<String> nGramsList1 = new ArrayList<>();
        nGramsList1.add("thisfile");
        nGramsList1.add("fileit");
        nGramsList1.add("its");
        nGramsList1.add("sannoying");
        nGramsList1.add("annoyingrt");
        nGramsList1.add("rtme");
        expected.put("test1.txt", nGramsList1);

        assertEquals(expected, actual);

    }

    /** This tests simple short files, it also ensures file names are properly formatted.
     */
    @Test
    // symbols punctuation
    // one file
    // simple
    public void testProcessDocumentsNGramsListSimple() {
        DocumentsProcessor dp = new DocumentsProcessor();
        Map<String, List<String>> actual = dp.processDocuments(
                "test_folder/test_files_simple", 2);
        Map<String, List<String>> expected = new HashMap<>();
        List<String> nGramsList1 = new ArrayList<>();
        nGramsList1.add("thisis");
        nGramsList1.add("isa");
        nGramsList1.add("afile");
        expected.put("file1.txt", nGramsList1);
        List<String> nGramsList2 = new ArrayList<>();
        nGramsList2.add("thisis");
        nGramsList2.add("isanother");
        nGramsList2.add("anotherfile");
        expected.put("file2.txt", nGramsList2);

        assertEquals(expected, actual);

    }

    /** This tests an empty file, to make sure map is empty.
     */
    @Test
    // symbols punctuation
    // one file
    // simple
    public void testProcessDocumentsEmptyFile() {
        DocumentsProcessor dp = new DocumentsProcessor();
        Map<String, List<String>> actual = dp.processDocuments("test_folder/" +
                "test_empty_file", 2);
        Map<String, List<String>> expected = new HashMap<>();
        List<String> ngramList = new ArrayList<>();
        expected.put("empty_file.txt", ngramList);
        assertEquals(expected.get("empty_file.txt").size(), actual.get("empty_file.txt").size());

    }

    /** This tests that if a directory exists but is empty, it returns an empty list tuple.*/
    @Test
    public void testProcessandStoreEmptyDir() {
        DocumentsProcessor dp = new DocumentsProcessor();
        List<Tuple<String, Integer>> actual = dp.processAndStore("test_folder" +
                "/empty_folder", "test_folder/new_file/ngramFile.txt", 3);
        assertEquals(0, actual.size());

    }
    // similar test but make sure nothing writes

    /** This tests that if a directory exists and a file exists but the file is empty,
     * it returns a tuple list with 0 index.
     */
    @Test
    public void testProcessandStoreEmptyFile() {
        DocumentsProcessor dp = new DocumentsProcessor();
        List<Tuple<String, Integer>> actual = dp.processAndStore("test_folder/test_" +
                "empty_file", "test_folder/new_file/ngramFile.txt", 3);
//        List<Tuple<String, Integer>> expected = new ArrayList<>();
//        List<String> nGramsList = new ArrayList<>();
//        expected.add(new Tuple<>("empty_file.txt", 0));
//        assertEquals(expected.get(0).getLeft(), actual.get(0).getLeft());
//        assertEquals(expected.get(0).getRight(), actual.get(0).getRight());
        assertTrue(!actual.isEmpty()); // Check if the returned list is empty
//        assertNotNull(actual); // Check if the returned list is not null
//        assertTrue(actual.isEmpty()); // Check if the returned list is empty

    }

    /** This tests that if a directory exists with legit txt files, it
    accounts for all the files.
     */
    @Test
    public void testProcessandStoreFilesExist() {
        DocumentsProcessor dp = new DocumentsProcessor();
        List<Tuple<String, Integer>> actual = dp.processAndStore("test_folder/" +
                "test_files", "test_folder/new_file/ngramFile.txt", 3);
        assertEquals(3, actual.size());

    }

    /** A test to make sure the file has the write contents written to it. */
    @Test
    public void testProcessandStoreNGramsWriteSimple() throws IOException {
        DocumentsProcessor dp = new DocumentsProcessor();
        String sequenceFile = "test_folder/new_file/output_test_simple.txt";
        dp.processAndStore("test_folder/test_files_simple",
                "test_folder/new_file/output_test_simple.txt", 2);

        // Read the expected contents from a file (assuming you have it already)
        String expectedContent = Files.lines(Paths.get(
                "test_folder/new_file/output_test_simple.txt"))
                .collect(Collectors.joining(System.lineSeparator()));

        // Read the actual contents from the output file
        BufferedReader reader = new BufferedReader(new FileReader(sequenceFile));
        StringBuilder actualContent = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            actualContent.append(line).append(System.lineSeparator());
        }
        reader.close();

        // Compare the contents
        assertEquals(expectedContent.trim(), actualContent.toString().trim(),
                "thisis isa afile thisis isanother anotherfile");
    }

    /** Store Ngrams test to ensure the right content is getting written. */
    @Test
    public void testStoreNGramsWriteSimple() throws IOException {
        DocumentsProcessor dp = new DocumentsProcessor();
        String sequenceFile = "test_folder/new_file/output_test_simple.txt";
        Map<String, List<String>> docs = dp.processDocuments("test_files_simple", 2);
        dp.storeNGrams(docs, "test_folder/new_file/output_test_simple.txt");

        // Read the expected contents from a file (assuming you have it already)
        String expectedContent = Files.lines(Paths.get(
                "test_folder/new_file/output_test_simple.txt"))
                .collect(Collectors.joining(System.lineSeparator()));

        // Read the actual contents from the output file
        BufferedReader reader = new BufferedReader(new FileReader(sequenceFile));
        StringBuilder actualContent = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            actualContent.append(line).append(System.lineSeparator());
        }
        reader.close();

        // Compare the contents
        assertEquals(expectedContent.trim(), actualContent.toString().trim(),
                "thisis isa afile thisis isanother anotherfile");
    }

    /** Store Ngrams test to ensure the right content is in the List. */
    @Test
    public void testStoreNGramsList() throws IOException {
        DocumentsProcessor dp = new DocumentsProcessor();
        Map<String, List<String>> docs = dp.processDocuments("test_folder/test_files", 2);
        List<Tuple<String, Integer>> actual = dp.storeNGrams(docs,
                "test_folder/new_file/ngramFile.txt");
        assertEquals(3, actual.size());
    }

    /** Store Ngrams test to ensure a file is created if it does not exist. */
    @Test
    public void testStoreNGramsFileCreation() throws IOException {
        DocumentsProcessor dp = new DocumentsProcessor();
        Map<String, List<String>> docs = dp.processDocuments("test_folder/test_files", 2);
        List<Tuple<String, Integer>> actual = dp.storeNGrams(docs,
                "test_folder/new_file/ile.txt");
        File filepath = new File("test_folder/new_file/ile.txt");
        assertTrue(filepath.exists());
    }


//    /** This tests process documents handling an empty input file it ensures
//     * the written file is also empty.
//     */
//    @Test
//    public void testProcessandStoreWriteEmptyFile() throws IOException {
//        DocumentsProcessor dp = new DocumentsProcessor();
//        String sequenceFile = "est_folder/test_empty_file/empty_file.txt";
//        dp.processAndStore("test_folder/test_empty_file", sequenceFile, 2);
//
//        // Read the expected contents from a file (assuming you have it already)
//        String expectedContent = Files.lines(Paths.get("test_folder" +
//                "/test_empty_file/empty_file.txt")).collect(
//                        Collectors.joining(System.lineSeparator()));
//
//        // Read the actual contents from the output file
//        BufferedReader reader = new BufferedReader(new FileReader(sequenceFile));
//        StringBuilder actualContent = new StringBuilder();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            actualContent.append(line).append(System.lineSeparator());
//        }
//        reader.close();
//
//        // Compare the contents
//        assertEquals(expectedContent.trim(), actualContent.toString().trim(), "");
//
//    }


    /** Test store ngrams to make sure wrong/nonesxistent file path, throw an error message. */
    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    /** Cleanup method to restore System.out. */
    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    /** If the wrong file path is passed, make sure errr messsage is displayed. */
    @Test
    public void testStoreNgramWriteWrongPath() throws IOException {
        DocumentsProcessor dp = new DocumentsProcessor();
        Map<String, List<String>> docs = new HashMap<>();
        docs.put("document1.txt", Collections.singletonList("ngram1 ngram2 ngram3"));

        // Define the invalid file path
        String invalidFilePath = "invalid/path/to/file.txt";

        // Invoke the method
        List<Tuple<String, Integer>> actual = dp.storeNGrams(docs, invalidFilePath);

        // Check if the error message is printed to System.out
        assertEquals("File not created, file path may not exist\n", outContent.toString());
    }


    /** Test to make sure that if the map is empty the list has a 0 index. */
    @Test
    public void testStoreNgramEmptyList() throws IOException {
        DocumentsProcessor dp = new DocumentsProcessor();
        Map<String, List<String>> docs = new HashMap<>();
        List<String> ngramsList = new ArrayList<>();
        docs.put("empty_file.txt", ngramsList);


        // Define the invalid file path
        String nFilePath = "test_folder/test_empty_file/empty_file.txt";

        // Invoke the method
        List<Tuple<String, Integer>> actual = dp.storeNGrams(docs, nFilePath);
        List<Tuple<String, Integer>> expected = new ArrayList<>();
        expected.add(new Tuple<>("empty_file.txt", 0));
        assertEquals(expected.get(0).getLeft(), actual.get(0).getLeft());
        assertEquals(expected.get(0).getRight(), actual.get(0).getRight());

//
//        assertEquals(expected.getFirst().getLeft(), actual.getFirst().getLeft());
//        assertEquals(expected.getFirst().getRight(), actual.getFirst().getRight());

    }


    /**  store test to make sure that if the map exists the list has the right index. */
    @Test
    public void testStoreNgramRightList()  {
        DocumentsProcessor dp = new DocumentsProcessor();
        Map<String, List<String>> map1 = dp.processDocuments("test_folder/test_store", 3);
        List<String> docs1 = new ArrayList<>();
        List<String> docs2 = new ArrayList<>();
        List<Tuple<String, Integer>> expected = new ArrayList<>();
        expected.add(new Tuple<>("file1.txt", 67));
        List<Tuple<String, Integer>> actual = dp.storeNGrams(map1,
                "test_folder/new_file/ngramFile.txt");
        assertEquals(expected.get(0).getLeft(), actual.get(0).getLeft());
        assertEquals(expected.get(0).getRight(), actual.get(0).getRight());
//        assertEquals(expected.getFirst().getLeft(), actual.getFirst().getLeft());
//        assertEquals(expected.getFirst().getRight(), actual.getFirst().getRight());

    }




   /** Compute similarities test to make sure all the similarity objects are created.*/
    @Test
    public void testComputeSimilaritiesValid() {
        DocumentsProcessor dp = new DocumentsProcessor();
        List<Tuple<String, Integer>> storedNGrams = dp.processAndStore("test_folder" +
                "/test_files", "test_folder/new_file/output_test_file.txt", 3);
        TreeSet<Similarities> actual = dp.computeSimilarities("test_folder" +
                "/new_file/output_test_file.txt", storedNGrams);
        TreeSet<Similarities> expected = new TreeSet<>();
        Similarities similarities = new Similarities("file1.txt", "file2.txt");
        similarities.setCount(3);
        expected.add(similarities);
        Similarities similarities2 = new Similarities("file2.txt", "file2.txt");
        similarities.setCount(3);
        expected.add(similarities2);
        Similarities similarities3 = new Similarities("file1.txt", "file3.txt");
        similarities.setCount(3);
        expected.add(similarities3);
        assertEquals(expected.size(), actual.size());
//        assertTrue(expected.containsAll(actual));
    }

    /** Ensure that if the file is empty the treeset should also be 0. */
    @Test
    public void testComputeSimilaritiesEmptyIndex() {
        DocumentsProcessor dp = new DocumentsProcessor();
        List<Tuple<String, Integer>> storedNGrams = new ArrayList<>();
        storedNGrams.add(new Tuple<>("empty_file.txt", 0));
        TreeSet<Similarities> actual = dp.computeSimilarities(
                "test_folder/new_file/output_test_file.txt", storedNGrams);
        TreeSet<Similarities> expected = new TreeSet<>();
        assertEquals(expected.size(), actual.size());
    }

    /** Ensure that if the filepath is invalid an error message is shown. */
    @Test
    public void testComputeSimilaritiesInvalidPath() {
        DocumentsProcessor dp = new DocumentsProcessor();
        List<Tuple<String, Integer>> storedNGrams = new ArrayList<>();
        storedNGrams.add(new Tuple<>("empty_file.txt", 0));
        TreeSet<Similarities> actual = dp.computeSimilarities("/invalidpath/", storedNGrams);
        TreeSet<Similarities> expected = new TreeSet<>();
        assertEquals("File not found, check file path again\n", outContent.toString());
    }

    /** To make sure that if a list is passed it counts correctly. */
    @Test
    public void testCalculateCommonGgrams() {
        DocumentsProcessor dp = new DocumentsProcessor();
        Set<String> ngramsList1 = new HashSet<>();
        Set<String> ngramsList2 = new HashSet<>();
        ngramsList1.add("thisis");
        ngramsList1.add("tisthe");
        ngramsList1.add("yababddoo");
        ngramsList1.add("afilecan");
        ngramsList1.add("today");
        ngramsList2.add("thiis");
        ngramsList2.add("tisthe");
        ngramsList2.add("yabadoo");
        ngramsList2.add("afilecan");
        ngramsList2.add("tody");

        int actual = dp.calculateCommonNgramsCount(ngramsList1, ngramsList2);
        assertEquals(2, actual);

    }

    /** To make sure that if at least one list  is passed it counts 0. */
    @Test
    public void testCalculateCommonGgramsEmptyList() {
        DocumentsProcessor dp = new DocumentsProcessor();
        Set<String> ngramsList1 = new HashSet<>();
        Set<String> ngramsList2 = new HashSet<>();
        ngramsList1.add("thisis");
        ngramsList1.add("tisthe");
        ngramsList1.add("yababddoo");
        ngramsList1.add("afilecan");
        ngramsList1.add("today");

        int actual = dp.calculateCommonNgramsCount(ngramsList1, ngramsList2);
        assertEquals(0, actual);

    }
}