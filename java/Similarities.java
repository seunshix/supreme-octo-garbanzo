/**
 * @author ericfouh
 */
public class Similarities implements Comparable<Similarities> {
    /**
     * 
     */
    private String file1;  // n gram file
    private String file2;  // file I want to compare to
    private int    count;


    /**
     * @param file1
     * @param file2
     */
    public Similarities(String file1, String file2) {
        this.file1 = file1;
        this.file2 = file2;
        this.setCount(0);
    }


    /**
     * @return the file1
     */
    public String getFile1() {
        return file1;
    }


    /**
     * @return the file2
     */
    public String getFile2() {
        return file2;
    }


    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }


    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }


    @Override
    public int compareTo(Similarities o) {
        //TODO
        // COMPARE BY COUNT
        int countComparison = Integer.compare(o.count, this.count);
        if (countComparison != 0) {
            return countComparison; // If counts are different, return the result
        } else {
            // If counts are the same, compare by filenames
            int fileNameComparison = this.file1.compareTo(o.file1);
            if (fileNameComparison != 0) {
                return fileNameComparison; // Compare by the first filename
            } else {
                // If the first filenames are the same, compare by the second filenames
                return this.file2.compareTo(o.file2);
            }

        }
    }


    // Method to check if the Similarities object contains the given document names
    public boolean containsDocuments(String document1, String document2) {
        return (file1.equals(document1) && file2.equals(document2)) ||
                (file1.equals(document2) && file2.equals(document1));
    }






}
