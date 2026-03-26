package ui;

import core.ComputerPlayer;
import core.GameLogic;
import java.util.Scanner;

/**
 * GameConsoleUI.java (PSP2)
 *
 * Console-based UI (entry point). Contains main().
 * - Prompts user to choose opponent (Player vs Player or Player vs Computer)
 * - Prompts turns, rolls, and displays a 10x10 board
 */
public class GameConsoleUI {

    public static void main(String[] args) {
        new GameConsoleUI().run();
    }

    public void run() {
        Scanner in = new Scanner(System.in);
        GameLogic game = new GameLogic();

        System.out.println("=== PSP2: Board Race Game ===");
        System.out.println("Choose opponent:");
        System.out.println("  1) Another Player");
        System.out.println("  2) Computer");
        System.out.print("Enter 1 or 2: ");

        String choice = in.nextLine().trim();
        boolean vsComputer = "2".equals(choice);
        ComputerPlayer cpu = vsComputer ? new ComputerPlayer() : null;

        System.out.println();
        System.out.println("Begin Game.");
        displayBoard(game);
        System.out.println(game.playersPosition());
        System.out.println();

        while (!game.checkGameOver()) {
            playersTurn(in, game, vsComputer, cpu);
            System.out.println();
        }

        int winner = game.getWinner();
        if (winner == 1) {
            System.out.println("Game Over: Player 1 wins!");
        } else if (winner == 2) {
            System.out.println(vsComputer ? "Game Over: Computer (Player 2) wins!" : "Game Over: Player 2 wins!");
        } else {
            System.out.println("Game Over.");
        }

        in.close();
    }

    /**
     * Displays a 10x10 board (1..100) with special-square labels and player position markers.
     */
    public void displayBoard(GameLogic game) {
        int p1 = game.getPlayer1();
        int p2 = game.getPlayer2();

        // Print rows from top (91..100) down to bottom (1..10)
        for (int row = 9; row >= 0; row--) {
            int start = row * 10 + 1;
            int end = row * 10 + 10;

            boolean leftToRight = (row % 2 == 0); // row 0 (1..10) is left-to-right

            StringBuilder sb = new StringBuilder();
            if (leftToRight) {
                for (int sq = start; sq <= end; sq++) {
                    sb.append("|").append(String.format("%3s", cellText(game, sq, p1, p2)));
                }
            } else {
                for (int sq = end; sq >= start; sq--) {
                    sb.append("|").append(String.format("%3s", cellText(game, sq, p1, p2)));
                }
            }
            sb.append("|");
            System.out.println(sb);
        }
    }

    /**
     * Runs one turn for the current player.
     */
    public void playersTurn(Scanner in, GameLogic game, boolean vsComputer, ComputerPlayer cpu) {
        int current = game.getCurrentPlayer();

        // Determine roll
        int roll;
        if (vsComputer && current == 2) {
            roll = cpu.generateRoll();
            System.out.println("Computer (Player 2) rolls the dice...");
        } else {
            System.out.println("Player " + current + " - your turn. Press the <<Enter>> key to roll the dice.");
            in.nextLine();
            roll = game.roll();
        }

        // Apply move (THIS is what updates positions)
        String result = game.applyMove(roll);
        System.out.println(result);

        // Re-display board and positions
        displayBoard(game);
        System.out.println(game.playersPosition());
    }

    private String cellText(GameLogic game, int square, int p1, int p2) {
        // Player markers take priority so you can see progress.
        if (p1 == square && p2 == square) {
            return "B"; // both
        }
        if (p1 == square) {
            return "P1";
        }
        if (p2 == square) {
            return "P2";
        }

        String label = game.getSquareLabel(square);
        if (label != null) {
            return label;
        }

        return Integer.toString(square);
    }
}
// TODO: Add clearer player instructions before each turn.

// Added comment for pushing branch assignment