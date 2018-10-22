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
	
	
	public static void main(String args[]) 
	{
		System.out.println( "Frame" );

		// フレームシステムの初期化
		AIFrameSystem fs = new AIFrameSystem();
		
		// クラスフレーム human の生成
		fs.createClassFrame("human");
		
		// height スロットを設定(単位cm)
		fs.writeSlotValue( "human", "height_cm" ,new Integer( 160 ) );
		// weight スロットを設定(単位kg)
		fs.writeSlotValue( "human", "weight_kg", new Double(60.0) );
		// height と weight から BMI を計算するための式 bmi =  weight/height_m^2を
		// when-requested demon として bmi スロットに割り当てる
		fs.setWhenRequestedProc( "human", "bmi", new AIDemonProcBMI() );
		
		//クラスフレームhumanを継承するクラスフレームstudentの生成
		fs.createClassFrame("human","student");
		
		
		//インスタンスフレームの作成
		fs.createInstanceFrame( "student", "hirabayashi" );
		//インスタンスのデータの設定
		fs.writeSlotValue( "hirabayashi", "height_cm", new Integer( 200 ) );
		fs.writeSlotValue( "hirabayashi", "weight_kg", new Double( 100.0 ) );
		
		
		
		System.out.println("----------------------------");
		//データ表示
		System.out.println( fs.readSlotValue( "hirabayashi", "height_cm", false ) );
		System.out.println( fs.readSlotValue( "hirabayashi", "weight_kg", false ) );
		System.out.println( fs.readSlotValue( "hirabayashi", "bmi", false ) );
		//System.out.println( fs.readSlotValue( "student", "height_cm", false ) );
		//System.out.println( fs.readSlotValue( "student", "weight_kg", false ) );
		//System.out.println( fs.readSlotValue( "student", "bmi", false ) );

	}
 
}