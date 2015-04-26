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
    int offsetMaxX = (xSize - getWidth());
    int offsetMaxY = (ySize - getHeight());
    int offsetMinX = 0;
    int offsetMinY = 0;
    int camX;
    int camY;
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
                //weapon.keyPressed(e);
            }
        });
        setFocusable(true);
        world = new WorldGen(xSize, ySize);
        world.gen();
        drawMap();
        player = getStartPlayer();
        enemies = new Entity[500];
        for(int i=0;i<enemies.length;i++){
            enemies[i]=getStartEntity();
        }
        setVisible(true);

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
                pp = new Entity(this, startx, starty,player);
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

    public void move() {
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
        player.move();
        for(Entity e : enemies){
            e.move();
        }

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

    public String printRow(Tile[] t) {
        String res = "";
        for (Tile tt : t) {
            switch (tt.getType()) {
                case 0:
                    res = res + " . ";
                    break;
                case 2:
                    res = res + " # ";
                    break;
                default:
                    break;
            }
        }
        res = res + "<br>";
        return res;
    }

    public void testWorld() {
        int counter = 0;
        String mappy = "";
        for (Tile[] t : world.getWorld()) {
            mappy = mappy + printRow(t);
        }
        mappy = "<html>" + mappy + "</html>";
        JLabel jl = new JLabel();
        Font f = new Font("Monospaced", Font.BOLD, 12);
        jl.setFont(f);
        jl.setText(mappy);
        add(jl);

    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
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
        for (Entity e : enemies){
            e.paint(g2d);
        }
        
        g2d.translate(camX, camY);
    }
}
