package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tower.*;
/**
 * Test class for Tower.
 * Validates core logic, nesting physics, and lided cup behavior.
 */
public class TowerC1Test {
    private Tower tower;  // To 1th test
    private Tower tower2; // To 2th test

    @BeforeEach
    public void setUp() {
        // Initialize a tower with enough width and height for testing
        // For n=5: Width=9, MaxHeight=30
        tower = new Tower(9, 25);
        tower2 = new Tower(9, 11);
    }
    
    @AfterEach
    public void tearDown() {
        tower.exit();
        tower2.exit();
    }
    
    // -------------------------------
    // Little things
    // -------------------------------
    
    @Test
    public void shouldCreateTowerWithCorrectDimensions() {
        assertEquals(9, tower.getWidth());
        assertTrue(tower.ok());
        assertEquals(25, tower.getMaxHeight());
        assertTrue(tower.ok());
    }

    @Test
    public void shouldPushCupAndIncreaseHeight() {
        tower.pushCup(3); // Height of cup 3 is 2(3)-1 = 5
        assertTrue(tower.ok(), "Cup 3 should be pushed successfully");
        assertEquals(5, tower.height());
    }

    @Test
    public void shouldNotPushInvalidCup() {
        tower.pushCup(-1); // Negative ID
        assertFalse(tower.ok());
        
        tower.pushCup(100); // Too wide for width 9
        assertFalse(tower.ok());
    }

    @Test
    public void shouldManageLidedCupsCorrectlly() {
        tower.pushCup(3);
        tower.pushLid(3);
        
        int[] lided = tower.lidedCups();
        assertEquals(1, lided.length);
        assertEquals(3, lided[0]);
        // Height: Cup 3 (5cm) + Lid 3 (1cm) = 6cm
        assertEquals(6, tower.height());
    }

    @Test
    public void shouldRemoveBothWhenLidedCupIsRemoved() {
        tower.pushCup(2);
        tower.pushLid(2);
        assertEquals(2, tower.stackingItems().length);
        
        tower.removeCup(2); // Should remove both cup and lid
        assertEquals(0, tower.stackingItems().length);
        assertEquals(0, tower.height());
        assertEquals(0, tower.lidedCups().length);
    }

    @Test
    public void shouldNestingPhysicsWork() {
        // If we push a smaller cup inside a larger one
        tower.pushCup(5); // Top = 9
        tower.pushCup(2); // ID 2 < 5, should nest. 
        // Base of 2 = Base of 5 + 1 = 1. Top of 2 = 1 + (2*2-1) = 4.
        // Global height remains 9 because Cup 5 is taller.
        assertEquals(9, tower.height());
        
        // If we push a larger cup on top
        tower.pushCup(1); 
        tower.pushCup(4); 
        // Order: 5, 2, 1, 4. 4 is larger than 1, so it sits on top of 1's top.
        assertEquals(tower.height(), 11);
    }

    @Test
    public void shouldOrderTowerByDescendingID() {
        tower.pushCup(1);
        tower.pushCup(5);
        tower.pushCup(3);

        tower.orderTower();
        
        String[][] items = tower.stackingItems();
        // Base should be the largest ID (5)
        assertEquals("5", items[0][1]);
        assertEquals("3", items[1][1]);
        assertEquals("1", items[2][1]);
    }

    @Test
    public void shouldReverseTowerByAscendingID() {
        tower.pushCup(5);
        tower.pushCup(1);
        tower.pushCup(3);
        
        tower.reverseTower();
        
        String[][] items = tower.stackingItems();
        assertEquals("1", items[0][1]);
        assertEquals("3", items[1][1]);
        assertEquals("5", items[2][1]);
    }

    @Test
    public void shouldHandlePopOperations() {
        tower.pushCup(1);
        tower.pushCup(2);
        
        tower.popCup(); // Removes topmost cup (2)
        String[][] items = tower.stackingItems();
        assertEquals(1, items.length);
        assertEquals("1", items[0][1]);
    }
    
    @Test
    public void shouldFailWhenExceedingMaxHeight() {
        Tower tinyTower = new Tower(10, 2); // Max height only 2cm
        tinyTower.pushCup(5); // Cup 5 is 9cm tall
        assertFalse(tinyTower.ok(), "Should fail because height exceeds 2cm");
    }
    
    // -------------------------------
    // More special test with SPECIFIC NESTING SCENARIO
    // -------------------------------
    
    @Test
    public void shouldFollowSpecificNestingScenario() {
        // pushCup(5) -> Top: 9
        tower2.pushCup(5);
        assertEquals(9, tower2.height());
        //assertEquals(9, tower2.getCup(5).getTopPosition());

        // pushCup(4) -> Nests. Top: 8
        tower2.pushCup(4);
        assertEquals(9, tower2.height());
        //assertEquals(8, tower2.getCup(4).getTopPosition());

        // pushCup(2) -> Nests inside 4. Top: 5
        tower2.pushCup(2);
        assertEquals(9, tower2.height());
        //assertEquals(5, tower2.getCup(2).getTopPosition());

        // pushCup(3) -> ID 3 > 2. Sits ON TOP of 2. Top: 10
        tower2.pushCup(3);
        assertEquals(10, tower2.height());
        //assertEquals(10, tower2.getCup(3).getTopPosition());

        // pushCup(1) -> Nests inside 3. Top: 7
        tower2.pushCup(1);
        assertEquals(10, tower2.height());
        //assertEquals(7, tower2.getCup(1).getTopPosition());

        // pushLid(2) -> Nests. Top: 8
        tower2.pushLid(2);
        assertEquals(10, tower2.height());
        //assertEquals(8, tower2.getLid(2).getTopPosition());

        // pushLid(3) -> Matches Cup 3. Top: 11
        tower2.pushLid(3);
        assertEquals(11, tower2.height());
        //assertEquals(11, tower2.getLid(3).getTopPosition());
    }

    // --- RARE CASES & REMOVAL LOGIC ---

    @Test
    public void shouldDecreaseHeightAndRemoveLidsCorrectlly() {
        // Set up specific state
        tower2.pushCup(5);
        tower2.pushCup(3);
        tower2.pushLid(3); // Height is 6 + 1 = 7
        assertEquals(9, tower2.height());
        //assertEquals(7, tower2.getLid(3).getTopPosition());
        
        // Rare Case: Removing a Cup must remove its Lid
        tower2.removeCup(3); 
        assertEquals(0, tower2.lidedCups().length);
        //assertNull(tower2.getLid(3), "Lid 3 should be gone because its cup was removed");
        //assertEquals(9, tower2.height(), "Height should return to Cup 5's top");
    }

    @Test
    public void shouldHandlePopOperationsSuccessfully() {
        tower2.pushCup(5);
        tower2.pushCup(4);
        
        tower2.popCup(); // Should remove Cup 4 (topmost)
        String[][] items = tower2.stackingItems();
        assertEquals(1, items.length);
        assertEquals("5", items[0][1]);
        assertTrue(tower2.ok());
    }

    @Test
    public void shouldFailWhenOperationsAreImpossible() {
        tower2.pushCup(5);
        tower2.pushCup(5); // Duplicate ID
        assertFalse(tower2.ok(), "ok() should be false after duplicate push");
        
        tower.popLid(); // No lids to pop
        assertFalse(tower2.ok(), "ok() should be false after invalid pop");
    }

    @Test
    public void shouldReorganizeWithOrderTower() {
        tower2.pushCup(1);
        tower2.pushCup(5);
        tower2.pushCup(3);
        
        tower2.orderTower(); // Should sort 5 (bottom), 3, 1 (top)
        
        String[][] items = tower2.stackingItems();
        assertEquals("5", items[0][1]);
        assertEquals("3", items[1][1]);
        assertEquals("1", items[2][1]);
    }
}
