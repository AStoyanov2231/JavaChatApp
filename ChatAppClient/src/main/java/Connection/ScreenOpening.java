package Connection;

import javax.swing.*;

public class ScreenOpening {
    public static void openRelativeScreen(JFrame currentScreen, JFrame newScreen) {

        java.awt.Point location = currentScreen.getLocation();

        SwingUtilities.invokeLater(() -> {
            newScreen.setSize(800, 620); // Set desired size for the new screen
            newScreen.setLocation(location); // Adjust location as needed
            newScreen.setVisible(true);
        });
    }

    public static void  OpenGroupManagerScreen(JFrame currentScreen, JFrame newScreen){
        java.awt.Point location = currentScreen.getLocation();

        SwingUtilities.invokeLater(() -> {
            newScreen.setSize(600,540);;
            newScreen.setLocation(location);
            newScreen.setVisible(true);
        });
    }
}
