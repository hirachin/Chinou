import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.awt.Point;

public class Field
{
    private HashMap<String,Block> m_hashMap = new HashMap<String,Block>();

    int tableY = 500;

    Point handPos = new Point(700,300);
    Point handSize = new Point(150,150);

    public Field()
    {
        load();
    }

    public Point getDrawHandAreaPos(Block b)
    {
        Point pos = new Point(handPos.x + handSize.x/2 - b.getSize().x/2,handPos.y + handSize.y/2 - b.getSize().y/2);
        return pos;
    }

    public void load()
    {
        m_hashMap.put("A", new Block(100,tableY - 100,100,100,"A"));
        m_hashMap.put("B", new Block(300,tableY - 100,100,100,"B"));
        m_hashMap.put("C", new Block(500,tableY - 100,100,100,"C"));
    }

    public void draw(Graphics g)
    {
        g.drawLine(50, tableY, 950, tableY);

        g.drawRect(handPos.x,handPos.y,handSize.x,handSize.y);
        g.drawString("hand",handPos.x + 50 ,handPos.y + handSize.y + 20);

        for(String key :m_hashMap.keySet())
        {
            m_hashMap.get(key).draw(g);
        }
    }

    public boolean isPlaceOn(String _a,String _b)
    {
        Block a = m_hashMap.get(_a);
        Block b = m_hashMap.get(_b);

        if(a == null || b == null){return false;}

        Point pos = new Point(b.getPos().x,b.getPos().y - b.getSize().y);

        return a.getPos().equals(pos);
    }

    public boolean isOnTable(String _a)
    {
        Block a = m_hashMap.get(_a);

        if(a == null){return false;}

        return a.getPos().y  + a.getSize().y == tableY;
    }

    public boolean placeOn(String _a,String _b)
    {
        Block a = m_hashMap.get(_a);
        Block b = m_hashMap.get(_b);

        if(a == null || b == null){return false;}

        a.setPos(b.getPos().x,b.getPos().y - b.getSize().y);
        return true;
    }

    public boolean removeFrom(String _a,String _b)
    {
        Block a = m_hashMap.get(_a);
        Block b = m_hashMap.get(_b);

        if(a == null || b == null){return false;}
        if(!isPlaceOn(_a, _b)){return false;}
        
        a.setPos(getDrawHandAreaPos(a));
        return true;
    }

    public boolean pickUpFromTable(String _a)
    {
        Block a = m_hashMap.get(_a);
        if(a == null){return false;}

        a.setPos(getDrawHandAreaPos(a));
        return true;
    }

    public int getEmptyTablePosX()
    {
        int posX = -100;
        boolean hasSamePos = false;
        do
        {
            hasSamePos = false;
            posX += 200;

            for(String key :m_hashMap.keySet())
            {
                if(m_hashMap.get(key).getPos().x == posX)
                {
                    hasSamePos = true;
                    break;
                }
            }
    
        }while(hasSamePos);

        return posX;
    }

    public boolean putDownOnTable(String _a)
    {
        Block a = m_hashMap.get(_a);
        if(a == null){return false;}
        if(isOnTable(_a)){return false;}
        
        a.setPos(getEmptyTablePosX(),tableY - a.getSize().y);
        return true;
    }
}