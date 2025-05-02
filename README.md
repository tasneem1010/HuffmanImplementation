# Huffman Coding Implementation

A Java-based implementation of the Huffman coding algorithm for lossless data compression and decompression. This project provides both a command-line interface and a graphical user interface for compressing and decompressing files using Huffman coding.

## Overview

Huffman coding is a lossless data compression algorithm that assigns variable-length codes to input characters, with shorter codes assigned to more frequent characters. This implementation includes:

- File compression and decompression
- Custom Huffman tree generation
- Binary file handling
- GUI interface for easy interaction
- Command-line interface for batch processing

## Project Structure

```
src/main/java/com/example/huffman/
├── MainScreen.java    # GUI implementation
├── Compress.java      # Compression logic
├── Decompress.java    # Decompression logic
├── Huffman.java       # Core Huffman algorithm
├── MinHeap.java       # Priority queue implementation
├── Node.java          # Tree node structure
└── Driver.java        # Main entry point
```

## Features

- **Compression**: Convert text files into compressed binary format (.huff)
- **Decompression**: Restore original files from compressed format
- **GUI Interface**: User-friendly interface for file operations
- **Efficient Implementation**: Uses min-heap for optimal performance
- **Cross-Platform**: Works on Windows, macOS, and Linux

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Installation

1. Clone the repository:
   ```bash
   git clone [repository-url]
   ```

2. Navigate to the project directory:
   ```bash
   cd Huffman
   ```

3. Build the project using Maven:
   ```bash
   ./mvnw clean install
   ```

## Usage

### GUI Mode

1. Run the application:
   ```bash
   ./mvnw exec:java -Dexec.mainClass="com.example.huffman.Driver"
   ```

2. Use the graphical interface to:
   - Select files for compression
   - Choose files for decompression
   - View compression statistics

### Command Line Mode

1. Compress a file:
   ```bash
   java -cp target/classes com.example.huffman.Compress input.txt output.huff
   ```

2. Decompress a file:
   ```bash
   java -cp target/classes com.example.huffman.Decompress input.huff output.txt
   ```

## File Formats

- **Input Files**: Plain text files (.txt)
- **Compressed Files**: Binary files with .huff extension
- **Decompressed Files**: Restored text files (.txt)

## Performance

The implementation achieves good compression ratios for text files, especially those with repeated patterns. The compression ratio depends on the input file's content and character distribution.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Based on David A. Huffman's original algorithm
- Built with Java and JavaFX for the GUI 