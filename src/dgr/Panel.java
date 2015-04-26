package dgr;

 // @author laptopng34
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Panel extends JPanel {

    WorldGen world;
    int xSize = 300;
    int ySize = 300;
    Player player;
    int scale = 20;
    BufferedImage map;
    int offsetMaxX = (xSize * scale - getWidth() / scale);
    int offsetMaxY = (ySize * scale - getHeight() / scale);
    int offsetMinX = 0;
    int offsetMinY = 0;
    int camX;
    int camY;
    int hp = 1000;
    Entity[] enemies;

    public Panel() {
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                player.keyReleased(e);
                //weapon.keyPressed(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                player.keyPressed(e);

                move();
                repaint();
                //weapon.keyPressed(e);
            }
        });
        setFocusable(true);
        world = new WorldGen(xSize, ySize);
        world.gen();
        drawMap();
        player = getStartPlayer();
        enemies = new Entity[500];
        for (int i = 0; i < enemies.length; i++) {
            enemies[i] = getStartEntity();
        }
        setVisible(true);
    }

    public void initCam() {
        moveCamera();
        repaint();
    }

    public Entity getStartEntity() {
        Entity pp = null;
        Random rgen = new Random();
        boolean success = false;
        while (success == false) {

            int startx = rgen.nextInt(xSize - 10) + 10;
            int starty = rgen.nextInt(ySize - 10) + 10;

            if (world.getWorld()[startx][starty].getType() == 0
                    && world.getWorld()[startx - 1][starty].getType() == 0
                    && world.getWorld()[startx + 1][starty].getType() == 0) {
                pp = new Entity(this, startx, starty, player);
                success = true;
            }

        }
        return pp;
    }

    public Player getStartPlayer() {
        Player pp = null;
        Random rgen = new Random();
        boolean success = false;
        while (success == false) {

            int startx = rgen.nextInt(xSize - 10) + 10;
            int starty = rgen.nextInt(ySize - 10) + 10;

            if (world.getWorld()[startx][starty].getType() == 0
                    && world.getWorld()[startx - 1][starty].getType() == 0
                    && world.getWorld()[startx + 1][starty].getType() == 0) {
                pp = new Player(this, startx, starty);
                success = true;
            }

        }
        return pp;
    }

    public void moveCamera() {
        camX = (player.getX() - getWidth() / scale / 2);
        camY = (player.getY() - getHeight() / scale / 2);

        if (camX > offsetMaxX) {
            camX = offsetMaxX;
        } else if (camX < offsetMinX) {
            camX = offsetMinX;
        }
        if (camY > offsetMaxY) {
            camY = offsetMaxY;
        } else if (camY < offsetMinY) {
            camY = offsetMinY;
        }
    }

    public void move() {
        player.move();
        for (Entity e : enemies) {
            if (e.getX() == player.getX() && e.getY() == player.getY()) {
                hp -= 10;
            }
            int enemyrange = 5;
            int ex = player.getX() - e.getX();
            int ey = player.getY() - e.getY();
            if (ex > 0) {
                if (ex < enemyrange) {
                    e.move();
                }
            } else if (ex < 0) {
                if (ex < -enemyrange) {
                    e.move();
                }
            }
            if (ey > 0) {
                if (ey < enemyrange) {
                    e.move();
                }
            } else if (ey < 0) {
                if (ey < -enemyrange) {
                    e.move();
                }
            }

        }
        moveCamera();

    }

    public void drawMap() {
        map = new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_ARGB);
        Graphics g = map.createGraphics();

        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                switch (world.getWorld()[i][j].getType()) {
                    case 0:
                        g.setColor(Color.GRAY);
                        g.fillRect(i, j, 1, 1);
                        break;
                    case 2:
                        g.setColor(Color.BLACK);
                        g.fillRect(i, j, 1, 1);
                        break;
                    default:
                        break;
                }
            }
        }
        g.dispose();
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
        ;

        AffineTransform tx = new AffineTransform();
        tx.translate(-camX, -camY);

        AffineTransformOp op = new AffineTransformOp(tx,
                AffineTransformOp.TYPE_BILINEAR);
        double scaled = scale;
        g2d.scale(scaled, scaled);
        g2d.translate(-camX, -camY);

        g2d.drawImage(map, null, this);
        g2d.setColor(Color.red);

        player.paint(g2d);

        g2d.setColor(Color.green);
        for (Entity e : enemies) {
            e.paint(g2d);
        }
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Verdana", Font.BOLD, 1));;
        g2d.drawString("H: " + hp, camX + 1, camY + 2);

        g2d.translate(camX, camY);

    }
}
