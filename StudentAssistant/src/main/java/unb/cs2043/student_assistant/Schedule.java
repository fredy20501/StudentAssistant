package unb.cs2043.student_assistant;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class representing a schedule, containing a list of courses.
 * @author Tye Shutty
 * @author Frederic Verret
 */
@SuppressWarnings("serial")
public class Schedule extends NamedList<Course> implements Comparable<Schedule> {
	
//-------Instance Variables--------//
	
	private static final AtomicLong NEXT_ID = new AtomicLong(0);
	private final long id = NEXT_ID.getAndIncrement();
	
//-------Constructors--------//
	
	public Schedule(String name){super(name);}
	public Schedule(Schedule other){super(other);}
	
//--------Getters---------//
	
	/**
	 * Returns a formatted string showing information about this schedule.
	 * @return A formatted string showing information about this schedule.
	 */
	public String getFormattedString(){
		String description=name+":\n";
		if(list.size()==0){
			description+="empty\n";
		}
		else{
			for(int x=0; x<list.size();x++){
				description+=list.get(x).getFormattedString();
			}
		}
		return description;
	}
	
	/**
	 * Schedules are equal if, and only if, they contain the same courses.
	 * (according to equals() method of courses)
	 * (regardless of the name of the schedules)
	 * @param obj The other schedule.
	 * @return True if both schedules are equal (see above description), false otherwise.
	 */
	@Override
	public boolean equals(Object obj){
		boolean result;
		
		if (obj instanceof Schedule) {
			Schedule sc = (Schedule) obj;
			
			if (this.getSize() != sc.getSize()) {
				//False if don't have same number of courses
				result = false;
			}
			else {
				boolean sameCourses = true;
				for (int i=0; i<this.getSize() && sameCourses; i++) {
					sameCourses = sc.getItem(i).equals(this.getItem(i));
				}
				result = sameCourses;
			}
		} 
		else {
			result = false;
		}
		
		return result;
	}
	
	
	/**
	 * Only returns 0 if they are equal (see equals() method above)
	 * Otherwise, sort by number of courses (most courses first)
	 * (If same number of courses, return 1)
	 * @param other The other schedule.
	 * @return 0 if they are equal, positive integer if this < other, negative integer if this > other.
	 */
	@Override
	public int compareTo(Schedule other) {
		int result;
		if (this.equals(other)) {
			result = 0;
		}
		else {
			result = other.getSize() - this.getSize();
			if (result==0) {
				//Use unique id when have same number of courses
				result = other.id - this.id > 0 ? 1:-1;
			}
		}
		
		return  result;
	}
	
	
	/**
	 * Returns true if all courses in this schedule are present in the other schedule.
	 * Note: Returns false if this and other are equal.
	 * @param other Other schedule.
	 * @return True if all courses in this schedule are present in the other schedule.
	 */
	public boolean isSubsetOf(Schedule other) {
		int index=Collections.indexOfSubList(other.copyList(), list);
		
		return index>=0 && !this.equals(other) ? true : false;
	}
}
