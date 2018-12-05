package unb.cs2043.StudentAssistant;

import static org.junit.Assert.*;

import org.junit.Test;

import unb.cs2043.student_assistant.Course;
import unb.cs2043.student_assistant.Schedule;
import unb.cs2043.student_assistant.Section;

/**
 * Tests the equals() method of schedule.
 * @author Frederic Verret
 */
public class EqualsTest {
	
	@Test
	public void ScheduleEqualsTest() {
		
		Schedule sc = new Schedule("1");
		Schedule sc2 = new Schedule("2");
		sc.add(new Course("test"));
		sc2.add(new Course("test"));
		
		assertEquals(false, sc==sc2);
		assertEquals(true, sc.equals(sc2));
		assertEquals(false, sc.copyList()==sc2.copyList());
		assertEquals(true, sc.copyList().equals(sc2.copyList()));
		
		Schedule sc3 = new Schedule("");
		assertEquals(false, sc.equals(sc3));
	}
	
	@Test
	public void CourseEqualsTest() {
		
		Course c1 = new Course("1");
		Course c2 = new Course("1");
		Course c3 = new Course("1");
		Section section = new Section("test");
		c1.add(section);
		c2.add(section);
		c3.add(new Section("test"));
		
		assertEquals(false, c1==c2);
		assertEquals(true, c1.equals(c2));
		assertEquals(false, c1.copyList()==c2.copyList());
		assertEquals(true, c1.copyList().equals(c2.copyList()));
		
		Course c4 = new Course("");
		assertEquals(false, c1.equals(c4));
	}
}
