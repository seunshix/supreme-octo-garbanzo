#include <iostream>
#include <fstream>
#include <sstream>
#include <cstring>

// Function to check if the given option is valid
bool isValidOption(const char* option) {
    return std::strcmp(option, "-c") == 0 ||
           std::strcmp(option, "-l") == 0 ||
           std::strcmp(option, "-w") == 0 ||
           std::strcmp(option, "-m") == 0;
}

// Function to count bytes, lines, words, and/or characters in a file or input stream
void countFile(std::istream& input, bool countBytes, bool countLines, bool countWords, bool countChars, std::string filename = "") {
    int byteCount = 0, lineCount = 0, wordCount = 0, charCount = 0;
    std::string line, word;

    while (std::getline(input, line)) {
        if (countLines) ++lineCount;
        if (countWords) {
            std::istringstream lineStream(line);
            while (lineStream >> word) ++wordCount;
        }
        if (countBytes || countChars) {
            byteCount += line.length() + 1; // Include newline
            charCount += std::mblen(line.c_str(), line.length()) + 1; // Count multibyte characters
        }
    }

    // Adjust byte count for files without a trailing newline
    if (input.eof() && !input.fail()) byteCount--;

    // Output results
    if (countLines) std::cout << " " << lineCount;
    if (countWords) std::cout << " " << wordCount;
    if (countBytes) std::cout << " " << byteCount;
    if (countChars) std::cout << " " << charCount;

    if (!filename.empty()) std::cout << " " << filename;

    std::cout << std::endl;
}

int main(int argc, char *argv[]) {
    bool countBytes = false, countLines = false, countWords = false, countChars = false;

    // Handling arguments and determining the mode of operation
    if (argc == 3 && isValidOption(argv[1])) {
        // Set flags based on the specified option
        if (std::strcmp(argv[1], "-c") == 0) countBytes = true;
        else if (std::strcmp(argv[1], "-l") == 0) countLines = true;
        else if (std::strcmp(argv[1], "-w") == 0) countWords = true;
        else if (std::strcmp(argv[1], "-m") == 0) countChars = true;

        // Open the file and handle errors
        std::ifstream file(argv[2]);
        if (!file) {
            std::cerr << "Cannot open file: " << argv[2] << std::endl;
            return 1;
        }
        countFile(file, countBytes, countLines, countWords, countChars, argv[2]);
        file.close();
    } else if (argc == 2 && !isValidOption(argv[1])) {
        // Default case when only the filename is provided
        countBytes = countLines = countWords = true;
        std::ifstream file(argv[1]);
        if (!file) {
            std::cerr << "Cannot open file: " << argv[1] << std::endl;
            return 1;
        }
        countFile(file, countBytes, countLines, countWords, countChars, argv[1]);
        file.close();
    } else if (argc == 1 || (argc == 2 && isValidOption(argv[1]))) {
        // Reading from standard input
        if (argc == 2 && std::strcmp(argv[1], "-m") == 0) countChars = true;
        else countBytes = countLines = countWords = true;

        countFile(std::cin, countBytes, countLines, countWords, countChars);
    } else {
        // Invalid arguments provided
        std::cerr << "Usage: ccwc [-c|-l|-w|-m] [filename]\n";
        return 1;
    }

    return 0;
}

/*
    To run ccwc on file
        - clang++ -o ccwc main.cpp (if using g++ compiler replace 'clang++ with g++)
        - ./ccwc -c | -l | -w | -m 'filename'

    STEP ONE - Focusing on '-c' option
        - Read the file
        - Count the number of bytes in the file
        - Output the count follow by the filename

    STEP TWO - Focusing on '-l' option
        - Read file line by line
        - Count the number of lines in the file

    STEP THREE - Focusing on '-w' option
        - Read words
        - Count the number of words in the file

    STEP FOUR - Focusing on '-m' option
        - Count number of words
        - Similar to '-c'

    STEP FIVE - When no option is specified

    STEP SIX - Reading from standard input

*/