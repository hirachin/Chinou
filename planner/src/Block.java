
import java.awt.*;

public class Block
{
    private Point m_pos;
    private Point m_size;

    private String m_name;
    private String m_shape = null;
    private String m_color = null;
    
    private void setNameAndState(String _name)
    {   
        System.out.println("<>"+_name);
        //名前と(形,色)を分離
        String[] datas = _name.split("\\(");
        System.out.println("<>"+datas.length);

        if(datas.length != 2)
        {
            m_name = _name;
            return;
        }

        m_name = datas[0];

        //形と色を分離
        //")"を除いてから分離している
         datas = _name.split("\\)")[0].split(",");

        m_shape = datas[0];
        m_color = datas[1];
    }

    public Block(Point _pos,Point _size,String _name)
    {
        m_pos = _pos;
        m_size = _size;
        setNameAndState(_name);
    }

    public Block(int _posX,int _posY,int _sizeX,int _sizeY,String _name)
    {
       this(new Point(_posX,_posY),new Point(_sizeX,_sizeY),_name);
    }

    public Block(int _posX,int _posY,String _name,String _shape,String _color)
    {
        m_name = _name;
        m_shape = _shape;
        m_color = _color;

        m_pos = new Point(_posX,_posY);
        m_size = new Point(100,100);
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
        setColor(g);

        if(m_shape == null || m_shape.equals("square"))
        {
            drawRect(g);
        }        
        else if(m_shape.equals("triangle"))
        {
            drawTriangle(g);
        }
        else if(m_shape.equals("parallelogram"))
        {
            drawParallelogram(g);
        }


        g.setColor(Color.BLACK);
        g.drawString(m_name, m_pos.x+m_size.x/2, m_pos.y+m_size.y/2);
    }

    public void setColor(Graphics g)
    {
        if(m_color == null)
        {
            g.setColor(Color.WHITE);
            return;
        }

        if(m_color.equals("red"))
        {
            g.setColor(Color.RED);
        }
        else if(m_color.equals("blue"))
        {
            g.setColor(Color.BLUE);
        }
        else if(m_color.equals("green"))
        {
            g.setColor(Color.GREEN);
        }
        else
        {
            g.setColor(Color.WHITE);
        }
    }

    private void drawRect(Graphics g)
    {
        g.fillRect(m_pos.x, m_pos.y, m_size.x,m_size.y);
    }

    private void drawTriangle(Graphics g)
    {
        g.fillPolygon(new int[] {m_pos.x,m_pos.x+m_size.x/2,m_pos.x+m_size.x}, new int[] {m_pos.y+m_size.y,m_pos.y,m_pos.y+m_size.y}, 3);
    }

    private void drawParallelogram(Graphics g)
    {
        int length = (int)(m_size.x*0.3);
        g.fillPolygon(new int[] {m_pos.x,m_pos.x+length,m_pos.x+m_size.x,m_pos.x+m_size.x-length}, new int[] {m_pos.y+m_size.y,m_pos.y,m_pos.y,m_pos.y+m_size.y}, 4);
    }
}