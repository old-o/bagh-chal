/*
 * Copyright 2003, Daniel Newman (danielnewman106@hotmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Some refactorings in 2016 by Oliver Doepner
 */

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
