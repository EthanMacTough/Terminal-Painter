import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

///
/// Handles all terminal screen output based on user input.
/// 
/// This program uses Lanterna to represent the terminal as a drawable screen
/// with mouse and keyboard input handling, to create a simple drawing application.
/// 

public class OutputHandler {

    // Canvas object holds a 2D array of Pixel objects
    private final Canvas canvas;

    // Screen object to handle terminal output
    private final Screen screen;

    // Output Handler width and height do not include the border of the canvas
    private final int width;
    private final int height;

    // Graphics object to handle printing to screen
    private final TextGraphics graphics;

    private final Scanner scanner = new Scanner(System.in);


    // Constructor initializes the OutputHandler with the given terminal width and height
    public OutputHandler(Screen screen, int terminalWidth, int terminalHeight) {
        
        // InputHandler passes down its screen object so both classes can use the same screen
        this.screen = screen;
        this.graphics = screen.newTextGraphics();

        // Lanterna has a bug that does not allow mouse input past 94
        // Max dimensions of the canvas are 96x96 to account for border characters
        if (terminalWidth > 96) this.width = 96; else this.width = terminalWidth;
        if (terminalHeight > 96) this.height = 96; else this.height = terminalHeight;

        // Create canvas object with given dimensions
        this.canvas = new Canvas(width, height);
    }


    // Public function to update the screen with a pixel drawn on an (x, y) coordinate
    public void drawPixel(int x, int y) throws IOException {

        // Check bounds of the canvas
        if (x >= width - 1 || y >= height - 1 ||
            x <= 0 || y <= 0) {
            return;
        }

        // Log coordinate paint (x, y) in Canvas object
        canvas.paintAtCoords(x, y);

        // Draw pixel at coordinates (x, y)
        graphics.putString(x, y + 8, "█");

        // Refresh screen
        screen.refresh();
    }


    // Public function to draw on the canvas from (x0, y0) to (x1, y1) using Bresenham's algorithm
    // Uses verbage and methodology from article https://www.geeksforgeeks.org/dsa/bresenhams-line-generation-algorithm/
    public void drawPixel_Bresenham(int x0, int y0, int x1, int y1) throws IOException {

        // Gather slope and error values for Bresenham's algorithm
        int m = 2 * (y1 - y0);
        int slope_error = m - (x1 - x0);

        // Loop through x0 -> x1 coordinates
        for (int x = x0, y = y0; x <= x1; x++) {

            // Draw pixel at (x, y) coordinate
            drawPixel(x, y);

            // Add slope to increment angle formed
            slope_error += m;

            // Slope error reached limit, time to
            // increment y and update slope error.
            if (slope_error >= 0) {
                y++;
                slope_error -= 2 * (x1 - x0);
            }
        }
    }


    // Public function to reset the canvas and screen
    public void clearCanvas() throws IOException {

        // Clear canvas
        canvas.clear();

        // Draw screen with blank canvas
        renderCanvas();
    }


    // Public function to render the canvas on the terminal screen
    public void renderCanvas() throws IOException{

        // Clear screen
        screen.clear();

        // Draw header
        graphics.putString(2, 1, "-- Terminal Painter --");
        graphics.putString(2, 3, "Use mouse input to draw on the canvas.");
        graphics.putString(2, 4, "Press 'q' to quit, 'c' to clear, and 's' to save.");

        // Draw canvas, starting at terminal row 8
        Pixel[][] pixels = canvas.getPixels();

        for (int y = 0; y < pixels.length; y++) {
            for (int x = 0; x < pixels[y].length; x++) {

                String character = switch (pixels[y][x]) {
                    case BLANK -> " ";
                    case DRAW -> "█";
                    case BORDER_NS -> "_";
                    case BORDER_EW -> "|";
                };

                graphics.putString(x, y + 8, character);
            }
        }

        // Refresh output screen
        screen.refresh();
    }


    // Public function to save the canvas as a file with user-input filename
    public void saveCanvas() {

        // Gather filename from terminal user
        System.out.print("Enter filename:\n--- ");
        String fileName = scanner.nextLine().trim();
        System.out.println();

        if (!fileName.isEmpty()) {

            // Check if file already exists
            if (Files.exists(Path.of("./saved_canvas/" + fileName + ".txt"))) {

                System.out.println("File already exists. Save cancelled.");
                
            } else{

                try {
                
                    // Save as a text file
                    saveAsTxt(fileName);

                } catch (IOException e) {

                    System.out.println("Error occurred while saving canvas. Filename may be invalid.");
                    return;
                }

                System.out.println("Canvas saved as " + fileName + ".txt");
            }

        } else {

            System.out.println("Save cancelled.");
        }

        System.out.println("Press Enter to return to the canvas...");
        scanner.nextLine();
    }

    
    // Private function to save the canvas as a text file
    private void saveAsTxt(String filename) throws IOException {

        // Set path as ./saved_canvas/filename.txt
        Path dir = Paths.get("./saved_canvas");
        Files.createDirectories(dir);

        // Open writer for new file with given filename
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dir.resolve(filename + ".txt").toFile()))) {

            // Write filename at top of text file
            writer.write(filename);
            writer.newLine();
            writer.newLine();
            
            // Write canvas data into file
            Pixel[][] pixels = canvas.getPixels();
            for (Pixel[] pixelArr : pixels) {
                for (Pixel pixel : pixelArr) {
                    String character = switch (pixel) {
                        case BLANK -> " ";
                        case DRAW -> "█";
                        case BORDER_NS -> "_";
                        case BORDER_EW -> "|";
                    };
                    writer.write(character);
                }
                writer.newLine();
            }
        }
    }
}