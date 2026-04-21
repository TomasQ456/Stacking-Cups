package test;

import tower.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Enhanced Test class for Tower - Cycle 2.
 * Focuses on height calculation, stacking order, and lidded cup consistency.
 */
public class TowerC2Test1 {
    private Tower tower;
    private Tower towerSmall;
    private Tower towerS;
    private Tower towerP;

    @BeforeEach
    public void setUp() {
        // Standard Tower: Uses the greedy marathon solution for h=n^2
        tower = new Tower(5); 
        // Limited Tower: Max Height 10
        towerSmall = new Tower(10, 10);
        towerS = new Tower(9, 22);
        towerP = new Tower(9, 21);
    }

    @AfterEach
    public void tearDown() {
        tower.exit();
        towerSmall.exit();
        towerS.exit();
        towerP.exit();
    }

    // -------------------------------------------------------------------
    // TESTS FOR tower(n)
    // -------------------------------------------------------------------

    @Test
    public void shouldCreateMassiveTowerWithCorrectHeight() {
        // For n=5, the default constructor usually aims for max height (h = n^2 = 25)
        // or a valid greedy solution. 
        assertTrue(tower.ok());
        assertEquals(5, tower.stackingItems().length);
        
        // If your tower(5) uses the marathon solution for max height, it should be 25.
        // If it uses min height (nested), it should be 9.
        assertTrue(tower.height() == 9, "Height should be either min (9)");
        
        tower.reverseTower();
        assertTrue(tower.height() == 25, "Height should be either max (25)");
    }

    // -------------------------------------------------------------------
    // TESTS FOR swap(o1, o2) 
    // -------------------------------------------------------------------

    @Test
    public void shouldUpdateHeightCorrectlyWhenSwappingBase() {
        // Setup: Cup 5 at base, Cup 1 nested inside.
        // Cup 5 (9cm), Cup 1 (1cm). Nested height = 9cm.
        towerSmall.pushCup(5);
        towerSmall.pushCup(1);
        assertEquals(9, towerSmall.height());

        String[] cup5 = {"cup", "5"};
        String[] cup1 = {"cup", "1"};

        // Swap: Cup 1 at base, Cup 5 on top.
        // Height = 1cm (Cup 1) + 9cm (Cup 5) - (0 nesting) = 10cm.
        towerSmall.swap(cup5, cup1);
        
        assertTrue(towerSmall.ok());
        assertEquals(10, towerSmall.height(), "Height must increase to 10 after un-nesting via swap");
        
        String[][] items = towerSmall.stackingItems();
        assertEquals("1", items[0][1], "Base should now be Cup 1");
        assertEquals("5", items[1][1], "Top should now be Cup 5");
    }

    @Test
    public void shouldFailSwapIfLiddedCupExceedsHeight() {
        // towerSmall max height = 10.
        // Cup 4 is 7cm. Lid 4 is 1cm. Total = 8cm.
        towerSmall.pushCup(4);
        towerSmall.pushCup(2);
        towerSmall.pushLid(3);
        towerSmall.pushCup(3);
        
        assertTrue(towerSmall.ok());

        String[] cup4 = {"cup", "4"};
        String[] cup2 = {"cup", "3"};

        // Swap: Cup 2 at base, Cup 4 (+ lid) on top.
        // New Height: 3 (Cup 2) + 7 (Cup 4) + 1 (Lid 4) = 11cm.
        // 11 > 10 (Max Height) -> Should fail.
        towerSmall.swap(cup4, cup2);
        
        assertFalse(towerSmall.ok(), "Swap should fail because 11cm exceeds max height of 10cm");
    }

    // BADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
    @Test
    public void shouldMaintainLidAttachmentDuringSwap() {
        tower.removeCup(5);
        tower.removeCup(4);
        tower.removeCup(3);
        tower.removeCup(2);
        tower.removeCup(1);
        
        tower.pushCup(3);
        tower.pushLid(3);
        tower.pushCup(5); // 5 on top of 3
        
        String[] cup3 = {"cup", "3"};
        String[] cup5 = {"cup", "5"};
        
        tower.swap(cup3, cup5);
        
        assertTrue(tower.ok());
        assertEquals(9, tower.height());
        // Verify Lid 3 moved with Cup 3
        int[] lidded = tower.lidedCups();
        assertEquals(1, lidded.length);
        assertEquals(3, lidded[0]);
    }

    // -------------------------------------------------------------------
    // TESTS FOR cover()
    // -------------------------------------------------------------------

    @Test
    public void shouldCoverComplexNestedStructure() {
        tower.pushCup(5); // size 9
        tower.pushCup(3); // size 5 (nested)
        tower.pushCup(1); // size 1 (nested)
        
        tower.cover();
        assertTrue(tower.ok());
        
        tower.pushLid(3); // Cup 3, lidded
        tower.pushLid(1); // Cup 3 is inside cup 3 (lidded), so is not possible
        
        assertEquals(1, tower.lidedCups().length);
        assertTrue(tower.height() == 9);
    }

    // BADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
    @Test
    public void shouldCoverNothingForItemsBlocked() {
        towerSmall.pushCup(4); 
        towerSmall.pushCup(2); 
        assertTrue(towerSmall.ok());
        
        towerSmall.pushLid(4); 
        towerSmall.pushLid(2); 
        towerSmall.pushLid(1); 

        towerSmall.cover();
        assertTrue(towerSmall.ok());
        assertEquals(10, towerSmall.height());
        
        assertEquals(10, towerSmall.height());
        assertEquals(1, towerSmall.lidedCups().length);
    }

    // 
    @Test
    public void shouldLiddedCupsBeSortedAndNotCoverOneIfLidsExceedMaxHeight() {
        towerS.pushCup(4);
        towerS.pushLid(5);
        towerS.pushCup(2);
        towerS.pushCup(5);
        towerS.pushLid(4);
        towerS.pushLid(2);
        
        towerS.cover();
        
        int[] lidded = towerS.lidedCups();
        
        assertEquals(2, lidded.length);
    }
}