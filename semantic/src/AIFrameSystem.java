/*
 AIFrameSystem.java
  フレームシステム
*/

import java.util.*;

public
class AIFrameSystem {

final static String sTopFrameName = "top_level_frame"; 

// すべてのフレームを格納するための辞書．
// フレーム名をインデックスとして利用．
private HashMap<String,AIFrame> mFrames = new HashMap<String,AIFrame>();


/**
 * AIFrameSystem
 *  コンストラクタ
 */
public
AIFrameSystem() {
 mFrames.put( sTopFrameName,
  new AIClassFrame( this, null, sTopFrameName ) );
}


/**
 * createClassFrame
 *  クラスフレーム inName を作成する．
 */
public
void createClassFrame( String inName ) {
 createFrame( sTopFrameName, inName, false );
}


/**
 * createClassFrame
 *  スーパーフレームとして inSuperName を持つクラスフレーム
 *  inName を作成する．
 *
 *  @param inSuperName スーパーフレームのフレーム名
 *  @param inName フレーム名
 */
public
void createClassFrame( String inSuperName, String inName ) {
 createFrame( inSuperName, inName, false );
}


/**
 * createInstanceFrame
 *  スーパーフレームとして inSuperName を持つインスタンスフレーム
 *  inName を作成する．
 *
 *  @param inSuperName スーパーフレームのフレーム名
 *  @param inName フレーム名
 */
public
void createInstanceFrame( String inSuperName, String inName ) {
 createFrame( inSuperName, inName, true ); 
}


/*
 * createFrame 
 *  フレームを作成する
 *
 *  @param inSuperName スーパーフレームのフレーム名
 *  @param inName フレーム名
 *  @param inIsInstance インスタンスフレームなら true
 */
void createFrame(
 String inSuperName,
 String inName,
 boolean inIsInstance )
{
 AIClassFrame frame;
 try {
  frame = (AIClassFrame) mFrames.get( inSuperName );
  createFrame( frame, inName, inIsInstance );
 } catch ( Throwable err ) {
 }
}


/*
 * createFrame 
 *  フレームを作成する
 *
 *  @param inSuperName スーパーフレーム
 *  @param inName フレーム名
 *  @param inIsInstance インスタンスフレームなら true
 */

void createFrame(
 AIClassFrame inSuperFrame,
 String inName,
 boolean inIsInstance )
{
 AIFrame frame;
 if ( inIsInstance == true ) {
  frame = new AIInstanceFrame( this, inSuperFrame, inName );
 } else {
  frame = new AIClassFrame( this, inSuperFrame, inName );
 }
 mFrames.put( inName, frame );
}


/**
 * readSlotValue 
 *  スロット値を返す
 *
 *  @param inFrameName フレーム名
 *  @param inSlotName スロット名
 *  @param inDefault デフォルト値を優先したいなら true
 */
public
Object readSlotValue(
 String inFrameName,
 String inSlotName,
 boolean inDefault )
{
 AIFrame frame = (AIFrame) mFrames.get( inFrameName );
 return frame.readSlotValue( this, inSlotName, inDefault );
}


/**
 * readSlotValue 
 *  スロット値を返す
 *
 *  @param inFrameName フレーム名
 *  @param inSlotName スロット名
 */
public
Object readSlotValue(
 String inFrameName,
 String inSlotName )
{
 AIFrame frame = (AIFrame) mFrames.get( inFrameName );
 return frame.readSlotValue( this, inSlotName, false );
}


/**
 * readSlotValue 
 *  スロット値を返す
 *
 *  @param inFrameName フレーム名
 *  @param inSlotName スロット名
 *  @param inFacetName ファセット名
 */
public
Object readSlotValue(
 String inFrameName,
 String inSlotName,
 String inFacetName )
{
 AIFrame frame = (AIFrame) mFrames.get( inFrameName );
 return frame.readSlotValue( this, inSlotName, false );
}


/**
 * writeSlotValue 
 *  スロット値を設定する．
 *
 *  @param inFrameName フレーム名
 *  @param inSlotName スロット名
 *  @param inSlotValue スロット値
 */
public
void writeSlotValue(
 String inFrameName,
 String inSlotName,
 Object inSlotValue )
{
 AIFrame frame = (AIFrame) mFrames.get( inFrameName );
 frame.writeSlotValue( this, inSlotName, inSlotValue );
}


// demon procedure の設定

/**
 * setWhenConstructedProc
 *  when-constructed procedure を設定する．
 */
public
void setWhenConstructedProc(
 String inFrameName,
 String inSlotName,
 AIWhenConstructedProc inDemonProc )
{
 AIFrame frame = (AIFrame) mFrames.get( inFrameName );
 if ( frame != null )
  frame.setWhenConstructedProc( inDemonProc );
}

public
void setWhenConstructedProc(
 String inFrameName,
 String inSlotName,
 String inClassName )
{
 try {
  AIWhenConstructedProc demonProc =
    (AIWhenConstructedProc) Class.forName( inClassName ).newInstance();
  AIFrame frame = (AIFrame) mFrames.get( inFrameName );
  if ( frame != null )
   frame.setWhenConstructedProc( demonProc );
 } catch ( Exception err ) {
  System.out.println( err );
 }
}


/**
 * setWhenRequestedProc
 *  when-requested procedure を設定する．
 */
public
void setWhenRequestedProc(
 String inFrameName,
 String inSlotName,
 AIDemonProc inDemonProc )
{
 setDemonProc( AISlot.WHEN_REQUESTED, inFrameName,
  inSlotName, inDemonProc );
}

public
void setWhenRequestedProcClass(
 String inFrameName,
 String inSlotName,
 String inClassName )
{
 setDemonProcClass( AISlot.WHEN_REQUESTED,
  inFrameName, inSlotName, inClassName );
}


/**
 * setWhenReadProc
 *  when-read procedure を設定する．
 */
public
void setWhenReadProc(
 String inFrameName,
 String inSlotName,
 AIDemonProc inDemonProc )
{
 setDemonProc( AISlot.WHEN_READ,
  inFrameName, inSlotName, inDemonProc );
}

public
void setWhenReadProcClass(
 String inFrameName,
 String inSlotName,
 String inClassName )
{
 setDemonProcClass( AISlot.WHEN_READ,
  inFrameName, inSlotName, inClassName );
}


/**
 * setWhenWrittenProc
 *  when-written procedure を設定する．
 */
public
void setWhenWrittenProc(
 String inFrameName,
 String inSlotName,
 AIDemonProc inDemonProc )
{
 setDemonProc( AISlot.WHEN_WRITTEN,
  inFrameName, inSlotName, inDemonProc );
}

public
void setWhenWrittenProcClass(
 String inFrameName,
 String inSlotName,
 String inClassName )
{
 setDemonProcClass( AISlot.WHEN_WRITTEN,
  inFrameName, inSlotName, inClassName );
}


/*
 * setDemonProc
 *  demon procedure を設定する．
 */
void setDemonProc(
 int inType,
 String inFrameName,
 String inSlotName,
 AIDemonProc inDemonProc )
{
 AIFrame frame = (AIFrame) mFrames.get( inFrameName );
 if ( frame != null )
  frame.setDemonProc( inType, inSlotName, inDemonProc );
}


/*
 * setDemonClass
 *  demon procedure を設定する．
 */
void setDemonProcClass(
 int inType,
 String inFrameName,
 String inSlotName,
 String inClassName )
{
 try {
  AIDemonProc demon =
   (AIDemonProc) Class.forName( inClassName ).newInstance();
  setDemonProc( inType, inFrameName, inSlotName, demon );
 } catch ( Exception err ) {
  System.out.println( err );
 }
}

/**
 * quest
 * 質問を表示し内容を表示する
 * 
 * @param fs AIFrameSystemのインスタンス
 * @param questions 検索内容のArrayList
 */

public void quest(AIFrameSystem fs, ArrayList<String> questions)
{
	for(int i = 0; i < questions.size(); i++)
	{
		System.out.println("Quetion:"+questions.get(i));
		System.out.println("result-----------------------------");
		
		//インスタンス化と初期化
		Searcher searcher = new Searcher();
		
		//検索を行うメソッドSearchの呼び出し
		searcher.Search(fs,questions.get(i));
		
		//出力の一部
		System.out.println("-----------------------------------");
	}
	System.out.println("end");
}

//検索を行うためのクラス
class Searcher{
	StringTokenizer st; //トークンを保存する
	String buffer[];
	
	Searcher(){}
	
	public void Search(AIFrameSystem fs, String question)
	{	
		//トークンに分ける
		st = new StringTokenizer(question);
		
		//stのサイズが2でなければ失敗
		if(st.countTokens() != 2)
		{
			System.out.println("検索フォームが違います");
			return;
		}
		
		//検索を行う
		//トークンが変数かどうかで分岐
		buffer = new String[st.countTokens()];
		buffer[0] = st.nextToken();
		buffer[1] = st.nextToken();
		
		if(var(buffer[0]) && var(buffer[1])) doubleVarSearching(fs,buffer[0],buffer[1]);
		if(var(buffer[0]) && !var(buffer[1])) frontVarSearching(fs,buffer[0],buffer[1]);
		if(!var(buffer[0]) && var(buffer[1])) backVarSearching(fs,buffer[0],buffer[1]);
		if(!var(buffer[0]) && !var(buffer[1])) FrameSearching(fs,buffer[0],buffer[1]);
	}
	
	//両方とも変数の場合
	void doubleVarSearching(AIFrameSystem fs, String token1, String token2)
	{
		for(String key1:fs.mFrames.keySet()) 
		{
			//フレーム名を一つずつ取り出し処理する
			HashMap<String,String> binding = new HashMap<String,String>();
			
			//変数の束縛を保存
			binding.put(token1, key1);
			AIFrame frame = mFrames.get(key1);
			ArrayList<String> list = new ArrayList<String>();

			while(true)
			{
				//スロット名を一つずつ取り出し処理する
				for(String key2:frame.getSlots().keySet())
				{
					if(binding.containsKey(token2))
					{
						if(key2.equals(binding.get(token2)) && !list.contains(key2))
						{
							//変数がtoken1と同じでスロット名にもあるなら表示
							System.out.println(binding.get(token1)+" "+binding.get(token1)+" "+fs.readSlotValue(binding.get(token1), binding.get(token1)));
							list.add(key2);
						}
					}
				
					else
					{
						if(!key2.equals("is-a") && !key2.equals("ako") && !list.contains(key2))
						{
							System.out.println(key1+" "+key2+" "+fs.readSlotValue(key1,key2));
							list.add(key2);
						}
					}
				}
				if(frame.getSupers() == null)
					break;
				else
					frame = (AIFrame)frame.getSupers().next();
			}
		}
	}
	
	//前だけ変数の場合
	void frontVarSearching(AIFrameSystem fs,String token1,String token2)
	{
		for(String key:fs.mFrames.keySet())
		{
			//フレーム名を一つずつ取り出し処理する
			AIFrame frame = fs.mFrames.get(key);
			
			while(true)
			{
				if(frame.getSlots().containsKey(token2))
				{
					//token2がスロット名にあるなら表示
					System.out.println(key+" "+token2+" "+fs.readSlotValue(key, token2));
					break;
				}
				if(frame.getSupers() == null)
					break;
				else
					frame = (AIFrame)frame.getSupers().next();
			}
		}
	}
	
	//後ろだけ変数の場合
	void backVarSearching(AIFrameSystem fs,String token1, String token2)
	{
		if(fs.mFrames.containsKey(token1))
		{
			//token1がフレーム名にあるなら処理
			AIFrame frame = fs.mFrames.get(token1);
			ArrayList<String> list = new ArrayList<String>();
			while(true)
			{
				for(String key:frame.getSlots().keySet())
				{
					//スロット名を一つずつ取り出し表示
					if(!key.equals("is-a") && !key.equals("ako") && !list.contains(key))
					{
						System.out.println(token1+" "+key+" "+fs.readSlotValue(token1, key));
						list.add(key);
					}
				}
				if(frame.getSupers() == null)
					break;
				else
					frame = (AIFrame)frame.getSupers().next();
			}
		}
	}
	
	//両方とも変数でない場合
	void  FrameSearching(AIFrameSystem fs,String token1, String token2)
	{
		if(fs.mFrames.containsKey(token1))
		{
			AIFrame frame = fs.mFrames.get(token1);
			
			while(true)
			{
				if(frame.getSlots().containsKey(token2))
				{
					//token1がフレーム名にありtoken2がスロット名にあるなら表示
					System.out.println(token1+" "+token2+" "+fs.readSlotValue(token1, token2));
					break;
				}
				if(frame.getSupers() == null)
					break;
				else
					frame = (AIFrame)frame.getSupers().next();
			}
		}
	}
	
	boolean var(String str)
	{
		//先頭が?なら変数
		return str.startsWith("?");
	}
}

} // end of class definition