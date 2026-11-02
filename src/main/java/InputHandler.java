import java.io.IOException;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.MouseAction;
import com.googlecode.lanterna.input.MouseActionType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.MouseCaptureMode;

///
/// Handles all mouse and keyboard input from the terminal.
/// 
/// This program uses Lanterna to represent the terminal as a drawable screen
/// with mouse and keyboard input handling, to create a simple drawing application.
/// 

public class InputHandler {

    private final OutputHandler outputHandler;
    private final DefaultTerminalFactory factory;
    private final Screen screen;
 
    // Constructor initializes the OutputHandler with the given terminal width and height
    public InputHandler() throws IOException {

        // Create a terminal screen with mouse capture enabled
        factory = new DefaultTerminalFactory()
            .setForceTextTerminal(true)
            .setMouseCaptureMode(
                MouseCaptureMode.CLICK_RELEASE_DRAG
            );

        // Create a screen that handles all input and output
        screen = factory.createScreen();
        screen.startScreen();
        screen.setCursorPosition(null);

        // Get the terminal size
        TerminalSize size = screen.getTerminalSize();

        // Initialize the OutputHandler with the terminal size (with padding)
        outputHandler = new OutputHandler(screen, size.getColumns(), size.getRows() - 8);
    }

    
    // Function to handle user input on the terminal screen and call OutputHandler
    public void start() throws IOException {

        // Used for line generation for mouse dragging to prevent input skipping
        int previousX = -1;
        int previousY = -1;

        outputHandler.clearCanvas();

        boolean running = true;

        // Main loop to handle input events
        while (running) {

            // Read user input (mouse and keyboard)
            KeyStroke keyStroke = screen.readInput();

            // Determine input type (character, mouse event or EOF)
            switch (keyStroke.getKeyType()) {

                // Handle character (keyboard) input
                case KeyType.Character -> {

                    char character = keyStroke.getCharacter();

                    switch (character) {

                        // Quit
                        case 'q' -> running = false;

                        // Clear Canvas
                        case 'c' -> outputHandler.clearCanvas();

                        // Save Canvas
                        case 's' -> {

                            // Return to terminal
                            screen.stopScreen();

                            // Save canvas with user input
                            outputHandler.saveCanvas();

                            // Start and redraw screen canvas as it was
                            screen.startScreen();
                            screen.setCursorPosition(null);
                            outputHandler.renderCanvas();
                        }
                    }
                }

                // Handle mouse input
                case KeyType.MouseEvent -> {

                    // Handle mouse input
                    MouseAction mouse = (MouseAction) keyStroke;

                    // Get mouse coordinates (Y is offset by 8 because of header text)
                    int mouseX = mouse.getPosition().getColumn();
                    int mouseY = mouse.getPosition().getRow() - 8;

                    // Get type of mouse action (click, drag, release)
                    MouseActionType action = mouse.getActionType();

                    // Handle mouse input based on action type
                    switch (action) {

                        // Draw a single pixel on a simple click
                        case CLICK_DOWN -> {

                            previousX = mouseX;
                            previousY = mouseY;

                            outputHandler.drawPixel(mouseX, mouseY);
                        }

                        // Draw a line between drag inputs to prevent skipping
                        case DRAG -> {

                            // If there is a previous coordinate, draw a line to the new coords
                            if (previousX != -1 && previousY != -1) {
                                
                                outputHandler.drawPixel_Bresenham(
                                    previousX, previousY,
                                    mouseX, mouseY
                                );

                            // Handle normal mouse action if there are no previous coordinates
                            } else {

                                outputHandler.drawPixel(mouseX, mouseY);
                            }

                            // Gather new previous mouse coordinates
                            previousX = mouseX;
                            previousY = mouseY;
                        }

                        // Reset coordinates upon mouse release
                        case CLICK_RELEASE -> {

                            previousX = -1;
                            previousY = -1;
                        }

                        // Ignore unhandled input
                        default -> {}
                    }
                }

                case KeyType.EOF -> running = false;

                // Ignore unhandled input
                default -> {}
            }
        }

        screen.stopScreen();
    }
}