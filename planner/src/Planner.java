import java.util.*;
import java.io.*;

public class Planner 
{
	Vector operators;
	Random rand;
	Vector plan;

	public static void main(String argv[]) 
	{
		(new Planner()).start();
	}

	Planner() 
	{
		rand = new Random();
	}

	public void start() 
	{
		initOperators("testOperators.data");
		Vector goalList = initGoalList("goalList.data");
		Vector initialState = initInitialState("initialState.data");
		//initOperators();
		//Vector goalList = initGoalList();
		//Vector initialState = initInitialState();

		Hashtable theBinding = new Hashtable();
		plan = new Vector();
		planning(goalList, initialState, theBinding);

		System.out.println("***** This is a plan! *****");
		for (int i = 0; i < plan.size(); i++) 
		{
			Operator op = (Operator) plan.elementAt(i);
			System.out.println((op.instantiate(theBinding)).name);
		}
	}

	private boolean planning(Vector theGoalList, Vector theCurrentState, Hashtable theBinding) 
	{
		System.out.println("*** GOALS ***" + theGoalList);
		if (theGoalList.size() == 1) 
		{
			String aGoal = (String) theGoalList.elementAt(0);
			if (planningAGoal(aGoal, theCurrentState, theBinding, 0) != -1) 
			{
				return true;
			}
			else 
			{
				return false;
			}
		} 
		else 
		{
			String aGoal = (String) theGoalList.elementAt(0);
			int cPoint = 0;
			while (cPoint < operators.size()) 
			{
				// System.out.println("cPoint:"+cPoint);
				// Store original binding
				Hashtable orgBinding = new Hashtable();
				
				//HashMap->HashMap->HashMap
				//orgBinding += theBinding
				for (Enumeration e = theBinding.keys(); e.hasMoreElements();) 
				{
					String key = (String) e.nextElement();
					String value = (String) theBinding.get(key);
					orgBinding.put(key, value);
				}

				Vector orgState = new Vector();
				
				//Vector->Vector->Vector
				//orgState += theCurrentState
				for (int i = 0; i < theCurrentState.size(); i++) 
				{
					orgState.addElement(theCurrentState.elementAt(i));
				}

				int tmpPoint = planningAGoal(aGoal, theCurrentState, theBinding, cPoint);
				// System.out.println("tmpPoint: "+tmpPoint);
				if (tmpPoint != -1) 
				{
					theGoalList.removeElementAt(0);
					System.out.println(theCurrentState);
					if (planning(theGoalList, theCurrentState, theBinding)) 
					{
						// System.out.println("Success !");
						return true;
					}
					else 
					{
						cPoint = tmpPoint;
						// System.out.println("Fail::"+cPoint);
						//削除したやつを元に戻す
						theGoalList.insertElementAt(aGoal, 0);

						theBinding.clear();

						//HashMap->HashMap->HashMap
						//theBinding += orgBinding
						for (Enumeration e = orgBinding.keys(); e.hasMoreElements();) 
						{
							String key = (String) e.nextElement();
							String value = (String) orgBinding.get(key);
							theBinding.put(key, value);
						}

						theCurrentState.removeAllElements();

						//theCurrentState += orgState
						for (int i = 0; i < orgState.size(); i++) 
						{
							theCurrentState.addElement(orgState.elementAt(i));
						}
					}
				}
				else 
				{
					theBinding.clear();
					for (Enumeration e = orgBinding.keys(); e.hasMoreElements();) 
					{
						String key = (String) e.nextElement();
						String value = (String) orgBinding.get(key);
						theBinding.put(key, value);
					}
					theCurrentState.removeAllElements();
					for (int i = 0; i < orgState.size(); i++) 
					{
						theCurrentState.addElement(orgState.elementAt(i));
					}
					return false;
				}
			}
			return false;
		}
	}

	private int planningAGoal(String theGoal, Vector theCurrentState, Hashtable theBinding, int cPoint) 
	{
		System.out.println("**" + theGoal);

		//theGoalがtheCurrentStateで既に満たされているか確認
		int size = theCurrentState.size();
		for (int i = 0; i < size; i++) 
		{
			String aState = (String) theCurrentState.elementAt(i);
			if ((new Unifier()).unify(theGoal, aState, theBinding)) 
			{
				return 0;
			}
		}

		//ランダムにオペレータ選ぶ
		int randInt = Math.abs(rand.nextInt()) % operators.size();
		Operator op = (Operator) operators.elementAt(randInt);
		operators.removeElementAt(randInt);
		operators.addElement(op);

		for (int i = cPoint; i < operators.size(); i++) 
		{
			Operator anOperator = rename((Operator) operators.elementAt(i));

			// 現在のCurrent state, Binding, planをbackup
			Hashtable orgBinding = new Hashtable();
			for (Enumeration e = theBinding.keys(); e.hasMoreElements();) 
			{
				String key = (String) e.nextElement();
				String value = (String) theBinding.get(key);
				orgBinding.put(key, value);
			}
			Vector orgState = new Vector();
			for (int j = 0; j < theCurrentState.size(); j++) 
			{
				orgState.addElement(theCurrentState.elementAt(j));
			}
			Vector orgPlan = new Vector();
			for (int j = 0; j < plan.size(); j++) 
			{
				orgPlan.addElement(plan.elementAt(j));
			}
			//backup end

			Vector addList = (Vector) anOperator.getAddList();
			for (int j = 0; j < addList.size(); j++) 
			{
				if ((new Unifier()).unify(theGoal, (String) addList.elementAt(j), theBinding)) 
				{
					Operator newOperator = anOperator.instantiate(theBinding);
					Vector newGoals = (Vector) newOperator.getIfList();
					System.out.println(newOperator.name);
					if (planning(newGoals, theCurrentState, theBinding))
					{
						System.out.println(newOperator.name);
						plan.addElement(newOperator);
						theCurrentState = newOperator.applyState(theCurrentState);
						return i + 1;
					}
					else 
					{
						// 失敗したら元に戻す．
						theBinding.clear();
						for (Enumeration e = orgBinding.keys(); e.hasMoreElements();)
						{
							String key = (String) e.nextElement();
							String value = (String) orgBinding.get(key);
							theBinding.put(key, value);
						}
						theCurrentState.removeAllElements();
						for (int k = 0; k < orgState.size(); k++) 
						{
							theCurrentState.addElement(orgState.elementAt(k));
						}
						plan.removeAllElements();
						for (int k = 0; k < orgPlan.size(); k++)
						{
							plan.addElement(orgPlan.elementAt(k));
						}
					}
				}
			}
		}
		return -1;
	}

	int uniqueNum = 0;

	private Operator rename(Operator theOperator)
	{
		Operator newOperator = theOperator.getRenamedOperator(uniqueNum);
		uniqueNum = uniqueNum + 1;
		return newOperator;
	}

	private Vector initGoalList()
	{
		Vector goalList = new Vector();
		goalList.addElement("B on C");
		goalList.addElement("A on B");
		return goalList;
	}

	private Vector initGoalList(String _path)
	{
		Vector goalList = new Vector();

		try
		{
			FileReader f = new FileReader(_path);
			StreamTokenizer st = new StreamTokenizer(f);
			
            while(st.nextToken() != StreamTokenizer.TT_EOF)
			{
				goalList.addElement(st.sval);
            }
		}
		catch(Exception e)
		{
            System.out.println(e);
		}

		return goalList;
	}

	private Vector initInitialState()
	{
		Vector initialState = new Vector();
		initialState.addElement("clear A");
		initialState.addElement("clear B");
		initialState.addElement("clear C");

		initialState.addElement("ontable A");
		initialState.addElement("ontable B");
		initialState.addElement("ontable C");
		initialState.addElement("handEmpty");
		return initialState;
	}

	private Vector initInitialState(String _path)
	{
		Vector initialState = new Vector();

        try
		{
			FileReader f = new FileReader(_path);
			StreamTokenizer st = new StreamTokenizer(f);
			
            while(st.nextToken() != StreamTokenizer.TT_EOF)
			{
				initialState.addElement(st.sval);
            }
		}
		catch(Exception e)
		{
            System.out.println(e);
		}
		
		return initialState;
	}

	private void initOperators()
	{
		operators = new Vector();

		// OPERATOR 1
		/// NAME
		String name1 = new String("Place ?x on ?y");
		/// IF
		Vector ifList1 = new Vector();
		ifList1.addElement(new String("clear ?y"));
		ifList1.addElement(new String("holding ?x"));
		/// ADD-LIST
		Vector addList1 = new Vector();
		addList1.addElement(new String("?x on ?y"));
		addList1.addElement(new String("clear ?x"));
		addList1.addElement(new String("handEmpty"));
		/// DELETE-LIST
		Vector deleteList1 = new Vector();
		deleteList1.addElement(new String("clear ?y"));
		deleteList1.addElement(new String("holding ?x"));
		Operator operator1 = new Operator(name1, ifList1, addList1, deleteList1);
		operators.addElement(operator1);

		// OPERATOR 2
		/// NAME
		String name2 = new String("remove ?x from on top ?y");
		/// IF
		Vector ifList2 = new Vector();
		ifList2.addElement(new String("?x on ?y"));
		ifList2.addElement(new String("clear ?x"));
		ifList2.addElement(new String("handEmpty"));
		/// ADD-LIST
		Vector addList2 = new Vector();
		addList2.addElement(new String("clear ?y"));
		addList2.addElement(new String("holding ?x"));
		/// DELETE-LIST
		Vector deleteList2 = new Vector();
		deleteList2.addElement(new String("?x on ?y"));
		deleteList2.addElement(new String("clear ?x"));
		deleteList2.addElement(new String("handEmpty"));
		Operator operator2 = new Operator(name2, ifList2, addList2, deleteList2);
		operators.addElement(operator2);

		// OPERATOR 3
		/// NAME
		String name3 = new String("pick up ?x from the table");
		/// IF
		Vector ifList3 = new Vector();
		ifList3.addElement(new String("ontable ?x"));
		ifList3.addElement(new String("clear ?x"));
		ifList3.addElement(new String("handEmpty"));
		/// ADD-LIST
		Vector addList3 = new Vector();
		addList3.addElement(new String("holding ?x"));
		/// DELETE-LIST
		Vector deleteList3 = new Vector();
		deleteList3.addElement(new String("ontable ?x"));
		deleteList3.addElement(new String("clear ?x"));
		deleteList3.addElement(new String("handEmpty"));
		Operator operator3 = new Operator(name3, ifList3, addList3, deleteList3);
		operators.addElement(operator3);

		// OPERATOR 4
		/// NAME
		String name4 = new String("put ?x down on the table");
		/// IF
		Vector ifList4 = new Vector();
		ifList4.addElement(new String("holding ?x"));
		/// ADD-LIST
		Vector addList4 = new Vector();
		addList4.addElement(new String("ontable ?x"));
		addList4.addElement(new String("clear ?x"));
		addList4.addElement(new String("handEmpty"));
		/// DELETE-LIST
		Vector deleteList4 = new Vector();
		deleteList4.addElement(new String("holding ?x"));
		Operator operator4 = new Operator(name4, ifList4, addList4, deleteList4);
		operators.addElement(operator4);
	}

	private void initOperators(String _path)
	{
		operators = new Vector();
        try
		{
			int token;
			FileReader f = new FileReader(_path);
			StreamTokenizer st = new StreamTokenizer(f);
			
            while((token = st.nextToken())!= StreamTokenizer.TT_EOF)
			{
                switch(token)
				{	
					case StreamTokenizer.TT_WORD:
					{
						String name = null;
						Vector ifList = null;
						Vector addList = null;
						Vector deleteList = null;
                        if("NAME".equals(st.sval))
						{
							st.nextToken();
							name = st.sval;
							st.nextToken();
							if("IF".equals(st.sval))
							{
                                ifList = new Vector();
                                st.nextToken();
								while(!"ADD".equals(st.sval))
								{
									ifList.add(st.sval);
						            st.nextToken();
								}
								
								if("ADD".equals(st.sval))
								{
									addList = new Vector();
                                    st.nextToken();
                                    while(!"DELETE".equals(st.sval))
									{
										addList.add(st.sval);
						            	st.nextToken();
									}

									if("DELETE".equals(st.sval))
									{
										deleteList = new Vector();
										st.nextToken();
										while(!"END".equals(st.sval))
										{
											deleteList.add(st.sval);
											st.nextToken();
										}
									}
                                }
                            }
						}

						// ルールの生成
						Operator operator = new Operator(name,ifList, addList, deleteList);
						System.out.println(operator);
                        operators.addElement(operator);
						break;
					}
                    default:
                    {
						System.out.println("default:" + token);
                        break;
					}
                }

            }
        } catch(Exception e){
            System.out.println(e);
        }
	}

}
