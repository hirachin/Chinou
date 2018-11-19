//ブロックの状態を記述するクラス
class State 
{
    String name;   //ブロック名
    String shape;  //形
    String colour; //色

    State(String theName, String theShape, String theColour)
    {
    	name = theName;
    	shape = theShape;
    	colour = theColour;
    }

    public String getName()
    {
    	return name;
    }

    //与えられた文字列が特徴かどうか判定するメソッド
    public boolean judgeFeature(String feature)
    {
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
    public String rename(String theName)
    {
		if(theName.equals(name))
			return name+"("+shape+","+colour+")";
		else
			return theName;
	}
}