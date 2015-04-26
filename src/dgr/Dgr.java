package dgr;

// @author laptopng34
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Dgr extends JFrame {

    private Panel p = new Panel();

    public static void main(String[] args) throws InterruptedException {
        Dgr d = new Dgr();
        d.setGame();
    }

    public Dgr() {

    }

    public void setGame() throws InterruptedException {

        p.setSize(600, 600);
        setSize(600, 600);
        //p.setPreferredSize(new Dimension(p.xSize*p.scale,p.xSize*p.scale));
        add(p);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //JScrollPane scrPane = new JScrollPane(p);
        //p.testWorld();
        //d.add(scrPane);
        //d.setExtendedState(MAXIMIZED_BOTH);
        setVisible(true);
//        while(true){
//            p.move();
//            p.repaint();
//            Thread.sleep(20);
//        }
        runGame();
    }

    public void runGame() {

        long initialTime = System.nanoTime();
        final double timeU = 1000000000 / 20;
        final double timeF = 1000000000 / 20;
        double deltaU = 0, deltaF = 0;
        int frames = 0, ticks = 0;
        long timer = System.currentTimeMillis();

        while (true) {
            long currentTime = System.nanoTime();
            deltaU += (currentTime - initialTime) / timeU;
            deltaF += (currentTime - initialTime) / timeF;
            initialTime = currentTime;
            if (deltaU >= 1) {
                p.move();
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
