import java.io.FileReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

public class Planner
{
	Vector operators;
	Vector initialState;
	Vector goalList;
	Vector banList;
	ArrayList<State> states;

	static final double rule = 1.0;

	Random rand;
	Vector plan;
	int trials;


	/*public static void main(String argv[])
	{
		int repeat = 0;
		File f = new File("resultImp_2.csv");
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(f))){
			while(repeat < 100) {
				//本文
				long start = System.nanoTime();
				Planner planDo = new Planner();
				planDo.start();
				long end = System.nanoTime();

				System.out.println("Time:"+(end - start) / 1000000f+"ms");
				//本文終了
				bw.write(planDo.plan.size()+","+planDo.trials+","+(end-start) / 1000000f);
				bw.newLine();
				repeat++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}*/

	Planner()
	{
		rand = new Random();
		trials = 0;
		initOperators("testOperators.data");
		goalList = initGoalList("goalList.data");
		initialState = initInitialState("initialState.data");
		banList = initBanList("banList.data");
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
		banList = renewGoalList(banList,states);
		if(inBanList(goalList,banList))
		{
			System.out.println("ゴールが物理的制約に反しています");
			System.exit(-1);
		}
		goalList = sortGoalList(goalList);
		//initOperators();
		//Vector goalList = initGoalList();
		//Vector initialState = initInitialState();

		Hashtable theBinding = new Hashtable();
		plan = new Vector();

		boolean judge = planning(goalList, initialState, theBinding);

		if(judge == false)
		{
			System.out.println("無限ループ");
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
		banList = renewGoalList(banList,states);
		if(inBanList(goalList,banList))
		{
			System.out.println("ゴールが物理的制約に反しています");
			System.exit(-1);
		}
		goalList = sortGoalList(goalList);

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
				if(uniqueNum > 50000)
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
		String[] buf = theGoal.split(" ");
		String x_buf = "";
		String y_buf = "";
		for(int i = 0; i < buf.length; i++)
		{
			if(buf[i].startsWith("?"))
			{
				if(buf[i].contains("y"))
				{
					y_buf = buf[i];
					x_buf = buf[i].replace("y","x");
				}
			}
		}

		// 現在のCurrent state, Binding, planをbackup
		Hashtable orgBinding = new Hashtable();
		for (Enumeration e = theBinding.keys(); e.hasMoreElements();)
		{
			String key = (String) e.nextElement();
			String value = (String) theBinding.get(key);
			orgBinding.put(key, value);
		}

		for (int i = 0; i < size; i++)
		{
			String aState = (String) theCurrentState.elementAt(i);
			if ((new Unifier()).unify(theGoal, aState, theBinding))
			{
				if(theBinding.get(y_buf) == null || theBinding.get(x_buf) == null)
					return 0;
				else if(!(theBinding.get(x_buf).equals(theBinding.get(y_buf))))
					return 0;
				else
					// 失敗したら元に戻す．
					theBinding.clear();
					for (Enumeration e = orgBinding.keys(); e.hasMoreElements();)
					{
						String key = (String) e.nextElement();
						String value = (String) orgBinding.get(key);
						theBinding.put(key, value);
					}
			}
		}

		//ランダムにオペレータ選ぶ
		//int randInt = Math.abs(rand.nextInt()) % operators.size();
		//Operator op = (Operator) operators.elementAt(randInt);
		//operators.removeElementAt(randInt);
		//operators.addElement(op);

		//ヒューリスティックに基づいてソート
		heuristicOperators(theGoal, theCurrentState, theBinding);

		//優先度でソート
		/*double randDouble = Math.random();
		if(randDouble < rule)
			randomOperators();

		Collections.sort(operators, new OperatorComparator());
		//優先度をリセット
		resetOperators();*/


		for (int i = cPoint; i < operators.size(); i++)
		{
			Operator anOperator = rename((Operator) operators.elementAt(i));

			// 現在のCurrent state, Binding, planをbackup
			orgBinding = new Hashtable();
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
					//changePriorityOperator(anOperator);
					Operator newOperator = anOperator.instantiate(theBinding);
					Vector newGoals = (Vector) newOperator.getIfList();
					System.out.println(newOperator.name);
					if (planning(newGoals, theCurrentState, theBinding) && !inBanList(newOperator.instantiate(theBinding).addList,banList))
					{
						System.out.println("追加するオペレータ:"+newOperator.name);

						newOperator = newOperator.instantiate(theBinding);
						//changePriorityOperator(anOperator);

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

	//禁止リストを外部から読み込むメソッド
	private Vector initBanList(String _path)
	{
		Vector banList = new Vector();

		try
		{
			FileReader f = new FileReader(_path);
			StreamTokenizer st = new StreamTokenizer(f);

            while(st.nextToken() != StreamTokenizer.TT_EOF)
			{
            	String ban = st.sval;
            	banList.addElement(st.sval);
            }
		}
		catch(Exception e)
		{
            System.out.println(e);
		}

		return banList;
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

				if(judge == false && j % 2 == 0 &&!goalToken[j].startsWith("?"))
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

	//ゴール状態を下から並び替えるメソッド
	private Vector sortGoalList(Vector goalList)
	{
		int repeat = 0;
		int repeat_limit = (goalList.size()*goalList.size() - goalList.size()) / 2;
		for(int i = 0; i < goalList.size()-1; i++)
		{
			//ゴールリストから一つ取り出す
			String goal = (String)goalList.elementAt(i);
			String[] goalToken = goal.split(" ");
			for(int j = i+1; j < goalList.size(); j++)
			{
				String comGoal = (String)goalList.elementAt(j);
				String[] comGoalToken = comGoal.split(" ");
				if(comGoalToken[0].equals(goalToken[0]) || comGoalToken[2].equals(goalToken[2]))
				{
					//先頭もしくは最後が同じトークンならゴール不可(AonB,AonC or B on A, C on A)
					System.out.println(goal+"と"+comGoal+"はゴール不可です");
					System.exit(-1);
				}

				if(repeat > repeat_limit)
				{
					//AonB,BonAでは無限に続くので強制終了
					System.out.println("無限ループです");
					System.exit(-1);
				}

				if(goalToken[2].equals(comGoalToken[0]))
				{
					//自分より下の状態を記述したゴールがあれば場所を入れ替え
					goalList.set(i,comGoal);
					goalList.set(j,goal);
					i--;
					repeat++;
					break;
				}
			}
		}

		//並べ替えた後のゴールリストを返す
		return goalList;
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
				if(theOp.name.contains("remove") || op.name.contains("Place")) {
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
				if(theOp.name.contains("Place") || theOp.name.contains("remove")) {
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
				if(theOp.name.contains("pick") || theOp.name.contains("put")) {
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
				if(theOp.name.contains("put") || theOp.name.contains("pick")) {
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

	//リストがbanListにあるかどうかの判定(あったらtrue)
	private boolean inBanList(Vector lists, Vector banList)
	{
		for(int i = 0; i < lists.size(); i++)
		{
			String list = (String)lists.get(i);
			for(int j = 0; j < banList.size(); j++)
			{
				String ban = (String)banList.get(j);
				String[] listToken = list.split(" ");
				String[] banToken = ban.split(" ");
				int judge = 0;
				if(listToken.length == banToken.length)
				{
					for(int k = 0; k < listToken.length; k++)
					{
						if(banToken[k].startsWith("?"))
							judge++;
						else if(listToken[k].equals(banToken[k]))
							judge++;
					}
				}
				if(judge == listToken.length)
				{
					System.out.println("NG:"+list+"<-"+ban);
					return true;
				}
			}
		}
		return false;
	}

	//ヒューリスティックに基づいてオペレータを入れ替え
	private void heuristicOperators(String theGoal, Vector theCurrentState, Hashtable theBinding)
	{
		String[] goalToken = theGoal.split(" ");
		if(goalToken.length == 2)
		{
			if(goalToken[0].equals("holding"))
			{
				//目標がholdingかつ対象が地面にあればpickを選択
				if(theCurrentState.contains("ontable "+goalToken[1]))
				{
					for(int i = 0; i < operators.size(); i++)
					{
						Operator op = (Operator)operators.elementAt(i);
						if(op.name.contains("pick"))
						{
							operators.setElementAt((Operator)operators.elementAt(0),i);
							operators.setElementAt(op, 0);
						}
					}
				}
				//対象が地面にないならremoveを選択
				else
				{
					for(int i = 0; i < operators.size(); i++)
					{
						Operator op = (Operator)operators.elementAt(i);
						if(op.name.contains("remove"))
						{
							operators.setElementAt((Operator)operators.elementAt(0),i);
							operators.setElementAt(op, 0);
						}
					}
				}
			}

			else if(goalToken[0].equals("clear"))
			{
				//目標がclearならremoveを選択
				for(int i = 0;  i < operators.size(); i++)
				{
					Operator op = (Operator)operators.elementAt(i);
					if(op.name.contains("remove"))
					{
						operators.setElementAt((Operator)operators.elementAt(0),i);
						operators.setElementAt(op, 0);
					}
				}
			}
		}

		else if(goalToken.length == 1 && goalToken[0].equals("handEmpty"))
		{
			//目標がhandEmptyならputを選択
			for(int i = 0;  i < operators.size(); i++)
			{
				Operator op = (Operator)operators.elementAt(i);
				if(op.name.contains("put"))
				{
					operators.removeElementAt(i);;
					operators.addElement(op);
				}
			}
		}
	}
}




