package dgr;

 // @author laptopng34
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;
import java.util.Random;

public class Entity {

    int x;
    int xa = 1;
    int y;
    int ya = 1;
    private static final int WIDTH = 1;
    private static final int HEIGHT = 1;
    Panel panel;
    Player player;
    int xc;
    int yc;

    public Entity(Panel panel, int x, int y, Player player) {
        this.x = x;
        this.y = y;
        this.panel = panel;
        this.player = player;
    }

    public int getX() {
        return xc;
    }

    public int getY() {
        return yc;
    }

    public void move() {/*
        Random rnd = new Random();
        boolean r = rnd.nextBoolean();
        boolean l = rnd.nextBoolean();

        boolean m = rnd.nextBoolean();

        boolean u = rnd.nextBoolean();

        boolean d = rnd.nextBoolean();
        if (m) {
            if (l) {
                if (panel.world.getWorld()[x - xa][y].getType() == 0) {
                    x = x - xa;
                    xc = panel.world.getWorld()[x - xa][y].getX();
                    yc = panel.world.getWorld()[x - xa][y].getY();
                }
            }
            if (r) {
                if (panel.world.getWorld()[x + xa][y].getType() == 0) {
                    x = x + xa;
                    xc = panel.world.getWorld()[x + xa][y].getX();
                    yc = panel.world.getWorld()[x + xa][y].getY();
                }
            }

            if (d) {
                if (panel.world.getWorld()[x][y + ya].getType() == 0) {
                    y = y + ya;
                    xc = panel.world.getWorld()[x][y + ya].getX();
                    yc = panel.world.getWorld()[x][y + ya].getY();
                }
            }
            if (u) {
                if (panel.world.getWorld()[x][y - ya].getType() == 0) {
                    y = y - ya;
                    xc = panel.world.getWorld()[x][y - ya].getX();
                    yc = panel.world.getWorld()[x][y - ya].getY();
                }
            }
        }*/
        int targetx = player.getX() - x;
        int targety = player.getY() - y;
        
        if(targetx>0){
            if (panel.world.getWorld()[x+xa][y].getType() == 0&&!(x+xa==player.getX()&&y==player.getY())){
                x = x+xa;
            }
        }
        if(targetx<0){
            if (panel.world.getWorld()[x-xa][y].getType() == 0&&!(x-xa==player.getX()&&y==player.getY())){
                x = x-xa;
            }
        }
        if(targety>0){
            if (panel.world.getWorld()[x][y+ya].getType() == 0&&!(x==player.getX()&&y+ya==player.getY())){
                y = y+ya;
            }
        }
        if(targety<0){
            if (panel.world.getWorld()[x][y-ya].getType() == 0&&!(x==player.getX()&&y-ya==player.getY())){
                y = y-ya;
            }
        }
        
    }

    public void paint(Graphics2D g) {
        g.fillRect(x, y, WIDTH, HEIGHT);
    }

}
