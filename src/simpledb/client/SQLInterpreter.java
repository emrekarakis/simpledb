package simpledb.client;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;

import simpledb.student.*;
import simpledb.remote.SimpleDriver;
import sun.security.util.Length;
import sun.util.locale.StringTokenIterator;

import java.io.*;

public class SQLInterpreter {
	private static Connection conn = null;

	public static void main(String[] args) {
		try {
			Driver d = new SimpleDriver();
			conn = d.connect("jdbc:simpledb://localhost", null); ///jdbc connection line

			Reader rdr = new InputStreamReader(System.in);//to read line as a query 
			BufferedReader br = new BufferedReader(rdr);  

			while (true) {
				// process one line of input
				System.out.print("\nSQL> ");
				String cmd = br.readLine().trim();
				System.err.println(cmd.trim());
				System.out.println();
				if (cmd.startsWith("exit"))
					break;
				else if (cmd.startsWith("doHorizontal"))// dohorizontal is a word which divides student table into subtables  
				{
					doHorizontalStudent(cmd);            //the required tables are determined and then created
				}	
				else if(cmd.startsWith("select"))
				{
					doQuery(cmd);                     //when a requested query is given or when a search according to GradYear including Student keyword,query is executed from the subtables otherwise from the student table
				}
				else
					doUpdate(cmd);                  //insert operation is performed here, after insertin an object to the subtable, updating in subtables is performed. 
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (conn != null)
					conn.close();           //closing jdbc connection 
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static ArrayList<Student> db = new ArrayList<Student>();    //creating a new Student arraylist including Student's characteristics such as SId,SName,MajorId,GradYear etc
	static int requiredTablesNo = 0;  //it is necessary to determine number of required tables,we get this value from the operation class with the function of GetDistinctValueByGradYear().                 
	private static void logicalHorizontal()//This function shall generally execute inside another function because the function is an important part of all implementation. 
	{
		String selectAll = "select SId, SName, MajorId, GradYear from Student"; //* is not implemented on simpledb we needed to use the whole Student table drawn 

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(selectAll);     //selectAll query is executed 


			// export all student table into db arraylist
			while(rs.next())
			{

				Student s = new Student();                 //creating a student object and then entering the whole features of Student and then given to the arraylist.

				s.setSId(rs.getInt("SId"));                //this loop indicates that each student features is held on an object and that object is sent to the arraylist
				s.setSName(rs.getString("SName"));
				s.setMajorId(rs.getInt("MajorId"));                   
				s.setGradYear(rs.getInt("GradYear"));      

				db.add(s);
			}

			Operations op = new Operations();             //we need to use the function of operations class,there are important function at that class

			// # of distinct gradyears
			requiredTablesNo = op.getDistinctValueByGradYear(db); //one of them is getDistinctValueByGradYear which returns the number of subtables needing to be generated.

			Collections.sort(db,new Student());        //for each student student objects are sorted according to gradyear by using collections and comparable class.
			op.assigntNumberOfSameGradYears(db);       //it determines in findIndex method and then assign how many 2004 exist in db arraylist for example.

		}catch(Exception ex){
			System.err.println(ex.toString());
		}
	}

	private static void doHorizontalStudent(String cmd)
	{

		String selectAll = "select SId, SName, MajorId, GradYear from Student";

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(selectAll);


			// export all student table into db arraylist
			while(rs.next())
			{

				Student s = new Student();

				s.setSId(rs.getInt("SId"));
				s.setSName(rs.getString("SName"));
				s.setMajorId(rs.getInt("MajorId"));
				s.setGradYear(rs.getInt("GradYear"));         // Adding the Student tables' specifications to the ArrayList

				db.add(s);
			}

			Operations op = new Operations();

			// # of distinct gradyears
			int requiredTablesNo = op.getDistinctValueByGradYear(db);//how many subtables required

			Collections.sort(db,new Student()); //sorting with compare method
			op.assigntNumberOfSameGradYears(db);//how many 2004 exist in arraylist and then assigning to db 

			for(Student x : db)
			{
				System.out.println(x.getGradYear() + " " + x.getSId() + " " + x.getDistinctVal() ); //writing the gradyear sid and numberofdistinctvalue of an object in the arraylist 
			}


			int counter=0;
			ArrayList<Student> tlist = new ArrayList<>();
			tlist = op.getDistinctValueByGradYearValues(db);//this function returns to an arraylist that is to say new arraylist returns here not to have two element which is the same as GradYear,for example we add only one 2004 to the arraylist by using the getDistinctValueByGradYearValues function.

			for(int i=0; i<tlist.size(); i++)
			{
				System.out.println(tlist.get(i).getMajorId() + " " + tlist.get(i).getGradYear());//this is just see the distinct GradYear objects having been taken from the getDistinctValueByGradYearValues() function in operations class 
				
			}
		
			for(int i=0; i<requiredTablesNo; i++)
			{
				int gradYearsCount = db.get(counter).getDistinctVal();//we have taken the distinct value of gradyears
				String TableName = "S"+tlist.get(i).getGradYear();//partition tables are called as S"GradYearValue" such as S2004 subtable

				// createTableQuery
				String createTable = "create table "+ TableName +" (SId int, SName varchar(10), MajorId int, GradYear int)";//subtables was created as the number of required tables and by executing  create table query
				stmt.executeUpdate(createTable);
				System.out.println("table " + TableName + " created");// this indicates the table is created and to have just the confirmation message  

				for(int t=counter; t<counter+gradYearsCount; t++)
				{
					String s = "insert into "+TableName+ " (SId, SName, MajorId, GradYear) values (" + 
							db.get(t).getSId()+ ", " + "'"+
							db.get(t).getSName()+"'"+", "+                                       //inserting to the values of students from the arraylist db   
							db.get(t).getMajorId()+", "+
							db.get(t).getGradYear()+")";

					System.out.println(s);
					stmt.executeUpdate(s);
				}
				counter = counter + db.get(counter).getDistinctVal();

			}
		}catch(Exception ex){
			System.err.println(ex.toString());
		}



		// count(x) extract "x" and run -> select x from Student import into arraylist and count from arraylist
	}

	private static boolean isStudentTbl(String sql) //does the entered query contains Student keyword?
	{
		if(sql.contains("Student"))
			return true;
		else 
			return false;
	}

	private static ArrayList divideIntoSubQueries(String sql)  //
	{
		logicalHorizontal();
		Operations op = new Operations();
		ArrayList<String> subQueries = new ArrayList<>();
														//changing the student with S2004 to pull data from this S2004 subtable
		for(int i=0; i<requiredTablesNo; i++)			//and then added to the arraylist
		{
			subQueries.add(sql.replace("Student", "S"+String.valueOf(op.getDistinctValueByGradYearValues(db).get(i).getGradYear())));
		}

		return subQueries;

	}


	private static void doQuery(String cmd) {
		try {

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(cmd);
			ResultSetMetaData md = rs.getMetaData();
			int numcols = md.getColumnCount();
			int totalwidth = 0;

			// print header
			for(int i=1; i<=numcols; i++) {
				int width = md.getColumnDisplaySize(i);
				totalwidth += width;
				String fmt = "%" + width + "s";
				System.out.format(fmt, md.getColumnName(i));
			}
			System.out.println();
			for(int i=0; i<totalwidth; i++)
				System.out.print("-");
			System.out.println();
			Operations op = new Operations();
			if(isStudentTbl(cmd))   
			{
				
				logicalHorizontal(); // update
				ArrayList<String> queryList = divideIntoSubQueries(cmd);//this arraylist includes S2004 table not student
				for(int k=0; k<queryList.size(); k++)
				{
					rs = stmt.executeQuery(queryList.get(k));//each string query is executed here
					System.out.println("query runs for table: S" + op.getDistinctValueByGradYearValues(db).get(k).getGradYear());//queries run for the S___ table
					// print records
					while(rs.next()) {
						for (int i=1; i<=numcols; i++) {
							String fldname = md.getColumnName(i);
							int fldtype = md.getColumnType(i);
							String fmt = "%" + md.getColumnDisplaySize(i);
							if (fldtype == Types.INTEGER)
								System.out.format(fmt + "d", rs.getInt(fldname));
							else
								System.out.format(fmt + "s", rs.getString(fldname));
						}
						System.out.println();
					}
				}
			}
			else
			{
				while(rs.next()) {
					for (int i=1; i<=numcols; i++) {
						String fldname = md.getColumnName(i);
						int fldtype = md.getColumnType(i);
						String fmt = "%" + md.getColumnDisplaySize(i);
						if (fldtype == Types.INTEGER)
							System.out.format(fmt + "d", rs.getInt(fldname));
						else
							System.out.format(fmt + "s", rs.getString(fldname));
					}
					System.out.println();
				}
				rs.close();
			}
		}
		catch (SQLException e) {
			System.out.println("SQL Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void doUpdate(String cmd) {//doUpdate function includes insert query operation, as soon as a new record is inserted,the subtables are updated according to the data having been added   
		try {
			Statement stmt = conn.createStatement();
			Operations op = new Operations();
			stmt.executeUpdate(cmd);   //query must be executed 
			
			String grad = cmd.trim().substring(cmd.length()-5,cmd.length()-1); //we taked four value being gradyear value before the last paranthesis of insert command 
			System.out.println(grad);//we observed the gradYear here
			boolean isFound = false;
			String old = cmd.trim(); //old string query is assigned to old string
			String q = null;
			
			if(cmd.contains("Student")) 
			{	
				System.out.println(op.getDistinctValueByGradYear(db));
				for(int i=0; i<op.getDistinctValueByGradYear(db); i++)
				{	
					System.out.println(grad);
					if(Integer.parseInt(grad) == op.getDistinctValueByGradYearValues(db).get(i).getGradYear())
					{
						String x = "S"+grad;  
						
						q = op.replaceQuery(old, x); ////if query includes Student keyword,we replaced with the subtable name for insertion
						System.out.println("if:" + x); 
						isFound = true;
					}
				}
				if(isFound==false)// if the gradyear of the new record that will be inserted  is not found in the arraylist,create a new table called S2010 for example,and then insert the new record into new subtable
				{	
					String TableName = "S"+grad;
					// createTableQuery
					String createTable = "create table "+ TableName +" (SId int, SName varchar(10), MajorId int, GradYear int)";
					System.out.println("table created: " + TableName);
					stmt.executeUpdate(createTable);
					q = op.replaceQuery(old, TableName);
				}
			}
			System.err.println(q);
	
//  insert into Student (SId, SName, MajorId, GradYear) values (1, 'emre', 10, 2004)			
			
			int howmany = stmt.executeUpdate(q);
			System.out.println(howmany + " records processed");//confirmed the insertion operation
		}
		catch (SQLException e) {
			System.out.println("SQL Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
