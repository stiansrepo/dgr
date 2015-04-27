package dgr;

 // @author laptopng34
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Panel extends JPanel implements ComponentListener {

    WorldGen world;
    int xSize = 150;
    int ySize = 150;
    Player player;
    int scale = 15;
    BufferedImage map;
    int offsetMaxX = (xSize - getWidth());
    int offsetMaxY = (ySize - getHeight());
    int offsetMinX = 0;
    int offsetMinY = 0;
    int camX;
    int camY;
    int hp = 1000;
    ArrayList<Entity> enemies;

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
        this.addComponentListener(this);
        setFocusable(true);
        world = new WorldGen(xSize, ySize);
        world.gen();
        drawMap();
        player = getStartPlayer();
        enemies = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            enemies.add(getStartEntity());
        }

        setVisible(true);
    }

    public void drawMapToFile() throws IOException {
        File outputfile = new File("image.jpg");
        ImageIO.write(map, "jpg", outputfile);
    }

    public void initCam() {
        offsetMaxX = xSize - (getWidth() / scale);
        offsetMaxY = ySize - (getHeight() / scale);
        moveCamera();
        repaint();
    }

    public void componentResized(ComponentEvent e) {
        initCam();
    }

    public void componentHidden(ComponentEvent e) {

    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
        moveCamera();
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
        ArrayList<Entity> rem = new ArrayList<>();
        Iterator<Entity> eit = enemies.iterator();
        while (eit.hasNext()) {
            Entity e = eit.next();
            e.move();
            if (e.getX() == player.getX() && e.getY() == player.getY()) {
                hp -= 10;
                e.setHp(e.getHp() - 10);

                if (e.getHp() <= 0) {
                    rem.add(e);
                }
            }
        }
        for (Entity e : rem) {
            enemies.remove(e);
        }

        moveCamera();

    }

    public void drawMap() {
        map = new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_ARGB);
        Graphics g = map.createGraphics();
        Font f = new Font("Monospace",Font.PLAIN,513);
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                switch (world.getWorld()[i][j].getType()) {
                    case 0:
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillRect(i, j, 1, 1);
                        break;
                    case 1:
                        g.setColor(Color.DARK_GRAY);
                        g.fillRect(i, j, 1, 1);
                        break;
                    case 2:
                        g.setColor(Color.BLACK);
                        g.fillRect(i, j, 1, 1);
                        break;
                    case 3:
                        g.setColor(Color.GRAY);
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
        g2d.setColor(new Color(195,125,95));

        player.paint(g2d);

        g2d.setColor(new Color(115,35,0));
        Iterator<Entity> eit = enemies.iterator();
        while (eit.hasNext()) {
            Entity e = eit.next();
            e.paint(g2d);
        }
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Verdana", Font.BOLD, 2));;
        g2d.drawString("H: " + hp, camX + 1, camY + 2);

        g2d.translate(camX, camY);
    }
}
