import java.io.*;
import java.util.*;

// ============================================================
//   SCHOOL CRM - DSA Lab Project
//   Data Structures Used:
//   1. LinkedList  - for Attendance Records
//   2. Stack       - for Marks (last entered mark can be undone)
//   3. HashMap     - for Students and Teachers (fast lookup by ID)
// ============================================================


// ======================== NODE FOR LINKED LIST ========================
// Each node holds one attendance record and points to the next node
class AttendanceNode {
    String studentId;
    String date;
    String status; // "Present" or "Absent"
    AttendanceNode next; // pointer to next node

    AttendanceNode(String studentId, String date, String status) {
        this.studentId = studentId;
        this.date = date;
        this.status = status;
        this.next = null;
    }
}

// ======================== LINKED LIST CLASS ========================
// A simple singly linked list to store attendance records
class AttendanceLinkedList {
    AttendanceNode head; // first node

    AttendanceLinkedList() {
        head = null;
    }

    // Add a new attendance record at the end of the list
    void addRecord(String studentId, String date, String status) {
        AttendanceNode newNode = new AttendanceNode(studentId, date, status);

        // If list is empty, new node becomes head
        if (head == null) {
            head = newNode;
            return;
        }

        // Otherwise go to last node and attach new node
        AttendanceNode current = head;
        while (current.next != null) {
            current = current.next;
        }
        current.next = newNode;
    }

    // Update existing record if same student and same date
    // Returns true if updated, false if not found
    boolean updateRecord(String studentId, String date, String status) {
        AttendanceNode current = head;
        while (current != null) {
            if (current.studentId.equals(studentId) && current.date.equals(date)) {
                current.status = status;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // Count total days for a student between two dates
    int countTotalDays(String studentId, String startDate, String endDate) {
        int count = 0;
        AttendanceNode current = head;
        while (current != null) {
            if (current.studentId.equals(studentId)) {
                if (current.date.compareTo(startDate) >= 0 && current.date.compareTo(endDate) <= 0) {
                    count++;
                }
            }
            current = current.next;
        }
        return count;
    }

    // Count present days for a student between two dates
    int countPresentDays(String studentId, String startDate, String endDate) {
        int count = 0;
        AttendanceNode current = head;
        while (current != null) {
            if (current.studentId.equals(studentId) && current.status.equals("Present")) {
                if (current.date.compareTo(startDate) >= 0 && current.date.compareTo(endDate) <= 0) {
                    count++;
                }
            }
            current = current.next;
        }
        return count;
    }
}


// ======================== NODE FOR STACK ========================
// Each node holds one marks record and points to the node below it
class MarksNode {
    String studentId;
    String examId;
    double marksObtained;
    MarksNode below; // points to node below in stack

    MarksNode(String studentId, String examId, double marksObtained) {
        this.studentId = studentId;
        this.examId = examId;
        this.marksObtained = marksObtained;
        this.below = null;
    }
}

// ======================== STACK CLASS ========================
// A simple stack (Last In First Out) to store marks
// Top of stack = most recently entered mark
class MarksStack {
    MarksNode top; // top of stack
    int size;

    MarksStack() {
        top = null;
        size = 0;
    }

    // Push a new marks entry on top of stack
    void push(String studentId, String examId, double marksObtained) {
        MarksNode newNode = new MarksNode(studentId, examId, marksObtained);
        newNode.below = top;
        top = newNode;
        size++;
    }

    // Remove and return top entry (undo last marks entry)
    MarksNode pop() {
        if (top == null) {
            return null;
        }
        MarksNode removed = top;
        top = top.below;
        size--;
        return removed;
    }

    // Check if mark exists for a student in a specific exam
    // If yes, update it; if no, push new entry
    void addOrUpdate(String studentId, String examId, double marksObtained) {
        MarksNode current = top;
        while (current != null) {
            if (current.studentId.equals(studentId) && current.examId.equals(examId)) {
                current.marksObtained = marksObtained; // update existing
                return;
            }
            current = current.below;
        }
        push(studentId, examId, marksObtained); // add new
    }

    // Find marks for a specific student and exam
    // Returns -1 if not found
    double findMarks(String studentId, String examId) {
        MarksNode current = top;
        while (current != null) {
            if (current.studentId.equals(studentId) && current.examId.equals(examId)) {
                return current.marksObtained;
            }
            current = current.below;
        }
        return -1;
    }
}


// ======================== MODEL CLASSES ========================

class Student {
    String id;
    String name;
    String className;
    String section;
    String parentContact;
    double totalFee;
    double feePaid;

    Student(String id, String name, String className, String section, String parentContact, double totalFee, double feePaid) {
        this.id = id;
        this.name = name;
        this.className = className;
        this.section = section;
        this.parentContact = parentContact;
        this.totalFee = totalFee;
        this.feePaid = feePaid;
    }

    double getOutstanding() {
        return totalFee - feePaid;
    }

    // Convert to CSV line for file saving
    String toCSV() {
        return id + "," + name + "," + className + "," + section + "," + parentContact + "," + totalFee + "," + feePaid;
    }

    // Create Student object from a CSV line
    static Student fromCSV(String line) {
        String[] parts = line.split(",");
        return new Student(parts[0], parts[1], parts[2], parts[3], parts[4],
                Double.parseDouble(parts[5]), Double.parseDouble(parts[6]));
    }
}

class Teacher {
    String id;
    String name;
    String subject;
    String username;
    String password;
    double salary;
    double salaryPaid;

    Teacher(String id, String name, String subject, String username, String password, double salary, double salaryPaid) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.username = username;
        this.password = password;
        this.salary = salary;
        this.salaryPaid = salaryPaid;
    }

    double getRemainingSalary() {
        return salary - salaryPaid;
    }

    String toCSV() {
        return id + "," + name + "," + subject + "," + username + "," + password + "," + salary + "," + salaryPaid;
    }

    static Teacher fromCSV(String line) {
        String[] parts = line.split(",");
        return new Teacher(parts[0], parts[1], parts[2], parts[3], parts[4],
                Double.parseDouble(parts[5]), Double.parseDouble(parts[6]));
    }
}

class Exam {
    String examId;
    String examType;
    String className;
    String subject;
    String date;

    Exam(String examId, String examType, String className, String subject, String date) {
        this.examId = examId;
        this.examType = examType;
        this.className = className;
        this.subject = subject;
        this.date = date;
    }

    String toCSV() {
        return examId + "," + examType + "," + className + "," + subject + "," + date;
    }

    static Exam fromCSV(String line) {
        String[] parts = line.split(",");
        return new Exam(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }
}

class Expense {
    String name;
    double amount;
    String date;

    Expense(String name, double amount, String date) {
        this.name = name;
        this.amount = amount;
        this.date = date;
    }

    String toCSV() {
        return name + "," + amount + "," + date;
    }

    static Expense fromCSV(String line) {
        String[] parts = line.split(",");
        return new Expense(parts[0], Double.parseDouble(parts[1]), parts[2]);
    }
}


// ======================== SCHOOL CLASS ========================
class School {
    String schoolName;
    String adminPassword;
    String folderPath;

    // --- DATA STRUCTURES ---
    HashMap<String, Student> students = new HashMap<>();   // key = student ID
    HashMap<String, Teacher> teachers = new HashMap<>();   // key = teacher ID
    AttendanceLinkedList attendanceList = new AttendanceLinkedList(); // Linked List
    MarksStack marksStack = new MarksStack();              // Stack
    ArrayList<Exam> examList = new ArrayList<>();
    ArrayList<Expense> expenseList = new ArrayList<>();

    School(String schoolName, String adminPassword) {
        this.schoolName = schoolName;
        this.adminPassword = adminPassword;
        this.folderPath = "SchoolData_" + schoolName;
        createFolder();
        loadAllData();
    }

    // Create folder for this school's data files
    void createFolder() {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    // Get full path to a file inside school folder
    String getFilePath(String fileName) {
        return folderPath + "/" + fileName;
    }

    // Check if date is in valid format YYYY-MM-DD (simple check)
    boolean isValidDate(String date) {
        if (date.length() != 10) return false;
        if (date.charAt(4) != '-' || date.charAt(7) != '-') return false;
        return true;
    }

    // Check if class is 1-10 and section is A, B, or C
    boolean isValidClassSection(String className, String section) {
        int classNumber;
        try {
            classNumber = Integer.parseInt(className);
        } catch (NumberFormatException e) {
            return false;
        }
        if (classNumber < 1 || classNumber > 10) return false;
        if (!section.equals("A") && !section.equals("B") && !section.equals("C")) return false;
        return true;
    }

    // Get grade from percentage
    String getGrade(double percentage) {
        if (percentage >= 90) return "A+";
        else if (percentage >= 80) return "A";
        else if (percentage >= 70) return "B";
        else if (percentage >= 60) return "C";
        else if (percentage >= 50) return "D";
        else return "F";
    }


    // ============================================================
    //   LOAD / SAVE FUNCTIONS (File Handling with BufferedReader)
    // ============================================================

    void loadAllData() {
        loadStudents();
        loadTeachers();
        loadAttendance();
        loadExams();
        loadMarks();
        loadExpenses();
    }

    void saveAllData() {
        saveStudents();
        saveTeachers();
        saveAttendance();
        saveExams();
        saveMarks();
        saveExpenses();
    }

    void loadStudents() {
        File file = new File(getFilePath("students.csv"));
        if (!file.exists()) return;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Student s = Student.fromCSV(line);
                    students.put(s.id, s);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading students: " + e.getMessage());
        }
    }

    void saveStudents() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(getFilePath("students.csv")));
            for (Student s : students.values()) {
                writer.println(s.toCSV());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving students: " + e.getMessage());
        }
    }

    void loadTeachers() {
        File file = new File(getFilePath("teachers.csv"));
        if (!file.exists()) return;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Teacher t = Teacher.fromCSV(line);
                    teachers.put(t.id, t);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading teachers: " + e.getMessage());
        }
    }

    void saveTeachers() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(getFilePath("teachers.csv")));
            for (Teacher t : teachers.values()) {
                writer.println(t.toCSV());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving teachers: " + e.getMessage());
        }
    }

    // Save attendance: walk the linked list and write each node to file
    void saveAttendance() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(getFilePath("attendance.csv")));
            AttendanceNode current = attendanceList.head;
            while (current != null) {
                writer.println(current.studentId + "," + current.date + "," + current.status);
                current = current.next;
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving attendance: " + e.getMessage());
        }
    }

    // Load attendance: read file line by line and add nodes to linked list
    void loadAttendance() {
        File file = new File(getFilePath("attendance.csv"));
        if (!file.exists()) return;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    attendanceList.addRecord(parts[0], parts[1], parts[2]);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading attendance: " + e.getMessage());
        }
    }

    void loadExams() {
        File file = new File(getFilePath("exams.csv"));
        if (!file.exists()) return;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    examList.add(Exam.fromCSV(line));
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading exams: " + e.getMessage());
        }
    }

    void saveExams() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(getFilePath("exams.csv")));
            for (Exam e : examList) {
                writer.println(e.toCSV());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving exams: " + e.getMessage());
        }
    }

    // Save marks: collect all stack nodes into a temp list, then write to file
    void saveMarks() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(getFilePath("marks.csv")));
            MarksNode current = marksStack.top;
            while (current != null) {
                writer.println(current.studentId + "," + current.examId + "," + current.marksObtained);
                current = current.below;
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving marks: " + e.getMessage());
        }
    }

    // Load marks: read file and push each entry onto the stack
    void loadMarks() {
        File file = new File(getFilePath("marks.csv"));
        if (!file.exists()) return;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    marksStack.push(parts[0], parts[1], Double.parseDouble(parts[2]));
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading marks: " + e.getMessage());
        }
    }

    void loadExpenses() {
        File file = new File(getFilePath("expenses.csv"));
        if (!file.exists()) return;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    expenseList.add(Expense.fromCSV(line));
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading expenses: " + e.getMessage());
        }
    }

    void saveExpenses() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(getFilePath("expenses.csv")));
            for (Expense e : expenseList) {
                writer.println(e.toCSV());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving expenses: " + e.getMessage());
        }
    }


    // ============================================================
    //   STUDENT FUNCTIONS
    // ============================================================

    void addStudent(String id, String name, String className, String section, String parentContact, double totalFee) {
        if (students.containsKey(id)) {
            System.out.println("Student ID already exists!");
            return;
        }
        if (!isValidClassSection(className, section)) {
            System.out.println("Invalid class (1-10) or section (A/B/C)!");
            return;
        }
        if (totalFee <= 0) {
            System.out.println("Total fee must be more than 0!");
            return;
        }
        Student newStudent = new Student(id, name, className, section, parentContact, totalFee, 0);
        students.put(id, newStudent);
        saveStudents();
        System.out.println("Student added successfully.");
    }

    void removeStudent(String id) {
        if (!students.containsKey(id)) {
            System.out.println("Student not found.");
            return;
        }
        students.remove(id);
        saveStudents();
        System.out.println("Student removed.");
    }

    void editStudent(String id, String newName, String newClass, String newSection, String newContact, double newTotalFee) {
        if (!students.containsKey(id)) {
            System.out.println("Student not found.");
            return;
        }
        if (!isValidClassSection(newClass, newSection)) {
            System.out.println("Invalid class or section.");
            return;
        }
        Student s = students.get(id);
        if (newTotalFee < s.feePaid) {
            System.out.println("New total fee cannot be less than already paid amount: " + s.feePaid);
            return;
        }
        s.name = newName;
        s.className = newClass;
        s.section = newSection;
        s.parentContact = newContact;
        s.totalFee = newTotalFee;
        saveStudents();
        System.out.println("Student updated.");
    }

    void viewAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.println("\n--- All Students ---");
        for (String id : students.keySet()) {
            Student s = students.get(id);
            System.out.println("ID: " + s.id + " | Name: " + s.name + " | Class: " + s.className + "-" + s.section
                    + " | Fee Paid: " + s.feePaid + " | Outstanding: " + s.getOutstanding());
        }
    }

    void viewStudentsByClass(String className, String section) {
        boolean found = false;
        System.out.println("\n--- Students in Class " + className + "-" + section + " ---");
        for (String id : students.keySet()) {
            Student s = students.get(id);
            if (s.className.equals(className) && s.section.equals(section)) {
                System.out.println("ID: " + s.id + " | Name: " + s.name + " | Outstanding: " + s.getOutstanding());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No students in this class-section.");
        }
    }

    void searchStudentByName(String name) {
        boolean found = false;
        for (String id : students.keySet()) {
            Student s = students.get(id);
            if (s.name.toLowerCase().contains(name.toLowerCase())) {
                System.out.println("ID: " + s.id + " | Name: " + s.name + " | Class: " + s.className + "-" + s.section);
                found = true;
            }
        }
        if (!found) System.out.println("No student found with name: " + name);
    }

    void searchStudentByClass(String className) {
        boolean found = false;
        for (String id : students.keySet()) {
            Student s = students.get(id);
            if (s.className.equals(className)) {
                System.out.println("ID: " + s.id + " | Name: " + s.name + " | Section: " + s.section);
                found = true;
            }
        }
        if (!found) System.out.println("No students in class: " + className);
    }

    void searchStudentById(String rollId) {
        if (students.containsKey(rollId)) {
            Student s = students.get(rollId);
            System.out.println("ID: " + s.id + " | Name: " + s.name + " | Class: " + s.className + "-" + s.section);
        } else {
            System.out.println("Student not found with ID: " + rollId);
        }
    }


    // ============================================================
    //   TEACHER FUNCTIONS
    // ============================================================

    void addTeacher(String id, String name, String subject, String username, String password, double salary) {
        if (teachers.containsKey(id)) {
            System.out.println("Teacher ID already exists!");
            return;
        }
        // Check if username is already taken
        for (String key : teachers.keySet()) {
            if (teachers.get(key).username.equals(username)) {
                System.out.println("Username already taken!");
                return;
            }
        }
        // Check if subject already assigned to another teacher
        for (String key : teachers.keySet()) {
            if (teachers.get(key).subject.equalsIgnoreCase(subject)) {
                System.out.println("A teacher already teaches: " + subject);
                return;
            }
        }
        Teacher newTeacher = new Teacher(id, name, subject, username, password, salary, 0);
        teachers.put(id, newTeacher);
        saveTeachers();
        System.out.println("Teacher added successfully.");
    }

    void removeTeacher(String id) {
        if (!teachers.containsKey(id)) {
            System.out.println("Teacher not found.");
            return;
        }
        teachers.remove(id);
        saveTeachers();
        System.out.println("Teacher removed.");
    }

    void viewAllTeachers() {
        if (teachers.isEmpty()) {
            System.out.println("No teachers found.");
            return;
        }
        System.out.println("\n--- All Teachers ---");
        for (String id : teachers.keySet()) {
            Teacher t = teachers.get(id);
            System.out.println("ID: " + t.id + " | Name: " + t.name + " | Subject: " + t.subject
                    + " | Salary: " + t.salary + " | Paid: " + t.salaryPaid);
        }
    }

    void searchTeacherBySubject(String subject) {
        boolean found = false;
        for (String id : teachers.keySet()) {
            Teacher t = teachers.get(id);
            if (t.subject.equalsIgnoreCase(subject)) {
                System.out.println("ID: " + t.id + " | Name: " + t.name + " | Subject: " + t.subject);
                found = true;
            }
        }
        if (!found) System.out.println("No teacher found for subject: " + subject);
    }

    Teacher teacherLogin(String username, String password) {
        for (String id : teachers.keySet()) {
            Teacher t = teachers.get(id);
            if (t.username.equals(username) && t.password.equals(password)) {
                return t;
            }
        }
        return null;
    }


    // ============================================================
    //   ATTENDANCE FUNCTIONS  (uses Linked List)
    // ============================================================

    void markAttendance(String studentId, String date, String status) {
        if (!students.containsKey(studentId)) {
            System.out.println("Student not found.");
            return;
        }
        if (!isValidDate(date)) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
        }
        if (!status.equals("Present") && !status.equals("Absent")) {
            System.out.println("Status must be Present or Absent.");
            return;
        }

        // Try to update existing record first
        boolean updated = attendanceList.updateRecord(studentId, date, status);

        if (!updated) {
            // No existing record found, add new node to linked list
            attendanceList.addRecord(studentId, date, status);
        }

        saveAttendance();
        System.out.println("Attendance " + (updated ? "updated." : "marked."));
    }

    // Mark attendance for an ENTIRE class-section, one student at a time.
    // Teacher selects Class + Section + Date, then for every enrolled
    // student (sorted by Roll No / ID) is asked to enter P (Present) or A (Absent).
    void markAttendanceByClass(String className, String section, String date, Scanner scanner) {
        if (!isValidDate(date)) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
        }
        if (!isValidClassSection(className, section)) {
            System.out.println("Invalid class (1-10) or section (A/B/C).");
            return;
        }

        // Collect all students of this class-section, sorted by Roll No (ID)
        ArrayList<Student> classStudents = new ArrayList<>();
        for (String id : students.keySet()) {
            Student s = students.get(id);
            if (s.className.equals(className) && s.section.equals(section)) {
                classStudents.add(s);
            }
        }

        if (classStudents.isEmpty()) {
            System.out.println("No students enrolled in Class " + className + "-" + section + ".");
            return;
        }

        // Sort by Roll No (student ID) so the list is in order
        classStudents.sort((a, b) -> a.id.compareTo(b.id));

        System.out.println("\n--- Marking Attendance: Class " + className + "-" + section + " | Date: " + date + " ---");
        System.out.println("Enter P for Present, A for Absent:\n");

        int presentCount = 0;
        int absentCount = 0;

        for (Student s : classStudents) {
            String status = null;
            while (status == null) {
                System.out.print(s.name + " (Roll No: " + s.id + ") - P/A: ");
                String input = scanner.nextLine().trim().toUpperCase();
                if (input.equals("P")) {
                    status = "Present";
                } else if (input.equals("A")) {
                    status = "Absent";
                } else {
                    System.out.println("Invalid input! Please type P or A only.");
                }
            }

            boolean updated = attendanceList.updateRecord(s.id, date, status);
            if (!updated) {
                attendanceList.addRecord(s.id, date, status);
            }

            if (status.equals("Present")) presentCount++;
            else absentCount++;
        }

        saveAttendance();

        System.out.println("\nAttendance recorded for Class " + className + "-" + section + " on " + date);
        System.out.println("Present: " + presentCount + " | Absent: " + absentCount + " | Total: " + classStudents.size());
    }

    // Calculate and print attendance percentage for one student
    void showStudentAttendance(String studentId, String startDate, String endDate) {
        if (!students.containsKey(studentId)) {
            System.out.println("Student not found.");
            return;
        }
        int totalDays = attendanceList.countTotalDays(studentId, startDate, endDate);
        int presentDays = attendanceList.countPresentDays(studentId, startDate, endDate);

        if (totalDays == 0) {
            System.out.println("No attendance records found for this student in given range.");
            return;
        }

        double percentage = (presentDays * 100.0) / totalDays;
        Student s = students.get(studentId);
        System.out.println(s.name + " (" + studentId + ") : " + presentDays + "/" + totalDays + " = " + String.format("%.2f", percentage) + "%");
    }

    // Print attendance report for all students
    void attendanceReportAllStudents(String startDate, String endDate) {
        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
        }
        System.out.println("\n--- Attendance Report (" + startDate + " to " + endDate + ") ---");
        for (String id : students.keySet()) {
            Student s = students.get(id);
            int total = attendanceList.countTotalDays(id, startDate, endDate);
            int present = attendanceList.countPresentDays(id, startDate, endDate);
            double percent = 0;
            if (total > 0) {
                percent = (present * 100.0) / total;
            }
            System.out.println(s.name + " (" + id + ") : " + String.format("%.2f", percent) + "%");
        }
    }

    // Print class-wise average attendance
    void attendanceReportByClass(String startDate, String endDate) {
        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
        }
        System.out.println("\n--- Class-wise Attendance (" + startDate + " to " + endDate + ") ---");

        HashMap<String, Integer> classCount = new HashMap<>();
        HashMap<String, Double> classTotal = new HashMap<>();

        for (String id : students.keySet()) {
            Student s = students.get(id);
            int total = attendanceList.countTotalDays(id, startDate, endDate);
            int present = attendanceList.countPresentDays(id, startDate, endDate);
            double percent = 0;
            if (total > 0) {
                percent = (present * 100.0) / total;
            }

            if (!classCount.containsKey(s.className)) {
                classCount.put(s.className, 0);
                classTotal.put(s.className, 0.0);
            }
            classCount.put(s.className, classCount.get(s.className) + 1);
            classTotal.put(s.className, classTotal.get(s.className) + percent);
        }

        for (String cls : classCount.keySet()) {
            int count = classCount.get(cls);
            double average = classTotal.get(cls) / count;
            System.out.println("Class " + cls + " : Average = " + String.format("%.2f", average) + "%");
        }
    }

    // Filter attendance by date range and student name
    void filterAttendance(String startDate, String endDate, String studentName) {
        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
        }
        System.out.println("\n--- Filtered Attendance ---");
        AttendanceNode current = attendanceList.head;
        while (current != null) {
            if (current.date.compareTo(startDate) >= 0 && current.date.compareTo(endDate) <= 0) {
                Student s = students.get(current.studentId);
                if (s != null && s.name.toLowerCase().contains(studentName.toLowerCase())) {
                    System.out.println(s.name + " (" + current.studentId + ") | " + current.date + " | " + current.status);
                }
            }
            current = current.next;
        }
    }


    // ============================================================
    //   EXAM & MARKS FUNCTIONS  (Marks uses Stack)
    // ============================================================

    void createExam(String examId, String examType, String className, String subject, String date) {
        // Check for duplicate exam ID
        for (Exam e : examList) {
            if (e.examId.equals(examId)) {
                System.out.println("Exam ID already exists!");
                return;
            }
        }
        if (!isValidDate(date)) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
        }
        examList.add(new Exam(examId, examType, className, subject, date));
        saveExams();
        System.out.println("Exam created.");
    }

    void enterMarks(String studentId, String examId, double obtainedMarks) {
        if (!students.containsKey(studentId)) {
            System.out.println("Student not found.");
            return;
        }
        if (obtainedMarks < 0 || obtainedMarks > 100) {
            System.out.println("Marks must be between 0 and 100.");
            return;
        }
        // addOrUpdate handles both new entry and update in stack
        marksStack.addOrUpdate(studentId, examId, obtainedMarks);
        saveMarks();
        System.out.println("Marks saved.");
    }

    // Undo last marks entry (pop from stack)
    void undoLastMarks() {
        MarksNode removed = marksStack.pop();
        if (removed == null) {
            System.out.println("No marks to undo.");
        } else {
            saveMarks();
            System.out.println("Undone: " + removed.studentId + " | Exam: " + removed.examId + " | Marks: " + removed.marksObtained);
        }
    }

    void generateReportCard(String studentId) {
        if (!students.containsKey(studentId)) {
            System.out.println("Student not found.");
            return;
        }
        Student s = students.get(studentId);
        System.out.println("\n========== REPORT CARD ==========");
        System.out.println("Name: " + s.name + " | ID: " + s.id);
        System.out.println("Class: " + s.className + "-" + s.section);
        System.out.println("Parent Contact: " + s.parentContact);
        System.out.println("Fee Paid: " + s.feePaid + " | Outstanding: " + s.getOutstanding());
        System.out.println("\n--- Exam Results ---");

        for (Exam exam : examList) {
            if (exam.className.equals(s.className)) {
                double obtained = marksStack.findMarks(studentId, exam.examId);
                if (obtained >= 0) {
                    System.out.println("Exam: " + exam.examType + " | Subject: " + exam.subject
                            + " | Marks: " + obtained + " | Grade: " + getGrade(obtained));
                } else {
                    System.out.println("Exam: " + exam.examType + " | Subject: " + exam.subject + " | Marks: Not entered");
                }
            }
        }
        System.out.println("=================================\n");
    }

    void classPerformanceReport(String examId) {
        Exam targetExam = null;
        for (Exam e : examList) {
            if (e.examId.equals(examId)) {
                targetExam = e;
                break;
            }
        }
        if (targetExam == null) {
            System.out.println("Exam not found.");
            return;
        }
        System.out.println("\n--- Class Performance: " + targetExam.examType + " (" + targetExam.subject + ") ---");
        for (String id : students.keySet()) {
            Student s = students.get(id);
            if (s.className.equals(targetExam.className)) {
                double obtained = marksStack.findMarks(id, examId);
                if (obtained >= 0) {
                    System.out.println(s.name + " (" + id + ") : " + obtained + " | Grade: " + getGrade(obtained));
                } else {
                    System.out.println(s.name + " (" + id + ") : Marks not entered");
                }
            }
        }
    }


    // ============================================================
    //   FEE & SALARY FUNCTIONS
    // ============================================================

    void payStudentFee(String studentId, double amount) {
        if (!students.containsKey(studentId)) {
            System.out.println("Student not found.");
            return;
        }
        Student s = students.get(studentId);
        if (amount <= 0) {
            System.out.println("Amount must be more than 0.");
            return;
        }
        double outstanding = s.getOutstanding();
        if (amount > outstanding) {
            System.out.println("Cannot pay more than outstanding: " + outstanding);
            return;
        }
        s.feePaid = s.feePaid + amount;
        saveStudents();
        System.out.println("Fee paid. New outstanding: " + s.getOutstanding());
    }

    void payTeacherSalary(String teacherId, double amount) {
        if (!teachers.containsKey(teacherId)) {
            System.out.println("Teacher not found.");
            return;
        }
        Teacher t = teachers.get(teacherId);
        double remaining = t.getRemainingSalary();
        if (amount <= 0) {
            System.out.println("Amount must be more than 0.");
            return;
        }
        if (amount > remaining) {
            System.out.println("Cannot pay more than remaining salary: " + remaining);
            return;
        }
        t.salaryPaid = t.salaryPaid + amount;
        saveTeachers();
        System.out.println("Salary paid to " + t.name + ". Total paid: " + t.salaryPaid);
    }

    void addExpense(String name, double amount, String date) {
        if (amount <= 0) {
            System.out.println("Amount must be more than 0.");
            return;
        }
        if (!isValidDate(date)) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
        }
        expenseList.add(new Expense(name, amount, date));
        saveExpenses();
        System.out.println("Expense added.");
    }

    void financialReport() {
        double totalFeeCollected = 0;
        double totalSalaryPaid = 0;
        double totalExpenses = 0;

        for (String id : students.keySet()) {
            totalFeeCollected = totalFeeCollected + students.get(id).feePaid;
        }
        for (String id : teachers.keySet()) {
            totalSalaryPaid = totalSalaryPaid + teachers.get(id).salaryPaid;
        }
        for (Expense e : expenseList) {
            totalExpenses = totalExpenses + e.amount;
        }

        double netRevenue = totalFeeCollected - totalSalaryPaid;
        double netProfit = totalFeeCollected - totalSalaryPaid - totalExpenses;

        System.out.println("\n========== FINANCIAL REPORT ==========");
        System.out.println("Total Fees Collected : " + String.format("%.2f", totalFeeCollected));
        System.out.println("Total Salaries Paid  : " + String.format("%.2f", totalSalaryPaid));
        System.out.println("Total Other Expenses : " + String.format("%.2f", totalExpenses));
        System.out.println("Net Revenue          : " + String.format("%.2f", netRevenue));
        System.out.println("Net Profit           : " + String.format("%.2f", netProfit));
        System.out.println("=======================================\n");
    }

    void feeReminders() {
        System.out.println("\n--- Fee Reminders ---");
        boolean anyOutstanding = false;
        for (String id : students.keySet()) {
            Student s = students.get(id);
            if (s.getOutstanding() > 0) {
                System.out.println("ALERT: " + s.name + " (ID: " + s.id + ") owes: " + s.getOutstanding());
                anyOutstanding = true;
            }
        }
        if (!anyOutstanding) {
            System.out.println("No outstanding fees.");
        }
    }

    // Promote all students to next class (class 10 students graduate and are removed)
    void promoteStudents(boolean carryFee, boolean resetFee, double newTotalFee) {
        HashMap<String, Student> updatedStudents = new HashMap<>();

        for (String id : students.keySet()) {
            Student s = students.get(id);
            int currentClass = Integer.parseInt(s.className);

            if (currentClass == 10) {
                // Graduate - do not carry forward
                System.out.println("Graduated: " + s.name);
                continue;
            }

            int nextClass = currentClass + 1;
            double newFeePaid = 0;
            if (carryFee) {
                newFeePaid = s.feePaid;
            }
            double newTotal = s.totalFee;
            if (resetFee) {
                newTotal = newTotalFee;
            }

            Student promoted = new Student(s.id, s.name, String.valueOf(nextClass), "A",
                    s.parentContact, newTotal, newFeePaid);
            updatedStudents.put(id, promoted);
        }

        students.clear();
        students.putAll(updatedStudents);
        saveStudents();
        System.out.println("Promotion done. Class 10 students graduated.");
    }

}


// ======================== MAIN APPLICATION ========================
public class SchoolCRM {

    static HashMap<String, School> schoolMap = new HashMap<>();
    static Scanner scanner = new Scanner(System.in);
    static School currentSchool = null;
    static Teacher loggedTeacher = null;

    public static void main(String[] args) {
        loadSchoolCredentials();

        while (true) {
            if (currentSchool == null) {
                System.out.println("\n===== SCHOOL CRM =====");
                System.out.println("1. Login to School");
                System.out.println("2. Register New School");
                System.out.println("3. Exit");
                System.out.print("Choice: ");
                String choice = scanner.nextLine();

                if (choice.equals("1")) {
                    login();
                } else if (choice.equals("2")) {
                    registerSchool();
                } else if (choice.equals("3")) {
                    saveAllSchools();
                    System.out.println("Goodbye!");
                    return;
                } else {
                    System.out.println("Invalid choice.");
                }
            } else {
                if (loggedTeacher == null) {
                    showAdminMenu();
                } else {
                    showTeacherMenu();
                }
            }
        }
    }

    // Load school names and passwords from file
    static void loadSchoolCredentials() {
        File file = new File("school_credentials.txt");
        if (!file.exists()) return;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0];
                    String pass = parts[1];
                    School school = new School(name, pass);
                    schoolMap.put(name, school);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading credentials: " + e.getMessage());
        }
    }

    // Save one school credential to file (append mode)
    static void saveSchoolCredential(String schoolName, String password) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("school_credentials.txt", true));
            writer.println(schoolName + "," + password);
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving credential.");
        }
    }

    static void saveAllSchools() {
        for (String name : schoolMap.keySet()) {
            schoolMap.get(name).saveAllData();
        }
    }

    static void registerSchool() {
        System.out.print("Enter School Name: ");
        String name = scanner.nextLine();
        if (schoolMap.containsKey(name)) {
            System.out.println("School already exists!");
            return;
        }
        System.out.print("Set Admin Password: ");
        String pass = scanner.nextLine();
        School newSchool = new School(name, pass);
        schoolMap.put(name, newSchool);
        saveSchoolCredential(name, pass);
        System.out.println("School registered successfully!");
    }

    static void login() {
        System.out.print("School Name: ");
        String name = scanner.nextLine();
        School school = schoolMap.get(name);
        if (school == null) {
            System.out.println("School not found.");
            return;
        }

        System.out.print("Admin Password (leave blank to try teacher login): ");
        String pass = scanner.nextLine();

        if (!pass.isEmpty() && pass.equals(school.adminPassword)) {
            currentSchool = school;
            loggedTeacher = null;
            System.out.println("Admin login successful. Welcome to " + name);
            return;
        }

        // Try teacher login
        System.out.print("Teacher Username: ");
        String username = scanner.nextLine();
        System.out.print("Teacher Password: ");
        String tpass = scanner.nextLine();
        Teacher t = school.teacherLogin(username, tpass);

        if (t != null) {
            currentSchool = school;
            loggedTeacher = t;
            System.out.println("Teacher login successful. Welcome " + t.name);
        } else {
            System.out.println("Invalid credentials.");
        }
    }


    // ============================================================
    //   ADMIN MENUS
    // ============================================================

    static void showAdminMenu() {
        System.out.println("\n===== " + currentSchool.schoolName + " - Admin =====");
        System.out.println("1.  Student Management");
        System.out.println("2.  Teacher Management");
        System.out.println("3.  Attendance");
        System.out.println("4.  Exams & Marks");
        System.out.println("5.  Fee & Salary");
        System.out.println("6.  Add Expense");
        System.out.println("7.  Reports");
        System.out.println("8.  Fee Reminders");
        System.out.println("9.  Promote Students");
        System.out.println("10. Search & Filter");
        System.out.println("11. Logout");
        System.out.print("Choice: ");
        String ch = scanner.nextLine();

        if (ch.equals("1")) {
            adminStudentMenu();
        } else if (ch.equals("2")) {
            adminTeacherMenu();
        } else if (ch.equals("3")) {
            adminAttendanceMenu();
        } else if (ch.equals("4")) {
            adminExamMenu();
        } else if (ch.equals("5")) {
            adminFeeSalaryMenu();
        } else if (ch.equals("6")) {
            adminAddExpense();
        } else if (ch.equals("7")) {
            adminReportsMenu();
        } else if (ch.equals("8")) {
            currentSchool.feeReminders();
        } else if (ch.equals("9")) {
            adminPromotionMenu();
        } else if (ch.equals("10")) {
            adminSearchMenu();
        } else if (ch.equals("11")) {
            currentSchool.saveAllData();
            currentSchool = null;
            loggedTeacher = null;
            System.out.println("Logged out.");
        }  else {
            System.out.println("Invalid choice.");
        }
    }

    static void adminStudentMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Student Management ---");
            System.out.println("1. Add Student");
            System.out.println("2. Edit Student");
            System.out.println("3. Remove Student");
            System.out.println("4. View All Students");
            System.out.println("5. View by Class & Section");
            System.out.println("6. Back");
            System.out.print("Choice: ");
            String opt = scanner.nextLine();

            if (opt.equals("1")) {
                System.out.print("ID: "); String id = scanner.nextLine();
                System.out.print("Name: "); String name = scanner.nextLine();
                System.out.print("Class (1-10): "); String cls = scanner.nextLine();
                System.out.print("Section (A/B/C): "); String sec = scanner.nextLine();
                System.out.print("Parent Contact: "); String contact = scanner.nextLine();
                System.out.print("Total Fee: "); double fee = Double.parseDouble(scanner.nextLine());
                currentSchool.addStudent(id, name, cls, sec, contact, fee);
            } else if (opt.equals("2")) {
                System.out.print("Student ID: "); String id = scanner.nextLine();
                if (!currentSchool.students.containsKey(id)) {
                    System.out.println("Student not found.");
                } else {
                    System.out.print("New Name: "); String name = scanner.nextLine();
                    System.out.print("New Class (1-10): "); String cls = scanner.nextLine();
                    System.out.print("New Section (A/B/C): "); String sec = scanner.nextLine();
                    System.out.print("New Parent Contact: "); String contact = scanner.nextLine();
                    System.out.print("New Total Fee: "); double fee = Double.parseDouble(scanner.nextLine());
                    currentSchool.editStudent(id, name, cls, sec, contact, fee);
                }
            } else if (opt.equals("3")) {
                System.out.print("Student ID: "); String id = scanner.nextLine();
                currentSchool.removeStudent(id);
            } else if (opt.equals("4")) {
                currentSchool.viewAllStudents();
            } else if (opt.equals("5")) {
                System.out.print("Class (1-10): "); String cls = scanner.nextLine();
                System.out.print("Section (A/B/C): "); String sec = scanner.nextLine();
                currentSchool.viewStudentsByClass(cls, sec);
            } else if (opt.equals("6")) {
                running = false;
            } else {
                System.out.println("Invalid.");
            }
        }
    }

    static void adminTeacherMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Teacher Management ---");
            System.out.println("1. Add Teacher");
            System.out.println("2. Remove Teacher");
            System.out.println("3. View All Teachers");
            System.out.println("4. Search by Subject");
            System.out.println("5. Back");
            System.out.print("Choice: ");
            String opt = scanner.nextLine();

            if (opt.equals("1")) {
                System.out.print("ID: "); String id = scanner.nextLine();
                System.out.print("Name: "); String name = scanner.nextLine();
                System.out.print("Subject: "); String sub = scanner.nextLine();
                System.out.print("Username: "); String uname = scanner.nextLine();
                System.out.print("Password: "); String pwd = scanner.nextLine();
                System.out.print("Monthly Salary: "); double sal = Double.parseDouble(scanner.nextLine());
                currentSchool.addTeacher(id, name, sub, uname, pwd, sal);
            } else if (opt.equals("2")) {
                System.out.print("Teacher ID: "); String id = scanner.nextLine();
                currentSchool.removeTeacher(id);
            } else if (opt.equals("3")) {
                currentSchool.viewAllTeachers();
            } else if (opt.equals("4")) {
                System.out.print("Subject: "); String sub = scanner.nextLine();
                currentSchool.searchTeacherBySubject(sub);
            } else if (opt.equals("5")) {
                running = false;
            } else {
                System.out.println("Invalid.");
            }
        }
    }

    static void adminAttendanceMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Attendance ---");
            System.out.println("1. Mark Attendance (Class & Section)");
            System.out.println("2. Student-wise Report");
            System.out.println("3. Class-wise Report");
            System.out.println("4. Back");
            System.out.print("Choice: ");
            String opt = scanner.nextLine();

            if (opt.equals("1")) {
                System.out.print("Class (1-10): "); String cls = scanner.nextLine();
                System.out.print("Section (A/B/C): "); String sec = scanner.nextLine();
                System.out.print("Date (YYYY-MM-DD): "); String dt = scanner.nextLine();
                currentSchool.markAttendanceByClass(cls, sec, dt, scanner);
            } else if (opt.equals("2")) {
                System.out.print("Start Date (YYYY-MM-DD): "); String sd = scanner.nextLine();
                System.out.print("End Date (YYYY-MM-DD): "); String ed = scanner.nextLine();
                currentSchool.attendanceReportAllStudents(sd, ed);
            } else if (opt.equals("3")) {
                System.out.print("Start Date (YYYY-MM-DD): "); String sd = scanner.nextLine();
                System.out.print("End Date (YYYY-MM-DD): "); String ed = scanner.nextLine();
                currentSchool.attendanceReportByClass(sd, ed);
            } else if (opt.equals("4")) {
                running = false;
            } else {
                System.out.println("Invalid.");
            }
        }
    }

    static void adminExamMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Exams & Marks ---");
            System.out.println("1. Create Exam");
            System.out.println("2. Enter Marks");
            System.out.println("3. Undo Last Marks Entry");
            System.out.println("4. Generate Report Card");
            System.out.println("5. Class Performance Report");
            System.out.println("6. Back");
            System.out.print("Choice: ");
            String opt = scanner.nextLine();

            if (opt.equals("1")) {
                System.out.print("Exam ID: "); String eid = scanner.nextLine();
                System.out.print("Type (Test/Mid Term/Final Term): "); String type = scanner.nextLine();
                System.out.print("Class (1-10): "); String cls = scanner.nextLine();
                System.out.print("Subject: "); String sub = scanner.nextLine();
                System.out.print("Date (YYYY-MM-DD): "); String dt = scanner.nextLine();
                currentSchool.createExam(eid, type, cls, sub, dt);
            } else if (opt.equals("2")) {
                System.out.print("Student ID: "); String sid = scanner.nextLine();
                System.out.print("Exam ID: "); String eid = scanner.nextLine();
                System.out.print("Marks (0-100): "); double m = Double.parseDouble(scanner.nextLine());
                currentSchool.enterMarks(sid, eid, m);
            } else if (opt.equals("3")) {
                currentSchool.undoLastMarks();
            } else if (opt.equals("4")) {
                System.out.print("Student ID: "); String sid = scanner.nextLine();
                currentSchool.generateReportCard(sid);
            } else if (opt.equals("5")) {
                System.out.print("Exam ID: "); String eid = scanner.nextLine();
                currentSchool.classPerformanceReport(eid);
            } else if (opt.equals("6")) {
                running = false;
            } else {
                System.out.println("Invalid.");
            }
        }
    }

    static void adminFeeSalaryMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Fee & Salary ---");
            System.out.println("1. Pay Student Fee");
            System.out.println("2. Pay Teacher Salary");
            System.out.println("3. Financial Report");
            System.out.println("4. Back");
            System.out.print("Choice: ");
            String opt = scanner.nextLine();

            if (opt.equals("1")) {
                System.out.print("Student ID: "); String sid = scanner.nextLine();
                System.out.print("Amount: "); double amt = Double.parseDouble(scanner.nextLine());
                currentSchool.payStudentFee(sid, amt);
            } else if (opt.equals("2")) {
                System.out.print("Teacher ID: "); String tid = scanner.nextLine();
                System.out.print("Amount: "); double amt = Double.parseDouble(scanner.nextLine());
                currentSchool.payTeacherSalary(tid, amt);
            } else if (opt.equals("3")) {
                currentSchool.financialReport();
            } else if (opt.equals("4")) {
                running = false;
            } else {
                System.out.println("Invalid.");
            }
        }
    }

    static void adminAddExpense() {
        System.out.print("Expense Name: "); String name = scanner.nextLine();
        System.out.print("Amount: "); double amt = Double.parseDouble(scanner.nextLine());
        System.out.print("Date (YYYY-MM-DD): "); String dt = scanner.nextLine();
        currentSchool.addExpense(name, amt, dt);
    }

    static void adminReportsMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Reports ---");
            System.out.println("1. Financial Report");
            System.out.println("2. Student Attendance Report");
            System.out.println("3. Class-wise Attendance Report");
            System.out.println("4. View All Students");
            System.out.println("5. Back");
            System.out.print("Choice: ");
            String opt = scanner.nextLine();

            if (opt.equals("1")) {
                currentSchool.financialReport();
            } else if (opt.equals("2")) {
                System.out.print("Start Date (YYYY-MM-DD): "); String sd = scanner.nextLine();
                System.out.print("End Date (YYYY-MM-DD): "); String ed = scanner.nextLine();
                currentSchool.attendanceReportAllStudents(sd, ed);
            } else if (opt.equals("3")) {
                System.out.print("Start Date (YYYY-MM-DD): "); String sd = scanner.nextLine();
                System.out.print("End Date (YYYY-MM-DD): "); String ed = scanner.nextLine();
                currentSchool.attendanceReportByClass(sd, ed);
            } else if (opt.equals("4")) {
                currentSchool.viewAllStudents();
            } else if (opt.equals("5")) {
                running = false;
            } else {
                System.out.println("Invalid.");
            }
        }
    }

    static void adminPromotionMenu() {
        System.out.print("Carry forward paid fee? (yes/no): ");
        String ans1 = scanner.nextLine();
        boolean carryFee = ans1.equalsIgnoreCase("yes");

        System.out.print("Reset total fee to new amount? (yes/no): ");
        String ans2 = scanner.nextLine();
        boolean resetFee = ans2.equalsIgnoreCase("yes");

        double newFee = 0;
        if (resetFee) {
            System.out.print("New total fee amount: ");
            newFee = Double.parseDouble(scanner.nextLine());
        }
        currentSchool.promoteStudents(carryFee, resetFee, newFee);
    }

    static void adminSearchMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Search & Filter ---");
            System.out.println("1. Search Student by Name");
            System.out.println("2. Search Student by Class");
            System.out.println("3. Search Student by ID");
            System.out.println("4. Filter Attendance by Date & Name");
            System.out.println("5. Back");
            System.out.print("Choice: ");
            String opt = scanner.nextLine();

            if (opt.equals("1")) {
                System.out.print("Name: "); String nm = scanner.nextLine();
                currentSchool.searchStudentByName(nm);
            } else if (opt.equals("2")) {
                System.out.print("Class (1-10): "); String cls = scanner.nextLine();
                currentSchool.searchStudentByClass(cls);
            } else if (opt.equals("3")) {
                System.out.print("Student ID: "); String rid = scanner.nextLine();
                currentSchool.searchStudentById(rid);
            } else if (opt.equals("4")) {
                System.out.print("Start Date (YYYY-MM-DD): "); String sd = scanner.nextLine();
                System.out.print("End Date (YYYY-MM-DD): "); String ed = scanner.nextLine();
                System.out.print("Student Name: "); String nm = scanner.nextLine();
                currentSchool.filterAttendance(sd, ed, nm);
            } else if (opt.equals("5")) {
                running = false;
            } else {
                System.out.println("Invalid.");
            }
        }
    }


    // ============================================================
    //   TEACHER MENUS
    // ============================================================

    static void showTeacherMenu() {
        System.out.println("\n===== " + currentSchool.schoolName + " - Teacher: " + loggedTeacher.name + " =====");
        System.out.println("1. Mark Attendance (Class & Section)");
        System.out.println("2. Enter Exam Marks");
        System.out.println("3. View Student Report Card");
        System.out.println("4. Class Performance Report");
        System.out.println("5. Attendance Reports");
        System.out.println("6. Logout");
        System.out.print("Choice: ");
        String ch = scanner.nextLine();

        if (ch.equals("1")) {
            System.out.print("Class (1-10): "); String cls = scanner.nextLine();
            System.out.print("Section (A/B/C): "); String sec = scanner.nextLine();
            System.out.print("Date (YYYY-MM-DD): "); String dt = scanner.nextLine();
            currentSchool.markAttendanceByClass(cls, sec, dt, scanner);
        } else if (ch.equals("2")) {
            System.out.print("Exam ID: "); String eid = scanner.nextLine();
            System.out.print("Student ID: "); String sid = scanner.nextLine();
            System.out.print("Marks (0-100): "); double m = Double.parseDouble(scanner.nextLine());
            currentSchool.enterMarks(sid, eid, m);
        } else if (ch.equals("3")) {
            System.out.print("Student ID: "); String sid = scanner.nextLine();
            currentSchool.generateReportCard(sid);
        } else if (ch.equals("4")) {
            System.out.print("Exam ID: "); String eid = scanner.nextLine();
            currentSchool.classPerformanceReport(eid);
        } else if (ch.equals("5")) {
            System.out.print("Start Date (YYYY-MM-DD): "); String sd = scanner.nextLine();
            System.out.print("End Date (YYYY-MM-DD): "); String ed = scanner.nextLine();
            currentSchool.attendanceReportAllStudents(sd, ed);
            currentSchool.attendanceReportByClass(sd, ed);
        } else if (ch.equals("6")) {
            currentSchool.saveAllData();
            currentSchool = null;
            loggedTeacher = null;
            System.out.println("Logged out.");
        } else {
            System.out.println("Invalid choice.");
        }
    }
}