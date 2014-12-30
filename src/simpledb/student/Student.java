package simpledb.student;

import java.util.Comparator;

public class Student implements Comparator<Student> {

	String  SName;
	int MajorId, GradYear,SId,DistinctVal;
	 
	

	public int getDistinctVal() {
		return DistinctVal;
	}

	public void setDistinctVal(int distinctVal) {
		DistinctVal = distinctVal;
	}

	public int getSId() {
		return SId;
	}

	public void setSId(int sId) {
		SId = sId;
	}

	public String getSName() {
		return SName;
	}

	public void setSName(String sName) {
		SName = sName;
	}

	public int getMajorId() {
		return MajorId;
	}

	public void setMajorId(int majorId) {
		MajorId = majorId;
	}

	public int getGradYear() {
		return GradYear;
	}

	public void setGradYear(int gradYear) {
		GradYear = gradYear;
	}


	@Override
	public int compare(Student s1, Student s2) {
		// TODO Auto-generated method stub
		return s1.GradYear - s2.GradYear;
				
	}



}
