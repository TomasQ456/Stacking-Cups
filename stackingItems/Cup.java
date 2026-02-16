/**
 * Represents a cylindrical cup in the tower.
 * Each cup has a unique id, a logical diameter, and a color.
 * The height is calculated as (2 * id - 1) cm, following the
 * ICPC 2025 "Stacking Cups" problem formula.
 * 
 * <p>The diameter is a logical value (equal to the id) used for
 * relative sizing. The actual pixel dimensions are computed by
 * the GUI layer (CupGUI) using a scale factor.</p>
 * 
 * @author Tomás
 * @version 1.0
 */
public class Cup {
    private int id;
    private int diameter;
    private int height;
    private String color;

    /**
     * Create a new Cup with the given properties.
     * Height is automatically calculated as (2 * id - 1).
     * @param id the unique identifier of the cup (positive integer)
     * @param diameter the logical diameter of the cup
     * @param color the color of the cup (e.g. "red", "blue", "green")
     */
    public Cup(int id, int diameter, String color) {
        this.id = id;
        this.diameter = diameter;
        this.height = 2 * id - 1;
        this.color = color;
    }

    /**
     * Get the unique identifier of this cup.
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Get the logical diameter of this cup.
     * Used for visual proportional sizing and width validation.
     * @return the diameter
     */
    public int getDiameter() {
        return diameter;
    }

    /**
     * Get the height of this cup in cm.
     * Calculated as (2 * id - 1) following the marathon problem formula.
     * @return the height in cm
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the color of this cup.
     * @return the color string (e.g. "red", "blue")
     */
    public String getColor() {
        return color;
    }
}
