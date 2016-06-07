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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * Entry point of the game
 */
public final class Main {

    public static void main(String... args) {

        System.setProperty("sun.java2d.opengl", "true");

        final Phases phases = new Phases();

        final Sound sound = new Sound(phases);
        final Images images = new Images(phases);

        final Board board = new Board(new BoardListener() {
            @Override
            public void onPredatorTake() {
                sound.playPredatorKills();
            }

            @Override
            public void onPredatorStep() {
                sound.playPredatorStep();
            }

            @Override
            public void afterReset() {
                sound.play("welcome.wav");
            }
        });

        final Strategy strategy = new Strategy(board);

        final PreyManager preyManager = new PreyManager(images, board, phases);
        final UI ui = new UI(board, preyManager, images, phases);

        ui.setPreferredSize(new Dimension(500, 500));
        ui.startLevel();

        final JFrame frame = new JFrame("Bagh-Chal");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        final JButton newGameBtn = new JButton("New Game");
        newGameBtn.addActionListener(e -> ui.start());
        final JButton nextLevelBtn = new JButton("Next Level");
        nextLevelBtn.addActionListener(e -> ui.nextLevel());
        nextLevelBtn.setEnabled(false);

        final JToolBar toolBar = new JToolBar();
        toolBar.add(newGameBtn);
        toolBar.add(nextLevelBtn);

        frame.add(toolBar, BorderLayout.PAGE_START);
        frame.add(ui, BorderLayout.CENTER);

        preyManager.setEventHandler(new EventHandler() {
            @Override
            public void dragged(MouseEvent e) {
                ui.repaint(e.getX() - 30, e.getY() - 30, 60, 60);
            }

            @Override
            public void moveDone() {
                strategy.doMoveOrEndPhase(phases);
                final boolean predatorsLostLevel = phases.isEnd();
                if (predatorsLostLevel) {
                    // TODO: Find a good triumphant sound effect:
                    // sound.play("tataa.wav");
                }
                nextLevelBtn.setEnabled(predatorsLostLevel);
                ui.repaint();
            }

            @Override
            public void draggingStarted() {
                sound.playPrey();
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

}
