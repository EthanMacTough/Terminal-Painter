
///
/// An Object representation of the terminal canvas, stored as a 2D array of enum Pixel objects.
/// 

public class Canvas {

    private final Pixel[][] pixels;

    // Constructor initializes the canvas with a given width and height
    public Canvas(int width, int height) {

        // Initialize 2D array of Pixels
        pixels = new Pixel[height][width];

        // Set all pixels to blank
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y][x] = Pixel.BLANK;
            }
        }

        // Set border around the canvas
        for (int i = 1; i < width - 1; i++) {
            pixels[0][i] = Pixel.BORDER_NS; // Top border
            pixels[height - 1][i] = Pixel.BORDER_NS; // Bottom border
        }
        for (int i = 1; i < height; i++) {
            pixels[i][0] = Pixel.BORDER_EW; // Left border
            pixels[i][width - 1] = Pixel.BORDER_EW; // Right border
        }
    }


    // Return pixel array
    public Pixel[][] getPixels() {    

        return pixels;
    }


    // Paint the pixel(s) at a given coordinate x, y
    public void paintAtCoords(int x, int y) {

        // Check if the coordinates are within the canvas bounds
        if (x > 0 && x < pixels[0].length - 1 && y > 0 && y < pixels.length - 1) {
            pixels[y][x] = Pixel.DRAW; // Set the pixel to drawn
        }
    }
    

    // Public function to clear the canvas
    public void clear() {

        // Set all non-border pixels to 0 (blank)
        for (int i = 1; i < pixels.length - 1; i++) {
            for (int j = 1; j < pixels[i].length - 1; j++) {

                pixels[i][j] = Pixel.BLANK;
            }
        }
    }
}
