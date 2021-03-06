package unb.cs2043.student_assistant;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**@author Allan Boutilier **/

@SuppressWarnings("serial")
public class ClassTime implements Serializable {

	private ArrayList<String> days;
	private String type;
	private LocalTime startTime;
	private LocalTime endTime;

	public ClassTime(String type, ArrayList<String> daysIn, LocalTime startTimeIn, LocalTime endTimeIn ) {
		this.type = type;
		this.days = daysIn;
		this.startTime = startTimeIn;
		this.endTime = endTimeIn;
	}

	/**********Getters*********/
	public String getDays(){
		String s = "";
		for(int i=0; i<days.size(); i++) {
			s+=days.get(i);
		}
		return s;
	}
	
	public ArrayList<String> copyDays() {
		return new ArrayList<String>(days);
	}
	
	public LocalTime getStartTime() {
		return startTime;
	}
	
	public LocalTime getEndTime() {
		return endTime;
	}

	public String getType() {
		return type;
	}

	public String toString() {
		return getType() + " " + getDays() + " " + getStartTime().toString() + "-" + getEndTime().toString();
	}

	/**********Setters*********/
	public void setDays(ArrayList<String> newDaysIn) {
		boolean check=true;
		for(int i=0; i<newDaysIn.size(); i++) {
			if(!isValidDay(days.get(i))) {
				check = false;
			}
		}
	
		if(check == true) {
			days = newDaysIn;
		}
	}
	

	public void setStartTime(LocalTime newStartTime) {
			startTime = newStartTime;
	}

	public void setEndTime(LocalTime newEndTime) {
			endTime = newEndTime;
	}

	public void replace(ClassTime newClassTime) {
		this.type = newClassTime.getType();
		this.days = newClassTime.copyDays();
		this.startTime = newClassTime.getStartTime();
		this.endTime = newClassTime.getEndTime();
	}
	
	/*********Check if Valid**************/
	public boolean isValidDay(String dayIn) {
		boolean result = false;
		String lowerCaseDay = dayIn.toLowerCase();
		if (lowerCaseDay.equals("m")||
			lowerCaseDay.equals("t")||
			lowerCaseDay.equals("w")||
			lowerCaseDay.equals("th")||
			lowerCaseDay.equals("f")||
			lowerCaseDay.equals("sa")||
			lowerCaseDay.equals("su")) {
			result = true;
		}
		
		return result;
	}
	
	public boolean conflictsWith(ClassTime other) {
		List<String> thisDays = this.copyDays();
		List<String> otherDays = other.copyDays();
		
		//Get all common days of both times
		List<String> commonDays = new ArrayList<>(thisDays);
		commonDays.retainAll(otherDays);
		
		return commonDays.size()!=0 && this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
	}
	
	public boolean equivalent(ClassTime other) {
		boolean sameType = this.type.equals(other.type);
		boolean sameDays = this.days.equals(other.days);
		boolean sameTime = this.startTime.equals(other.startTime) && this.endTime.equals(other.endTime);
		return  sameType && sameDays && sameTime;
	}
}
