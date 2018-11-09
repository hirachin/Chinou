import javax.swing.*;
import java.awt.*;

public class GUITest extends JFrame implements Runnable 
{
	/*public static void main(String args[]) 
	{
		JFrame frm = new JFrame("Kitty on your lap");
		frm.setBounds(0 , 0 , 400 , 200);

		Container contentPane = frm.getContentPane();
		contentPane.setLayout(new FlowLayout());
		contentPane.add(new Button("Kitty on your lap"));

		frm.setVisible(true);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}*/
	
	public static void main(String args[]) 
	{
		JFrame frame = new GUITest();
		frame.setBounds(10 , 10 , 400 , 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.show();
	}

	private int color;
	public GUITest() { new Thread(this).start(); }
	public void run() 
	{
		while(true) 
		{
			color += 0x050505;
			if (color == 0xFFFFFF) color = 0;

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					setForeground(new Color(color));
				}
			});
			
			try
			{
				Thread.sleep(100);
			}
			catch(Exception e){e.printStackTrace();}
		}
	}
	
	public void paint(Graphics g)
	{
		g.fillRect(0 , 0 , getWidth() , getHeight());
	}
}
