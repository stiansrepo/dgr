package dgr;

 // @author laptopng34
public class Tile {

    private int x;
    private int y;
    private int type;
    private TileType t;
    private Cell r;

    public Tile(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
  
    }

    public Tile(int x, int y, int type, Cell r) {
        this.r = r;
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public Tile(Tile ttt) {
        this.r = ttt.getRoom();
        this.x = ttt.getX();
        this.y = ttt.getY();
        this.type = ttt.getType();

    }

    public Cell getRoom() {
        return r;
    }

    public void setXY(int sx, int sy) {
        x = sx;
        y = sy;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getType() {
        return type;
    }

    public void setType(int t) {
        type = t;
    }

    public void test() {
    }

    public enum TileType {

        FLOOR,
        RUBBLE,
        WALL;
    }

}
