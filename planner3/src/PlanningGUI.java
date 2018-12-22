import java.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class PlanningGUI extends JFrame implements Runnable
{
    private static final long serialVersionUID = 1L;
    
    static ArrayList<String> plan;
    static int planIdx = 0;

    static JButton nextBt;
    static JButton resetBt;
    static JButton rePlanBt;

    static Field field;
    
    static void doPlan(String _target)
    {
        String[] datas = _target.split(" ");

        if(datas[0].equals("Place") && datas.length == 4)
        {
            String a = splitState(datas[1]).getName();
            String b = splitState(datas[3]).getName();
            field.placeOn(a,b);
        }

        else if(datas[0].equals("remove") && datas.length == 6)
        {
            String a = splitState(datas[1]).getName();
            String b = splitState(datas[5]).getName();
            field.removeFrom(a,b);
        }
        
        else if(datas[0].equals("pick") && datas.length == 6)
        {
            String a = splitState(datas[2]).getName();
            field.pickUpFromTable(a);
        }

        else if(datas[0].equals("put") && datas.length == 6)
        {
            String a = splitState(datas[2]).getName();
            field.putDownOnTable(a);
        }
    }

    static State splitState(String _state)
    {
        String[] datas = _state.split("\\(");

        if(datas.length != 2)
        {
            return new State(_state,null,null);
        }

        String name = datas[0];

        //形と色を分離
        //")"を除いてから分離している
         datas = _state.split("\\)")[0].split(",");

        String shape = datas[0];
        String color = datas[1];

        return new State(name,shape,color);
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

    static class Reset implements ActionListener
    {
        @Override public void actionPerformed(ActionEvent e)
        {
            System.out.println("resetBt pushed");
            field = new Field();
            planIdx = 0; 
            nextBt.setEnabled(true);

        }   
    }

    static class RePlan implements ActionListener
    {
        @Override public void actionPerformed(ActionEvent e)
        {
            System.out.println("rePlanBt pushed");
            plan = new Planner().startAndgetPlan();
            field = new Field();
            planIdx = 0; 
            nextBt.setEnabled(true);
        }   
    }

    public PlanningGUI()
    {
        plan = new Planner().startAndgetPlan();

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
            
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    if(nextBt == null){return;}

                    if(planIdx >= plan.size())
                    {
                        nextBt.setEnabled(false);
                    }
                }
            });
            
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
        nextBt.setBounds(50,10,150,30);
        nextBt.addActionListener(new GoNext());

        resetBt = new JButton("reset");
        resetBt.setBounds(210,10,150,30);
        resetBt.addActionListener(new Reset());

        rePlanBt = new JButton("rePlan");
        rePlanBt.setBounds(370,10,150,30);
        rePlanBt.addActionListener(new RePlan());
        
        frame.add(nextBt);
        frame.add(resetBt);
        frame.add(rePlanBt);
        frame.setVisible(true);
    }

    @Override public void paint(Graphics g)
    {
        Dimension size = getSize();
        Image back = createImage(size.width,size.height);
        Graphics buffer = back.getGraphics();

        super.paint(buffer);

        buffer.drawLine(0,100,1280,100);

        field.draw(buffer);

        g.drawImage(back,0,0,this);
        

        /*
        super.paint(g);
        g.drawLine(0,100,1280,100);
        field.draw(g);
        */
    }
}