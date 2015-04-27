package dgr;

 // @author laptopng34
import java.awt.List;
import java.util.ArrayList;
import java.util.Random;

public class WorldGen {

    private int x;
    private int y;
    Tile[][] world;
    Tile[][] tmpWorld;
    Random rnd = new Random();
    private int avgRoomSizeX = 16;
    private int avgRoomSizeY = 20;
    private int marginSizeX = 4;
    private int marginSizeY = 4;
    private int cellX;
    private int cellY;
    private int cellamt;
    private int cellSize;
    private int caveinamt = 1;
    private int cavecorridoramt = 70;
    private int caveoutamt = 70;
    //ArrayList<Coordinate> path = new ArrayList();

    public WorldGen(int x, int y) {
        this.x = x;
        this.y = y;
        cellSize = x / 5;
        cellX = cellSize;
        cellY = cellSize;
        cellamt = (x * y) / (cellX * cellY);

    }

    public void setNewSize(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public ArrayList<Cell> generateCellListFromMap(Tile[][] tileMap) {

        ArrayList<Cell> cellList = new ArrayList();

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
                    cell.setTiles(i, j, new Tile(tileMap[i + osX][j + osY].getX(), tileMap[i + osX][j + osY].getY(), tileMap[i + osX][j + osY].getType(), cell));
                }
            }

            cellList.add(cell);
            osX += cellX;
        }
        return cellList;
    }

    public ArrayList<Cell> getCellList() {
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
            for (int i = 1; i < roomx - rnd.nextInt(2) + 1; i++) {
                for (int j = 1; j < roomy + 1; j++) {
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
        return cellList;
    }

    public void genRoomWorld() {
        world = new Tile[x][y];

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

                        world[offsetX + i][offsetY + j] = getCellList().get(q).getRoom()[i][j];

                    }
                }
            }
            offsetX += cellX;
        }

    }

    public void gen() {

        world = new Tile[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                int rtype = 0;
                if (rnd.nextInt(100) < 50) {
                    rtype = 2;
                }
                world[i][j] = new Tile(i, j, rtype);
            }
        }

        /*
         cellularAutomataTwo();
         cellularAutomataTwo();
         buildCorridors(generateCellListFromMap(world));
         cellularAutomataTwo();
        
         cellularAutomata();
         buildCorridors(generateCellListFromMap(world));
         caveOut();
         cellularAutomata();
         cellularAutomata();
         cellularAutomata();
         cellularAutomata();
         cellularAutomata();
         */
        cellularAutomata();
        cellularAutomata();
        cellularAutomata();
        
        cellularAutomataTwo();
        
        cellularAutomataTwo();

        int threshold = 0;

        while (threshold < 1000) {

            threshold=flood(world, new boolean[x][y], rnd.nextInt(x), rnd.nextInt(y), 0, 2);
        }
        world = tmpWorld;
    }

    public void cellularAutomata() {
        Tile[][] newmap = new Tile[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {

                if (i > 0 && i < x - 1 && j > 0 && j < y - 1) {
                    int wallcount = 0;
                    if (world[i + 1][j].getType() == 2) {
                        wallcount++;
                    }
                    if (world[i + 1][j + 1].getType() == 2) {
                        wallcount++;
                    }
                    if (world[i][j + 1].getType() == 2) {
                        wallcount++;
                    }
                    if (world[i - 1][j + 1].getType() == 2) {
                        wallcount++;
                    }
                    if (world[i - 1][j - 1].getType() == 2) {
                        wallcount++;
                    }
                    if (world[i - 1][j].getType() == 2) {
                        wallcount++;
                    }
                    if (world[i][j - 1].getType() == 2) {
                        wallcount++;
                    }
                    if (world[i + 1][j - 1].getType() == 2) {
                        wallcount++;
                    }
                    if (world[i][j].getType() == 0) {
                        if (wallcount >= 5) {
                            newmap[i][j] = new Tile(i, j, 2, world[i][j].getRoom());
                        } else {
                            newmap[i][j] = new Tile(i, j, 0, world[i][j].getRoom());
                        }
                    } else if (world[i][j].getType() == 2) {
                        if (wallcount >= 4) {
                            newmap[i][j] = new Tile(i, j, 2, world[i][j].getRoom());
                        } else {
                            newmap[i][j] = new Tile(i, j, 0, world[i][j].getRoom());
                        }
                    } else {
                        newmap[i][j] = new Tile(i, j, 0, world[i][j].getRoom());
                    }
                } else {
                    newmap[i][j] = new Tile(i, j, 2, world[i][j].getRoom());
                }
            }
        }
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                world[i][j] = new Tile(newmap[i][j]);
            }
        }
    }

    private int flood(Tile[][] tgtWorld, boolean[][] mark,
            int row, int col, int srcTileType, int tgtTileType) {
        // make sure row and col are inside the image
        if (row < 0) {
            return -1;
        }
        if (col < 0) {
            return -1;
        }
        if (row >= y) {
            return -1;
        }
        if (col >= x) {
            return -1;
        }

        // make sure this pixel hasn't been visited yet
        if (mark[row][col]) {
            return -1;
        }

        // make sure this pixel is the right color to fill
        if (tgtWorld[row][col].getType() == 2) {
            return -1;
        }

        // fill pixel with target color and mark it as visited
        tgtWorld[col][row].setType(tgtTileType);
        mark[row][col] = true;

        // recursively fill surrounding pixels
        // (this is equivelant to depth-first search)
        flood(tgtWorld, mark, row - 1, col, srcTileType, tgtTileType);
        flood(tgtWorld, mark, row + 1, col, srcTileType, tgtTileType);
        flood(tgtWorld, mark, row, col - 1, srcTileType, tgtTileType);
        flood(tgtWorld, mark, row, col + 1, srcTileType, tgtTileType);
        int marked = 0;
        for (int i = 0; i < mark.length; i++) {
            for (int j = 0; j < mark[i].length; j++) {
                if (mark[i][j] == true) {
                    marked++;
                }
            }
        }
        tmpWorld = tgtWorld;
        return marked;
    }

    public void cellularAutomataTwo() {
        Tile[][] newmap = new Tile[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {

                if (i > 1 && i < x - 2 && j > 1 && j < y - 2) {
                    int[] wallcounter = new int[]{0, 0};
                    for (int ct = 0; ct < wallcounter.length; ct++) {
                        if (world[i + ct][j].getType() == 2) {
                            wallcounter[ct]++;
                        }
                        if (world[i + ct][j - ct].getType() == 2) {
                            wallcounter[ct]++;
                        }
                        if (world[i + ct][j + ct].getType() == 2) {
                            wallcounter[ct]++;;
                        }
                        if (world[i][j + ct].getType() == 2) {
                            wallcounter[ct]++;;
                        }
                        if (world[i - ct][j + ct].getType() == 2) {
                            wallcounter[ct]++;;
                        }
                        if (world[i - ct][j - ct].getType() == 2) {
                            wallcounter[ct]++;;
                        }
                        if (world[i - ct][j].getType() == 2) {
                            wallcounter[ct]++;;
                        }
                        if (world[i][j - ct].getType() == 2) {
                            wallcounter[ct]++;;
                        }
                    }
                    if (world[i][j].getType() == 0) {
                        if (wallcounter[0] >= 5 || wallcounter[1] <= 2) {
                            newmap[i][j] = new Tile(i, j, 2, world[i][j].getRoom());
                        } else {
                            newmap[i][j] = new Tile(i, j, 0, world[i][j].getRoom());
                        }
                    } else if (world[i][j].getType() == 2) {
                        if (wallcounter[0] >= 4) {
                            newmap[i][j] = new Tile(i, j, 2, world[i][j].getRoom());
                        } else {
                            newmap[i][j] = new Tile(i, j, 0, world[i][j].getRoom());
                        }
                    } else {
                        newmap[i][j] = new Tile(i, j, 0, world[i][j].getRoom());
                    }

                } else {
                    newmap[i][j] = new Tile(i, j, 2, world[i][j].getRoom());
                }

            }

        }
        for (int i = 0;
                i < x;
                i++) {
            for (int j = 0; j < y; j++) {
                world[i][j] = new Tile(newmap[i][j]);
            }
        }
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
                    if (randomX < cellX && randomY < cellY) {
                        {
                            //restargets[targetcounter] = new Coordinate(r.get(randomCell).getRoom()[randomX][randomY].getX(), r.get(randomCell).getRoom()[randomX][randomY].getY());

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
        Random rnd = new Random();
        int tempX = 0;
        int tempY = 0;
        int offsetPathX = 0;
        int offsetPathY = 0;
        int offsetCounter = 0;

        for (int i = 0; i < targets.length; i++) {
            int offStep1 = rnd.nextInt(4);
            int offStep2 = offStep1 + rnd.nextInt(8);
            int offStep3 = offStep2 + rnd.nextInt(10);
            int offStep4 = offStep3 + rnd.nextInt(24);
            int offStep5 = offStep4 + rnd.nextInt(30);
            int offStep6 = offStep5 + rnd.nextInt(36);
            int offStep7 = offStep6 + rnd.nextInt(40);
            if (i == targets.length - 1) {
                tempX = 0;
                tempY = 0;
            } else {
                tempX = targets[i + 1].getX() - targets[i].getX();
                tempY = targets[i + 1].getY() - targets[i].getY();
            }
            if (tempY < 0) {
                for (int k = 0; k < tempY; k++) {

                    if (offsetCounter > offStep1 && offsetCounter < offStep2) {
                        offsetPathX = 1;
                    }

                    if (offsetCounter > offStep2 && offsetCounter < offStep3) {
                        offsetPathX = 2;
                    }

                    if (offsetCounter > offStep3 && offsetCounter < offStep4) {
                        offsetPathX = 1;
                    }

                    if (offsetCounter > offStep4 && offsetCounter < offStep5) {
                        offsetPathX = 2;
                    }

                    if (offsetCounter > offStep5 && offsetCounter < offStep6) {
                        offsetPathX = 1;
                    }

                    if (offsetCounter > offStep7) {
                        offsetPathX = 0;
                    }

                    world[targets[i].getX() + offsetPathX][targets[i].getY() - k].setType(0);
                    if (rnd.nextBoolean()) {
                        world[targets[i].getX() + offsetPathX][targets[i].getY() - k].setType(0);

                    }
                    if (rnd.nextBoolean()) {
                        world[targets[i].getX() - offsetPathX][targets[i].getY() - k].setType(0);
                    }

                    //offsetCounter++;
                }
            }

            if (tempX < 0) {
                for (int j = 0; j < tempX; j++) {
                    if (offsetCounter > offStep1 && offsetCounter < offStep2) {
                        offsetPathX = 1;
                    }

                    if (offsetCounter > offStep2 && offsetCounter < offStep3) {
                        offsetPathX = 2;
                    }

                    if (offsetCounter > offStep3 && offsetCounter < offStep4) {
                        offsetPathX = 3;
                    }

                    if (offsetCounter > offStep4 && offsetCounter < offStep5) {
                        offsetPathX = 2;
                    }

                    if (offsetCounter > offStep5 && offsetCounter < offStep6) {
                        offsetPathX = 1;
                    }

                    if (offsetCounter > offStep7) {
                        offsetPathX = 0;
                    }
                    world[targets[i].getX() - j][targets[i].getY() + tempY + offsetPathX].setType(0);
                    if (rnd.nextBoolean()) {
                        world[targets[i].getX() - j][targets[i].getY() - offsetPathX].setType(0);
                    }
                    if (rnd.nextBoolean()) {
                        world[targets[i].getX() - j][targets[i].getY() - offsetPathX].setType(0);
                    }
                    //offsetCounter++;
                }

            }
            if (tempY > 0) {
                for (int k = 0; k < tempY; k++) {
                    if (offsetCounter > offStep1 && offsetCounter < offStep2) {
                        offsetPathX = 1;
                    }

                    if (offsetCounter > offStep2 && offsetCounter < offStep3) {
                        offsetPathX = 2;
                    }

                    if (offsetCounter > offStep3 && offsetCounter < offStep4) {
                        offsetPathX = 3;
                    }

                    if (offsetCounter > offStep4 && offsetCounter < offStep5) {
                        offsetPathX = 2;
                    }

                    if (offsetCounter > offStep5 && offsetCounter < offStep6) {
                        offsetPathX = 1;
                    }

                    if (offsetCounter > offStep7) {
                        offsetPathX = 0;
                    }
                    world[targets[i].getX() - offsetPathX][targets[i].getY() + k].setType(0);
                    //offsetCounter++;
                }

            }
            if (tempX > 0) {
                for (int j = 0; j < tempX; j++) {
                    if (offsetCounter > offStep1 && offsetCounter < offStep2) {
                        offsetPathX = 1;
                    }

                    if (offsetCounter > offStep2 && offsetCounter < offStep3) {
                        offsetPathX = 2;
                    }

                    if (offsetCounter > offStep3 && offsetCounter < offStep4) {
                        offsetPathX = 3;
                    }

                    if (offsetCounter > offStep4 && offsetCounter < offStep5) {
                        offsetPathX = 2;
                    }

                    if (offsetCounter > offStep5 && offsetCounter < offStep6) {
                        offsetPathX = 1;
                    }

                    if (offsetCounter > offStep7) {
                        offsetPathX = 0;
                    }
                    world[targets[i].getX() + j][targets[i].getY() + tempY + offsetPathX].setType(0);
                    //offsetCounter++;
                }
            }

        }
    }

    public void caveCorridor() {
        int caveinCounter = 0;
        Random caver = new Random();
        while (caveinCounter < 150) {
            int rx = caver.nextInt(x - 4) + 2;
            int ry = caver.nextInt(y - 4) + 2;
            if (world[rx][ry].getType() == 0) {
                if (world[rx + 1][ry].getType() == 2
                        && world[rx - 1][ry].getType() == 2) {
                    if (caver.nextBoolean()) {
                        world[rx + 1][ry].setType(0);
                    }
                    if (caver.nextBoolean()) {
                        world[rx - 1][ry].setType(0);
                    }
                    caveinCounter++;
                } else if (world[rx][ry + 1].getType() == 2
                        && world[rx][ry - 1].getType() == 2) {
                    if (caver.nextBoolean()) {
                        world[rx][ry + 1].setType(0);
                    }
                    if (caver.nextBoolean()) {
                        world[rx][ry - 1].setType(0);
                    }
                    caveinCounter++;
                }
            }
        }
    }

    public void caveIn() {
        int caveinCounter = 0;
        Random caver = new Random();
        while (caveinCounter < 500) {
            int rx = caver.nextInt(x - 4) + 2;
            int ry = caver.nextInt(y - 4) + 2;
            if (world[rx][ry].getType() == 2) {
                if (world[rx][ry + 1].getType() == 0) {
                    world[rx][ry + 1].setType(1);
                    caveinCounter++;
                }
                if (world[rx][ry - 1].getType() == 0) {
                    world[rx][ry - 1].setType(1);
                    caveinCounter++;
                }
                if (world[rx + 1][ry].getType() == 0) {
                    world[rx + 1][ry].setType(1);
                    caveinCounter++;
                }
                if (world[rx + 1][ry + 1].getType() == 0) {
                    world[rx + 1][ry + 1].setType(1);
                    caveinCounter++;
                }
                if (world[rx + 1][ry - 1].getType() == 0) {
                    world[rx + 1][ry - 1].setType(1);
                    caveinCounter++;
                }
                if (world[rx - 1][ry + 1].getType() == 0) {
                    world[rx - 1][ry + 1].setType(1);
                    caveinCounter++;
                }

                if (world[rx - 1][ry - 1].getType() == 0) {
                    world[rx - 1][ry - 1].setType(1);
                    caveinCounter++;
                }
                if (world[rx - 1][ry].getType() == 0) {
                    world[rx - 1][ry].setType(1);
                    caveinCounter++;
                }
            }
        }
    }

    public void caveInTwo() {
        int caveinCounter = 0;
        Random caver = new Random();
        while (caveinCounter < 500) {
            int rx = caver.nextInt(x - 4) + 2;
            int ry = caver.nextInt(y - 4) + 2;
            if (world[rx][ry].getType() == 1) {
                if (world[rx][ry + 1].getType() == 0) {
                    world[rx][ry + 1].setType(3);
                    caveinCounter++;
                }
                if (world[rx][ry - 1].getType() == 0) {
                    world[rx][ry - 1].setType(3);
                    caveinCounter++;
                }
                if (world[rx + 1][ry].getType() == 0) {
                    world[rx + 1][ry].setType(3);
                    caveinCounter++;
                }
                if (world[rx + 1][ry + 1].getType() == 0) {
                    world[rx + 1][ry + 1].setType(3);
                    caveinCounter++;
                }
                if (world[rx + 1][ry - 1].getType() == 0) {
                    world[rx + 1][ry - 1].setType(3);
                    caveinCounter++;
                }
                if (world[rx - 1][ry + 1].getType() == 0) {
                    world[rx - 1][ry + 1].setType(3);
                    caveinCounter++;
                }

                if (world[rx - 1][ry - 1].getType() == 0) {
                    world[rx - 1][ry - 1].setType(3);
                    caveinCounter++;
                }
                if (world[rx - 1][ry].getType() == 0) {
                    world[rx - 1][ry].setType(3);
                    caveinCounter++;
                }
            }
        }
    }

    public void caveOut() {
        int caveinCounter = 0;
        Random caver = new Random();
        while (caveinCounter < 200) {
            int rx = caver.nextInt(x - 4) + 2;
            int ry = caver.nextInt(y - 4) + 2;
            if (world[rx][ry].getType() == 0) {
                if (world[rx + 1][ry].getType() == 2) {
                    world[rx + 1][ry].setType(0);
                    caveinCounter++;
                }
                if (world[rx - 1][ry].getType() == 2) {
                    world[rx - 1][ry].setType(0);
                    caveinCounter++;
                }
                if (world[rx + 1][ry + 1].getType() == 2) {
                    world[rx + 1][ry + 1].setType(0);
                    caveinCounter++;
                }
                if (world[rx - 1][ry - 1].getType() == 2) {
                    world[rx - 1][ry - 1].setType(0);
                    caveinCounter++;
                }
                if (world[rx][ry - 1].getType() == 2) {
                    world[rx][ry - 1].setType(0);
                    caveinCounter++;
                }
                if (world[rx][ry + 1].getType() == 2) {
                    world[rx][ry + 1].setType(0);
                    caveinCounter++;
                }
                if (world[rx + 1][ry - 1].getType() == 2) {
                    world[rx + 1][ry - 1].setType(0);
                    caveinCounter++;
                }
                if (world[rx - 1][ry + 1].getType() == 2) {
                    world[rx - 1][ry + 1].setType(0);
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
