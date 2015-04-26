package dgr;

 // @author laptopng34
import java.awt.List;
import java.util.ArrayList;
import java.util.Random;

public class WorldGen {

    private int x;
    private int y;
    Tile[][] world;
    Random rnd = new Random();
    private int avgRoomSizeX = 20;
    private int avgRoomSizeY = 20;
    private int marginSizeX = 6;
    private int marginSizeY = 6;
    private int cellX;
    private int cellY;
    private int cellamt;
    private int cellSize = 10;
    //ArrayList<Coordinate> path = new ArrayList();

    public WorldGen(int x, int y) {
        this.x = x;
        this.y = y;
        cellX = 30;
        cellY = 30;
        cellamt = (x * y) / (cellX * cellY);

    }

    public void setNewSize(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void gen() {
        ArrayList<Cell> cellList = new ArrayList();
        int roomx = 0;
        int roomy = 0;
        int osX = 0;
        int osY = 0;

        for (int s = 0; s < cellamt; s++) {
            Cell cell = new Cell(new Tile[cellX][cellY]);
            if ((s % (x / cellX)) == 0) {
                if (s > 0) {
                    osX = 0;
                    osY += cellY;
                }
            }
            for (int i = 0; i < cellX; i++) {
                for (int j = 0; j < cellY; j++) {
                    cell.setTiles(i, j, new Tile(osX + i, osY + j, 2, cell));
                }
            }
            osX += cellX;

            roomx = rnd.nextInt(avgRoomSizeX) + marginSizeX;
            roomy = rnd.nextInt(avgRoomSizeY) + marginSizeY;
            for (int i = 1 + rnd.nextInt(4); i < roomx - rnd.nextInt(2) + 1; i++) {
                for (int j = 1 + rnd.nextInt(4); j < roomy - rnd.nextInt(2) + 1; j++) {
                    {
                        /* WALLS
                         if (i == 0 || j == 0 || i == roomx || j == roomy) {
                         cell[i][j].setType(2);
                         } else
                         */
                        cell.getRoom()[i][j].setType(0);
                    }
                }
            }
            cellList.add(cell);
        }

        world = new Tile[x][y];

        //for (int q = 0; q < cellamt; q++) {
        int offsetX = 0;
        int offsetY = 0;
        for (int q = 0;
                q < cellamt;
                q++) {

            if ((q % (x / cellX)) == 0) {
                if (q > 0) {
                    offsetX = 0;
                    offsetY += cellY;
                }
            }
            for (int i = 0; i < cellX; i++) {
                for (int j = 0; j < cellY; j++) {
                    {

                        world[offsetX + i][offsetY + j] = cellList.get(q).getRoom()[i][j];

                    }
                }
            }
            offsetX += cellX;
        }

        caveIn();
        caveIn();
        caveIn();
        caveIn();
        caveIn();
        caveOut();
        caveOut();
        caveOut();   
        caveOut();
        buildCorridors(cellList);

        caveOut();
        caveOut();
        caveOut();        
        caveOut();
        caveOut();
        caveOut();   
        caveOut();
       
    }

    public Coordinate[] getTargets(ArrayList<Cell> r) {
        Random rgen = new Random();
        Coordinate[] restargets = new Coordinate[cellamt];
        int marked = 0;
        int targetcounter = 0;
        while (marked < cellamt) {

            int randomX = rgen.nextInt(cellX);
            int randomY = rgen.nextInt(cellY);
            int randomCell = rgen.nextInt(cellamt);
            if (!r.get(randomCell).isConnected()) {
                if ((r.get(randomCell).getRoom()[randomX][randomY].getType() == 0)) {
                    if (randomX < cellX - 1 && randomY < cellY - 1) {
                        {
                            restargets[targetcounter] = new Coordinate(r.get(randomCell).getRoom()[randomX][randomY].getX(), r.get(randomCell).getRoom()[randomX][randomY].getY());
                            r.get(randomCell).setConnected();
                            marked++;
                            targetcounter++;
                        }
                    }
                }
            }
        }
        return restargets;
    }

    public void buildCorridors(ArrayList<Cell> r) {
        Coordinate[] targetsextra = getTargets(r);
        buildTunnels(targetsextra);
    }

    public Tile[][] getWorld() {
        return world;
    }

    public void buildTunnels(Coordinate[] targets) {
        int tempX = 0;
        int tempY = 0;
        for (int i = 0; i < targets.length; i++) {

            if (i == targets.length - 1) {
                tempX = 0;
                tempY = 0;
            } else {
                tempX = targets[i + 1].getX() - targets[i].getX();
                tempY = targets[i + 1].getY() - targets[i].getY();
            }
            if (tempY < 0) {
                for (int k = 0; k < tempY; k++) {
                    world[targets[i].getX()][targets[i].getY() - k].setType(0);
                }
            }

            if (tempX < 0) {
                for (int j = 0; j < tempX; j++) {
                    world[targets[i].getX() - j][targets[i].getY() + tempY].setType(0);
                }
            }
            if (tempY > 0) {
                for (int k = 0; k < tempY; k++) {
                    world[targets[i].getX()][targets[i].getY() + k].setType(0);
                }

            }
            if (tempX > 0) {
                for (int j = 0; j < tempX; j++) {
                    world[targets[i].getX() + j][targets[i].getY() + tempY].setType(0);
                }
            }

        }
    }
    
        public void caveIn() {
        int caveinCounter = 0;
        Random caver = new Random();
        while (caveinCounter < 200) {
            int rx = caver.nextInt(x - 4) + 2;
            int ry = caver.nextInt(y - 4) + 2;
            if (world[rx][ry].getType() == 2) {
                if (world[rx][ry + 1].getType() == 0) {
                    world[rx][ry + 1].setType(2);
                    caveinCounter++;
                } if (world[rx][ry - 1].getType() == 0) {
                    world[rx][ry - 1].setType(2);
                    caveinCounter++;
                } if (world[rx + 1][ry + 1].getType() == 0) {
                    world[rx + 1][ry + 1].setType(2);
                    caveinCounter++;
                } if (world[rx - 1][ry - 1].getType() == 0) {
                    world[rx - 1][ry - 1].setType(2);
                    caveinCounter++;
                }
            }
        }
    }

    public void caveOut() {
        int caveinCounter = 0;
        Random caver = new Random();
        while (caveinCounter < 400) {
            int rx = caver.nextInt(x - 4) + 2;
            int ry = caver.nextInt(y - 4) + 2;
            if (world[rx][ry].getType() == 0) {
                if (world[rx+1][ry].getType() == 2) {
                    world[rx+1][ry].setType(0);
                    caveinCounter++;
                } if (world[rx-1][ry].getType() == 2) {
                    world[rx-1][ry].setType(0);
                    caveinCounter++;
                } if (world[rx + 1][ry + 1].getType() == 2) {
                    world[rx + 1][ry + 1].setType(0);
                    caveinCounter++;
                } if (world[rx - 1][ry - 1].getType() == 2) {
                    world[rx - 1][ry - 1].setType(0);
                    caveinCounter++;
                }
            }
        }
    }

    /*
     public void buildTunnels(Coordinate[] targets) {

     int tempX = 0;
     int tempY = 0;
     int tempXX;
     int tempYY;
     for (int i = 0; i < targets.length - 1; i++) {

     tempX = targets[i + 1].getX() - targets[i].getX();
     tempY = targets[i + 1].getY() - targets[i].getY();

     boolean connected = false;

     }
     }*/
}
