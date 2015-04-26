package dgr;

 // @author laptopng34
 
public class Cell {
    
    private Tile[][] room;
    private boolean isConnected=false;
    
    public Cell(Tile[][] room){
        this.room=room;
    }
    
    public Tile[][] getRoom(){
        return room;
    }
    
    public void setTiles(int x, int y, Tile t){
        room[x][y]=t;
    }
    
    public boolean isConnected(){
        return isConnected;
    }
    
    public void setConnected(){
        isConnected=true;
    }

}
