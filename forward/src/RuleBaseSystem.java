import java.util.*;
import java.io.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


class LogAnim
{
	private int count = 0;
	private ArrayList<String> log;
	
	private String printData = "";
	
	//アサーションのアニメーション追加可視化用
	private ArrayList<String> assertions;
	
	public void setAssertions(ArrayList<String> _assertions)
	{
		assertions = new ArrayList<String>(_assertions);
	}
	
	public ArrayList<String> getAssertions()
	{
		return assertions;
	}
	
	public LogAnim(ArrayList<String> _log)
	{
		log = new ArrayList<String>(_log);
	}
	
	public void nextLine()
	{
		printData += "<br />";
	}
	
	public boolean update()
	{
		if(count >= log.size()){return false;}
		
		String logData = log.get(count);
		
		if(logData.contains("apply"))
		{
			printData = logData;
			nextLine();
		}
		
		else if(logData.contains("Success"))
		{
			printData += logData;
			assertions.add(logData.split(":")[1]);
			nextLine();
		}
		
		count++;
		return true;
	}
	
	public String getPrintData()
	{
		return "<html><body>" + printData + "</body></html>";
	}
}


/**
 * RuleBaseSystem
 * 
 */
public class RuleBaseSystem extends JFrame implements Runnable
{
	static LogAnim logAnim;
	
	static JTextArea assText;
	static JTextArea qryText;
	static JButton addAssBt;
	static JButton addQryBt;
	static JButton startBt;
	
	static JLabel resultLabel;
	static JLabel assertionsLabel;
	static JLabel queriesLabel;
	static JLabel questionLabel;
	
	static RuleBase rb;
	
	static boolean isAnim = false;
	
	static ArrayList<String> queries;
	
	public RuleBaseSystem() 
	{
		new Thread(this).start();
	}
	
	static class AddAssertion implements ActionListener
	{
		@Override public void actionPerformed(ActionEvent e)
		{
			rb.addAssertion(assText.getText());
			assText.setText("");
			//assertionsを更新する
			assertionsLabel.setText(setAssertionsToHTML(rb.getWmAssertions()));
		}
	}
	
	static class AddQuery implements ActionListener
	{
		@Override public void actionPerformed(ActionEvent e)
		{
			queries.add(qryText.getText());
			qryText.setText("");
			//queryを更新する
			queriesLabel.setText(setAssertionsToHTML(queries));
		}
	}
	
	static class StartAction implements ActionListener
	{
		@Override public void actionPerformed(ActionEvent e)
		{
			//LogAnimのアサーション用に推論前のを保存
			ArrayList<String> oldAssertions = new ArrayList<String>(rb.getWmAssertions());
			
			//推論の過程のログ
			ArrayList<String> result = rb.forwardChainToArrayString();
			
			logAnim = new LogAnim(result);
			logAnim.setAssertions(oldAssertions);
			isAnim = true;
			
			//GUI表示用のアサーションをLogAnimのものに設定
			assertionsLabel.setText(setAssertionsToHTML(logAnim.getAssertions()));
		}
	}
	
	static String setAssertionsToHTML(ArrayList<String> assertions)
	{
		String str = "<html><body>";
		for(String s:assertions)
		{
			str += s;
			str+="<br />";
		}
		
		str += "</body></html>";
		
		return str;
	}

	public static void main(String args[])
	{
		rb = new RuleBase("CarShop.data","CarShopAss.data");
		queries = new ArrayList<String>();
		
		JFrame frame = new RuleBaseSystem();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setSize(640, 480);
		
		//アサーション追加ボタン
		addAssBt = new JButton("addAssertion");
		addAssBt.setBounds(50,10,100,20);
		addAssBt.addActionListener(new AddAssertion());
		
		//クエリー追加ボタン
		addQryBt = new JButton("addQuery");
		addQryBt.setBounds(50,40,100,20);
		addQryBt.addActionListener(new AddQuery());
		
		//推論開始ボタン
		startBt = new JButton("start");
		startBt.setBounds(50,70,100,20);
		startBt.addActionListener(new StartAction());
		
		//追加アサーション記述テキストボックス
		assText = new JTextArea(1,20);
		assText.setBounds(160,10,200,20);
		
		//追加アサーション記述テキストボックス
		qryText = new JTextArea(1,20);
		qryText.setBounds(160,40,200,20);
		
		//アサーションを表示するエリア
		assertionsLabel = new JLabel();
		assertionsLabel.setBounds(0,100,320,200);
		//左詰めにする
		assertionsLabel.setHorizontalAlignment(JLabel.LEADING);
		//上詰めにする
		assertionsLabel.setVerticalAlignment(JLabel.TOP);
		assertionsLabel.setText(setAssertionsToHTML(rb.getWmAssertions()));
		
		//クエリを表示するエリア
		queriesLabel = new JLabel();
		queriesLabel.setBounds(320,100,320,200);
		//左詰めにする
		queriesLabel.setHorizontalAlignment(JLabel.LEADING);
		//上詰めにする
		queriesLabel.setVerticalAlignment(JLabel.TOP);
		queriesLabel.setText(setAssertionsToHTML(queries));
		
		//出力結果を表示するエリア
		resultLabel = new JLabel();
		resultLabel.setBounds(0,300,320,380);
		//左詰めにする
		resultLabel.setHorizontalAlignment(JLabel.LEADING);
		//上詰めにする
		resultLabel.setVerticalAlignment(JLabel.TOP);
		resultLabel.setText("");
		
		//出力結果を表示するエリア
		questionLabel = new JLabel();
		questionLabel.setBounds(320,300,320,380);
		//左詰めにする
		questionLabel.setHorizontalAlignment(JLabel.LEADING);
		//上詰めにする
		questionLabel.setVerticalAlignment(JLabel.TOP);
		questionLabel.setText("");
		
		frame.add(addAssBt);
		frame.add(addQryBt);
		frame.add(assText);
		frame.add(qryText);
		frame.add(startBt);
		frame.add(assertionsLabel);
		frame.add(queriesLabel);
		frame.add(resultLabel);
		frame.add(questionLabel);
		
		frame.show();
	}
	
	
	
	int count=0;
	
	public void run()
	{
		while(true)
		{
			//assTextが空の時addAssBtを無効化する
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					if(addAssBt == null || assText == null){return;}
					addAssBt.setEnabled(!assText.getText().equals(""));
				}
			});
			
			//アニメーション中GUIの機能無効化
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					if(startBt == null){return;}
					startBt.setEnabled(!isAnim);
					if(addAssBt == null){return;}
					startBt.setEnabled(!isAnim);
					if(assText == null){return;}
					assText.setEnabled(!isAnim);
					if(addQryBt == null){return;}
					addQryBt.setEnabled(!isAnim);
					if(qryText == null){return;}
					qryText.setEnabled(!isAnim);
				}
			});
			
			//アニメーション実行
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					if(!isAnim || resultLabel== null)
					{
						if(resultLabel== null){return;}
						resultLabel.setText("");
						return;
					}
					
					//一定時間ごとにアニメーションを進める
					if(count%10==0)
					{
						isAnim = logAnim.update();
						assertionsLabel.setText(setAssertionsToHTML(logAnim.getAssertions()));
						
						String printLog = logAnim.getPrintData();
					
						if(printLog != null)
						{
							resultLabel.setText(printLog);
						}
					}
				}
			});
			
			try
			{
				Thread.sleep(100);
			}
			catch(Exception e){e.printStackTrace();}
			
			count++;
		}
	}

	@Override public void paint(Graphics g)
	{
		super.paint(g);
		paintLine(g,0,130,640,130);
		paintLine(g,0,300,640,300);
		paintLine(g,320,130,320,480);
	}

	void paintLine(Graphics g,int startX,int startY,int endX,int endY)
	{
		g.drawLine(startX, startY, endX, endY);
	}
}

/**
 * ワーキングメモリを表すクラス．
 *
 * 
 */
class WorkingMemory {
    ArrayList<String> assertions;    

    WorkingMemory(){
        assertions = new ArrayList<String>();
    }

    /**
     * マッチするアサーションに対するバインディング情報を返す
     * （再帰的）
     *
     * @param     前件を示す ArrayList
     * @return    バインディング情報が入っている ArrayList
     */
    public ArrayList matchingAssertions(ArrayList<String> theAntecedents){
        ArrayList bindings = new ArrayList();
        return matchable(theAntecedents,0,bindings);
    }

    private ArrayList matchable(ArrayList<String> theAntecedents,int n,ArrayList bindings){
        if(n == theAntecedents.size()){
            return bindings;
        } else if (n == 0){
            boolean success = false;
            for(int i = 0 ; i < assertions.size() ; i++){
                HashMap<String,String> binding = new HashMap<String,String>();
                if((new Matcher()).matching(
                    (String)theAntecedents.get(n),
                    (String)assertions.get(i),
                    binding)){
                    bindings.add(binding);
                    success = true;
                }
            }
            if(success){
                return matchable(theAntecedents, n+1, bindings);
            } else {
                return null;
            }
        } else {
            boolean success = false;
            ArrayList newBindings = new ArrayList();
            for(int i = 0 ; i < bindings.size() ; i++){
                for(int j = 0 ; j < assertions.size() ; j++){
                    if((new Matcher()).matching(
                        (String)theAntecedents.get(n),
                        (String)assertions.get(j),
                        (HashMap)bindings.get(i))){
                        newBindings.add(bindings.get(i));
                        success = true;
                    }
                }
            }
            if(success){
                return matchable(theAntecedents,n+1,newBindings);
            } else {
                return null;
            }
        }
    }
    
    /**
     * アサーションをワーキングメモリに加える．
     *
     * @param     アサーションを表す String
     */
    public void addAssertion(String theAssertion){
        System.out.println("ADD:"+theAssertion);
        assertions.add(theAssertion);
    }

    /**
     * 指定されたアサーションがすでに含まれているかどうかを調べる．
     *
     * @param     アサーションを表す String
     * @return    含まれていれば true，含まれていなければ false
     */
    public boolean contains(String theAssertion){
        return assertions.contains(theAssertion);
    }

    /**
     * ワーキングメモリの情報をストリングとして返す．
     *
     * @return    ワーキングメモリの情報を表す String
     */
    public String toString(){
        return assertions.toString();
    }
	
	public ArrayList<String> getAssertions()
	{
		return assertions;
	}
    
}

/**
 * ルールベースを表すクラス．
 *
 * 
 */
class RuleBase {
    String fileName;
    FileReader f;
    StreamTokenizer st;
    WorkingMemory wm;
    ArrayList<Rule> rules;
	
	public ArrayList<Rule> getRules(){return rules;}
    
    RuleBase(){
        fileName = "CarShop.data";
        wm = new WorkingMemory();
        wm.addAssertion("my-car is inexpensive");
        wm.addAssertion("my-car has a VTEC engine");
        wm.addAssertion("my-car is stylish");
        wm.addAssertion("my-car has several color models");
        wm.addAssertion("my-car has several seats");
        wm.addAssertion("my-car is a wagon");
        rules = new ArrayList<Rule>();
        loadRules(fileName);
    }
	
	RuleBase(String _rulesFilePath)
	{
		fileName = _rulesFilePath;
		wm = new WorkingMemory();
		rules = new ArrayList<Rule>();
		loadRules(fileName);
	}
	
	RuleBase(String _rulesFilePath,String _assertionsFilePath)
	{
		fileName = _rulesFilePath;
		wm = new WorkingMemory();
		loadAssertions(_assertionsFilePath);
		rules = new ArrayList<Rule>();
		loadRules(fileName);
	}
	
	public void loadAssertions(String _path)
	{
		if(wm == null){return;}
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(_path), "UTF-8"));
			String line;
			while((line = reader.readLine()) != null)
			{
				if(line.equals("") || line.charAt(0) == '#'){continue;}
				wm.addAssertion(line);
			}
			
			reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void addAssertion(String _assertion)
	{
		wm.addAssertion(_assertion);
	}
	
	public ArrayList<String> getWmAssertions()
	{
		return wm.getAssertions();
	}

    /**
     * 前向き推論を行うためのメソッド
     *
     */
    public void forwardChain(){
        boolean newAssertionCreated;
        // 新しいアサーションが生成されなくなるまで続ける．
        do {
            newAssertionCreated = false;
            for(int i = 0 ; i < rules.size(); i++){
                Rule aRule = (Rule)rules.get(i);
                System.out.println("apply rule:"+aRule.getName());
                ArrayList<String> antecedents = aRule.getAntecedents();
                String consequent  = aRule.getConsequent();
                //HashMap bindings = wm.matchingAssertions(antecedents);
                ArrayList bindings = wm.matchingAssertions(antecedents);
                if(bindings != null){
                    for(int j = 0 ; j < bindings.size() ; j++){
                        //後件をインスタンシエーション
                        String newAssertion =
                            instantiate((String)consequent,
                                        (HashMap)bindings.get(j));
                        //ワーキングメモリーになければ成功
                        if(!wm.contains(newAssertion)){
                            System.out.println("Success: "+newAssertion);
                            wm.addAssertion(newAssertion);
                            newAssertionCreated = true;
                        }
                    }
                }
            }
            System.out.println("Working Memory"+wm);
        } while(newAssertionCreated);
        System.out.println("No rule produces a new assertion");
    }
	
	public ArrayList<String> forwardChainToArrayString()
	{
		ArrayList<String> result = new ArrayList<String>();
		boolean newAssertionCreated;
		// 新しいアサーションが生成されなくなるまで続ける．
		do
		{
			newAssertionCreated = false;
			for(int i = 0 ; i < rules.size(); i++)
			{
				Rule aRule = (Rule)rules.get(i);
				result.add("apply :"+aRule.getName());
				ArrayList<String> antecedents = aRule.getAntecedents();
				String consequent  = aRule.getConsequent();
				ArrayList bindings = wm.matchingAssertions(antecedents);
				if(bindings != null)
				{
					for(int j = 0 ; j < bindings.size() ; j++)
					{
						//後件をインスタンシエーション
						String newAssertion = instantiate((String)consequent, (HashMap)bindings.get(j));
						//ワーキングメモリーになければ成功
						if(!wm.contains(newAssertion))
						{
							result.add("Success: "+newAssertion);
							wm.addAssertion(newAssertion);
							newAssertionCreated = true;
						}
					}
				}
			}
				result.add("Working Memory"+wm);
		} while(newAssertionCreated);
		
		result.add("No rule produces a new assertion");
		
		return result;
	}

    private String instantiate(String thePattern, HashMap theBindings){
        String result = new String();
        StringTokenizer st = new StringTokenizer(thePattern);
        for(int i = 0 ; i < st.countTokens();){
            String tmp = st.nextToken();
            if(var(tmp)){
                result = result + " " + (String)theBindings.get(tmp);
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

    private void loadRules(String theFileName)
	{
        String line;
        try
		{
            int token;
            f = new FileReader(theFileName);
            st = new StreamTokenizer(f);
            while((token = st.nextToken())!= StreamTokenizer.TT_EOF)
			{
                switch(token)
				{
                    case StreamTokenizer.TT_WORD:
                        String name = null;
                        ArrayList<String> antecedents = null;
                        String consequent = null;
                        if("rule".equals(st.sval))
						{
								st.nextToken();
//                            if(st.nextToken() == '"'){
                                name = st.sval;
                                st.nextToken();
                                if("if".equals(st.sval)){
                                    antecedents = new ArrayList<String>();
                                    st.nextToken();
                                    while(!"then".equals(st.sval)){
                                        antecedents.add(st.sval);
                                        st.nextToken();
                                    }
                                    if("then".equals(st.sval)){
                                        st.nextToken();
                                        consequent = st.sval;
                                    }
                                }
//                            } 
                        }
			// ルールの生成
                        rules.add(new Rule(name,antecedents,consequent));
                        break;
                    default:
                        System.out.println(token);
                        break;
                }
            }
        } catch(Exception e){
            System.out.println(e);
        }
        for(int i = 0 ; i < rules.size() ; i++){
            System.out.println(((Rule)rules.get(i)).toString());
        }
    }
}

/**
 * ルールを表すクラス．
 *
 * 
 */
class Rule {
    String name;
    ArrayList<String> antecedents;
    String consequent;

    Rule(String theName,ArrayList<String> theAntecedents,String theConsequent){
        this.name = theName;
        this.antecedents = theAntecedents;
        this.consequent = theConsequent;
    }

    /**
     * ルールの名前を返す．
     *
     * @return    名前を表す String
     */
    public String getName(){
        return name;
    }

    /**
     * ルールをString形式で返す
     *
     * @return    ルールを整形したString
     */
    public String toString(){
        return name+" "+antecedents.toString()+"->"+consequent;
    }

    /**
     * ルールの前件を返す．
     *
     * @return    前件を表す ArrayList
     */
    public ArrayList<String> getAntecedents(){
        return antecedents;
    }

    /**
     * ルールの後件を返す．
     *
     * @return    後件を表す String
     */
    public String getConsequent(){
        return consequent;
    }
    
}

class Matcher {
    StringTokenizer st1;
    StringTokenizer st2;
    HashMap<String,String> vars;
    
    Matcher(){
        vars = new HashMap<String,String>();
    }

    public boolean matching(String string1,String string2,HashMap<String,String> bindings){
        this.vars = bindings;
        return matching(string1,string2);
    }
    
    public boolean matching(String string1,String string2){
        //System.out.println(string1);
        //System.out.println(string2);
        
        // 同じなら成功
        if(string1.equals(string2)) return true;
        
        // 各々トークンに分ける
        st1 = new StringTokenizer(string1);
        st2 = new StringTokenizer(string2);

        // 数が異なったら失敗
        if(st1.countTokens() != st2.countTokens()) return false;
                
        // 定数同士
        for(int i = 0 ; i < st1.countTokens();){
            if(!tokenMatching(st1.nextToken(),st2.nextToken())){
                // トークンが一つでもマッチングに失敗したら失敗
                return false;
            }
        }
        
        // 最後まで O.K. なら成功
        return true;
    }

    boolean tokenMatching(String token1,String token2){
        //System.out.println(token1+"<->"+token2);
        if(token1.equals(token2)) return true;
        if( var(token1) && !var(token2)) return varMatching(token1,token2);
        if(!var(token1) &&  var(token2)) return varMatching(token2,token1);
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
            vars.put(vartoken,token);
        }
        return true;
    }

    boolean var(String str1){
        // 先頭が ? なら変数
        return str1.startsWith("?");
    }

}
