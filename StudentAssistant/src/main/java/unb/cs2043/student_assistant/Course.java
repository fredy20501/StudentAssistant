package unb.cs2043.student_assistant;

/**
 * Represents a section of a course, containing class times.
 * @author Frederic Verret
 * @author Tye Shutty
 */
@SuppressWarnings("serial")
public class Course extends NamedList<Section> {
	
//-------Instance Variables--------//
	
	private String fullName;
	
//-------Constructors--------//
	
	public Course(String name) {super(name);}
	public Course(Course other) {super(other);}
	
//--------Getters---------//
	
	/**
	 * The full name of the course.
	 * @return The full name of the course.
	 */
	public String getFullName() {
		return fullName;
	}
	
	/**
	 * Returns a formatted string showing information about this course.
	 * @return A formatted string showing information about this course.
	 */
	public String getFormattedString() {
		String description=name+" ("+fullName+"):\n";
		if(list.size()==0) {
			description+="   empty\n";
		}
		else{
			for(int x=0; x<list.size();x++) {
				description+="   "+list.get(x).getFormattedString();
			}
		}
		return description;
	}
	
	/**
	 * Courses are equal if they have the same name and same sections.
	 */
	@Override
	public boolean equals(Object obj){
		boolean result;
		
		if (obj instanceof Course) {
			Course course = (Course) obj;
			
			boolean sameSections = this.copyList().equals(course.copyList());
			boolean sameName = this.getName().equals(course.getName());
			result = sameSections && sameName;
		} 
		else {
			result = false;
		}
		
		return result;
	}
	
//-----------Setters------------//
	
	public boolean setFullName(String fullName){
		this.fullName=fullName;
		return true;
	}
}
