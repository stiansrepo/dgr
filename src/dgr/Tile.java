package dgr;

 // @author laptopng34
 
public class Tile {
    
    private int x;
    private int y;
    private int type;
    private TileType t;
    private Cell r;
    
    public Tile(int x,int y,int type, Cell r){
        this.r=r;
        this.x=x;
        this.y=y;
        this.type=type;
        switch(type){
            case 0:
                t=TileType.FLOOR;
                break;
            case 1:
                t=TileType.WALL;
                break;
            default:
                t=TileType.FLOOR;
                break;
        }
    }
    
    public Cell getRoom(){
        return r;
    }
    
    public void setXY(int sx, int sy){
        x=sx;
        y=sy;
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
  
    
    public int getType(){
        return type;
    }
    
    public void setType(int t){
        type=t;
    }
    
    public void test(){
    }
    
    public enum TileType{
        FLOOR,
        WALL;
    }

}
