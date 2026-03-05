
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Lid class.
 * Verifies constructor and getters.
 * 
 * @author Tomás
 * @version 1.0
 */
public class LidTest
{
    private Lid lid1;
    private Lid lid3;

    /**
     * Sets up the test fixture.
     * Creates lids with different ids for testing.
     */
    @BeforeEach
    public void setUp()
    {
        lid1 = new Lid(1, 1, "red");
        lid3 = new Lid(3, 3, "green");
    }

    /**
     * Tears down the test fixture.
     */
    @AfterEach
    public void tearDown()
    {
    }

    /**
     * Test that Lid returns the correct id.
     */
    @Test
    public void testGetId()
    {
        assertEquals(1, lid1.getId());
        assertEquals(3, lid3.getId());
    }

    /**
     * Test that Lid returns the correct width.
     */
    @Test
    public void testGetWidth()
    {
        assertEquals(1, lid1.getWidth());
        assertEquals(3, lid3.getWidth());
    }

    /**
     * Test that Lid returns the correct color.
     */
    @Test
    public void testGetColor()
    {
        assertEquals("red", lid1.getColor());
        assertEquals("green", lid3.getColor());
    }

    /**
     * Test that Lid color matches its cup color convention.
     */
    @Test
    public void testLidColorMatchesCup()
    {
        Cup cup = new Cup(2, 2, "blue");
        Lid lid = new Lid(2, 2, cup.getColor());
        assertEquals(cup.getColor(), lid.getColor());
    }
}
