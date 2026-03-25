package core;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * GameLogic.java (PSP2)
 *
 * Core (model) module for a simple 2-player race game on a 1..100 board.
 * - Players start at position 0 (START)
 * - Dice roll: 1..6
 * - Win condition: land EXACTLY on 100 (overshoot => no movement)
 * - Special squares (labels shown on board):
 *   L1, L2 (ladders), S1, S2 (snakes), T1, T2 (traps), H1, H2 (holes)
 *
 * NOTE: This class is intentionally self-contained for PSP submissions.
 */
public class GameLogic {

    // define variables: currentPlayer, player1, player2
    private int currentPlayer; // 1 or 2
    private int player1;       // 0..100 (0 means START)
    private int player2;       // 0..100 (0 means START)

    private boolean gameOver;
    private int lastRoll;

    private final Random rng;

    // special square label (ex: 3 -> "L1")
    private final Map<Integer, String> labels;

    // special square actions (ex: 3 -> 22 means ladder)
    private final Map<Integer, Integer> jumps;

    public GameLogic() {
        this(new Random());
    }

    public GameLogic(Random rng) {
        this.rng = rng;
        this.currentPlayer = 1;
        this.player1 = 0;
        this.player2 = 0;
        this.gameOver = false;
        this.lastRoll = 0;

        this.labels = new HashMap<>();
        this.jumps = new HashMap<>();
        initSpecialSquares();
    }

    private void initSpecialSquares() {
        // These match the board labels shown in the assignment screenshot.
        // You can adjust the destination squares if your instructor provided different values.

        // Ladders
        labels.put(3, "L1");
        jumps.put(3, 22);

        labels.put(27, "L2");
        jumps.put(27, 54);

        // Snakes
        labels.put(97, "S1");
        jumps.put(97, 78);

        labels.put(74, "S2");
        jumps.put(74, 53);

        // Traps (send player backward)
        labels.put(19, "T1");
        jumps.put(19, 11);

        labels.put(36, "T2");
        jumps.put(36, 6);

        // Holes (send player back near start)
        labels.put(25, "H1");
        jumps.put(25, 1);

        labels.put(57, "H2");
        jumps.put(57, 1);
    }

    /**
     * roll(): Generate a dice roll (1..6).
     */
    public int roll() {
        lastRoll = rng.nextInt(6) + 1;
        return lastRoll;
    }

    /**
     * playersPosition(): Returns a readable summary of both player positions.
     */
    public String playersPosition() {
        return "Start positions: P1=" + player1 + " | P2=" + player2 + " (0 means START)";
    }

    /**
     * checkGameOver(): True if a player has reached 100.
     */
    public boolean checkGameOver() {
        return gameOver;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        if (currentPlayer != 1 && currentPlayer != 2) {
            throw new IllegalArgumentException("currentPlayer must be 1 or 2");
        }
        this.currentPlayer = currentPlayer;
    }

    public int getPlayer1() {
        return player1;
    }

    public int getPlayer2() {
        return player2;
    }

    public int getLastRoll() {
        return lastRoll;
    }

    /**
     * Returns label like "L1" or "S2" for display purposes, or null if none.
     */
    public String getSquareLabel(int square) {
        return labels.get(square);
    }

    /**
     * Apply a roll for the CURRENT player: updates position and switches turn.
     * Returns a message describing what happened.
     */
    public String applyMove(int roll) {
        if (gameOver) {
            return "Game is already over.";
        }
        if (roll < 1 || roll > 6) {
            return "Invalid roll: " + roll + " (must be 1..6)";
        }

        int before = (currentPlayer == 1) ? player1 : player2;
        int tentative = before + roll;

        StringBuilder msg = new StringBuilder();
        msg.append("Player ").append(currentPlayer).append(" rolled a ").append(roll).append(".");

        int after = before;
        if (tentative > 100) {
            msg.append(" Overshot 100 (need exact roll). No movement.");
        } else {
            after = tentative;
            msg.append(" Moved to ").append(after).append(".");

            // Apply any jump (ladder/snake/trap/hole)
            if (jumps.containsKey(after)) {
                int dest = jumps.get(after);
                String label = labels.get(after);
                msg.append(" Landed on ").append(label == null ? "a special square" : label)
                   .append(" => go to ").append(dest).append(".");
                after = dest;
            }

            if (after == 100) {
                gameOver = true;
                msg.append(" WINNER!");
            }
        }

        if (currentPlayer == 1) {
            player1 = after;
        } else {
            player2 = after;
        }

        // Switch turn (even if no movement due to overshoot)
        if (!gameOver) {
            currentPlayer = (currentPlayer == 1) ? 2 : 1;
        }

        return msg.toString();
    }

    /**
     * Returns 0 if no winner yet, else 1 or 2.
     */
    public int getWinner() {
        if (player1 == 100) return 1;
        if (player2 == 100) return 2;
        return 0;
    }
}
