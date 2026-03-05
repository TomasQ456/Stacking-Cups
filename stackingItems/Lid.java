import java.awt.Color;

/**
 * Represents a lid for a cup in the tower.
 * Each lid has a unique id (matching its corresponding cup), a logical width,
 * and a color (which should match its cup's color).
 * Lids are always 1 cm tall
 * 
 * @author Acero - Quiceno
 * @version 3.0
 */
public class Lid extends Item {

    /**
     * Create a new Lid with the given properties.
     * 
     * @param id the unique identifier (should match its cup's id)
     * @param color the color of the lid (should match its cup's color)
     */
    public Lid(int id, Color color) {
        super(id, color);
        this.diameter = 2 * id - 1;
        this.type = "lid";
        this.height = thick;
    }
}
