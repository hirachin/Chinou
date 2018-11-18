import java.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PlanningGUI extends JFrame implements Runnable
{
    static ArrayList<String> plan;
    static int planIdx = 0;

    static JButton nextBt;

    static Field field;
    
    static void doPlan(String _target)
    {
        String[] datas = _target.split(" ");

        if(datas[0].equals("place") && datas.length == 4)
        {
            field.placeOn(datas[1], datas[3]);
        }

        else if(datas[0].equals("remove") && datas.length == 6)
        {
            field.removeFrom(datas[1], datas[5]);
        }
        
        else if(datas[0].equals("pick") && datas.length == 6)
        {
            field.pickUpFromTable(datas[2]);
        }

        else if(datas[0].equals("put") && datas.length == 6)
        {
            field.putDownOnTable(datas[2]);
        }
    }


    static class GoNext implements ActionListener
    {
        @Override public void actionPerformed(ActionEvent e)
        {
            if(planIdx >= plan.size()){return;}

            String target = plan.get(planIdx);
            System.out.println("nextBt pushed:" + target);
            doPlan(target);
            planIdx++;
        }   
    }

    public PlanningGUI()
    {
        Planner planner = new Planner();
        plan = planner.startAndgetPlan();

        field = new Field();

        System.out.println("***** This is a plan! *****");
        for(String elem:plan)
        {
            System.out.println(elem);
        }

        new Thread(this).start();
    }


    @Override public void run()
    {
        while(true)
        {
            /*
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {

                }
            });
            */
            super.repaint();

            try
            {
                Thread.sleep(100);
            }
            catch(Exception e){e.printStackTrace();}

        }
    }

    public static void main(String[] args)
    {
        JFrame frame = new PlanningGUI();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
        frame.setSize(1280, 960);

        nextBt = new JButton("next");
        nextBt.setBounds(50,10,150,20);
        nextBt.addActionListener(new GoNext());

        frame.add(nextBt);
        frame.setVisible(true);
    }

    @Override public void paint(Graphics g)
    {
        super.paint(g);
        g.drawLine(0,100,1280,100);

        field.draw(g);
    }

    public void drawBlocks(Graphics g)
    {
       new Block(150,150,50,50,"A").draw(g);
    }
}