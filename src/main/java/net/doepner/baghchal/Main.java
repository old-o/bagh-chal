package net.doepner.baghchal;

import javax.swing.*;
import java.awt.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * Entry point of the game
 */
public final class Main {

    public static void main(String... args) {

        final Sound sound = new Sound();

        final Images images = new Images();
        final Image goat = images.getGoatImage();

        final Board board = new Board(sound);
        final Phases phases = new Phases();
        final Strategy strategy = new Strategy(board);

        final GoatsManager goatsManager = new GoatsManager(goat, board, phases);
        final UI ui = new UI(board, goatsManager, images, phases);

        goatsManager.setEventHandler(new EventHandler() {
            @Override
            public void boardChanged() {
                ui.repaint();
            }

            @Override
            public void goatMoveDone() {
                strategy.updatePossibleMoves();
                if (strategy.isOver()) {
                    phases.setEnd();
                    ui.offerNextLevel();
                } else {
                    strategy.generateMove(ui.getLevel());
                }
                ui.repaint();
            }
        });

        final JFrame frame = new JFrame("Bagh-Chal");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.add(ui);
        frame.setSize(550, 550);
        frame.setVisible(true);
    }
}
