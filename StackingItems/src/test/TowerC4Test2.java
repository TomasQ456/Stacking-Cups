package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Color;

import tower.*;

/**
 * Test class for Tower — Cycle 4.
 * Focuses on special behaviors: Opener, Hierarchical, Fearful, and Crazy.
 *
 * @author Acero - Quiceno
 * @version 2.0
 */
public class TowerC4Test2 {
    private Tower tower;

    @BeforeEach
    public void setUp() {
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
        tower.pushCup(5);
        tower.pushLid(5);

        tower.pushCup("opener", 2);

        assertTrue(tower.ok());
        assertEquals(2, tower.stackingItems().length);

        String[][] items = tower.stackingItems();
        for (String[] item : items) {
            assertNotEquals("normalLid", item[0]);
        }
    }

    // -------------------------------------------------------------------
    // TESTS FOR HIERARCHICAL CUP
    // -------------------------------------------------------------------

    @Test
    public void shouldHierarchicalCupLockAtBottom() {
        tower.pushCup("hierarchical", 10);

        String[] hCup = {"hierarchicalCup", "10"};
        String[] otherCup = {"normalCup", "5"};
        tower.pushCup(5);

        tower.swap(hCup, otherCup);

        assertFalse(tower.ok(), "Hierarchical cup at bottom should be locked");
    }

    // -------------------------------------------------------------------
    // TESTS FOR FEARFUL LID
    // -------------------------------------------------------------------

    @Test
    public void shouldFearfulLidFailIfOwnerMissing() {
        tower.pushLid("fearfulLid", 3);

        assertFalse(tower.ok(), "Fearful lid should not enter without its cup");
    }

    @Test
    public void shouldFearfulLidStayPutWhenCovering() {
        tower.pushCup(4);
        tower.pushLid("fearfulLid", 4);
        tower.cover();

        String[] fLid = {"fearfulLid", "4"};
        String[] other = {"normalLid", "4"};

        tower.swap(fLid, other);
        assertFalse(tower.ok(), "Fearful lid cannot be moved once it is covering");
    }

    // -------------------------------------------------------------------
    // TESTS FOR CRAZY LID
    // -------------------------------------------------------------------

    @Test
    public void shouldCrazyLidPositionAtBase() {
        tower.pushCup(5);
        tower.pushLid("crazy", 5);

        assertTrue(tower.ok());

        String[][] items = tower.stackingItems();
        assertEquals("crazyLid", items[0][0]);
        assertEquals("normalCup", items[1][0]);
    }

    // -------------------------------------------------------------------
    // TESTS FOR ATOMICITY & REFACTORING
    // -------------------------------------------------------------------

    @Test
    public void shouldMaintainConsistencyWithMixedTypes() {
        tower.pushCup("hierarchical", 8);
        tower.pushCup("opener", 4);
        tower.pushLid("fearful", 8);

        tower.cover();

        assertTrue(tower.ok());
        int[] lidded = tower.lidedCups();
        boolean found = false;
        for (int id : lidded) if (id == 8) found = true;
        assertTrue(found);
    }
}
