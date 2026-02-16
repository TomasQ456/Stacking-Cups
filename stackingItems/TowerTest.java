
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Tower class.
 * Tests all public methods: push, pop, remove, order, reverse,
 * height, lidedCups, stackingItems, and ok.
 * 
 * @author Tomás
 * @version 1.0
 */
public class TowerTest
{
    private Tower tower;

    /**
     * Sets up the test fixture.
     * Creates a tower with width=10 and maxHeight=30 for general testing.
     */
    @BeforeEach
    public void setUp()
    {
        tower = new Tower(10, 30);
    }

    /**
     * Tears down the test fixture.
     */
    @AfterEach
    public void tearDown()
    {
    }

    // ---- Constructor ----

    /**
     * Test that a new tower starts with height 0 and ok = true.
     */
    @Test
    public void testConstructor()
    {
        assertEquals(0, tower.height());
        assertTrue(tower.ok());
    }

    // ---- pushCup ----

    /**
     * Test pushing a single cup increases the height correctly.
     */
    @Test
    public void testPushCupSingle()
    {
        tower.pushCup(1);
        assertTrue(tower.ok());
        assertEquals(1, tower.height()); // 2*1 - 1 = 1
    }

    /**
     * Test pushing multiple cups accumulates height.
     */
    @Test
    public void testPushCupMultiple()
    {
        tower.pushCup(1); // height 1
        tower.pushCup(2); // height 3
        tower.pushCup(3); // height 5
        assertTrue(tower.ok());
        assertEquals(9, tower.height()); // 1 + 3 + 5 = 9
    }

    /**
     * Test pushing a cup with duplicate id fails.
     */
    @Test
    public void testPushCupDuplicate()
    {
        tower.pushCup(1);
        tower.pushCup(1);
        assertFalse(tower.ok());
        assertEquals(1, tower.height()); // only first cup added
    }

    /**
     * Test pushing a cup that exceeds tower width fails.
     */
    @Test
    public void testPushCupExceedsWidth()
    {
        tower.pushCup(11); // diameter 11 > width 10
        assertFalse(tower.ok());
        assertEquals(0, tower.height());
    }

    /**
     * Test pushing a cup that exceeds tower height fails.
     */
    @Test
    public void testPushCupExceedsHeight()
    {
        Tower small = new Tower(10, 3);
        small.pushCup(3); // height = 5 > maxHeight 3
        assertFalse(small.ok());
        assertEquals(0, small.height());
    }

    /**
     * Test pushing a cup with non-positive id fails.
     */
    @Test
    public void testPushCupNonPositiveId()
    {
        tower.pushCup(0);
        assertFalse(tower.ok());
        tower.pushCup(-1);
        assertFalse(tower.ok());
        assertEquals(0, tower.height());
    }

    // ---- pushLid ----

    /**
     * Test pushing a single lid increases height by 1.
     */
    @Test
    public void testPushLidSingle()
    {
        tower.pushLid(1);
        assertTrue(tower.ok());
        assertEquals(1, tower.height());
    }

    /**
     * Test pushing a lid with duplicate id fails.
     */
    @Test
    public void testPushLidDuplicate()
    {
        tower.pushLid(1);
        tower.pushLid(1);
        assertFalse(tower.ok());
        assertEquals(1, tower.height());
    }

    /**
     * Test pushing a lid that exceeds tower height fails.
     */
    @Test
    public void testPushLidExceedsHeight()
    {
        Tower small = new Tower(10, 1);
        small.pushCup(1); // height 1, fills maxHeight
        small.pushLid(1); // would need 1 more cm
        assertFalse(small.ok());
        assertEquals(1, small.height());
    }

    /**
     * Test lid is placed on top of its matching cup.
     */
    @Test
    public void testPushLidOnCup()
    {
        tower.pushCup(1);
        tower.pushLid(1);
        assertTrue(tower.ok());
        String[] si = tower.stackingItems();
        assertEquals("cup", si[0]);
        assertEquals("1", si[1]);
        assertEquals("lid", si[2]);
        assertEquals("1", si[3]);
    }

    /**
     * Test pushing a lid that exceeds tower width fails.
     */
    @Test
    public void testPushLidExceedsWidth()
    {
        Tower narrow = new Tower(3, 30);
        narrow.pushLid(5); // lidWidth = 5 > width 3
        assertFalse(narrow.ok());
        assertEquals(0, narrow.height());
    }

    /**
     * Test pushing a lid with non-positive id fails.
     */
    @Test
    public void testPushLidNonPositiveId()
    {
        tower.pushLid(0);
        assertFalse(tower.ok());
        assertEquals(0, tower.height());
    }

    // ---- removeCup ----

    /**
     * Test removing an existing cup by id.
     */
    @Test
    public void testRemoveCup()
    {
        tower.pushCup(1);
        tower.pushCup(2);
        tower.removeCup(1);
        assertTrue(tower.ok());
        assertEquals(3, tower.height()); // only cup 2 remains (height 3)
    }

    /**
     * Test removing a non-existent cup fails.
     */
    @Test
    public void testRemoveCupNotFound()
    {
        tower.pushCup(1);
        tower.removeCup(99);
        assertFalse(tower.ok());
        assertEquals(1, tower.height());
    }

    // ---- removeLid ----

    /**
     * Test removing an existing lid by id.
     */
    @Test
    public void testRemoveLid()
    {
        tower.pushLid(1);
        tower.pushLid(2);
        tower.removeLid(1);
        assertTrue(tower.ok());
        assertEquals(1, tower.height()); // only lid 2 remains
    }

    /**
     * Test removing a non-existent lid fails.
     */
    @Test
    public void testRemoveLidNotFound()
    {
        tower.pushLid(1);
        tower.removeLid(99);
        assertFalse(tower.ok());
        assertEquals(1, tower.height());
    }

    // ---- popCup ----

    /**
     * Test popping removes the topmost cup and updates height.
     */
    @Test
    public void testPopCup()
    {
        tower.pushCup(1);
        tower.pushCup(2);
        tower.popCup();
        assertTrue(tower.ok());
        assertEquals(1, tower.height()); // only cup 1 remains
    }

    /**
     * Test popping a cup from empty tower fails.
     */
    @Test
    public void testPopCupEmpty()
    {
        tower.popCup();
        assertFalse(tower.ok());
    }

    // ---- popLid ----

    /**
     * Test popping removes the topmost lid and updates height.
     */
    @Test
    public void testPopLid()
    {
        tower.pushLid(1);
        tower.pushLid(2);
        tower.popLid();
        assertTrue(tower.ok());
        assertEquals(1, tower.height());
    }

    /**
     * Test popping a lid from empty tower fails.
     */
    @Test
    public void testPopLidEmpty()
    {
        tower.popLid();
        assertFalse(tower.ok());
    }

    // ---- height ----

    /**
     * Test height on empty tower is 0.
     */
    @Test
    public void testHeightEmpty()
    {
        assertEquals(0, tower.height());
    }

    /**
     * Test height after mixed pushes.
     */
    @Test
    public void testHeightMixed()
    {
        tower.pushCup(2); // height 3
        tower.pushLid(2); // height 1
        assertEquals(4, tower.height());
    }

    // ---- orderTower ----

    /**
     * Test orderTower sorts largest id at bottom, smallest at top.
     */
    @Test
    public void testOrderTower()
    {
        tower.pushCup(1);
        tower.pushCup(3);
        tower.pushCup(2);
        tower.orderTower();
        assertTrue(tower.ok());
        String[] si = tower.stackingItems();
        // Expected: cup3 (bottom), cup2, cup1 (top)
        assertEquals("cup", si[0]);
        assertEquals("3", si[1]);
        assertEquals("cup", si[2]);
        assertEquals("2", si[3]);
        assertEquals("cup", si[4]);
        assertEquals("1", si[5]);
    }

    /**
     * Test orderTower places matching lid on top of its cup.
     */
    @Test
    public void testOrderTowerWithLids()
    {
        tower.pushCup(1);
        tower.pushCup(3);
        tower.pushLid(1);
        tower.pushLid(3);
        tower.orderTower();
        String[] si = tower.stackingItems();
        // Expected: cup3, lid3, cup1, lid1
        assertEquals("cup", si[0]);
        assertEquals("3", si[1]);
        assertEquals("lid", si[2]);
        assertEquals("3", si[3]);
        assertEquals("cup", si[4]);
        assertEquals("1", si[5]);
        assertEquals("lid", si[6]);
        assertEquals("1", si[7]);
    }

    /**
     * Test orderTower with height constraint excludes items that don't fit.
     */
    @Test
    public void testOrderTowerOverflow()
    {
        Tower small = new Tower(10, 4);
        small.pushCup(1); // height 1
        small.pushCup(2); // height 3, total 4
        small.orderTower();
        assertTrue(small.ok());
        assertEquals(4, small.height());
    }

    // ---- reverseTower ----

    /**
     * Test reverseTower sorts smallest id at bottom, largest at top.
     */
    @Test
    public void testReverseTower()
    {
        tower.pushCup(3);
        tower.pushCup(1);
        tower.pushCup(2);
        tower.reverseTower();
        assertTrue(tower.ok());
        String[] si = tower.stackingItems();
        // Expected: cup1 (bottom), cup2, cup3 (top)
        assertEquals("cup", si[0]);
        assertEquals("1", si[1]);
        assertEquals("cup", si[2]);
        assertEquals("2", si[3]);
        assertEquals("cup", si[4]);
        assertEquals("3", si[5]);
    }

    /**
     * Test reverseTower places matching lid on top of its cup.
     */
    @Test
    public void testReverseTowerWithLids()
    {
        tower.pushCup(3);
        tower.pushCup(1);
        tower.pushLid(3);
        tower.pushLid(1);
        tower.reverseTower();
        String[] si = tower.stackingItems();
        // Expected: cup1, lid1, cup3, lid3
        assertEquals("cup", si[0]);
        assertEquals("1", si[1]);
        assertEquals("lid", si[2]);
        assertEquals("1", si[3]);
        assertEquals("cup", si[4]);
        assertEquals("3", si[5]);
        assertEquals("lid", si[6]);
        assertEquals("3", si[7]);
    }

    // ---- lidedCups ----

    /**
     * Test lidedCups returns empty when no cups are lided.
     */
    @Test
    public void testLidedCupsNone()
    {
        tower.pushCup(1);
        tower.pushCup(2);
        int[] lc = tower.lidedCups();
        assertEquals(0, lc.length);
    }

    /**
     * Test lidedCups returns empty on empty tower.
     */
    @Test
    public void testLidedCupsEmpty()
    {
        int[] lc = tower.lidedCups();
        assertEquals(0, lc.length);
    }

    /**
     * Test lidedCups detects a cup covered by its lid.
     */
    @Test
    public void testLidedCupsWithMatch()
    {
        tower.pushCup(1);
        tower.pushLid(1); // lid 1 directly on top of cup 1
        int[] lc = tower.lidedCups();
        assertEquals(1, lc.length);
        assertEquals(1, lc[0]);
    }

    /**
     * Test lidedCups does not detect when lid id differs from cup id.
     */
    @Test
    public void testLidedCupsNoMatchDifferentId()
    {
        tower.pushCup(1);
        tower.pushLid(2); // lid 2, not matching cup 1
        int[] lc = tower.lidedCups();
        assertEquals(0, lc.length);
    }

    /**
     * Test lidedCups after orderTower creates matching pairs.
     */
    @Test
    public void testLidedCupsAfterOrder()
    {
        tower.pushCup(1);
        tower.pushCup(3);
        tower.pushLid(1);
        tower.pushLid(3);
        tower.orderTower();
        int[] lc = tower.lidedCups();
        // After order: cup3, lid3, cup1, lid1 → both are lided
        assertEquals(2, lc.length);
        assertEquals(3, lc[0]);
        assertEquals(1, lc[1]);
    }

    // ---- stackingItems ----

    /**
     * Test stackingItems returns empty array for empty tower.
     */
    @Test
    public void testStackingItemsEmpty()
    {
        String[] si = tower.stackingItems();
        assertEquals(0, si.length);
    }

    /**
     * Test stackingItems returns correct format.
     */
    @Test
    public void testStackingItemsFormat()
    {
        tower.pushCup(4);
        tower.pushLid(4);
        String[] si = tower.stackingItems();
        assertEquals(4, si.length);
        assertEquals("cup", si[0]);
        assertEquals("4", si[1]);
        assertEquals("lid", si[2]);
        assertEquals("4", si[3]);
    }

    /**
     * Test stackingItems reflects insertion order (base to top).
     */
    @Test
    public void testStackingItemsOrder()
    {
        tower.pushCup(1);
        tower.pushCup(3);
        tower.pushLid(2);
        String[] si = tower.stackingItems();
        assertEquals(6, si.length);
        assertEquals("cup", si[0]);
        assertEquals("1", si[1]);
        assertEquals("cup", si[2]);
        assertEquals("3", si[3]);
        assertEquals("lid", si[4]);
        assertEquals("2", si[5]);
    }

    // ---- ok ----

    /**
     * Test ok returns true after successful operation.
     */
    @Test
    public void testOkAfterSuccess()
    {
        tower.pushCup(1);
        assertTrue(tower.ok());
    }

    /**
     * Test ok returns false after failed operation.
     */
    @Test
    public void testOkAfterFailure()
    {
        tower.pushCup(1);
        tower.pushCup(1); // duplicate
        assertFalse(tower.ok());
    }

    /**
     * Test ok resets after a new successful operation.
     */
    @Test
    public void testOkResetsAfterSuccess()
    {
        tower.pushCup(1);
        tower.pushCup(1); // fails
        assertFalse(tower.ok());
        tower.pushCup(2); // succeeds
        assertTrue(tower.ok());
    }
}
