import java.io.FileReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Comparator;
import java.util.Collections;

public class Planner
{
	Vector operators;
	Vector initialState;
	Vector goalList;
	ArrayList<State> states;
	
	Random rand;
	Vector plan;
	int trials;

	/*
	public static void main(String argv[])
	{
		long start = System.currentTimeMillis();
		(new Planner()).start();
		long end = System.currentTimeMillis();
		
		System.out.println("Time:"+(end - start)+"ms");
	}*/

	Planner()
	{
		rand = new Random();
		trials = 0;
		initOperators("testOperators.data");
		goalList = initGoalList("goalList.data");
		initialState = initInitialState("initialState.data");
		states = initStates("states.data");
	}

	public void start()
	{
		/*
		initOperators("testOperators.data");
		Vector goalList = initGoalList("goalList.data");
		Vector initialState = initInitialState("initialState.data");
		ArrayList<State> States = initStates("States.data");
		*/

		goalList = renewGoalList(goalList,states);
		//initOperators();
		//Vector goalList = initGoalList();
		//Vector initialState = initInitialState();

		Hashtable theBinding = new Hashtable();
		plan = new Vector();

		boolean judge = planning(goalList, initialState, theBinding);

		if(judge == false)
		{
			System.out.println("無限ループ");
			Vector goalClone = initGoalList("goalList.data");
			goalClone = reverseList(goalClone);
			Vector initialClone = initInitialState("initialState.data");
			Hashtable theBindingClone = new Hashtable();
			plan = new Vector();
			trials = 0;
			uniqueNum  = 0;
			randomOperators();
			
			judge = planning(goalClone, initialClone,theBindingClone);
			
			if(judge == false)
				System.exit(-1);
		}
		System.out.println("***** This is a plan! *****");
		for (int i = 0; i < plan.size(); i++)
		{
			Operator op = (Operator) plan.elementAt(i);
			String resultOperator = (op.instantiate(theBinding)).name;
			resultOperator = plusState(resultOperator, states);
			System.out.println(resultOperator);
		}
		System.out.println("***result***");
		System.out.println("PlanSize:"+plan.size());
		System.out.println("Trials:"+trials);
	}
	
	public ArrayList<String> startAndgetPlan()
	{
		goalList = renewGoalList(goalList,states);
		
		Hashtable theBinding = new Hashtable();
		plan = new Vector();
		
		long start = System.currentTimeMillis();
		planning(goalList,initialState,theBinding);
		long end = System.currentTimeMillis();
		
		ArrayList<String> newPlan = new ArrayList<String>();
		
		for(int i = 0; i < plan.size(); i++)
		{
			Operator op = (Operator)plan.elementAt(i);
			String resultOperator = (op.instantiate(theBinding)).name;
			resultOperator = plusState(resultOperator, states);
			newPlan.add(resultOperator.trim());
		}
		
		System.out.println("***result***");
		System.out.println("PlanSize:"+plan.size());
		System.out.println("Trials:"+trials);
		System.out.println("Time:"+(end - start)+"ms");
		
		return newPlan;
	}
	
	private int judgeGoal(Vector goalList, Vector currentState)
	{
		for(int i = 0; i < goalList.size(); i++)
		{
			if(!currentState.contains(goalList.elementAt(i)))
			{
				return 1;
			}		
		}
		return 0;
	}
	
	private Vector reverseList(Vector list)
	{
		Vector newList = new Vector();
		for(int i = list.size() - 1; i >= 0; i--)
		{
			newList.addElement(list.elementAt(i));
		}
		return newList;
	}

	private boolean planning(Vector theGoalList, Vector theCurrentState, Hashtable theBinding)
	{
		System.out.println("*** GOALS ***" + theGoalList);

		if (theGoalList.size() == 1)
		{
			String aGoal = (String) theGoalList.elementAt(0);
			trials++;
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
				//変数が50以上なら無限ループと見なす
				if(uniqueNum > 50)
					return false;
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

				trials++;
				int tmpPoint = planningAGoal(aGoal, theCurrentState, theBinding, cPoint);
				// System.out.println("tmpPoint: "+tmpPoint);
				if (tmpPoint != -1)
				{
					theGoalList.removeElementAt(0);
					System.out.println(theCurrentState);
					if (planning(theGoalList, theCurrentState, theBinding))
					{
						//System.out.println("Success !");
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
		//int randInt = Math.abs(rand.nextInt()) % operators.size();
		//Operator op = (Operator) operators.elementAt(randInt);
		//operators.removeElementAt(randInt);
		//operators.addElement(op);
		
		//優先度でソート
		Collections.sort(operators, new OperatorComparator());
		//優先度をリセット
		resetOperators();


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
					changePriorityOperator(anOperator);
					Operator newOperator = anOperator.instantiate(theBinding);
					Vector newGoals = (Vector) newOperator.getIfList();
					System.out.println(newOperator.name);
					if (planning(newGoals, theCurrentState, theBinding))
					{
						System.out.println(newOperator.name);
						
						newOperator = newOperator.instantiate(theBinding);
						changePriorityOperator(anOperator);
						
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
            	String goal = st.sval;
            	String[] goalToken = goal.split(" ");
            	for(int i = 0; i < goalToken.length - 1; i++)
            	{
            		for(int j = i+1; j < goalToken.length;j++)
            		{
            			if(goalToken[i].equals(goalToken[j]))
            			{
            				System.out.println(goal+"はゴールできません");
            				System.exit(-1);
            			}
            		}
            	}
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
		/// Priority
		int priority1 = 3;
		Operator operator1 = new Operator(name1, ifList1, addList1, deleteList1, priority1);
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
		/// Priority
		int priority2 = 1;
		Operator operator2 = new Operator(name2, ifList2, addList2, deleteList2, priority2);
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
		/// Priority
		int priority3 = 2;
		Operator operator3 = new Operator(name3, ifList3, addList3, deleteList3, priority3);
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
		/// Priority
		int priority4 = 1;
		Operator operator4 = new Operator(name4, ifList4, addList4, deleteList4, priority4);
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
						int priority = 0;
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
										while(!"PRIORITY".equals(st.sval)) {
											deleteList.add(st.sval);
											st.nextToken();
										}
										
										if("PRIORITY".equals(st.sval))
										{
											st.nextToken();
											priority = (int)(st.nval);
											st.nextToken();
										}
									}
                                }
                            }
						}

						// ルールの生成
						Operator operator = new Operator(name,ifList, addList, deleteList,priority);
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


	//ゴールリストのブロックの特徴を名前に変更する
	private Vector renewGoalList(Vector goalList, ArrayList<State> States)
	{
		//返すベクター型を用意
		Vector newGoalList = new Vector();

		for(int i = 0; i < goalList.size(); i++)
		{
			//ゴールリストを一つずつ取り出し特徴があれば名前に変更する
			String goal = (String)goalList.elementAt(i);

			String[] goalToken = goal.split(" ");
			String newGoal = "";

			for(int j = 0; j < goalToken.length; j++)
			{
				boolean judge = false;
				for(int k = 0; k < States.size(); k++)
				{

					State theState = States.get(k);

					if(goalToken[j].split(",").length == 2)
					{
						if(theState.judgeFeature(goalToken[j].split(",")[0],goalToken[j].split(",")[1]))
						{
							newGoal = newGoal+" "+theState.getName();
							judge = true;
						}
					}
					else
					{
						if(theState.judgeFeature(goalToken[j]) || theState.getName().equals(goalToken[j]))
						{
							newGoal = newGoal+" "+theState.getName();
							judge = true;
						}
					}
				}

				if(judge == false && j % 2 == 0)
				{
					//存在しないブロックの特徴の場合
					System.out.println("ゴール状態:"+goal+"で特徴:"+goalToken[j]+"のような特徴を持ったブロックは存在しない");
					System.exit(-1);
				}
				else if(judge == false)
					newGoal = newGoal+" "+goalToken[j];
			}

			//System.out.println(newGoal.trim());
			newGoalList.addElement(newGoal.trim());
		}

		return newGoalList;
	}

	//外部から物質の状態を読み込むメソッド
	private ArrayList<State> initStates(String _path)
	{
		ArrayList<State> States = new ArrayList<State>();
		try
		{
			FileReader f = new FileReader(_path);
			StreamTokenizer st = new StreamTokenizer(f);

			//ファイルから読み取る
			while(st.nextToken() != StreamTokenizer.TT_EOF)
			{
				String feature = st.sval;
				String theName = "";
				String theShape = "";
				String theColour = "";

				String[] features = feature.split(",",0);
				for(int i = 0; i < features.length; i++)
				{
					if(i == 0)
						theName = features[i];
					if(i == 1)
						theShape = features[i];
					if(i == 2)
						theColour = features[i];
				}

				State newState = new State(theName,theShape,theColour);
				States.add(newState);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return States;
	}
	
	//結果の表示にブロックの情報をプラスする
	private String plusState(String resultOperator, ArrayList<State> States)
	{
		String[] resultSplit = resultOperator.split(" ");
		String newResult = "";
		for(int i = 0; i < resultSplit.length; i++)
		{
			boolean judge = false;
			for(int j = 0; j < States.size(); j++)
			{
				if(States.get(j).judgeName(resultSplit[i]))
				{
					newResult = newResult+" "+States.get(j).rename(resultSplit[i]);
					judge = true;
				}
			}

			if(judge == false)
				newResult = newResult+" "+resultSplit[i];
		}

		return newResult;
	}
	
	//Operatorsの中身の優先度をリセットするメソッド
	private void resetOperators() {
		for(int i = 0; i < operators.size(); i++) {
			Operator reOpe =(Operator)operators.elementAt(i);
			reOpe.setPriority(1);
			operators.set(i,reOpe);
		}
	}
	
	//Operatorの優先度を変えるメソッド
	private void changePriorityOperator(Operator op) {
		
		//選択したオペレータがPlaceの場合
		if(op.name.contains("Place")) {
			for(int i = 0; i < operators.size(); i++) {
				Operator theOp = (Operator)operators.elementAt(i);
				if(theOp.name.contains("remove")) {
					int pri = theOp.getPriority();
					theOp.setPriority(pri - 1);
					operators.removeElementAt(i);
					operators.addElement(theOp);
					break;
				}
			}
		}
		
		//選択したオペレータがremoveの場合
		else if(op.name.contains("remove")) {
			for(int i = 0; i < operators.size(); i++) {
				Operator theOp = (Operator)operators.elementAt(i);
				if(theOp.name.contains("Place")) {
					int pri = theOp.getPriority();
					theOp.setPriority(pri - 1);
					operators.removeElementAt(i);
					operators.addElement(theOp);
					break;
				}
			}
		}
		
		//選択したオペレータがputの場合
		else if(op.name.contains("put")) {
			for(int i = 0; i < operators.size(); i++) {
				Operator theOp = (Operator)operators.elementAt(i);
				if(theOp.name.contains("pick")) {
					int pri = theOp.getPriority();
					theOp.setPriority(pri - 1);
					operators.removeElementAt(i);
					operators.addElement(theOp);
					break;
				}
			}
		}
		
		//選択したオペレータがpickの場合
		else if(op.name.contains("pick")) {
			for(int i = 0; i < operators.size(); i++) {
				Operator theOp = (Operator)operators.elementAt(i);
				if(theOp.name.contains("put")) {
					int pri = theOp.getPriority();
					theOp.setPriority(pri - 1);
					operators.removeElementAt(i);
					operators.addElement(theOp);
					break;
				}
			}
		}
	}
	
	//オペレータの優先度をランダムに
	private void randomOperators() {
		for(int i = 0; i < operators.size(); i++) {
			Operator theOp = (Operator)operators.elementAt(i);
			int randInt = rand.nextInt();
			theOp.setPriority(randInt);
			System.out.println(theOp);
			operators.set(i,theOp);
		}
	}
}

class Operator {
    String name;
    Vector ifList;
    Vector addList;
    Vector deleteList;
    int priority;

    Operator(String theName,
	     Vector theIfList,Vector theAddList,Vector theDeleteList,int thePriority){
	name       = theName;
	ifList     = theIfList;
	addList    = theAddList;
	deleteList = theDeleteList;
	priority   = thePriority;
    }
    
    public Vector getAddList(){
	return addList;
    }

    public Vector getDeleteList(){
	return deleteList;
    }

    public Vector getIfList(){
	return ifList;
    }
    
    public int getPriority() {
    	return priority;
    }
    
    public void setPriority(int thePriority) {
    	priority = thePriority;
    }

    public String toString(){
	String result =
	    "NAME: "+name + "\n" +
	    "IF :"+ifList + "\n" +
	    "ADD:"+addList + "\n" +
	    "DELETE:"+deleteList + "\n" +
	    "PRIORITY:" + priority;
	return result;
    }

    public Vector applyState(Vector theState){
	for(int i = 0 ; i < addList.size() ; i++){
	    theState.addElement(addList.elementAt(i));
	}
	for(int i = 0 ; i < deleteList.size() ; i++){
	    theState.removeElement(deleteList.elementAt(i));
	}
	return theState;
    }


    public Operator getRenamedOperator(int uniqueNum){
	Vector vars = new Vector();
	// IfListの変数を集める
	for(int i = 0 ; i < ifList.size() ; i++){
	    String anIf = (String)ifList.elementAt(i);
	    vars = getVars(anIf,vars);
	}
	// addListの変数を集める
	for(int i = 0 ; i < addList.size() ; i++){
	    String anAdd = (String)addList.elementAt(i);
	    vars = getVars(anAdd,vars);
	}
	// deleteListの変数を集める
	for(int i = 0 ; i < deleteList.size() ; i++){
	    String aDelete = (String)deleteList.elementAt(i);
	    vars = getVars(aDelete,vars);
	}
	Hashtable renamedVarsTable = makeRenamedVarsTable(vars,uniqueNum);

	// 新しいIfListを作る
	Vector newIfList = new Vector();
	for(int i = 0 ; i < ifList.size() ; i++){
	    String newAnIf =
		renameVars((String)ifList.elementAt(i),
			   renamedVarsTable);
	    newIfList.addElement(newAnIf);
	}
	// 新しいaddListを作る
	Vector newAddList = new Vector();
	for(int i = 0 ; i < addList.size() ; i++){
	    String newAnAdd =
		renameVars((String)addList.elementAt(i),
			   renamedVarsTable);
	    newAddList.addElement(newAnAdd);
	}
	// 新しいdeleteListを作る
	Vector newDeleteList = new Vector();
	for(int i = 0 ; i < deleteList.size() ; i++){
	    String newADelete =
		renameVars((String)deleteList.elementAt(i),
			   renamedVarsTable);
	    newDeleteList.addElement(newADelete);
	}
	// 新しいnameを作る
	String newName = renameVars(name,renamedVarsTable);

	return new Operator(newName,newIfList,newAddList,newDeleteList,priority);
    }

    private Vector getVars(String thePattern,Vector vars){
	StringTokenizer st = new StringTokenizer(thePattern);
	for(int i = 0 ; i < st.countTokens();){
	    String tmp = st.nextToken();
	    if(var(tmp)){
		vars.addElement(tmp);
	    }
	}
	return vars;
    }

    private Hashtable makeRenamedVarsTable(Vector vars,int uniqueNum){
	Hashtable result = new Hashtable();
	for(int i = 0 ; i < vars.size() ; i++){
	    String newVar =
		(String)vars.elementAt(i) + uniqueNum;
	    result.put((String)vars.elementAt(i),newVar);
	}
	return result;
    }

    private String renameVars(String thePattern,
			      Hashtable renamedVarsTable){
	String result = new String();
	StringTokenizer st = new StringTokenizer(thePattern);
	for(int i = 0 ; i < st.countTokens();){
	    String tmp = st.nextToken();
	    if(var(tmp)){
		result = result + " " +
		    (String)renamedVarsTable.get(tmp);
	    } else {
		result = result + " " + tmp;
	    }
	}
	return result.trim();
    }


    public Operator instantiate(Hashtable theBinding){
	// name を具体化
	String newName =
	    instantiateString(name,theBinding);
	// ifList    を具体化
	Vector newIfList = new Vector();
	for(int i = 0 ; i < ifList.size() ; i++){
	    String newIf =
		instantiateString((String)ifList.elementAt(i),theBinding);
	    newIfList.addElement(newIf);
	}
	// addList   を具体化
	Vector newAddList = new Vector();
	for(int i = 0 ; i < addList.size() ; i++){
	    String newAdd =
		instantiateString((String)addList.elementAt(i),theBinding);
	    newAddList.addElement(newAdd);
	}
	// deleteListを具体化
	Vector newDeleteList = new Vector();
	for(int i = 0 ; i < deleteList.size() ; i++){
	    String newDelete =
		instantiateString((String)deleteList.elementAt(i),theBinding);
	    newDeleteList.addElement(newDelete);
	}
	return new Operator(newName,newIfList,newAddList,newDeleteList,priority);
    }

    private String instantiateString(String thePattern, Hashtable theBinding){
        String result = new String();
        StringTokenizer st = new StringTokenizer(thePattern);
        for(int i = 0 ; i < st.countTokens();){
            String tmp = st.nextToken();
            if(var(tmp)){
		String newString = (String)theBinding.get(tmp);
		if(newString == null){
		    result = result + " " + tmp;
		} else {
		    result = result + " " + newString;
		}
            } else {
                result = result + " " + tmp;
            }
        }
        return result.trim();
    }

    private boolean var(String str1){
        // 先頭が ? なら変数
        return str1.startsWith("?");
    }
}

class Unifier {
    StringTokenizer st1;
    String buffer1[];
    StringTokenizer st2;
    String buffer2[];
    Hashtable vars;

    Unifier(){
	//vars = new Hashtable();
    }

    public boolean unify(String string1,String string2,Hashtable theBindings){
	Hashtable orgBindings = new Hashtable();
	for(Enumeration e = theBindings.keys() ; e.hasMoreElements();){
	    String key = (String)e.nextElement();
	    String value = (String)theBindings.get(key);
	    orgBindings.put(key,value);
	}
	this.vars = theBindings;
	if(unify(string1,string2)){
	    return true;
	} else {
	    // 失敗したら元に戻す．
	    theBindings.clear();
	    for(Enumeration e = orgBindings.keys() ; e.hasMoreElements();){
		String key = (String)e.nextElement();
		String value = (String)orgBindings.get(key);
		theBindings.put(key,value);
	    }
	    return false;
	}
    }

    public boolean unify(String string1,String string2){
	// 同じなら成功
	if(string1.equals(string2)) return true;

	// 各々トークンに分ける
	st1 = new StringTokenizer(string1);
	st2 = new StringTokenizer(string2);

	// 数が異なったら失敗
	if(st1.countTokens() != st2.countTokens()) return false;

	// 定数同士
	int length = st1.countTokens();
	buffer1 = new String[length];
	buffer2 = new String[length];
	for(int i = 0 ; i < length; i++){
	    buffer1[i] = st1.nextToken();
	    buffer2[i] = st2.nextToken();
	}

	// 初期値としてバインディングが与えられていたら
	if(this.vars.size() != 0){
	    for(Enumeration keys = vars.keys(); keys.hasMoreElements();){
		String key = (String)keys.nextElement();
		String value = (String)vars.get(key);
		replaceBuffer(key,value);
	    }
	}

	for(int i = 0 ; i < length ; i++){
	    if(!tokenMatching(buffer1[i],buffer2[i])){
		return false;
	    }
	}

	return true;
    }

    boolean tokenMatching(String token1,String token2){
	if(token1.equals(token2)) return true;
	if( var(token1) && !var(token2)) return varMatching(token1,token2);
	if(!var(token1) &&  var(token2)) return varMatching(token2,token1);
	if( var(token1) &&  var(token2)) return varMatching(token1,token2);
	return false;
    }

    boolean varMatching(String vartoken,String token){
	if(vars.containsKey(vartoken)){
	    if(token.equals(vars.get(vartoken))){
		return true;
	    } else {
		return false;
	    }
	} else {
	    replaceBuffer(vartoken,token);
	    if(vars.contains(vartoken)){
		replaceBindings(vartoken,token);
	    }
	    vars.put(vartoken,token);
	}
	return true;
    }

    void replaceBuffer(String preString,String postString){
	for(int i = 0 ; i < buffer1.length ; i++){
	    if(preString.equals(buffer1[i])){
		buffer1[i] = postString;
	    }
	    if(preString.equals(buffer2[i])){
		buffer2[i] = postString;
	    }
	}
    }

    void replaceBindings(String preString,String postString){
	Enumeration keys;
	for(keys = vars.keys(); keys.hasMoreElements();){
	    String key = (String)keys.nextElement();
	    if(preString.equals(vars.get(key))){
		vars.put(key,postString);
	    }
	}
    }

    boolean var(String str1){
	// 先頭が ? なら変数
	return str1.startsWith("?");
    }

}

//ブロックの状態を記述するクラス
class State {
    String name;   //ブロック名
    String shape;  //形
    String colour; //色

    State(String theName, String theShape, String theColour){
    	name = theName;
    	shape = theShape;
    	colour = theColour;
    }

    public String getName(){
    	return name;
    }

    //与えられた文字列が特徴かどうか判定するメソッド
	public boolean judgeFeature(String feature){
		if(feature.equals(shape) || feature.equals(colour))
			return true;
		else
			return false;
	}

	//上記のメソッドの引数が二つの場合
	public boolean judgeFeature(String feature_1, String feature_2){
		if((feature_1.equals(shape) && feature_2.equals(colour)) || (feature_1.equals(colour) && feature_2.equals(shape)))
			return true;
		else
			return false;
	}

	//与えられた文字列がブロックの名前かどうか判定するメソッド
	public boolean judgeName(String theName)
	{
		if(theName.equals(name))
			return true;
		else
			return false;
	}

	//出力用
	public String rename(String theName){
		if(theName.equals(name))
			return name+"("+shape+","+colour+")";
		else
			return theName;
	}
}

//オペレータをソートするためのメソッド
class OperatorComparator implements Comparator<Operator>{
	@Override
	public int compare(Operator op1, Operator op2) {
		return op2.getPriority() - op1.getPriority();
	}
}