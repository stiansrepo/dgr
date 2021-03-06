package dgr;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;

public class Player {

    int x;
    int xa = 0;
    int y;
    int ya = 0;
    private static final int WIDTH = 1;
    private static final int HEIGHT = 1;
    Panel panel;
    int xc;
    int yc;

    public Player(Panel panel, int x, int y) {
        this.x = x;
        this.y = y;
        this.panel = panel;
        xc = panel.world.getWorld()[x][y].getX();
        yc = panel.world.getWorld()[x][y].getY();
    }

    public void move() {

        boolean legalX = false;
        boolean legalY = false;
        int[] legaltype = new int[]{0, 1, 3};
        for (int i : legaltype) {
            if (panel.world.getWorld()[x][y + ya].getType() == i) {
                legalY = true;
            }
        }
        for (int i : legaltype) {
            if (panel.world.getWorld()[x + xa][y].getType() == i) {
                legalX = true;
            }
        }
        
        

        if (legalX) {
            x = x + xa;
            xc = panel.world.getWorld()[x + xa][y].getX();
            yc = panel.world.getWorld()[x + xa][y].getY();
        }

        if (legalY) {
            y = y + ya;
            xc = panel.world.getWorld()[x][y + ya].getX();
            yc = panel.world.getWorld()[x][y + ya].getY();
        }

    }

    public int getX() {
        return xc;
    }

    public int getY() {
        return yc;
    }

    public void paint(Graphics2D g) {
        g.fillRect(x, y, WIDTH, HEIGHT);
    }

    public void keyReleased(KeyEvent e) {
        /*xa = 0;
         ya = 0;*/
        switch (e.getKeyCode()) {

            case KeyEvent.VK_A:
                xa = 0;
                break;
            case KeyEvent.VK_D:
                xa = 0;
                break;
            case KeyEvent.VK_W:
                ya = 0;
                break;
            case KeyEvent.VK_S:
                ya = 0;
                break;
        }
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {

            case KeyEvent.VK_A:
                xa = -1;
                break;
            case KeyEvent.VK_D:
                xa = 1;
                break;
            case KeyEvent.VK_W:
                ya = -1;
                break;
            case KeyEvent.VK_S:
                ya = 1;
                break;
        }
    }

}
