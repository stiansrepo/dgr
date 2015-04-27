package dgr;

// @author laptopng34
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Dgr extends JFrame {

    private Panel p;

    public static void main(String[] args) throws InterruptedException, IOException {
        Dgr d = new Dgr();
        d.setGame();
    }

    public Dgr() {

    }

    public void setGame() throws InterruptedException, IOException {

        p = new Panel();
        Dimension size = new Dimension(800, 800);
        p.setPreferredSize(size);
        setSize(size);
        setLayout(new GridLayout(1, 1));

        add(p);
        p.setSize(getWidth(), getHeight());
        p.initCam();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        centerFrame();
        setVisible(true);
        p.drawMapToFile();
    }
    
    public void centerFrame(){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    }

    public void runGame() {

        long initialTime = System.nanoTime();
        final double timeU = 1000000000 / 10;
        final double timeF = 1000000000 / 10;
        double deltaU = 0, deltaF = 0;
        int frames = 0, ticks = 0;
        long timer = System.currentTimeMillis();

        while (true) {
            long currentTime = System.nanoTime();
            deltaU += (currentTime - initialTime) / timeU;
            deltaF += (currentTime - initialTime) / timeF;
            initialTime = currentTime;
            if (deltaU >= 1) {
                ticks++;
                deltaU--;
            }

            if (deltaF >= 1) {
                p.repaint();
                frames++;
                deltaF--;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                if (true) {
                    System.out.println(String.format("UPS: %s, FPS: %s", ticks, frames));
                }
                frames = 0;
                ticks = 0;
                timer += 1000;
            }
        }

    }

}
