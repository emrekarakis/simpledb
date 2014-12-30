package simpledb.student;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class OperationsCourse {
	
	public void assignNumberOfSameDeptIds(ArrayList<Course> c)
	{
		for(int i=0; i<c.size(); i++)
		{
			c.get(i).setDistinctVal(findIndex(c, c.get(i).getDeptId()));
		}
		
	}
	
	private int findIndex(ArrayList<Course> c, int x)
	{
		int counter =0;
		for(int i=0; i<c.size(); i++)
		{
			if(c.get(i).getDeptId() == x)
				counter++;
		}
		
		return counter;
		
	}
	
	public int getDistinctValueByDeptId(ArrayList<Course> c)
	{
		ArrayList<Course> temp = new ArrayList<>();
		
		for(int i=0; i<c.size(); i++)
		{
			if(findIndex(temp, c.get(i).getDeptId()) == 0)
			{
				temp.add(c.get(i));
			}
		}
		return temp.size();
	}
	
	public String replaceQuery(String old, String n)
	{
		StringTokenizer st = new StringTokenizer(old);
		String query;
		
		query = st.nextToken();
		query += " " +st.nextToken();
		st.nextToken();
		query += " "+ n;
		while(st.hasMoreTokens())
		{
			query +=" "+st.nextToken();
		}
		return query;
	}
	
	public ArrayList<Course> getDistinctValueByDeptIdValues(ArrayList<Course> c)
	{
		ArrayList<Course> temp = new ArrayList<>();
		
		for(int i=0; i<c.size(); i++)
		{
			if(findIndex(temp, c.get(i).getDeptId()) == 0)
			{
				temp.add(c.get(i));
			}
		}
		return temp;
	}
	
	public int getDistinctValueByCId(ArrayList<Course> c)
	{
		ArrayList<Course> temp = new ArrayList<Course>(c);
		for(int i=0; i<temp.size()-1; i++)
		{
			int delete = temp.get(i).getCId();
			for(int j=i+1; j<temp.size(); j++)
			{
				if(temp.get(j).getCId()==(delete))
				{
					temp.remove(j);
				}
			}
		}
		
		return temp.size();
	}

}
