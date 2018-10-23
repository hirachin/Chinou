/*
 FrameExample.java

*/

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
	
	
	public static void setCreateFrame(AIFrameSystem _fs)
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

	
	public static void main(String args[]) 
	{
		System.out.println( "Frame" );

		// フレームシステムの初期化
		AIFrameSystem fs = new AIFrameSystem();
		
		setCreateFrame(fs);
		
		//各種clubのインスタンスフレームの作成
		createClubInstance(fs,"computer",40);
		createClubInstance(fs,"baseball",30);
		createClubInstance(fs,"tennis",50);
		
		createHumanInstance(fs,"kato",180,70);
		createStudentInstance(fs,"hirabayashi",170,64,"computer");
		createStudentInstance(fs,"sato",160,80);
		
		System.out.println("----------------------------");
		
		System.out.println( fs.readSlotValue( "kato", "height_cm", false ) );
		System.out.println( fs.readSlotValue( "kato", "weight_kg", false ) );
		System.out.println( fs.readSlotValue( "kato", "bmi", false ) );
		
		System.out.println( fs.readSlotValue( "hirabayashi", "height_cm", false ) );
		System.out.println( fs.readSlotValue( "hirabayashi", "weight_kg", false ) );
		System.out.println( fs.readSlotValue( "hirabayashi", "bmi", false ) );
		System.out.println( fs.readSlotValue( "hirabayashi", "memberOf", false ) );
		
		System.out.println( fs.readSlotValue( "sato", "height_cm", false ) );
		System.out.println( fs.readSlotValue( "sato", "weight_kg", false ) );
		System.out.println( fs.readSlotValue( "sato", "bmi", false ) );
		System.out.println( fs.readSlotValue( "sato", "memberOf", false ) );
		
		
		System.out.println(fs.readSlotValue( (String)fs.readSlotValue("hirabayashi","memberOf"), "memberNum"));
		
		
		//データ表示
		/*
		System.out.println( fs.readSlotValue( "hirabayashi", "height_cm", false ) );
		System.out.println( fs.readSlotValue( "hirabayashi", "weight_kg", false ) );
		System.out.println( fs.readSlotValue( "hirabayashi", "bmi", false ) );
		System.out.println( fs.readSlotValue( "hirabayashi", "memberOf", false ) );
		*/
		
		//System.out.println( fs.readSlotValue( "student", "height_cm", false ) );
		//System.out.println( fs.readSlotValue( "student", "weight_kg", false ) );
		//System.out.println( fs.readSlotValue( "student", "bmi", false ) );

	}
 
}