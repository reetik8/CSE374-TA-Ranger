import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class TARanger {

	/**
	 * 
	 * @param args 
	 * @throws FileNotFoundException Exception thrown if a file is not found
	 */
	@SuppressWarnings("resource")
	public static void main(String args[]) throws FileNotFoundException {

		// Input values from the user
		File profObj = new File("professor");
		Scanner sc2 = new Scanner(profObj);
		System.out.println("Enter Professor's name and desired course number");
		// Example: Mike Stahr
		// Example: CSE174
		Scanner sc = new Scanner(System.in);
		String pName = sc.nextLine();
		String pCourse = sc.nextLine();
		double pDgpa = 0.0;
		String pSections = "";
		String pSync = "";
		String pGrade = "";
		// Get the respective values for the professor
		try {
			while (sc2.hasNextLine()) {
				if (pName.equals(sc2.nextLine()) && pCourse.equals(sc2.nextLine())) {
					pSections = sc2.nextLine();
					pDgpa = Double.parseDouble(sc2.nextLine());
					pGrade = sc2.nextLine();
					pSync = sc2.nextLine();
				}
			}
		} catch (Exception e) {
			System.out.println("Some data fields might be empty");
		}
		if (pDgpa == 0.0) {
			System.out.println("Professor data not found");
			return;
		}
		// Check each student and store the matches in a Map.
		Map<String, Integer> studentMap = new HashMap<>();
		File obj = new File("Student");
		Scanner sc1 = new Scanner(obj);
		int c = 0;
		while (sc1.hasNextLine()) {
			String x = sc1.nextLine();
			if (pCourse.equals(x)) {
				String sName = sc1.nextLine();
				String sGrade = sc1.nextLine();
				double sDgpa = Double.parseDouble(sc1.nextLine());
				String sWork = sc1.nextLine();
				String sSections = sc1.nextLine();
				String sSync = sc1.nextLine();
				String sTime = sc1.nextLine();
				String[] time = new String[10];
				c = 0;
				String w = "";
				for (int i = 0; i < sTime.length(); i++) {
					char ch = sTime.charAt(i);
					if (ch != ' ')
						w = w + ch;
					else {
						time[c++] = w;
						w = "";
					}
				}
				String[] sTimeSlot = new String[c];
				sTimeSlot = Arrays.copyOfRange(time, 0, c);
				// calling evaluate method
				int sRank = EV(sName, sGrade, sDgpa, sWork, sSections, sSync, sTimeSlot, pCourse, pDgpa, pSections,
						pSync, pGrade);
				studentMap.put(sName, sRank);
			}
		}
		// Sort the Map based on the values
		LinkedHashMap<String, Integer> sortedStudentMap = new LinkedHashMap<>();
		studentMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(x -> sortedStudentMap.put(x.getKey(), x.getValue()));
		System.out.println("Following are the best TA applicants (in order) for \nProfessor " + pName + " in course " + pCourse);
		String format = "%-20s %5d\n";
		// Print the sorted values
		for (String name : sortedStudentMap.keySet()) {
			String key = name.toString();
			int value = sortedStudentMap.get(name);
			System.out.format(format, key, value);
		}
	}

	/**
	 * This method calls other methods and calculates the final value for each student.
	 * 
	 * @param sName Student name
	 * @param sGrade Student grade
	 * @param sDgpa Student department Gpa
	 * @param sWork Student work experience
	 * @param sSections Student sections
	 * @param sSync If student is available for sync sections
	 * @param sTimeSlot Student office hours
	 * @param pCourse Professor course
	 * @param pDgpa Minimum Gpa
	 * @param pSections Professor sections
	 * @param pSync If Professor needs a TA during class
	 * @param pGrade Minimum grade required
	 * 
	 * @return The total value for each student
	 */
	public static int EV(String sName, String sGrade, Double sDgpa, String sWork, String sSections, String sSync,
			String[] sTimeSlot, String pCourse, Double pDgpa, String pSections, String pSync, String pGrade) {
		int gpaRank = DGPA(sDgpa, pDgpa);
		int workRank = W(sWork);
		int gradeRank = GM(pGrade, sGrade);
		int timeRank = MA(pSync, pSections, sSync, sTimeSlot);
		int studentRank = gpaRank + workRank + gradeRank + timeRank;
		return studentRank;
	}

	/**
	 * This method calculates the value student receives according to the Gpa.
	 * 
	 * @param sDgpa Students Gpa
	 * 
	 * @param pDgpa Minimum Gpa required
	 * 
	 * @return the value student gets for Gpa
	 */
	public static int DGPA(double sDgpa, double pDgpa) {
		int gpaRank;
		if (sDgpa >= pDgpa)
			gpaRank = 10;
		else
			gpaRank = 0;
		return gpaRank;
	}

	/**
	 * This method calculates the value student receives for work experience.
	 *  
	 * @param sWork Student's work experience if any
	 * 
	 * @return the value student gets for work
	 */
	public static int W(String sWork) {
		int workRank;
		if (sWork.startsWith("work: true"))
			workRank = 20;
		else
			workRank = 0;
		return workRank;
	}

	/**
	 * This method calculates the value student receives for course grade.
	 * 
	 * @param pGrade Minimum grade required
	 * 
	 * @param sGrade Student's grade
	 * 
	 * @return the value student gets for grade.
	 */
	public static int GM(String pGrade, String sGrade) {
		String gradeList[] = { "A", "A-", "B+", "B", "B-", "C+", "C", "C-" };
		int index = 0;
		int sindex = 0; // position of that grade
		int gradeRank;
		for (int i = 0; i < 8; i++) {
			if (pGrade.equals(gradeList[i]))
				index = i;
			if (sGrade.equals(gradeList[i]))
				sindex = i;
		}
		if (sindex <= index)
			gradeRank = 20;
		else if (sindex == index + 1)
			gradeRank = 10;
		else if (sindex == index + 2)
			gradeRank = 5;
		else if (sindex == index + 3)
			gradeRank = 3;
		else
			gradeRank = 0;
		return gradeRank;
	}

	/**
	 * This method calculates the value for synchronous time and office hours.
	 * 
	 * @param pSync if professor needs student during the class time
	 * 
	 * @param pSections what sections professor needs students for, if any
	 * 
	 * @param sSync synchronous times student is available for
	 * 
	 * @param sTimeSlot office hours
	 * 
	 * @return the value student gets
	 */
	public static int MA(String pSync, String pSections, String sSync, String[] sTimeSlot) {
		int timeRank = 0;
		// Calculates the value for synchronous time
		if (pSync.equals("sync: true")) {
			for (int i = 0; i < sSync.length(); i++) {
				String ch = sSync.charAt(i) + "";
				if (pSections.indexOf(ch) != -1)
					timeRank = 50;
			}
			if (timeRank == 0)
				timeRank = -1000;
		}
		// Calculates the value for office hours
		for (int i = 0; i < sTimeSlot.length; i++) {
			int dash = sTimeSlot[i].indexOf('-');
			if (dash != -1) {
				String start = sTimeSlot[i].substring(0, dash);
				String end = sTimeSlot[i].substring(dash + 1);
				int startTime = Integer.parseInt(start);
				int endTime = Integer.parseInt(end);
				if (startTime >= 1100 && endTime <= 2200 && (endTime - startTime) >= 100) {
					timeRank += 50;
					break;
				}
			}
		}
		return timeRank;
	}
}