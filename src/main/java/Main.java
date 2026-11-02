
///
/// Main class. Starts the Input Handler, which loops the terminal painter.
/// 

public class Main {

    private static InputHandler inputHandler;

    public static void main(String[] args) throws Exception {

        inputHandler = new InputHandler();
        inputHandler.start();
    }
}
