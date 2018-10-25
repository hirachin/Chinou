/*
 FrameExample.java

*/

import java.io.*;
import java.util.*;


public class FrameExample 
{
	public static void example()
	{
		System.out.println( "Frame" );

		// フレームシステムの初期化
		AIFrameSystem fs = new AIFrameSystem();
		
		// クラスフレーム human の生成
		fs.createClassFrame( "human" );
		// height スロットを設定
		fs.writeSlotValue( "human", "height", new Integer( 160 ) );
		// height から weight を計算するための式 weight = 0.9*(height-100) を
		// when-requested demon として weight スロットに割り当てる  
		fs.setWhenRequestedProc( "human", "weight", new AIDemonProcReadTest() );
		
		// インスタンスフレーム tora のﾌ生成
		fs.createInstanceFrame( "human", "tora" );

		// height と weight はデフォルト値
		System.out.println( fs.readSlotValue( "tora", "height", false ) );
		System.out.println( fs.readSlotValue( "tora", "weight", false ) );

		// weight はデフォルト値
		fs.writeSlotValue( "tora", "height", new Integer( 165 ) );
		System.out.println( fs.readSlotValue( "tora", "height", false ) );
		System.out.println( fs.readSlotValue( "tora", "weight", false ) );

		// 再びデフォルト値を表示
		fs.writeSlotValue( "tora", "weight", new Integer( 50 ) );
		System.out.println( fs.readSlotValue( "tora", "height", true ) );
		System.out.println( fs.readSlotValue( "tora", "weight", true ) );

	}
	
	//ファイルからインスタンスフレームの情報を読み込む
	public static void loadFrame(AIFrameSystem _fs,String _path)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(_path), "UTF-8"));
			
			String line;
			
			while((line = reader.readLine()) != null)
			{
				if(line.equals("")){continue;}
				
				String[] datas = line.split(" ");
				

				if(datas[0].equals("human") && datas.length == 4)
				{
					createHumanInstance(_fs,datas[1],Integer.parseInt(datas[2]),Double.parseDouble(datas[3]));
				}
				
				else if(datas[0].equals("student"))
				{
					if(datas.length == 4)
					{
						createStudentInstance(_fs,datas[1],Integer.parseInt(datas[2]),Double.parseDouble(datas[3]));
					}
					
					else if(datas.length == 5)
					{
						createStudentInstance(_fs,datas[1],Integer.parseInt(datas[2]),Double.parseDouble(datas[3]),datas[4]);
					}
				}
				
				else if(datas[0].equals("club"))
				{
					if(datas.length == 2)
					{
						createClubInstance(_fs,datas[1]);
					}
					else if(datas.length == 3)
					{
						createClubInstance(_fs,datas[1],Integer.parseInt(datas[2]));
					}
				}
			}
			
			reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//クラスフレームの設定
	public static void setClassFrame(AIFrameSystem _fs)
	{
		
		// クラスフレーム human の生成
		_fs.createClassFrame("human");
		
		// height スロットを設定(単位cm)
		_fs.writeSlotValue( "human", "height_cm" ,new Integer( 160 ) );
		// weight スロットを設定(単位kg)
		_fs.writeSlotValue( "human", "weight_kg", new Double(60.0) );
		// height と weight から BMI を計算するための式 bmi =  weight/height_m^2を
		// when-requested demon として bmi スロットに割り当てる
		_fs.setWhenRequestedProc( "human", "bmi", new AIDemonProcBMI() );
		
		
		//クラスフレームhumanを継承するクラスフレームstudentの生成
		_fs.createClassFrame("human","student");
		
		//bmiについて再定義
		_fs.setWhenRequestedProc( "student", "bmi", new AIDemonProcBMI() );
		//所属クラブについて設定
		_fs.writeSlotValue("student","memberOf",new String("none"));
		
		
		//クラスフレームclubの生成
		_fs.createClassFrame("club");
		//memberNumスロットを設定
		_fs.writeSlotValue("club","memberNum",new Integer(0));
		
		//クラブに入ってない人用
		_fs.createInstanceFrame("club","none");
	}
	
	//humanクラスフレームのインスタンスを作成
	public static void createHumanInstance(AIFrameSystem _fs,String _name,int _height_cm,double _weight_kg)
	{
		//インスタンスフレームの作成
		_fs.createInstanceFrame( "human", _name);
		//インスタンスのデータの設定
		_fs.writeSlotValue(_name, "height_cm", new Integer(_height_cm));
		_fs.writeSlotValue(_name, "weight_kg", new Double(_weight_kg));
	}
	
	//stidentクラスフレームのインスタンスを作成
	public static void createStudentInstance(AIFrameSystem _fs,String _name,int _height_cm,double _weight_kg)
	{
		//インスタンスフレームの作成
		_fs.createInstanceFrame( "student", _name);
		//インスタンスのデータの設定
		_fs.writeSlotValue(_name, "height_cm", new Integer(_height_cm));
		_fs.writeSlotValue(_name, "weight_kg", new Double(_weight_kg));
	}
	
	//stidentクラスフレームのインスタンスを作成
	public static void createStudentInstance(AIFrameSystem _fs,String _name,int _height_cm,double _weight_kg,String _clubName)
	{
		createStudentInstance(_fs,_name,_height_cm,_weight_kg);
		//所属クラブについて設定
		_fs.writeSlotValue(_name, "memberOf", new String(_clubName));
	}
	
	//_clubクラスフレームのインスタンスを作成
	public static void createClubInstance(AIFrameSystem _fs,String _clubName)
	{
		//clubのインスタンスフレームの作成
		_fs.createInstanceFrame("club",_clubName);
	}
	
	//_clubクラスフレームのインスタンスを作成
	public static void createClubInstance(AIFrameSystem _fs,String _clubName,int _memberNum)
	{
		createClubInstance(_fs,_clubName);
		_fs.writeSlotValue(_clubName, "memberNum", new Integer(_memberNum));
	}
	
	//humanインスタンスの情報を出力
	public static void printHumanInstance(AIFrameSystem _fs,String _name)
	{
		System.out.println("名前：" + _name);
		System.out.println("身長："  + _fs.readSlotValue( _name, "height_cm"));
		System.out.println("体重："  + _fs.readSlotValue( _name, "weight_kg"));
		System.out.println(" BMI："  + _fs.readSlotValue( _name, "bmi"));
	}
	
	//studentインスタンスの情報を出力
	public static void printStudentInstance(AIFrameSystem _fs,String _name)
	{
		printHumanInstance(_fs,_name);
		System.out.println("所属："  + _fs.readSlotValue( _name, "memberOf"));
	}
	
	//clubインスタンスの情報を出力
	public static void printClubInstance(AIFrameSystem _fs,String _name)
	{
		System.out.println("名前：" + _name);
		System.out.println("人数："  + _fs.readSlotValue( _name, "memberNum"));
	}
	
	public static void main(String args[]) 
	{
		System.out.println( "Frame" );

		// フレームシステムの初期化
		AIFrameSystem fs = new AIFrameSystem();
		
		setClassFrame(fs);
		
		loadFrame(fs,"frameData.txt");
		
		/*
		System.out.println("----------------------------");
		
		printHumanInstance(fs,"kato");
		printHumanInstance(fs,"sato");
		printStudentInstance(fs,"hirabayashi");
		printStudentInstance(fs,"yamada");
		
		printClubInstance(fs,"computer");
		printClubInstance(fs,"soccer");
		printClubInstance(fs,"tennis");
		
		System.out.println("----------------------------");
		*/
		
		
		//検索をするための準備
		System.out.println("インスタンスフレーム名 スロット名");
		System.out.println("で検索してください");
		ArrayList<String> questions = new ArrayList<String>();
		Scanner sc = new Scanner(System.in);
		String search;
		  
		//検索するものの入力
		while(true)
		{
			System.out.print("検索文字列(0で終了):");
			search = sc.nextLine();
			if("0".equals(search))
				break;
			else
				questions.add(search);
		}
		  
		//検索を行う
		fs.quest(fs,questions);
		
	}
 
}