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
 */

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class GoatsAndTigers extends JFrame
        implements ActionListener, MouseMotionListener, MouseListener {

    Image offscreen;
    Color bgColor;
    int board[][];
    int phase;
    int level;
    int selectedGoat;
    int mouseX;
    int mouseY;
    int draggedPieceX;
    int draggedPieceY;
    Button newGameBtn;
    Button nextLevelBtn;
    Image goat;
    Image tiger;
    Image congrats;
    boolean dragging;
    boolean remainingGoat[];
    List<Move> possibleMoves;


    public GoatsAndTigers() {
        bgColor = Color.white;
        board = new int[5][5];
        dragging = false;
        remainingGoat = new boolean[20];
        possibleMoves = new ArrayList<>();
        setLayout(null);
        newGameBtn = new Button("New Game");
        add(newGameBtn);
        newGameBtn.addActionListener(this);
        newGameBtn.setBounds(410, 460, 80, 30);
        nextLevelBtn = new Button("Next Level");
        add(nextLevelBtn);
        nextLevelBtn.addActionListener(this);
        nextLevelBtn.setBounds(200, 225, 100, 50);
        nextLevelBtn.setVisible(false);
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newGameBtn)
            start();
        else if (e.getSource() == nextLevelBtn) {
            resetBoard();
            phase = 1;
            loadImages(level);
            repaint();
            nextLevelBtn.setVisible(false);
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (dragging) {
            mouseX = e.getX() - 16;
            mouseY = e.getY() - 16;
            repaint();
        }
    }

    public void mouseMoved(MouseEvent mouseevent) {
    }

    public void mouseEntered(MouseEvent mouseevent) {
    }

    public void mouseExited(MouseEvent mouseevent) {
    }

    public void mouseClicked(MouseEvent mouseevent) {
    }

    public void mouseReleased(MouseEvent e) {
        if (!dragging)
            return;
        dragging = false;
        int x = e.getX();
        int y = e.getY();
        if (x < 20 || y < 20 || x > 450 || y > 450) {
            if (phase == 1)
                remainingGoat[selectedGoat] = true;
            else
                board[draggedPieceX][draggedPieceY] = 1;
            repaint();
            return;
        }
        int i = (int) ((double) x / 100D + 0.25D);
        int j = (int) ((double) y / 100D + 0.25D);
        if (i < 0)
            i = 0;
        else if (i > 4)
            i = 4;
        if (j < 0)
            j = 0;
        else if (j > 4)
            j = 4;
        if (b(i, j) == 0 && (phase == 1 || validGoatMove(draggedPieceX, draggedPieceY, i, j))) {
            board[i][j] = 1;
            if (phase == 1 && endOfPhase1())
                phase = 2;
            if (i != draggedPieceX || j != draggedPieceY)
                doAI();
            repaint();
            return;
        }
        if (phase == 1)
            remainingGoat[selectedGoat] = true;
        else
            board[draggedPieceX][draggedPieceY] = 1;
        repaint();
    }

    boolean validGoatMove(int x1, int y1, int x2, int y2) {
        if (x1 < 0 || x1 > 4 || x2 < 0 || x2 > 4 || y1 < 0 || y1 > 4 || y2 < 0 || y2 > 4) {
            return false;
        }
        int dx = x1 - x2;
        int dy = y1 - y2;
        if (dx == 0 && dy == 0) {
            return false;
        }
        if (dx > 1 || dx < -1 || dy > 1 || dy < -1) {
            return false;
        }
        if (dx == 1) {
            if (dy == 1) {
                return canMoveUpLeft(x1, y1);
            }
            if (dy == -1) {
                return canMoveDownLeft(x1, y1);
            }
        } else if (dx == -1) {
            if (dy == 1) {
                return canMoveUpRight(x1, y1);
            }
            if (dy == -1) {
                return canMoveDownRight(x1, y1);
            }
        }
        return true;
    }

    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (x >= 460) {
            int i = (y - 10) / 40;
            if (remainingGoat[i]) {
                selectedGoat = i;
                remainingGoat[i] = false;
                dragging = true;
            }
        } else if (y >= 460) {
            int i = (x - 10) / 40 + 10;
            if (remainingGoat[i]) {
                selectedGoat = i;
                remainingGoat[i] = false;
                dragging = true;
            }
        }
        if (phase == 2 && x >= 20 && x <= 450 && y >= 20 && y <= 450) {
            int i = (int) ((double) x / 100D + 0.25D);
            int j = (int) ((double) y / 100D + 0.25D);
            if (b(i, j) == 1) {
                draggedPieceX = i;
                draggedPieceY = j;
                board[i][j] = 0;
                dragging = true;
            }
        }
    }

    public void start() {
        nextLevelBtn.setVisible(false);
        resetBoard();
        phase = 1;
        loadImages(1);
        repaint();
        draggedPieceX = draggedPieceY = -1;
    }

    boolean endOfPhase1() {
        for (int i = 0; i < 20; i++) {
            if (remainingGoat[i]) {
                return false;
            }
        }
        return true;
    }

    boolean canMoveDownLeft(int i, int j) {
        return !(i == 0 || j == 4) && (i + j == 4 || i == 2 && j == 0 || i == 1 && j == 1 || i == 4 && j == 2 || i == 3 && j == 3);
    }

    boolean canMoveDownRight(int i, int j) {
        return !(i == 4 || j == 4) && (i == j || i == 0 && j == 2 || i == 1 && j == 3 || i == 2 && j == 0 || i == 3 && j == 1);
    }

    boolean canMoveUpRight(int i, int j) {
        return j != 0 && (i + j == 4 || i == 0 && j == 2 || i == 1 && j == 1 || i == 2 && j == 4 || i == 3 && j == 3);
    }

    boolean canMoveUpLeft(int i, int j) {
        return !(j == 0 || i == 0) && (i == j || i == 1 && j == 3 || i == 2 && j == 4 || i == 3 && j == 1 || i == 4 && j == 2);
    }

    void updatePossibleMoves() {
        possibleMoves.clear();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++)
                if (board[i][j] == 2) {
                    if (j < 4)
                        if (board[i][j + 1] == 0)
                            possibleMoves.add(new Move(i, j, i, j + 1, board));
                        else if (j < 3 && board[i][j + 1] == 1 && board[i][j + 2] == 0)
                            possibleMoves.add(new Move(i, j, i, j + 2, board));
                    if (j > 0)
                        if (board[i][j - 1] == 0)
                            possibleMoves.add(new Move(i, j, i, j - 1, board));
                        else if (j > 1 && board[i][j - 1] == 1 && board[i][j - 2] == 0)
                            possibleMoves.add(new Move(i, j, i, j - 2, board));
                    if (i > 0)
                        if (board[i - 1][j] == 0)
                            possibleMoves.add(new Move(i, j, i - 1, j, board));
                        else if (i > 1 && board[i - 1][j] == 1 && board[i - 2][j] == 0)
                            possibleMoves.add(new Move(i, j, i - 2, j, board));
                    if (i < 4)
                        if (board[i + 1][j] == 0)
                            possibleMoves.add(new Move(i, j, i + 1, j, board));
                        else if (i < 3 && board[i + 1][j] == 1 && board[i + 2][j] == 0)
                            possibleMoves.add(new Move(i, j, i + 2, j, board));
                    if (i > 0 && j < 4 && canMoveDownLeft(i, j))
                        if (board[i - 1][j + 1] == 0)
                            possibleMoves.add(new Move(i, j, i - 1, j + 1, board));
                        else if (i > 1 && j < 3 && board[i - 1][j + 1] == 1 && board[i - 2][j + 2] == 0)
                            possibleMoves.add(new Move(i, j, i - 2, j + 2, board));
                    if (i < 4 && j < 4 && canMoveDownRight(i, j))
                        if (board[i + 1][j + 1] == 0)
                            possibleMoves.add(new Move(i, j, i + 1, j + 1, board));
                        else if (i < 3 && j < 3 && board[i + 1][j + 1] == 1 && board[i + 2][j + 2] == 0)
                            possibleMoves.add(new Move(i, j, i + 2, j + 2, board));
                    if (i > 0 && j > 0 && canMoveUpLeft(i, j))
                        if (board[i - 1][j - 1] == 0)
                            possibleMoves.add(new Move(i, j, i - 1, j - 1, board));
                        else if (i > 1 && j > 1 && board[i - 1][j - 1] == 1 && board[i - 2][j - 2] == 0)
                            possibleMoves.add(new Move(i, j, i - 2, j - 2, board));
                    if (i < 4 && j > 0 && canMoveUpRight(i, j))
                        if (board[i + 1][j - 1] == 0)
                            possibleMoves.add(new Move(i, j, i + 1, j - 1, board));
                        else if (i < 3 && j > 1 && board[i + 1][j - 1] == 1 && board[i + 2][j - 2] == 0)
                            possibleMoves.add(new Move(i, j, i + 2, j - 2, board));
                }

        }

    }

    public boolean isTakingMove(Move m) {
        return m.x1 - m.x2 == 2 || m.x2 - m.x1 == 2 || m.y1 - m.y2 == 2 || m.y2 - m.y1 == 2;
    }

    void doAI_1() {
        if (!tryToTake())
            doMove(possibleMoves.get((int) (Math.random() * (double) possibleMoves.size())));
    }

    int b(int i, int j) {
        if (i < 0 || i > 4 || j < 0 || j > 4)
            return -1;
        else
            return board[i][j];
    }

    int[][] copyBoard() {
        int a[][] = new int[5][5];
        for (int i = 0; i < 5; i++) {
            System.arraycopy(board[i], 0, a[i], 0, 5);

        }
        return a;
    }

    int numberOfPiecesThreatened(int brd[][]) {
        int r = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++)
                if (brd[i][j] == 2) {
                    if (i > 1) {
                        if (j > 1 && brd[i - 1][j - 1] == 1 && brd[i - 2][j - 2] == 0)
                            r++;
                        if (brd[i - 1][j] == 1 && brd[i - 2][j] == 0)
                            r++;
                        if (j < 3 && brd[i - 1][j + 1] == 1 && brd[i - 2][j + 2] == 0)
                            r++;
                    }
                    if (j < 3 && brd[i][j + 1] == 1 && brd[i][j + 2] == 0)
                        r++;
                    if (i < 3) {
                        if (j < 3 && brd[i + 1][j + 1] == 1 && brd[i + 2][j + 2] == 0)
                            r++;
                        if (brd[i + 1][j] == 1 && brd[i + 2][j] == 0)
                            r++;
                        if (j > 1 && brd[i + 1][j - 1] == 1 && brd[i + 2][j - 2] == 0)
                            r++;
                    }
                    if (j > 1 && brd[i][j - 1] == 1 && brd[i][j - 2] == 0)
                        r++;
                }

        }

        return r;
    }

    void doAI_2() {
        if (tryToTake()) {
            return;
        }
        final int s = possibleMoves.size();
        List<Move> threatensOne = new ArrayList<>();
        List<Move> threatensMany = new ArrayList<>();
        for (Move m : possibleMoves) {
            int brd[][] = copyBoard();
            brd[m.x2][m.y2] = brd[m.x1][m.y1];
            brd[m.x1][m.y1] = 0;
            int t = numberOfPiecesThreatened(brd);
            if (t == 1) {
                threatensOne.add(m);
            } else if (t > 1) {
                threatensMany.add(m);
            }
        }

        int s2 = threatensMany.size();
        if (s2 > 0) {
            doMove(possibleMoves.get((int) (Math.random() * (double) s2)));
        } else {
            s2 = threatensOne.size();
            if (s2 > 0) {
                doMove(possibleMoves.get((int) (Math.random() * (double) s2)));
            } else {
                doMove(possibleMoves.get((int) (Math.random() * (double) s)));
            }
        }

    }

    void doAI_3() {
        List<Move> takes = possibleMoves.stream().filter(this::isTakingMove).collect(toList());

        int s2 = takes.size();
        if (s2 > 0) {
            doMove(takes.get((int) (Math.random() * (double) s2)));
            return;
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++)
                if (board[i][j] == 2) {
                    if (canMoveUpLeft(i, j) && i > 1 && j > 1 && board[i - 1][j - 1] == 1 && board[i - 2][j - 2] == 0)
                        takes.add(new Move(i, j, i - 2, j - 2, board));
                    if (i > 1 && board[i - 1][j] == 1 && board[i - 2][j] == 0)
                        takes.add(new Move(i, j, i - 2, j, board));
                    if (canMoveDownLeft(i, j) && i > 1 && j < 3 && board[i - 1][j + 1] == 1 && board[i - 2][j + 2] == 0)
                        takes.add(new Move(i, j, i - 2, j + 2, board));
                    if (j < 3 && board[i][j + 1] == 1 && board[i][j + 2] == 0)
                        takes.add(new Move(i, j, i, j + 2, board));
                    if (canMoveDownRight(i, j) && i < 3 && j < 3 && board[i + 1][j + 1] == 1 && board[i + 2][j + 2] == 0)
                        takes.add(new Move(i, j, i + 2, j + 2, board));
                    if (i < 3 && board[i + 1][j] == 1 && board[i + 2][j] == 0)
                        takes.add(new Move(i, j, i + 2, j, board));
                    if (canMoveUpRight(i, j) && i < 3 && j > 1 && board[i + 1][j - 1] == 1 && board[i + 2][j - 2] == 0)
                        takes.add(new Move(i, j, i + 2, j - 2, board));
                    if (j > 1 && board[i][j - 1] == 1 && board[i][j - 2] == 0)
                        takes.add(new Move(i, j, i, j - 2, board));
                }

        }

        s2 = takes.size();
        if (s2 > 0) {
            doMove(takes.get((int) (Math.random() * (double) s2)));
        } else {
            doMove(possibleMoves.get((int) (Math.random() * (double) possibleMoves.size())));
        }
    }

    void doAI() {
        updatePossibleMoves();
        if (possibleMoves.size() == 0) {
            level++;
            phase = 3;
            repaint();
            nextLevelBtn.setVisible(true);
            return;
        }
        switch (level) {
            case 1: // '\001'
                doAI_1();
                break;

            case 2: // '\002'
                doAI_2();
                break;

            case 3: // '\003'
            default:
                doAI_3();
                break;
        }
        repaint();
    }

    boolean tryToTake() {
        List<Move> takingMoves = new ArrayList<>();
        for (Object possibleMove : possibleMoves) {
            Move m = (Move) possibleMove;
            if (isTakingMove(m))
                takingMoves.add(m);
        }

        int s2 = takingMoves.size();
        if (s2 > 0) {
            doMove(takingMoves.get((int) (Math.random() * (double) s2)));
            return true;
        } else {
            return false;
        }
    }

    void doMove(Move m) {
        board[m.x2][m.y2] = board[m.x1][m.y1];
        board[m.x1][m.y1] = 0;
        if (isTakingMove(m))
            board[m.x1 + m.x2 >> 1][m.y1 + m.y2 >> 1] = 0;
    }

    void loadImages(int i) {
        level = i;
        String s1 = "1_Buckeye.gif";
        String s2 = "";
        switch (i) {
            case 1: // '\001'
                s2 = "1_BrownMoth.gif";
                break;

            case 2: // '\002'
                s2 = "2_Bunny.gif";
                break;

            case 3: // '\003'
                s2 = "3_Crab.gif";
                break;

            case 4: // '\004'
                s2 = "4_Snail.gif";
                break;

            case 5: // '\005'
                s2 = "5_FishGold.gif";
                break;

            case 6: // '\006'
                s2 = "6_FishBlue.gif";
                break;

            case 7: // '\007'
                s2 = "7_CoolShark.gif";
                break;
        }
        goat = getImage(s1);
        tiger = getImage(s2);
        congrats = getImage("congrats.gif");
    }

    private Image getImage(String s) {
        try {
            return ImageIO.read(getClass().getResource(s));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    void resetBoard() {
        for (int i = 0; i < 20; i++)
            remainingGoat[i] = true;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++)
                board[i][j] = 0;

        }

        board[0][0] = board[4][0] = board[0][4] = board[4][4] = 2;
    }

    void drawBoard(Graphics2D g2) {
        g2.setColor(Color.black);
        g2.drawRect(30, 30, 400, 400);
        for (int i = 0; i < 3; i++) {
            int j = 130 + 100 * i;
            g2.drawLine(30, j, 430, j);
            g2.drawLine(j, 30, j, 430);
        }

        g2.drawLine(30, 30, 430, 430);
        g2.drawLine(30, 230, 230, 430);
        g2.drawLine(230, 30, 430, 230);
        g2.drawLine(30, 230, 230, 30);
        g2.drawLine(230, 30, 430, 230);
        g2.drawLine(230, 430, 430, 230);
        g2.drawLine(30, 430, 430, 30);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++)
                if (board[i][j] != 0) {
                    Image im = board[i][j] != 1 ? tiger : goat;
                    g2.drawImage(im, 14 + i * 100, 14 + j * 100, this);
                }

        }

    }

    void drawRemainingGoats(Graphics2D g2) {
        for (int i = 0; i < 10; i++)
            if (remainingGoat[i])
                g2.drawImage(goat, 460, 10 + i * 40, this);

        for (int i = 0; i < 10; i++)
            if (remainingGoat[i + 10])
                g2.drawImage(goat, 10 + i * 40, 460, this);

    }

    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;
        g2.setColor(bgColor);
        g2.fillRect(0, 0, 500, 500);
        if (phase > 2) {
            g2.setColor(bgColor);
            g2.fillRect(0, 0, 500, 500);
            g2.setColor(Color.black);
            g2.drawImage(congrats, 70, 80, this);
            String s;
            if (level > 7) {
                s = "You have completed Bagha Chal";
                nextLevelBtn.setVisible(false);
            } else {
                s = "Now try level " + level;
            }
            g2.setFont(new Font("SansSerif", 0, 34));
            g2.drawString(s, 250 - (g2.getFontMetrics().stringWidth(s) >> 1), 350);
            g.drawImage(offscreen, 0, 0, this);
            return;
        }
        if (phase == 0) {
            g2.setColor(bgColor);
            g2.fillRect(0, 0, 500, 500);
            g2.drawString("Downloading images...", 50, 50);
            g.drawImage(offscreen, 0, 0, this);
            return;
        }
        drawBoard(g2);
        drawRemainingGoats(g2);
        if (dragging)
            g2.drawImage(goat, mouseX, mouseY, this);
        g.drawImage(offscreen, 0, 0, this);
    }

}
