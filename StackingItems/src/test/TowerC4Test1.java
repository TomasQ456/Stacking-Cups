package test;

import tower.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Color;

/**
 * Test class for Tower - Cycle 4.
 * Focuses on special behaviors: Opener, Hierarchical, Fearful, and Crazy.
 * * @author Acero - Quiceno
 * @version 1.0
 */
public class TowerC4Test1 {
    private Tower tower;

    @BeforeEach
    public void setUp() {
        // Tower with width 20 and max height 50
        tower = new Tower(20, 50);
    }

    @AfterEach
    public void tearDown() {
        tower.exit();
    }

    // -------------------------------------------------------------------
    // TESTS FOR OPENER CUP
    // -------------------------------------------------------------------

    @Test
    public void shouldOpenerCupRemoveLidsOnItsWay() {
        // Setup: Add a normal cup and a lid
        tower.pushCup(5); 
        tower.pushLid(5); // Lid 5 is on top
        
        // Add Opener Cup 2. It should remove Lid 5 to enter.
        // Assuming your pushCup method is overloaded or handles types
        tower.pushCup("opener", 2);
        
        assertTrue(tower.ok());
        // Stacking items should only have Cup 5 and Opener Cup 2 (Lid 5 was deleted)
        assertEquals(2, tower.stackingItems().length);
        
        // Verify Lid 5 is no longer in the tower
        String[][] items = tower.stackingItems();
        for(String[] item : items) {
            assertNotEquals("normalLid", item[0]);
        }
    }

    // -------------------------------------------------------------------
    // TESTS FOR HIERARCHICAL CUP
    // -------------------------------------------------------------------

    @Test
    public void shouldHierarchicalCupLockAtBottom() {
        // Add Hierarchical Cup at the base (root)
        tower.pushCup("hierarchical", 10);
        
        String[] hCup = {"hierarchicalCup", "10"};
        String[] otherCup = {"normalCup", "5"};
        tower.pushCup(5);
        
        // Try to swap it or move it. Since it's at the bottom, it should be locked.
        tower.swap(hCup, otherCup);
        
        // Should fail because hierarchical at bottom is locked
        assertFalse(tower.ok(), "Hierarchical cup at bottom should be locked");
    }

    // -------------------------------------------------------------------
    // TESTS FOR FEARFUL LID
    // -------------------------------------------------------------------

    @Test
    public void shouldFearfulLidFailIfOwnerMissing() {
        // Try to add Fearful Lid 3 without Cup 3 in the tower
        tower.pushLid("fearfulLid", 3);
        
        assertFalse(tower.ok(), "Fearful lid should not enter without its cup");
    }

    @Test
    public void shouldFearfulLidStayPutWhenCovering() {
        tower.pushCup(4);
        tower.pushLid("fearfulLid", 4);
        tower.cover(); // Seal the cup
        
        String[] fLid = {"fearfulLid", "4"};
        String[] other = {"normalLid", "4"}; // Try to swap or remove
        
        tower.swap(fLid, other);
        assertFalse(tower.ok(), "Fearful lid cannot be moved once it is covering");
    }

    // -------------------------------------------------------------------
    // TESTS FOR CRAZY LID
    // -------------------------------------------------------------------

    @Test
    public void shouldCrazyLidPositionAtBase() {
        // Cup 5 height = 9
        tower.pushCup(5);
        tower.pushLid("crazyCup", 5);
        
        // Before cover, height is 9.
        // After cover, Crazy Lid 5 (height 1) goes to the BOTTOM of Cup 5.
        tower.cover();
        
        assertTrue(tower.ok());
        // Height should be 10 (9 of cup + 1 of lid as base)
        assertEquals(10, tower.height());
        
        // In stackingItems, the Lid should appear BEFORE the Cup (index 0)
        String[][] items = tower.stackingItems();
        assertEquals("crazyLid", items[0][0]);
        assertEquals("normalCup", items[1][0]);
    }

    // -------------------------------------------------------------------
    // TESTS FOR ATOMICITY & REFACTORING
    // -------------------------------------------------------------------

    @Test
    public void shouldMaintainConsistencyWithMixedTypes() {
        tower.pushCup("hierarchicalCup", 8);
        tower.pushCup("openerCup", 4);
        tower.pushLid("fearfulCup", 8);
        
        tower.cover();
        
        assertTrue(tower.ok());
        // Hierarchical 8 should be lidded
        int[] lidded = tower.lidedCups();
        boolean found = false;
        for(int id : lidded) if(id == 8) found = true;
        assertTrue(found);
    }
}