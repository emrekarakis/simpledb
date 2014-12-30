package simpledb.student;
import java.util.Comparator;

public class Course implements Comparator<Course> {
	String Title;
	int DeptId,CId,DistinctVal;



	public int getDistinctVal() {
		return DistinctVal;
	}

	public void setDistinctVal(int distinctVal) {
		DistinctVal = distinctVal;
	}

	public int getCId() {
		return CId;
	}

	public void setCId(int CId) {
		this.CId = CId;
	}


	public int getDeptId() {
		return DeptId;
	}

	public void setDeptId(int deptId) {
		DeptId = deptId;
	}
	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}


	@Override
	public int compare(Course c1, Course c2) {
		// TODO Auto-generated method stub
		return c1.DeptId-c2.DeptId;
	}



}