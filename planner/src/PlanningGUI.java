import java.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PlanningGUI extends JFrame implements Runnable
{
    static ArrayList<String> plan;

    public PlanningGUI()
    {
        new Thread(this).start();
    }


    @Override public void run()
    {

    }

    public static void main(String[] args)
    {
        JFrame frame = new PlanningGUI();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
        frame.setSize(1280, 960);
        
        frame.setVisible(true);

        Planner planner = new Planner();
        plan = planner.startAndgetPlan();


        System.out.println("***** This is a plan! *****");
        for(String elem:plan)
        {
            System.out.println(elem);
        }


    }
}