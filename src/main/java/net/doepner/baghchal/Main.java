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
import net.doepner.baghchal.play.GameLoop;
import net.doepner.baghchal.play.Player;
import net.doepner.baghchal.play.PredatorStrategy;
import net.doepner.baghchal.play.UserPlayer;
import net.doepner.baghchal.resources.Images;
import net.doepner.baghchal.resources.LevelProperties;
import net.doepner.baghchal.resources.LevelResources;
import net.doepner.baghchal.resources.Sound;
import net.doepner.baghchal.ui.BoardPanel;
import net.doepner.baghchal.ui.GameFrame;

import java.awt.Dimension;

import static net.doepner.baghchal.model.Piece.PREY;

/**
 * Entry point of the game
 */
public final class Main {

    public static void main(String... args) {

        System.setProperty("sun.java2d.opengl", "true");

        final int maxLevel = 2;
        final Dimension preferredSize = new Dimension(500, 500);
        final Dimension boardSize = new Dimension(5, 5);

        final LevelResources levelResources = new LevelResources("/net/doepner/baghchal/levels/%d/%s");
        final LevelProperties levelProperties = new LevelProperties(levelResources, "level.properties");
        final Levels levels = new Levels(levelProperties, levelResources, maxLevel);

        final Sound sound = new Sound(levels);
        final Images images = new Images(levels);

        final Board board = new Board(boardSize.width, boardSize.height, new BoardSound(sound));
        final BoardSetup boardSetup = new BoardSetup();
        final BoardPanel boardPanel = new BoardPanel(board, boardSetup, images, levels);

//        final Player preyPlayer = new PreyStrategy();
        final Player preyPlayer = new UserPlayer(PREY, boardPanel, images);
        final Player predatorPlayer = new PredatorStrategy(levels);
//        final Player predatorPlayer = new UserPlayer(PREDATOR, boardPanel, images);

        final GameFrame gameFrame = new GameFrame(boardPanel);
        final GameLoop gameLoop = new GameLoop(gameFrame, board, boardPanel, levels, sound, preyPlayer, predatorPlayer);

        gameFrame.show(preferredSize);
        gameLoop.start();
    }

}
