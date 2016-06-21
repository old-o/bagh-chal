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

import net.doepner.baghchal.model.Board;
import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.model.Move;
import net.doepner.baghchal.play.Player;
import net.doepner.baghchal.play.PredatorStrategy;
import net.doepner.baghchal.play.UserPreyPlayer;
import net.doepner.baghchal.resources.Images;
import net.doepner.baghchal.resources.LevelProperties;
import net.doepner.baghchal.resources.LevelResources;
import net.doepner.baghchal.resources.Sound;
import net.doepner.baghchal.ui.BoardPanel;
import net.doepner.baghchal.ui.MainFrame;

import java.awt.Dimension;

/**
 * Entry point of the game
 */
public final class Main {

    public static void main(String... args) {

        System.setProperty("sun.java2d.opengl", "true");

        final int maxLevel = 2;
        final Dimension preferredSize = new Dimension(500, 500);

        final LevelResources levelResources = new LevelResources("/net/doepner/baghchal/levels/%d/%s");
        final LevelProperties levelProperties = new LevelProperties(levelResources, "level.properties");
        final Levels levels = new Levels(levelProperties, maxLevel);

        final Sound sound = new Sound(levels, levelResources);
        final Images images = new Images(levels, levelResources);

        final Board board = new Board(new BoardSound(sound));
        final BoardPanel boardPanel = new BoardPanel(board, new BoardSetup(), images, levels);

        final Player preyPlayer = new UserPreyPlayer(boardPanel, images);
        final Player predatorPlayer = new PredatorStrategy(levels);

        final MainFrame mainFrame = new MainFrame(boardPanel);
        mainFrame.show(preferredSize);

        while (!levels.isGameOver()) {
            preyPlayer.play(board);
            final Move predatorMove = predatorPlayer.play(board);
            final boolean predatorsLostLevel = (predatorMove == null);
            if (predatorsLostLevel) {
                sound.playResource("/net/doepner/baghchal/congrats.wav");
            }
            levels.setLevelDone(predatorsLostLevel);
            mainFrame.enableNextLevel(predatorsLostLevel && !levels.isGameOver());
            boardPanel.repaint();
        }
    }

}
