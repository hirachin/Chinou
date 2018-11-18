
import java.awt.Graphics;
import java.awt.Point;

public class Block
{
    private Point m_pos;
    private Point m_size;
    private String m_name;

    public Block(Point _pos,Point _size,String _name)
    {
        m_pos = _pos;
        m_size = _size;
        m_name = _name;
    }

    public Block(int _posX,int _posY,int _sizeX,int _sizeY,String _name)
    {
       this(new Point(_posX,_posY),new Point(_sizeX,_sizeY),_name);
    }

    public Point getPos(){return m_pos;}
    public Point getSize(){return m_size;}

    public void setPos(Point _pos)
    {
        m_pos = _pos;
    }

    public void setPos(int _x,int _y)
    {
        m_pos.x = _x;
        m_pos.y = _y;
    }

    public void draw(Graphics g)
    {
        g.drawRect(m_pos.x, m_pos.y, m_size.x,m_size.y);
        g.drawString(m_name, m_pos.x+m_size.x/2, m_pos.y+m_size.y/2);
    }
}