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

        final Board board = new Board(sound);
        final Strategy strategy = new Strategy(board);

        final GoatsManager goatsManager = new GoatsManager(images, board, phases);
        final UI ui = new UI(board, goatsManager, images, phases);

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

        goatsManager.setEventHandler(new EventHandler() {
            @Override
            public void boardChanged(MouseEvent e) {
                ui.repaint(e.getX() - 30, e.getY() - 30, 60, 60);
            }

            @Override
            public void goatMoveDone() {
                strategy.updatePossibleMoves();
                if (strategy.isOver()) {
                    phases.setEnd();
                } else {
                    strategy.generateMove(phases.getLevel());
                }
                nextLevelBtn.setEnabled(strategy.isOver());
                ui.repaint();
            }

            @Override
            public void goatDraggingStarted() {
                sound.playGoat();
            }
        });


        frame.pack();
        frame.setVisible(true);

        sound.play("welcome.wav");
    }

}
