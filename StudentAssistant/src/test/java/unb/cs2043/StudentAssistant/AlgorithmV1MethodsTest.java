/**
 * 
 */
package unb.cs2043.StudentAssistant;

import static org.junit.Assert.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import unb.cs2043.student_assistant.ClassTime;
import unb.cs2043.student_assistant.Course;
import unb.cs2043.student_assistant.Schedule;
import unb.cs2043.student_assistant.Section;
import unb.cs2043.student_assistant.AlgorithmV1;

/**
 * Tests the component methods of algorithm version 1.
 * @author frede
 */
public class AlgorithmV1MethodsTest {
	
	private LocalTime time(int hr, int min) {
		return LocalTime.of(hr, min);
	}
	
	@Test
	public void testNoConflictsBetween() {
		ArrayList<String> days = new ArrayList<>();
		days.add("M");
		ClassTime time1 = new ClassTime("Lab", days, time(21,30), time(22,30));
		Section sec1 = new Section("S1");
		sec1.add(time1);
		Course c1 = new Course("C1");
		c1.add(sec1);
		
		ClassTime time2 = new ClassTime("Lab", days, time(5, 00), time(7, 00));
		Section sec2 = new Section("S2");
		sec2.add(time2);
		Course c2 = new Course("C2");
		c2.add(sec2);
		
		Schedule schedule = new Schedule("Schedule");
		schedule.add(c1);
		schedule.add(c2);
		
		//Same section
		assertEquals(false, AlgorithmV1.noConflictsBetween(schedule, sec2));
		
		ClassTime time3 = new ClassTime("Lab", days, time(2, 00), time(3, 00));
		Section sec3 = new Section("S3");
		sec3.add(time3);
		
		//No conflict
		assertEquals(true, AlgorithmV1.noConflictsBetween(schedule, sec3));
		
		
		/*
		 * This test does not test many cases as this method
		 * mostly relies on conflictsWith() method of Section class.
		 * (ie, as long as conflictsWith() works, this method should work)
		 */
	}
	
	
	@Test
	public void testIncrementAsCounter() {
		
		int[] indexes = {0, 0, 0};
		int[] maxIndexes = {2, 3, 1};
		
		int[][] steps = {
				{0, 0, 0},
				{1, 0, 0},
				{2, 0, 0},
				{0, 1, 0},
				{1, 1, 0},
				{2, 1, 0},
				{0, 2, 0},
				{1, 2, 0},
				{2, 2, 0},
				{0, 3, 0},
				{1, 3, 0},
				{2, 3, 0},
				{0, 0, 1},
				{1, 0, 1},
				{2, 0, 1},
				{0, 1, 1},
				{1, 1, 1},
				{2, 1, 1},
				{0, 2, 1},
				{1, 2, 1},
				{2, 2, 1},
				{0, 3, 1},
				{1, 3, 1},
				{2, 3, 1},	//Max
				{0, 0, 0}
		};
		
		for (int i=0; i<steps.length; i++) {
//			System.out.println(Arrays.toString(indexes));
			assertEquals(true, Arrays.equals(steps[i], indexes));
			AlgorithmV1.incrementAsCounter(indexes, maxIndexes);
		}
	}
	
	
	@Test
	public void testFactorial() {
		assertEquals(120, AlgorithmV1.factorial(5));
		assertEquals(40320, AlgorithmV1.factorial(8));
		assertEquals(2432902008176640000L, AlgorithmV1.factorial(20));
		assertEquals(1, AlgorithmV1.factorial(0));
		
		//Overflow
		boolean error = false;
		try {
			long result = AlgorithmV1.factorial(21);
			System.out.println(result);
		}
		catch (ArithmeticException e) {
			error = true;
		}
		assertEquals(true, error);
	}
}
