package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tower.*;

/**
 * The test class TowerC3Test.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class TowerContestC3Test
{
    private TowerContest contest;

    @BeforeEach
    public void setUp() {
        // Initialize the contest solver
        contest = new TowerContest();
    }
    
    @AfterEach
    public void tearDown() {
        
        contest = null;
    }
    
    // -------------------------------
    // Edge Cases & Bounds (Impossible)
    // -------------------------------
    
    @Test
    public void shouldReturnImpossibleWhenHeightIsTooLow() {
        // For n=4, formula 2n-1 gives min height = 7.
        // Trying to build height 6 should fail.
        assertEquals("impossible", contest.solve(4, 6));
    }

    @Test
    public void shouldReturnImpossibleWhenHeightIsTooHigh() {
        // For n=4, formula n^2 gives max height = 16.
        // Trying to build height 17 should fail.
        assertEquals("impossible", contest.solve(4, 17));
        
        // ICPC Sample Input 2
        assertEquals("impossible", contest.solve(4, 100));
    }

    // -------------------------------
    // Exact Mathematical Solutions
    // -------------------------------

    @Test
    public void shouldSolveForMinimumHeight() {
        // Min height (7) means cups strictly nested: 4, 3, 2, 1 
        // Actual sizes (2i-1) -> 7, 5, 3, 1
        assertEquals("7 5 3 1", contest.solve(4, 7));
    }

    @Test
    public void shouldSolveForMaximumHeight() {
        assertEquals("1 3 5 7", contest.solve(4, 16));
    }

    // -------------------------------
    // Specific ICPC Scenarios
    // -------------------------------

    @Test
    public void shouldFollowSpecificICPCScenario() {
        // ICPC Sample Input 1 (n=4, target=9)
        // Must return exact match for correct nested physics
        assertEquals("7 3 5 1", contest.solve(4, 9));
    }
    
    @Test
    public void shouldSolveForLargeConstraints() {
        // n=5 -> min=9, max=25. Testing a middle value: 15.
        String result = contest.solve(5, 15);
        assertNotEquals("impossible", result, "Height 15 is within bounds for n=5");
        
        assertEquals("1 3 5 9 7", contest.solve(5, 20));
        
        // The output must contain exactly 'n' cups
        String[] parts = result.split(" ");
        assertEquals(5, parts.length, "Solution must use all 5 cups");
    }

    // -------------------------------
    // Simulation Logic
    // -------------------------------

    @Test
    public void shouldSimulateWithoutThrowingExceptions() {
        // Since simulate() handles the Tower instantiation and UI logic internally,
        // we ensure that valid and invalid inputs don't crash the program.
        assertDoesNotThrow(() -> {
            contest.simulate(4, 9);   // Valid simulation
            contest.simulate(4, 100); // Impossible simulation (aborts safely)
        }, "Simulate method should handle all cases gracefully");
    }
}

