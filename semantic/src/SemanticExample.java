import java.util.*;
import java.io.*;

/***
 * Semantic Net の使用例
 */
 
public class SemanticExample 
{
	public static void loadLinks(String _path,SemanticNet _sn)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(_path), "UTF-8"));
			String line;
			while((line = reader.readLine()) != null)
			{
				if(line.equals("") || line.charAt(0) == '#'){continue;}
				
				String[] linkData = line.split(" ");
				
				if(linkData.length != 3)
				{
					System.out.println("dataError in loadLinks:" + line);
					continue;
				}
				
				_sn.addLink(new Link(linkData[0],linkData[1],linkData[2],_sn));

			}
			reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{
		
		if(args.length == 0)
		{
			System.exit(0);
		}
		
		SemanticNet sn = new SemanticNet();
		loadLinks("hirabayashi_links.txt",sn);
		
		sn.printLinks();
		sn.printNodes();
		
		
		ArrayList<Link> query = new ArrayList<Link>();
		
		for(int i = 0;i < args.length;i++)
		{
			String[] datas = args[i].split(" ");
			
			if(datas.length == 3)
			{
				query.add(new Link(datas[0],datas[1],datas[2]));
			}
		}
		
		sn.query(query);
	}
}

