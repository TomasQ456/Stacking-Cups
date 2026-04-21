package tower;

import javax.swing.JOptionPane;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Controls the visibility of the stacking tower simulation window and
 * displays error dialogs to the user. Singleton pattern.
 *
 * @author Acero - Quiceno
 * @version 5.0
 */
public class TowerCanvas {

    private static TowerCanvas instance;

    /**
     * Returns the singleton TowerCanvas instance, creating it if necessary.
     *
     * @return the TowerCanvas singleton
     */
    public static TowerCanvas getInstance() {
        if (instance == null) {
            instance = new TowerCanvas();
        }
        return instance;
    }

    private TowerGUI towerGUI;
    private boolean visible;

    /**
     * Private constructor for singleton pattern.
     */
    private TowerCanvas() {
        visible = false;
    }

    /**
     * Binds a tower model to this GUI, creating a fresh TowerGUI.
     *
     * @param tower the tower to display
     */
    public void bindTower(Tower tower) {
        if (towerGUI != null) {
            towerGUI.eraseAll();
        }
        towerGUI = new TowerGUI(tower);
    }

    /**
     * Shows the canvas window and renders the current tower.
     *
     * @return true if shown; false if the image does not fit on screen
     */
    public boolean show() {
        if (!fitsOnScreen()) {
            return false;
        }
        // Removed legacy BlueJ Canvas invocation here.
        visible = true;
        if (towerGUI != null) {
            towerGUI.render();
        }
        return true;
    }

    /**
     * Hides the canvas window and erases all drawn elements.
     */
    public void hide() {
        if (towerGUI != null) {
            towerGUI.eraseAll();
        }
        // Removed legacy BlueJ Canvas invocation here.
        visible = false;
    }

    /**
     * Re-renders the current tower state. Called after every tower mutation
     * when visible.
     */
    public void refresh() {
        if (visible && towerGUI != null) {
            towerGUI.render();
        }
    }

    /**
     * Displays an error message via JOptionPane when the simulator is visible.
     *
     * @param message the error message to display
     */
    public void showError(String message) {
        if (visible && towerGUI != null && !towerGUI.message()) {
            JOptionPane.showMessageDialog(
                null,
                message,
                "Oh come on, what happen my friend!",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    /**
     * Terminates the GUI: hides the window, erases all elements,
     * and resets the singleton.
     */
    public void dispose() {
        hide();
        towerGUI = null;
        instance = null;
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    /**
     * Checks if the tower image fits within the available screen dimensions.
     *
     * @return true if it fits on screen
     */
    private boolean fitsOnScreen() {
        if (towerGUI == null) return true;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        return towerGUI.requiredWidth()  <= screen.width  - 50
            && towerGUI.requiredHeight() <= screen.height - 100;
    }
}