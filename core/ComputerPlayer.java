package core;

import java.util.Random;

/**
 * ComputerPlayer.java (PSP2)
 *
 * A tiny helper that generates computer dice rolls.
 * The UI decides when to use the computer (Player 2).
 */
public class ComputerPlayer {

    private final Random rng;

    public ComputerPlayer() {
        this(new Random());
    }

    public ComputerPlayer(Random rng) {
        this.rng = rng;
    }

    /**
     * Generate a dice roll from 1..6.
     */
    public int generateRoll() {
        return rng.nextInt(6) + 1;
    }
}
