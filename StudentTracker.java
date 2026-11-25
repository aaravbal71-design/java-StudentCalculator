import java.util.Scanner;

/**
 * Student Grade Tracker - pure Java, 1D arrays only.
 *
 * Features: add students, enter grades, leaderboard (sorted), per-course stats,
 * name search, and an "at risk" report that suggests which subject to focus on.
 *
 * Grades are stored in ONE flat array: the grade for student i in course c
 * lives at grades[i * COURSES + c]. A value of -1 means "not entered yet".
 */
public class StudentTracker {

    static final int MAX = 50;
    static final int COURSES = 4;
    static final String[] COURSE_NAMES = {"Programming", "Math", "Physics", "English"};
    static final String[] COURSE_SHORT = {"Prog", "Math", "Phys", "Eng"};

    static String[] names = new String[MAX];
    static int[] ids = new int[MAX];
    static int[] grades = new int[MAX * COURSES];
    static int count = 0;

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== STUDENT GRADE TRACKER ===");
        boolean running = true;

        while (running) {
            System.out.println("\n1. Add student");
            System.out.println("2. Enter / update a grade");
            System.out.println("3. View all students");
            System.out.println("4. Leaderboard");
            System.out.println("5. Class stats per course");
            System.out.println("6. Search by name");
            System.out.println("7. At-risk report");
            System.out.println("8. Load demo data");
            System.out.println("0. Exit");

            int choice = readInt("Choose: ", 0, 8);
            System.out.println();

            switch (choice) {
                case 1: addStudent(); break;
                case 2: setGrade(); break;
                case 3: viewAll(); break;
                case 4: leaderboard(); break;
                case 5: classStats(); break;
                case 6: search(); break;
                case 7: atRisk(); break;
                case 8: loadDemo(); break;
                case 0:
                    System.out.println("Bye!");
                    running = false;
                    break;
            }
        }
    }

    // ---------- input helpers ----------

    static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v >= min && v <= max) {
                    return v;
                }
            } catch (NumberFormatException e) {
                // fall through to the error message
            }
            System.out.println("Enter a number between " + min + " and " + max + ".");
        }
    }

    static String readName() {
        while (true) {
            System.out.print("Name: ");
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) {
                return s.length() > 15 ? s.substring(0, 15) : s;
            }
            System.out.println("Name can't be empty.");
        }
    }

    // ---------- core logic ----------

    static int findById(int id) {
        for (int i = 0; i < count; i++) {
            if (ids[i] == id) {
                return i;
            }
        }
        return -1;
    }

    static double average(int i) {
        int sum = 0;
        int n = 0;
        for (int c = 0; c < COURSES; c++) {
            int g = grades[i * COURSES + c];
            if (g != -1) {
                sum += g;
                n++;
            }
        }
        return n == 0 ? 0 : (double) sum / n;
    }

    static boolean hasGrades(int i) {
        for (int c = 0; c < COURSES; c++) {
            if (grades[i * COURSES + c] != -1) {
                return true;
            }
        }
        return false;
    }

    // index of the lowest entered grade, or -1 if none
    static int weakestCourse(int i) {
        int worst = -1;
        for (int c = 0; c < COURSES; c++) {
            int g = grades[i * COURSES + c];
            if (g != -1 && (worst == -1 || g < grades[i * COURSES + worst])) {
                worst = c;
            }
        }
        return worst;
    }

    static String letter(double avg) {
        if (avg >= 90) return "A";
        if (avg >= 80) return "B";
        if (avg >= 70) return "C";
        if (avg >= 60) return "D";
        return "F";
    }

    static void addStudentRecord(String name) {
        names[count] = name;
        ids[count] = 2000 + count + 1;
        for (int c = 0; c < COURSES; c++) {
            grades[count * COURSES + c] = -1;
        }
        count++;
    }

    // ---------- menu actions ----------

    static void addStudent() {
        if (count == MAX) {
            System.out.println("Class is full (" + MAX + " students).");
            return;
        }
        String name = readName();
        addStudentRecord(name);
        System.out.println("Added " + name + " with ID " + ids[count - 1] + ".");
    }

    static void setGrade() {
        if (count == 0) {
            System.out.println("No students yet. Add one first.");
            return;
        }
        int id = readInt("Student ID: ", 0, 999999);
        int idx = findById(id);
        if (idx == -1) {
            System.out.println("No student with that ID.");
            return;
        }
        System.out.println("Student: " + names[idx]);
        for (int c = 0; c < COURSES; c++) {
            System.out.println((c + 1) + ". " + COURSE_NAMES[c]);
        }
        int course = readInt("Course: ", 1, COURSES) - 1;
        int grade = readInt("Grade (0-100): ", 0, 100);
        grades[idx * COURSES + course] = grade;
        System.out.println("Saved " + grade + " in " + COURSE_NAMES[course] + " for " + names[idx] + ".");
    }

    static void viewAll() {
        if (count == 0) {
            System.out.println("No students yet.");
            return;
        }
        System.out.printf("%-6s %-15s", "ID", "Name");
        for (int c = 0; c < COURSES; c++) {
            System.out.printf(" %5s", COURSE_SHORT[c]);
        }
        System.out.printf(" %6s %5s%n", "Avg", "Grade");
        System.out.println("-".repeat(6 + 1 + 15 + COURSES * 6 + 7 + 6));

        for (int i = 0; i < count; i++) {
            System.out.printf("%-6d %-15s", ids[i], names[i]);
            for (int c = 0; c < COURSES; c++) {
                int g = grades[i * COURSES + c];
                System.out.printf(" %5s", g == -1 ? "-" : String.valueOf(g));
            }
            if (hasGrades(i)) {
                double avg = average(i);
                System.out.printf(" %6.1f %5s%n", avg, letter(avg));
            } else {
                System.out.printf(" %6s %5s%n", "-", "-");
            }
        }
    }

    static void leaderboard() {
        if (count == 0) {
            System.out.println("No students yet.");
            return;
        }
        // sort an array of student indexes by average (selection sort, high to low)
        int[] order = new int[count];
        for (int i = 0; i < count; i++) {
            order[i] = i;
        }
        for (int i = 0; i < count - 1; i++) {
            int best = i;
            for (int j = i + 1; j < count; j++) {
                if (average(order[j]) > average(order[best])) {
                    best = j;
                }
            }
            int temp = order[i];
            order[i] = order[best];
            order[best] = temp;
        }

        System.out.println("--- LEADERBOARD ---");
        for (int r = 0; r < count; r++) {
            int s = order[r];
            String tag = r == 0 ? " (1st)" : r == 1 ? " (2nd)" : r == 2 ? " (3rd)" : "";
            System.out.printf("%2d. %-15s %6.1f  %s%s%n",
                    r + 1, names[s], average(s), letter(average(s)), tag);
        }
    }

    static void classStats() {
        if (count == 0) {
            System.out.println("No students yet.");
            return;
        }
        for (int c = 0; c < COURSES; c++) {
            int sum = 0;
            int n = 0;
            int topIdx = -1;
            int lowIdx = -1;
            for (int i = 0; i < count; i++) {
                int g = grades[i * COURSES + c];
                if (g == -1) continue;
                sum += g;
                n++;
                if (topIdx == -1 || g > grades[topIdx * COURSES + c]) topIdx = i;
                if (lowIdx == -1 || g < grades[lowIdx * COURSES + c]) lowIdx = i;
            }
            System.out.println(COURSE_NAMES[c] + ":");
            if (n == 0) {
                System.out.println("  no grades yet");
            } else {
                System.out.printf("  average %.1f | highest %d (%s) | lowest %d (%s)%n",
                        (double) sum / n,
                        grades[topIdx * COURSES + c], names[topIdx],
                        grades[lowIdx * COURSES + c], names[lowIdx]);
            }
        }
    }

    static void search() {
        System.out.print("Search name: ");
        String q = sc.nextLine().trim().toLowerCase();
        boolean found = false;
        for (int i = 0; i < count; i++) {
            if (names[i].toLowerCase().contains(q)) {
                found = true;
                if (hasGrades(i)) {
                    System.out.printf("%d - %s (avg %.1f, %s)%n",
                            ids[i], names[i], average(i), letter(average(i)));
                } else {
                    System.out.printf("%d - %s (no grades yet)%n", ids[i], names[i]);
                }
            }
        }
        if (!found) {
            System.out.println("No matches.");
        }
    }

    static void atRisk() {
        boolean any = false;
        System.out.println("--- AT-RISK REPORT (average below 60) ---");
        for (int i = 0; i < count; i++) {
            if (hasGrades(i) && average(i) < 60) {
                any = true;
                int w = weakestCourse(i);
                System.out.printf("%s - avg %.1f. Focus on %s (%d).%n",
                        names[i], average(i), COURSE_NAMES[w], grades[i * COURSES + w]);
            }
        }
        if (!any) {
            System.out.println("Nobody is at risk. Nice class!");
        }
    }

    static void loadDemo() {
        String[] demoNames = {"Aisha", "Liam", "Sofia", "Noah", "Maya", "Ethan"};
        // flat array again: 4 grades per student, in course order
        int[] demoGrades = {
                95, 88, 91, 84,
                72, 65, 58, 70,
                85, 92, 79, 88,
                48, 52, 61, 55,
                90, 76, 83, 94,
                66, 71, 49, 62
        };
        if (count + demoNames.length > MAX) {
            System.out.println("Not enough room for demo data.");
            return;
        }
        for (int s = 0; s < demoNames.length; s++) {
            addStudentRecord(demoNames[s]);
            for (int c = 0; c < COURSES; c++) {
                grades[(count - 1) * COURSES + c] = demoGrades[s * COURSES + c];
            }
        }
        System.out.println("Loaded " + demoNames.length + " demo students.");
    }
}
