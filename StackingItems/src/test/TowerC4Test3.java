package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tower.*;

/**
 * Extended test class for Tower — Cycle 4 (CC4).
 * Validates StackingException integration, polymorphic push behavior,
 * and edge cases for all cup and lid subtypes.
 *
 * @author Acero - Quiceno
 * @version 1.0
 */
public class TowerC4Test3 {
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
    // STACKING EXCEPTION TESTS
    // -------------------------------------------------------------------

    @Test
    public void shouldFailWhenPushingDuplicateCup() {
        tower.pushCup(3);
        assertTrue(tower.ok());

        tower.pushCup(3);
        assertFalse(tower.ok(), "Duplicate cup should fail");
    }

    @Test
    public void shouldFailWhenTowerIsFull() {
        Tower tinyTower = new Tower(10, 2);
        tinyTower.pushCup(5);
        assertFalse(tinyTower.ok(), "Cup too tall for tower");
        tinyTower.exit();
    }

    @Test
    public void shouldFailPushLidWithoutCupForFearful() {
        tower.pushLid("fearful", 5);
        assertFalse(tower.ok(), "Fearful lid should fail without companion cup");
    }

    // -------------------------------------------------------------------
    // POLYMORPHIC PUSH TESTS
    // -------------------------------------------------------------------

    @Test
    public void shouldNormalCupPushCorrectly() {
        tower.pushCup(5);
        assertTrue(tower.ok());
        assertEquals(9, tower.height());
    }

    @Test
    public void shouldOpenerCupClearLids() {
        tower.pushCup(3);
        tower.pushLid(3);
        tower.pushCup("opener", 2);

        assertTrue(tower.ok());

        String[][] items = tower.stackingItems();
        assertEquals(1, items.length);
        assertNotEquals("openerCup", items[0], 
            "Opener should have removed the lid (if is lidded, must be remove both)");
    }

    @Test
    public void shouldHierarchicalCupDisplaceSmallerItems() {
        tower.pushCup(2);
        tower.pushCup("hierarchical", 5);

        assertTrue(tower.ok());
        assertEquals(2, tower.stackingItems().length);
    }

    @Test
    public void shouldCrazyLidGoToBase() {
        tower.pushCup(3);
        tower.pushLid("crazy", 3);

        assertTrue(tower.ok());
        String[][] items = tower.stackingItems();
        assertEquals("crazyLid", items[0][0]);
    }

    // -------------------------------------------------------------------
    // OK() FLAG CONSISTENCY
    // -------------------------------------------------------------------

    @Test
    public void shouldOkBeConsistentAfterSuccessfulOperations() {
        tower.pushCup(5);
        assertTrue(tower.ok());

        tower.pushLid(5);
        assertTrue(tower.ok());

        tower.popCup();
        assertTrue(tower.ok());
    }

    @Test
    public void shouldOkBeFalseAfterFailedOperations() {
        tower.popCup();
        assertFalse(tower.ok(), "Popping from empty tower should fail");
    }
}
