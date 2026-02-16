
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the stacking items simulator.
 * Tests complex scenarios involving multiple operations in sequence,
 * verifying that StackItem, CupMixLid, and Tower work correctly together.
 * 
 * @author Tomás
 * @version 1.0
 */
public class ComplexTest
{
    private Tower tower;

    /**
     * Sets up the test fixture.
     * Creates a tower with width=10 and maxHeight=50 for complex scenarios.
     */
    @BeforeEach
    public void setUp()
    {
        tower = new Tower(10, 50);
    }

    /**
     * Tears down the test fixture.
     */
    @AfterEach
    public void tearDown()
    {
    }

    // ---- StackItem tests ----

    /**
     * Test StackItem correctly wraps a Cup.
     */
    @Test
    public void testStackItemWrappingCup()
    {
        Cup cup = new Cup(3, 3, "red");
        StackItem item = new StackItem(cup);
        assertEquals(3, item.getId());
        assertEquals(5, item.getHeight()); // 2*3 - 1 = 5
        assertEquals("cup", item.getType());
        assertNotNull(item.getCup());
        assertNull(item.getLid());
    }

    /**
     * Test StackItem correctly wraps a Lid.
     */
    @Test
    public void testStackItemWrappingLid()
    {
        Lid lid = new Lid(3, 3, "red");
        StackItem item = new StackItem(lid);
        assertEquals(3, item.getId());
        assertEquals(1, item.getHeight()); // lids are always 1 cm
        assertEquals("lid", item.getType());
        assertNull(item.getCup());
        assertNotNull(item.getLid());
    }

    // ---- CupMixLid tests ----

    /**
     * Test CupMixLid combines cup and lid correctly.
     */
    @Test
    public void testCupMixLid()
    {
        Cup cup = new Cup(4, 4, "blue");
        Lid lid = new Lid(4, 4, "blue");
        CupMixLid mix = new CupMixLid(cup, lid);
        assertEquals(4, mix.getId());
        assertEquals(8, mix.getHeight()); // 2*4-1 + 1 = 8
        assertSame(cup, mix.getCup());
        assertSame(lid, mix.getLid());
    }

    // ---- Complex Tower scenarios ----

    /**
     * Test full workflow: push cups and lids, order, check lidedCups.
     */
    @Test
    public void testFullWorkflow()
    {
        tower.pushCup(1); // height 1
        tower.pushCup(3); // height 5
        tower.pushCup(2); // height 3
        tower.pushLid(1); // height 1
        tower.pushLid(3); // height 1
        assertTrue(tower.ok());
        assertEquals(11, tower.height()); // 1+5+3+1+1

        tower.orderTower();
        // Expected: cup3, lid3, cup2, cup1, lid1
        String[] si = tower.stackingItems();
        assertEquals(10, si.length);
        assertEquals("cup", si[0]);
        assertEquals("3", si[1]);
        assertEquals("lid", si[2]);
        assertEquals("3", si[3]);
        assertEquals("cup", si[4]);
        assertEquals("2", si[5]);
        assertEquals("cup", si[6]);
        assertEquals("1", si[7]);
        assertEquals("lid", si[8]);
        assertEquals("1", si[9]);

        // lidedCups: cup3 has lid3 on top, cup1 has lid1 on top
        int[] lc = tower.lidedCups();
        assertEquals(2, lc.length);
        assertEquals(3, lc[0]);
        assertEquals(1, lc[1]);
    }

    /**
     * Test push, remove, and verify state consistency.
     */
    @Test
    public void testPushRemoveConsistency()
    {
        tower.pushCup(1);
        tower.pushCup(2);
        tower.pushLid(1);
        assertEquals(5, tower.height()); // 1 + 3 + 1

        tower.removeCup(1);
        assertTrue(tower.ok());
        assertEquals(4, tower.height()); // 3 + 1

        tower.removeLid(1);
        assertTrue(tower.ok());
        assertEquals(3, tower.height()); // only cup 2

        String[] si = tower.stackingItems();
        assertEquals(2, si.length);
        assertEquals("cup", si[0]);
        assertEquals("2", si[1]);
    }

    /**
     * Test order then reverse produces correct sequences.
     */
    @Test
    public void testOrderThenReverse()
    {
        tower.pushCup(2);
        tower.pushCup(1);
        tower.pushCup(3);

        tower.orderTower();
        String[] ordered = tower.stackingItems();
        assertEquals("3", ordered[1]); // cup3 at bottom
        assertEquals("2", ordered[3]); // cup2 middle
        assertEquals("1", ordered[5]); // cup1 at top

        tower.reverseTower();
        String[] reversed = tower.stackingItems();
        assertEquals("1", reversed[1]); // cup1 at bottom
        assertEquals("2", reversed[3]); // cup2 middle
        assertEquals("3", reversed[5]); // cup3 at top
    }

    /**
     * Test pop operations remove correct items from mixed stack.
     */
    @Test
    public void testPopFromMixedStack()
    {
        tower.pushCup(1);
        tower.pushLid(2);
        tower.pushCup(3);

        // popCup should remove cup 3 (topmost cup)
        tower.popCup();
        assertTrue(tower.ok());

        // popLid should remove lid 2 (only lid)
        tower.popLid();
        assertTrue(tower.ok());

        // Only cup 1 remains
        assertEquals(1, tower.height());
        String[] si = tower.stackingItems();
        assertEquals(2, si.length);
        assertEquals("cup", si[0]);
        assertEquals("1", si[1]);
    }

    /**
     * Test orderTower with overflow exclusion.
     */
    @Test
    public void testOrderWithOverflow()
    {
        Tower small = new Tower(10, 10);
        small.pushCup(1); // height 1
        small.pushCup(2); // height 3
        small.pushCup(3); // height 5
        // Total: 9, fits in 10
        assertEquals(9, small.height());

        small.pushLid(1); // +1 = 10, fits
        assertEquals(10, small.height());

        // Now add lid 3 — won't fit (10+1=11 > 10)
        small.pushLid(3);
        assertFalse(small.ok());

        // Order: cup3(5), cup2(3), cup1(1), lid1(1) = 10
        small.orderTower();
        assertEquals(10, small.height());
    }

    /**
     * Test multiple operations and verify ok() reflects latest operation.
     */
    @Test
    public void testOkReflectsLatestOperation()
    {
        tower.pushCup(1);
        assertTrue(tower.ok());

        tower.pushCup(1); // duplicate
        assertFalse(tower.ok());

        tower.pushLid(1);
        assertTrue(tower.ok());

        tower.removeCup(99); // not found
        assertFalse(tower.ok());

        tower.orderTower();
        assertTrue(tower.ok());
    }

    /**
     * Test lidedCups returns sorted ids after reverse.
     */
    @Test
    public void testLidedCupsAfterReverse()
    {
        tower.pushCup(3);
        tower.pushCup(1);
        tower.pushLid(3);
        tower.pushLid(1);
        tower.reverseTower();
        // Expected: cup1, lid1, cup3, lid3
        int[] lc = tower.lidedCups();
        assertEquals(2, lc.length);
        assertEquals(1, lc[0]);
        assertEquals(3, lc[1]);
    }

    /**
     * Test tower with only lids.
     */
    @Test
    public void testTowerWithOnlyLids()
    {
        tower.pushLid(1);
        tower.pushLid(2);
        tower.pushLid(3);
        assertEquals(3, tower.height());
        int[] lc = tower.lidedCups();
        assertEquals(0, lc.length); // no cups to be lided
    }

    /**
     * Test tower with only cups.
     */
    @Test
    public void testTowerWithOnlyCups()
    {
        tower.pushCup(1);
        tower.pushCup(2);
        assertEquals(4, tower.height()); // 1 + 3
        int[] lc = tower.lidedCups();
        assertEquals(0, lc.length); // no lids
    }
}
