package simpledb.client;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;

import simpledb.student.*;
import simpledb.remote.SimpleDriver;
//import sun.org.mozilla.javascript.internal.ast.Assignment;
import sun.util.locale.StringTokenIterator;

import java.io.*;

public class SQLInterpreterCourse {
	private static Connection conn = null;

	public static void main(String[] args) {
		try {
			Driver d = new SimpleDriver();
			conn = d.connect("jdbc:simpledb://localhost", null);

			Reader rdr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(rdr);

			while (true) {
				// process one line of input
				System.out.print("\nSQL> ");
				String cmd = br.readLine().trim();
				System.err.println(cmd.trim());
				System.out.println();
				if (cmd.startsWith("exit"))
					break;
				else if (cmd.startsWith("doHorizontal"))
				{
					doHorizontalCourse(cmd);
				}	
				else if(cmd.startsWith("select"))
				{
					doQuery(cmd);
				}
				else
					doUpdate(cmd);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (conn != null)
					conn.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static ArrayList<Course> dbcourse = new ArrayList<Course>();
	static int requiredTablesNoCourse = 0;
	private static void logicalHorizontal()
	{
		String selectAll = "select CId, Title, DeptId from Course";

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(selectAll);


			// export all student table into dbcourse arraylist
			while(rs.next())
			{

				Course c = new Course();

				c.setCId(rs.getInt("CId"));
				c.setTitle(rs.getString("Title"));
				c.setDeptId(rs.getInt("DeptId"));
				

				dbcourse.add(c);
			}

			OperationsCourse op = new OperationsCourse();

			// # of distinct gradyears
			requiredTablesNoCourse = op.getDistinctValueByDeptId(dbcourse);

			Collections.sort(dbcourse,new Course());
			op.assignNumberOfSameDeptIds(dbcourse);

		}catch(Exception ex){
			System.err.println(ex.toString());
		}
	}
////////////////////////
	private static void doHorizontalCourse(String cmd)
	{

		String selectAll = "select CId, Title, DeptId from Course";

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(selectAll);


			// export all student table into dbcourse arraylist
			while(rs.next())
			{

				Course c = new Course();

				c.setCId(rs.getInt("CId"));
				c.setTitle(rs.getString("Title"));
				c.setDeptId(rs.getInt("DeptId"));
				
				dbcourse.add(c);
			}

			OperationsCourse op = new OperationsCourse();

			// # of distinct deptIds
			int requiredTablesNoCourse = op.getDistinctValueByDeptId(dbcourse);

			Collections.sort(dbcourse,new Course());
			op.assignNumberOfSameDeptIds(dbcourse);

			for(Course x : dbcourse)
			{
				System.out.println(x.getDeptId() + " " + x.getCId() + " " + x.getDistinctVal() );
			}


			int counter=0;
			ArrayList<Course> clist = new ArrayList<>();
			clist = op.getDistinctValueByDeptIdValues(dbcourse);

			for(int i=0; i<clist.size(); i++)
			{
				System.out.println(clist.get(i).getCId() + " " + clist.get(i).getDeptId());
			}
			//insert get counter. elemanýn -1 incisini insert etcez databasede döngüde döngüde conterýncýyý insert edicez ve sonra counterý bir arttýrcaz
			for(int i=0; i<requiredTablesNoCourse; i++)
			{
				int gradYearsCount = dbcourse.get(counter).getDistinctVal();
				String TableName = "C"+clist.get(i).getDeptId();

				// createTableQuery
				String createTable = "create table "+ TableName +" (CId int, Title varchar(20), DeptId int)";
				stmt.executeUpdate(createTable);
				System.out.println("table " + TableName + " created");

				for(int t=counter; t<counter+gradYearsCount; t++)
				{
					String c = "insert into "+TableName+ " (CId, Title, DeptId) values (" + 
							dbcourse.get(t).getCId()+ ", " + "'"+
							dbcourse.get(t).getTitle()+"'"+", "+
							dbcourse.get(t).getDeptId()+")";

					System.out.println(c);
					stmt.executeUpdate(c);
				}
				counter = counter + dbcourse.get(counter).getDistinctVal();

			}
		}catch(Exception ex){
			System.err.println(ex.toString());
		}





		// count(x) extract "x" and run -> select x from Student import into arraylist and count from arraylist
	}

	private static boolean isCourseTbl(String sql)
	{
		if(sql.contains("Course"))
			return true;
		else 
			return false;
	}

	private static ArrayList divideIntoSubQueries(String sql)
	{
		logicalHorizontal();
		OperationsCourse op= new OperationsCourse();
		ArrayList<String> subQueries = new ArrayList<>();

		for(int i=1; i<=requiredTablesNoCourse; i++)
		{
			subQueries.add(sql.replace("Course", "C"+String.valueOf(op.getDistinctValueByDeptIdValues(dbcourse).get(i).getDeptId())));
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

			if(isCourseTbl(cmd))
			{

				ArrayList<String> queryList = divideIntoSubQueries(cmd);
				for(int k=0; k<queryList.size(); k++)
				{
					rs = stmt.executeQuery(queryList.get(k));
					System.out.println("query runs for table: S" + (k+1));
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

	private static void doUpdate(String cmd) {
		try {
			Statement stmt = conn.createStatement();
			
			int howmany = stmt.executeUpdate(cmd);
			System.out.println(howmany + " records processed");
		}
		catch (SQLException e) {
			System.out.println("SQL Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
}

