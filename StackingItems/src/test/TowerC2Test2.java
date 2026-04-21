package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tower.*;

/**
 * Acceptance test class for Tower.
 * Validates end-to-end scenarios covering all refactored functionality:
 * package structure, StackingException, polymorphic push, and Shape hierarchy.
 *
 * @author Acero - Quiceno
 * @version 1.0
 */
public class TowerC2Test2 {
    private Tower tower;

    @BeforeEach
    public void setUp() {
        tower = new Tower(20, 100);
    }

    @AfterEach
    public void tearDown() {
        tower.exit();
    }

    // -------------------------------------------------------------------
    // FULL SCENARIO TESTS
    // -------------------------------------------------------------------

    @Test
    public void shouldHandleCompleteWorkflow() {
        tower.pushCup(5);
        tower.pushCup(3);
        tower.pushLid(3);
        tower.pushLid(5);

        assertTrue(tower.ok());
        assertEquals(4, tower.stackingItems().length);

        tower.cover();
        assertTrue(tower.ok());
    }

    @Test
    public void shouldOrderAndReverseCorrectly() {
        tower.pushCup(1);
        tower.pushCup(5);
        tower.pushCup(3);

        tower.orderTower();

        String[][] items = tower.stackingItems();
        assertEquals("5", items[0][1]);
        assertEquals("3", items[1][1]);
        assertEquals("1", items[2][1]);

        tower.reverseTower();

        items = tower.stackingItems();
        assertEquals("1", items[0][1]);
        assertEquals("3", items[1][1]);
        assertEquals("5", items[2][1]);
    }

    @Test
    public void shouldHandleMixedTypePushes() {
        tower.pushCup("hierarchical", 8);
        assertTrue(tower.ok());

        tower.pushCup("opener", 3);
        assertTrue(tower.ok());

        tower.pushCup(2);
        assertTrue(tower.ok());

        tower.pushLid("fearful", 8);
        assertTrue(tower.ok());

        assertEquals(4, tower.stackingItems().length);
    }

    @Test
    public void shouldSwapItemsCorrectly() {
        tower.pushCup(5);
        tower.pushCup(1);

        String[] cup5 = {"cup", "5"};
        String[] cup1 = {"cup", "1"};

        tower.swap(cup5, cup1);
        assertTrue(tower.ok());
    }

    @Test
    public void shouldRejectInvalidOperationsGracefully() {
        tower.pushCup(-1);
        assertFalse(tower.ok());

        tower.pushCup(100);
        assertFalse(tower.ok());

        tower.popCup();
        assertFalse(tower.ok());

        tower.popLid();
        assertFalse(tower.ok());
    }

    @Test
    public void shouldTowerConstructorWithNCupsWork() {
        Tower prebuilt = new Tower(5);
        assertTrue(prebuilt.ok());
        assertEquals(5, prebuilt.stackingItems().length);
        prebuilt.exit();
    }

    @Test
    public void shouldLidedCupsReturnSortedIds() {
        tower.pushCup(4);
        tower.pushCup(2);
        tower.pushLid(4);
        tower.pushLid(2);

        tower.cover();

        int[] lidded = tower.lidedCups();
        assertEquals(1, lidded.length);
        assertEquals(4, lidded[0]);   //We can't joint when an Item is inside an lidded item
    }
}
