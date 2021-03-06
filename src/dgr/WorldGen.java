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
    private int avgRoomSizeX = 80;
    private int avgRoomSizeY = 84;
    private int marginSizeX = 8;
    private int marginSizeY = 8;
    private int cellX;
    private int cellY;
    private int cellamt;
    private int cellSize;
    private int caveinamt = 1;
    private int cavecorridoramt = 70;
    private int caveoutamt = 70;
    Path path;
    SortedList openlist = new SortedList();
    ArrayList closedlist = new ArrayList<>();
    int[][] heuristicMap = new int[x][y];
    Node[][] nodes = new Node[x][y];

    public WorldGen(int x, int y) {
        this.x = x;
        this.y = y;
        cellSize = x / 10;
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
    
    protected Node getFirstInOpen() {
        return (Node) openlist.first();
    }

    protected void addToOpen(Node node) {
        openlist.add(node);
    }

    protected boolean inOpenList(Node node) {
        return openlist.contains(node);
    }

    protected void removeFromOpen(Node node) {
        openlist.remove(node);
    }

    protected void addToClosed(Node node) {
        closedlist.add(node);
    }

    protected boolean inClosedList(Node node) {
        return closedlist.contains(node);
    }

    protected void removeFromClosed(Node node) {
        closedlist.remove(node);
    }

    protected boolean isValidLocation(int sx, int sy, int x, int y) {
        boolean invalid = (x < 0) || (y < 0) || (x >= x) || (y >= x);

        if ((!invalid) && ((sx != x) || (sy != y))) {
            invalid = (heuristicMap[x][y] == 9999);
        }

        return !invalid;
    }


    public Path findPathInt(int sx, int sy, int tox, int toy) {
        boolean checking = true;
        int movecost = 10;
        int movediagcost = 14;

        nodes[sx][sy].cost = 0;
        nodes[sx][sy].depth = 0;
        closedlist.clear();
        openlist.clear();
        openlist.add(nodes[sx][sy]);
        nodes[tox][toy].parent = null;

        int maxSearchDistance = 100;
        int maxDepth = 0;
        while ((openlist.size() != 0) && (maxDepth < maxSearchDistance)) {
            Node current = getFirstInOpen();
            if (current == nodes[tox][toy]) {
                break;
            }
            removeFromOpen(current);
            addToClosed(current);

            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {

                    if ((x == 0) && (y == 0)) {
                        continue;
                    }
                    int mcost = 10;
                    if ((x == -1 || x == 1) && (y == 1 || y == -1)) {
                        mcost = 14;
                    }

                    int xp = x + current.x;
                    int yp = y + current.y;

                    if (isValidLocation(sx, sy, xp, yp)) {

                        int nextStepCost = current.cost + heuristicMap[xp][yp] + mcost;
                        Node neighbour = nodes[xp][yp];

                        if (nextStepCost < neighbour.cost) {
                            if (inOpenList(neighbour)) {
                                removeFromOpen(neighbour);
                            }
                            if (inClosedList(neighbour)) {
                                removeFromClosed(neighbour);
                            }
                        }
                        if (!inOpenList(neighbour) && !(inClosedList(neighbour))) {
                            neighbour.cost = nextStepCost;
                            neighbour.h = current.h + heuristicMap[tox][toy];
                            maxDepth = Math.max(maxDepth, neighbour.setParent(current));
                            addToOpen(neighbour);
                        }

                    }
                }

            }

        }
        if (nodes[tox][toy].parent == null) {
            return null;
        }
        Path path = new Path();
        Node target = nodes[tox][toy];
        while (target != nodes[sx][sy]) {
            path.prependStep(target.x, target.y);
            target = target.parent;
        }
        path.prependStep(sx, sy);

        // thats it, we have our path 
        return path;
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
                int rtype = 2;
                if (rnd.nextInt(100) < 100) {
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
        buildTunnelsHeuristic(getTargets(generateCellListFromMap(world)));
        /*
         int threshold = 0;

         while (threshold < 1000) {

         threshold=flood(world, new boolean[x][y], rnd.nextInt(x), rnd.nextInt(y), 0, 2);
         }
         world = tmpWorld;
         */
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

            int randomX = 1 + rgen.nextInt(cellX - 1);
            int randomY = 1 + rgen.nextInt(cellY - 1);
            int randomCell = rgen.nextInt(cellamt);
            if (!r.get(randomCell).isConnected()) {
                if ((r.get(randomCell).getRoom()[randomX][randomY].getType() == 2)) {
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

    public int[][] buildHeuristic(int targetx, int targety) {
        int[][] heuristicMap = new int[x][y];
        for (int i = 0;i < x; i++) {
            for (int j = 0; j < y; j++) {
                int distancex;
                int distancey;
                distancex = Math.abs((targetx - i));
                distancey = Math.abs((targety - j));
                if (world[i][j].getType() == 2) {
                    heuristicMap[i][j] = distancex + distancey;

                } else {
                    heuristicMap[i][j] = 999999;
                }
            }
        }
        return heuristicMap;
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

    public void buildTunnelsHeuristic(Coordinate[] targets) {

        for (int i = 0; i < targets.length - 1; i++) {

            int[][] heur = buildHeuristic(targets[i].getX(), targets[i].getY());
            for (Coordinate c : findPathInt(targets[i].getX(), targets[i].getY(), targets[i + 1].getX(), targets[i + 1].getY(), heur)) {
                world[c.getX()][c.getY()].setType(0);
            }

        }
    }

    public ArrayList<Coordinate> findPathInt(int i, int j, int tox, int toy, int[][] heurmap) {
        boolean checking = true;
        int cheapestnode = 9999;
        int cheapestx = 0;
        int cheapesty = 0;
        ArrayList<Coordinate> ac = new ArrayList<>(1000000);

        while (checking) {
            // start på en node
            // sjekk de 8 rundt
            // velg den billigste
            // start fra denne
            // sjekk de 8 rundt
            // ...til man finder endepunktet
            if (!((i < 2 || i > x - 2) || (j < 2) || (j > y - 2))) {
                if ((heurmap[i][j + 1] < cheapestnode) || (!(ac.contains(new Coordinate(i, j + 1))))) {

                    cheapestnode = heurmap[i][j + 1];
                    cheapestx = i;
                    cheapesty = j + 1;
                }
                if ((heurmap[i][j - 1] < cheapestnode) || !(ac.contains(new Coordinate(i, j - 1)))) {
                    cheapestnode = heurmap[i][j - 1];
                    cheapestx = i;
                    cheapesty = j - 1;
                }
                if ((heurmap[i + 1][j] < cheapestnode) || !(ac.contains(new Coordinate(i + 1, j)))) {
                    cheapestnode = heurmap[i + 1][j];
                    cheapestx = i + 1;
                    cheapesty = j;
                }
                if ((heurmap[i + 1][j + 1] < cheapestnode) || !(ac.contains(new Coordinate(i + 1, j + 1)))) {
                    cheapestnode = heurmap[i + 1][j + 1];
                    cheapestx = i + 1;
                    cheapesty = j + 1;
                }
                if ((heurmap[i + 1][j - 1] < cheapestnode) || !(ac.contains(new Coordinate(i + 1, j - 1)))) {
                    cheapestnode = heurmap[i + 1][j - 1];
                    cheapestx = i + 1;
                    cheapesty = j - 1;
                }
                if ((heurmap[i - 1][j] < cheapestnode) || !(ac.contains(new Coordinate(i - 1, j)))) {
                    cheapestnode = heurmap[i - 1][j];
                    cheapestx = i - 1;
                    cheapesty = j;
                }
                if ((heurmap[i - 1][j + 1] < cheapestnode) || !(ac.contains(new Coordinate(i - 1, j + 1)))) {
                    cheapestnode = heurmap[i - 1][j + 1];
                    cheapestx = i - 1;
                    cheapesty = j + 1;
                }
                if ((heurmap[i - 1][j - 1] < cheapestnode) || !(ac.contains(new Coordinate(i - 1, j - 1)))) {
                    cheapestnode = heurmap[i - 1][j - 1];
                    cheapestx = i - 1;
                    cheapesty = j - 1;
                }

                if (cheapestx == tox && cheapesty == toy) {
                    checking = false;
                }

                if (!ac.contains(new Coordinate(cheapestx, cheapesty))) {
                    ac.add(new Coordinate(cheapestx, cheapesty));

                }

                i = cheapestx;
                j = cheapesty;

            }
        }
        return ac;
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
