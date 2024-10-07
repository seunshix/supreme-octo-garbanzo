import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class DocumentIterator implements Iterator<String> {

    private Reader r;
    private int n;
    private int    c = -1;
    private List<String> prevNGram;



    public DocumentIterator(Reader r, int n) {
        this.r = r;
        this.n = n;
        prevNGram = new ArrayList<>();
        skipNonLetters();
    }


    private void skipNonLetters() {
        try {
            this.c = this.r.read();
            while (!Character.isLetter(this.c) && this.c != -1) {
                this.c = this.r.read();
            }
        } catch (IOException e) {
            this.c = -1;
        }
    }


    @Override
    public boolean hasNext() {
        return (c != -1);
    }


    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        StringBuilder answer = new StringBuilder();


        if (prevNGram.isEmpty() && this.c != -1) {

            for (int i = 0; i < n; i++) {
                StringBuilder temp = new StringBuilder();
                while (Character.isLetter(this.c)) {

                    // append character to answer
                    answer.append((char) Character.toLowerCase(this.c));
                    temp.append((char) Character.toLowerCase(this.c));

                    try {
                        this.c = this.r.read();
                    } catch (IOException e) {
                        System.out.println("Trouble reading file in the next method");
                    }
                }

                skipNonLetters();
                prevNGram.add(String.valueOf(temp));

            }
            prevNGram.remove(0);
//            prevNGram.removeFirst();
            return String.valueOf(answer);

        }

        if (prevNGram.size() == n - 1) {
            try {
                StringBuilder temp = new StringBuilder();
                answer = new StringBuilder(String.join("", prevNGram));

//                answer.append(prevNGram);
//                for (int i = 0; i < n; i++) {
                while (Character.isLetter(this.c)) {
                    // append character to answer
                    answer.append((char) Character.toLowerCase(this.c));
                    temp.append((char) Character.toLowerCase(this.c));
                    this.c = this.r.read();
                }


                skipNonLetters();
                if (prevNGram.size() > 0) {
//              prevNGram.addLast(String.valueOf((temp)));
                    prevNGram.add(prevNGram.size(), String.valueOf(temp));
                    prevNGram.remove(0);
//              prevNGram.removeFirst();
                } else {
                    System.out.println("prev ngram is empty");
                }

            } catch (IOException e) {
                System.out.println("Trouble reading file in the next method");
            }
            return String.valueOf(answer);  // Convert StringBuilder to String

        }

        return String.valueOf(answer);  // Convert StringBuilder to String
    }

}
