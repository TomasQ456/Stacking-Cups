package tower;

import java.util.LinkedList;
import java.util.StringJoiner;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Solves and simulates the Stacking Cups competitive programming problem.
 * Implements the greedy algorithm and special-case handling.
 *
 * @author Acero - Quiceno
 * @version 2.0
 */
public class TowerContest {

    /**
     * Solves the Stacking Cups problem by finding the exact placement order.
     *
     * @param n the total number of cups
     * @param h the target height to achieve
     * @return a String with the sequence of cup sizes, or "impossible"
     */
    public String solve(int n, int h) {
        long minH = 2L * n - 1;
        long maxH = (long) n * n;

        if (h < minH || h > maxH || h == maxH - 2) {
            return "impossible";
        }

        if (h == minH + 2 && n >= 3) {
            return buildSpecial(n);
        }

        return greedySolve(n, h);
    }

    /**
     * Greedy algorithm to find a valid cup placement sequence.
     *
     * @param n the total number of cups
     * @param h the target height
     * @return the placement sequence as a space-separated string
     */
    private String greedySolve(int n, int h) {
        List<Integer> before = new ArrayList<>();
        List<Integer> after = new ArrayList<>();

        long remaining = h - (2 * n - 1);
        int bigCup = 2 * n - 1;

        for (int i = 1; i < n; i++) {
            int currentCupHeight = 2 * i - 1;
            if (remaining >= currentCupHeight) {
                before.add(currentCupHeight);
                remaining -= currentCupHeight;
            } else {
                after.add(currentCupHeight);
            }
        }

        StringJoiner sj = new StringJoiner(" ");
        for (int c : before) sj.add(String.valueOf(c));
        sj.add(String.valueOf(bigCup));
        Collections.sort(after, Collections.reverseOrder());
        for (int c : after) sj.add(String.valueOf(c));

        return sj.toString();
    }

    /**
     * Builds the special-case placement sequence (h = minH + 2).
     *
     * @param n the total number of cups
     * @return the placement sequence as a space-separated string
     */
    private String buildSpecial(int n) {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(String.valueOf(2 * n - 1));
        sj.add("3");
        sj.add(String.valueOf(2 * n - 3));
        sj.add("1");

        for (int i = n - 2; i >= 3; i--) {
            sj.add(String.valueOf(2 * i - 1));
        }
        return sj.toString();
    }

    /**
     * Simulates the exact solution visually using the Tower class.
     *
     * @param n the total number of cups
     * @param h the target height to achieve
     */
    public void simulate(int n, long h) {
        String sequence = solve(n, (int) h);

        if (sequence.equals("impossible")) {
            TowerCanvas.getInstance().showError("It's impossible to simulate this configuration.");
            return;
        }

        Tower tower = new Tower(n * 2, (int) h);

        String[] cupSizes = sequence.split(" ");
        for (String sizeStr : cupSizes) {
            int cupSize = Integer.parseInt(sizeStr);
            tower.pushCup(cupSize);
        }

        tower.makeVisible();
        TowerCanvas.getInstance().showError("Simulation successful for height: " + h);
    }
}
