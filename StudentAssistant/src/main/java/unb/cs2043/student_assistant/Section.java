package unb.cs2043.student_assistant;

/**
 * Represents a section of a course, containing class times.
 * @author Frederic Verret
 * @author Tye Shutty
 */
@SuppressWarnings("serial")
public class Section extends NamedList<ClassTime>{
	
//-------Constructors--------//
	
	public Section(String name){super(name);}
	public Section(Section other) {super(other);}
	
//--------Getters---------//
	
	/**
	 * Returns a formatted string showing information about this section.
	 * @return A formatted string showing information about this section.
	 */
	public String getFormattedString(){
		String description=name+":\n      ";
		if(list.size()==0){
			description+="empty";
		}
		else{
			for(int x=0; x<list.size();x++){
				//automatically calls toString on ClassTime object
				description+=list.get(x);
				if(x+1<list.size()){
					description+="\n      ";
				}
			}
		}
		return description+"\n";
	}
	
	/**
	 * Checks if any ClassTime in this section conflicts with any ClassTime in the other section.
	 * @param other The other section.
	 * @return True if the sections conflict with each other, false otherwise.
	 */
	public boolean conflictsWith(Section other) {
		boolean conflicting = false;
		
		for (int i=0; i<this.getSize() && !conflicting; i++) {
			for (int j=0; j<other.getSize() && !conflicting; j++) {
				conflicting = this.getItem(i).conflictsWith(other.getItem(j));
			}
		}
		
		return conflicting;
	}
	
	/**
	 * Checks if both sections contains the same class times (same days & times).
	 * @param other The other section.
	 * @return True if both sections have the same class times.
	 */
	public boolean sameClassTimes(Section other) {
		boolean sameClassTimes = true;
		
		if (this.getSize()!=other.getSize()) {
			sameClassTimes = false;
		}
		
		for (int i=0; i<this.getSize() && sameClassTimes; i++) {
			if (!this.getItem(i).equivalent(other.getItem(i))) {
				sameClassTimes = false;
			}
		}
		
		return sameClassTimes;
	}
}
