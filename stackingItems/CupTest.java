
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Cup class.
 * Verifies constructor, height formula, and getters.
 * 
 * @author Tomás
 * @version 1.0
 */
public class CupTest
{
    private Cup cup1;
    private Cup cup3;
    private Cup cup5;

    /**
     * Sets up the test fixture.
     * Creates cups with different ids for testing.
     */
    @BeforeEach
    public void setUp()
    {
        cup1 = new Cup(1, 1, "red");
        cup3 = new Cup(3, 3, "green");
        cup5 = new Cup(5, 5, "blue");
    }

    /**
     * Tears down the test fixture.
     */
    @AfterEach
    public void tearDown()
    {
    }

    /**
     * Test that Cup returns the correct id.
     */
    @Test
    public void testGetId()
    {
        assertEquals(1, cup1.getId());
        assertEquals(3, cup3.getId());
        assertEquals(5, cup5.getId());
    }

    /**
     * Test that Cup returns the correct diameter.
     */
    @Test
    public void testGetDiameter()
    {
        assertEquals(1, cup1.getDiameter());
        assertEquals(3, cup3.getDiameter());
        assertEquals(5, cup5.getDiameter());
    }

    /**
     * Test that Cup height follows the formula 2*id - 1.
     */
    @Test
    public void testGetHeight()
    {
        assertEquals(1, cup1.getHeight());   // 2*1 - 1 = 1
        assertEquals(5, cup3.getHeight());   // 2*3 - 1 = 5
        assertEquals(9, cup5.getHeight());   // 2*5 - 1 = 9
    }

    /**
     * Test that Cup returns the correct color.
     */
    @Test
    public void testGetColor()
    {
        assertEquals("red", cup1.getColor());
        assertEquals("green", cup3.getColor());
        assertEquals("blue", cup5.getColor());
    }

    /**
     * Test height formula for id = 1 (smallest cup).
     */
    @Test
    public void testSmallestCupHeight()
    {
        Cup smallest = new Cup(1, 1, "red");
        assertEquals(1, smallest.getHeight());
    }

    /**
     * Test height formula for a larger id.
     */
    @Test
    public void testLargeCupHeight()
    {
        Cup large = new Cup(10, 10, "magenta");
        assertEquals(19, large.getHeight()); // 2*10 - 1 = 19
    }
}
