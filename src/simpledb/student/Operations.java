package simpledb.student;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Operations {
	
	public void assigntNumberOfSameGradYears(ArrayList<Student> s)  
	{
		for(int i=0; i<s.size(); i++)
		{
			s.get(i).setDistinctVal(findIndex(s, s.get(i).getGradYear()));
		}
		
	}
	
	private int findIndex(ArrayList<Student> s, int x)     ////////for example it returns the number of 2004s in arraylist
	{
		int counter =0;
		for(int i=0; i<s.size(); i++)
		{
			if(s.get(i).getGradYear() == x)
				counter++;
		}
		
		return counter;
		
	}
	
	public int getDistinctValueByGradYear(ArrayList<Student> s)
	{
		ArrayList<Student> temp = new ArrayList<>();
		
		for(int i=0; i<s.size(); i++)
		{
			if(findIndex(temp, s.get(i).getGradYear()) == 0)//if a specific type of gradyear does not exist ,add it if exist dont add this gradyear
			{
				temp.add(s.get(i));
			}
		}
		return temp.size();//// to find number of tables we find distinct values of gradyears
	}
	
	public String replaceQuery(String old, String n)   //this function is to replace Student(old string) with the S2003(new string)
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
	
	public ArrayList<Student> getDistinctValueByGradYearValues(ArrayList<Student> s)//this function is to take arraylist including distinct gradyears
	{
		ArrayList<Student> temp = new ArrayList<>();
		
		for(int i=0; i<s.size(); i++)
		{
			if(findIndex(temp, s.get(i).getGradYear()) == 0)
			{
				temp.add(s.get(i));
			}
		}
		return temp;
	}
	
	
//	public int getDistinctValueByMajorId(ArrayList<Student> s)
//	{
//		ArrayList<Student> temp = new ArrayList<Student>(s);
//		for(int i=0; i<temp.size()-1; i++)
//		{
//			int delete = temp.get(i).getMajorId();
//			for(int j=i+1; j<temp.size(); j++)
//			{
//				if(temp.get(j).getMajorId()==(delete))
//				{
//					temp.remove(j);
//				}
//			}
//		}
//		
//		return temp.size();
//	}

}
