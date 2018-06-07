package ropegame;

import math.Vector2d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Created by sml on 22/05/2017.
 */

public class RopeGameView extends JComponent {

    static Dimension size = new Dimension(RopeGameState.width, RopeGameState.height);

    RopeGameState gameState;
    Vector2d anchor;

    public RopeGameView(RopeGameState gameState) {
        this.gameState = gameState;
        addMouseListener(new MyMouse());
        addMouseMotionListener(new MyDrag());
    }

    public Dimension getPreferredSize() {
        return size;
    }

    static Color bg = Color.black;

    public void paintComponent(Graphics gx) {
        Graphics2D g = (Graphics2D) gx;
        g.setColor(bg);
        g.fillRect(0, 0, size.width, size.height);
        g.setColor(Color.cyan);
        gameState.monkeyBall.draw(g);
    }

    class MyMouse extends MouseAdapter {
        public void mousePressed(MouseEvent event) {
            anchor = new Vector2d(event.getX(), event.getY());
        }
        public void mouseReleased(MouseEvent event) {
            anchor = null;
        }
    }

    class MyDrag extends MouseMotionAdapter {
        public void mouseDragged(MouseEvent event) {
            anchor = new Vector2d(event.getX(), event.getY());
        }
    }
}
