import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// ------------------------------ CLASSES ------------------------------------

// Student Model
class Student {
    String id, name, className, section, parentContact;
    double totalFee, feePaid;

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

    String toCSV() {
        return id + "," + name + "," + className + "," + section + "," + parentContact + "," + totalFee + "," + feePaid;
    }

    static Student fromCSV(String line) {
        String[] parts = line.split(",");
        return new Student(parts[0], parts[1], parts[2], parts[3], parts[4], Double.parseDouble(parts[5]), Double.parseDouble(parts[6]));
    }
}

// Teacher Model
class Teacher {
    String id, name, subject, username, password;
    double salary, salaryPaid;

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
        return new Teacher(parts[0], parts[1], parts[2], parts[3], parts[4], Double.parseDouble(parts[5]), Double.parseDouble(parts[6]));
    }
}

// Attendance Record
class AttendanceRecord {
    String studentId, date, status;

    AttendanceRecord(String studentId, String date, String status) {
        this.studentId = studentId;
        this.date = date;
        this.status = status;
    }

    String toCSV() {
        return studentId + "," + date + "," + status;
    }

    static AttendanceRecord fromCSV(String line) {
        String[] parts = line.split(",");
        return new AttendanceRecord(parts[0], parts[1], parts[2]);
    }
}

// Exam Model
class Exam {
    String examId, examType, className, subject, date;

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

// Marks Model
class Mark {
    String studentId, examId;
    double marksObtained;

    Mark(String studentId, String examId, double marksObtained) {
        this.studentId = studentId;
        this.examId = examId;
        this.marksObtained = marksObtained;
    }

    String toCSV() {
        return studentId + "," + examId + "," + marksObtained;
    }

    static Mark fromCSV(String line) {
        String[] parts = line.split(",");
        return new Mark(parts[0], parts[1], Double.parseDouble(parts[2]));
    }
}

// Expense Model
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

// ------------------------------ SCHOOL CLASS ------------------------------

class School {
    String schoolName;
    String adminPassword;
    String folderPath;

    // Data storage
    HashMap<String, Student> students = new HashMap<>();
    HashMap<String, Teacher> teachers = new HashMap<>();
    ArrayList<AttendanceRecord> attendanceRecords = new ArrayList<>();
    ArrayList<Exam> exams = new ArrayList<>();
    ArrayList<Mark> marks = new ArrayList<>();
    ArrayList<Expense> expenses = new ArrayList<>();

    // Helper maps for class-section grouping
    HashMap<String, ArrayList<String>> classSectionToStudents = new HashMap<>();

    // Constructor
    School(String schoolName, String adminPassword) {
        this.schoolName = schoolName;
        this.adminPassword = adminPassword;
        this.folderPath = "SchoolData_" + schoolName;
        createFolder();
        loadAllData();
    }

    // Function: Create school folder if not exists
    void createFolder() {
        File f = new File(folderPath);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    // Function: Get full file path inside school folder
    String getPath(String fileName) {
        return folderPath + "/" + fileName;
    }

    // ---------- LOAD / SAVE ALL DATA ----------
    void loadAllData() {
        loadStudents();
        loadTeachers();
        loadAttendance();
        loadExams();
        loadMarks();
        loadExpenses();
        rebuildClassSectionMap();
    }

    void saveAllData() {
        saveStudents();
        saveTeachers();
        saveAttendance();
        saveExams();
        saveMarks();
        saveExpenses();
    }

    // Helper: validate date format YYYY-MM-DD
    boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // Function: Load students from CSV
    void loadStudents() {
        File f = new File(getPath("students.csv"));
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                Student s = Student.fromCSV(line);
                students.put(s.id, s);
            }
        } catch (IOException e) {
            System.out.println("Error loading students: " + e.getMessage());
        }
    }

    // Function: Save students to CSV
    void saveStudents() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(getPath("students.csv")))) {
            for (Student s : students.values()) {
                pw.println(s.toCSV());
            }
        } catch (IOException e) {
            System.out.println("Error saving students: " + e.getMessage());
        }
    }

    // Function: Load teachers from CSV
    void loadTeachers() {
        File f = new File(getPath("teachers.csv"));
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                Teacher t = Teacher.fromCSV(line);
                teachers.put(t.id, t);
            }
        } catch (IOException e) {
            System.out.println("Error loading teachers: " + e.getMessage());
        }
    }

    // Function: Save teachers to CSV
    void saveTeachers() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(getPath("teachers.csv")))) {
            for (Teacher t : teachers.values()) {
                pw.println(t.toCSV());
            }
        } catch (IOException e) {
            System.out.println("Error saving teachers: " + e.getMessage());
        }
    }

    // Function: Load attendance records
    void loadAttendance() {
        File f = new File(getPath("attendance.csv"));
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                attendanceRecords.add(AttendanceRecord.fromCSV(line));
            }
        } catch (IOException e) {
            System.out.println("Error loading attendance: " + e.getMessage());
        }
    }

    // Function: Save attendance records
    void saveAttendance() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(getPath("attendance.csv")))) {
            for (AttendanceRecord ar : attendanceRecords) {
                pw.println(ar.toCSV());
            }
        } catch (IOException e) {
            System.out.println("Error saving attendance: " + e.getMessage());
        }
    }

    // Function: Load exams
    void loadExams() {
        File f = new File(getPath("exams.csv"));
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                exams.add(Exam.fromCSV(line));
            }
        } catch (IOException e) {
            System.out.println("Error loading exams: " + e.getMessage());
        }
    }

    // Function: Save exams
    void saveExams() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(getPath("exams.csv")))) {
            for (Exam e : exams) {
                pw.println(e.toCSV());
            }
        } catch (IOException e) {
            System.out.println("Error saving exams: " + e.getMessage());
        }
    }

    // Function: Load marks
    void loadMarks() {
        File f = new File(getPath("marks.csv"));
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                marks.add(Mark.fromCSV(line));
            }
        } catch (IOException e) {
            System.out.println("Error loading marks: " + e.getMessage());
        }
    }

    // Function: Save marks
    void saveMarks() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(getPath("marks.csv")))) {
            for (Mark m : marks) {
                pw.println(m.toCSV());
            }
        } catch (IOException e) {
            System.out.println("Error saving marks: " + e.getMessage());
        }
    }

    // Function: Load expenses
    void loadExpenses() {
        File f = new File(getPath("expenses.csv"));
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                expenses.add(Expense.fromCSV(line));
            }
        } catch (IOException e) {
            System.out.println("Error loading expenses: " + e.getMessage());
        }
    }

    // Function: Save expenses
    void saveExpenses() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(getPath("expenses.csv")))) {
            for (Expense e : expenses) {
                pw.println(e.toCSV());
            }
        } catch (IOException e) {
            System.out.println("Error saving expenses: " + e.getMessage());
        }
    }

    // Function: Rebuild class-section to student IDs map
    void rebuildClassSectionMap() {
        classSectionToStudents.clear();
        for (Student s : students.values()) {
            String key = s.className + "-" + s.section;
            if (!classSectionToStudents.containsKey(key)) {
                classSectionToStudents.put(key, new ArrayList<>());
            }
            classSectionToStudents.get(key).add(s.id);
        }
    }

    // ---------- BUSINESS FUNCTIONS ----------

    // Function: Validate class and section (1-10, A/B/C)
    boolean isValidClassSection(String className, String section) {
        try {
            int c = Integer.parseInt(className);
            if (c < 1 || c > 10) return false;
        } catch (NumberFormatException e) {
            return false;
        }
        return section.equals("A") || section.equals("B") || section.equals("C");
    }

    // Function: Add a student
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
            System.out.println("Total fee must be positive!");
            return;
        }
        Student s = new Student(id, name, className, section, parentContact, totalFee, 0);
        students.put(id, s);
        String key = className + "-" + section;
        if (!classSectionToStudents.containsKey(key)) {
            classSectionToStudents.put(key, new ArrayList<>());
        }
        classSectionToStudents.get(key).add(id);
        saveStudents();
        System.out.println("Student added successfully.");
    }

    // Function: Remove a student
    void removeStudent(String id) {
        if (!students.containsKey(id)) {
            System.out.println("Student not found.");
            return;
        }
        Student s = students.get(id);
        String key = s.className + "-" + s.section;
        if (classSectionToStudents.containsKey(key)) {
            classSectionToStudents.get(key).remove(id);
        }
        students.remove(id);
        saveStudents();
        System.out.println("Student removed.");
    }

    // Function: Edit student details
    void editStudent(String id, String newName, String newClass, String newSection, String newParentContact, double newTotalFee) {
        if (!students.containsKey(id)) {
            System.out.println("Student not found.");
            return;
        }
        Student s = students.get(id);
        if (!isValidClassSection(newClass, newSection)) {
            System.out.println("Invalid class/section.");
            return;
        }
        // Prevent setting total fee lower than already paid
        if (newTotalFee < s.feePaid) {
            System.out.println("New total fee cannot be less than already paid amount (" + s.feePaid + ").");
            return;
        }
        // Remove from old class-section map
        String oldKey = s.className + "-" + s.section;
        if (classSectionToStudents.containsKey(oldKey)) {
            classSectionToStudents.get(oldKey).remove(id);
        }
        // Update student
        s.name = newName;
        s.className = newClass;
        s.section = newSection;
        s.parentContact = newParentContact;
        s.totalFee = newTotalFee;
        // Add to new class-section map
        String newKey = newClass + "-" + newSection;
        if (!classSectionToStudents.containsKey(newKey)) {
            classSectionToStudents.put(newKey, new ArrayList<>());
        }
        classSectionToStudents.get(newKey).add(id);
        saveStudents();
        System.out.println("Student updated.");
    }

    // Function: View all students (sorted by ID)
    void viewAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No students.");
            return;
        }
        System.out.println("\n--- All Students ---");
        ArrayList<String> ids = new ArrayList<>(students.keySet());
        Collections.sort(ids);
        for (String id : ids) {
            Student s = students.get(id);
            System.out.printf("ID: %s | Name: %s | Class: %s-%s | Parent: %s | Fee Paid: %.2f | Outstanding: %.2f\n",
                    s.id, s.name, s.className, s.section, s.parentContact, s.feePaid, s.getOutstanding());
        }
    }

    // Function: View students by class & section
    void viewStudentsByClassSection(String className, String section) {
        String key = className + "-" + section;
        if (!classSectionToStudents.containsKey(key) || classSectionToStudents.get(key).isEmpty()) {
            System.out.println("No students in class " + className + "-" + section);
            return;
        }
        System.out.println("\n--- Students in Class " + className + "-" + section + " ---");
        for (String id : classSectionToStudents.get(key)) {
            Student s = students.get(id);
            System.out.printf("ID: %s | Name: %s | Fee Paid: %.2f | Outstanding: %.2f\n",
                    s.id, s.name, s.feePaid, s.getOutstanding());
        }
    }

    // Function: Add teacher with subject validation (simple global duplicate check)
    void addTeacher(String id, String name, String subject, String username, String password, double salary) {
        if (teachers.containsKey(id)) {
            System.out.println("Teacher ID already exists!");
            return;
        }
        // Check duplicate username
        for (Teacher t : teachers.values()) {
            if (t.username.equals(username)) {
                System.out.println("Username already taken!");
                return;
            }
        }
        // Optional: prevent duplicate subject (globally)
        for (Teacher t : teachers.values()) {
            if (t.subject.equalsIgnoreCase(subject)) {
                System.out.println("A teacher already teaches subject: " + subject + " (global check).");
                return;
            }
        }
        Teacher t = new Teacher(id, name, subject, username, password, salary, 0);
        teachers.put(id, t);
        saveTeachers();
        System.out.println("Teacher added successfully.");
    }

    // Function: Remove teacher
    void removeTeacher(String id) {
        if (!teachers.containsKey(id)) {
            System.out.println("Teacher not found.");
            return;
        }
        teachers.remove(id);
        saveTeachers();
        System.out.println("Teacher removed.");
    }

    // Function: View all teachers
    void viewAllTeachers() {
        if (teachers.isEmpty()) {
            System.out.println("No teachers.");
            return;
        }
        System.out.println("\n--- All Teachers ---");
        for (Teacher t : teachers.values()) {
            System.out.printf("ID: %s | Name: %s | Subject: %s | Username: %s | Salary: %.2f | Paid: %.2f\n",
                    t.id, t.name, t.subject, t.username, t.salary, t.salaryPaid);
        }
    }

    // Function: Search teacher by subject
    void searchTeacherBySubject(String subject) {
        boolean found = false;
        for (Teacher t : teachers.values()) {
            if (t.subject.equalsIgnoreCase(subject)) {
                System.out.printf("ID: %s | Name: %s | Subject: %s\n", t.id, t.name, t.subject);
                found = true;
            }
        }
        if (!found) System.out.println("No teacher found for subject: " + subject);
    }

    // Function: Mark attendance (by teacher, but we'll allow admin too)
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
        // Check if attendance already recorded for this student on this date
        boolean updated = false;
        for (int i = 0; i < attendanceRecords.size(); i++) {
            AttendanceRecord ar = attendanceRecords.get(i);
            if (ar.studentId.equals(studentId) && ar.date.equals(date)) {
                ar.status = status;
                updated = true;
                break;
            }
        }
        if (!updated) {
            attendanceRecords.add(new AttendanceRecord(studentId, date, status));
        }
        saveAttendance();
        System.out.println("Attendance " + (updated ? "updated" : "marked") + ".");
    }

    // Function: Get attendance percentage for a student in date range
    double getStudentAttendancePercentage(String studentId, String startDate, String endDate) {
        int totalDays = 0;
        int presentDays = 0;
        for (AttendanceRecord ar : attendanceRecords) {
            if (ar.studentId.equals(studentId) && ar.date.compareTo(startDate) >= 0 && ar.date.compareTo(endDate) <= 0) {
                totalDays++;
                if (ar.status.equals("Present")) presentDays++;
            }
        }
        if (totalDays == 0) return 0;
        return (presentDays * 100.0) / totalDays;
    }

    // Function: Student-wise attendance report with date filter
    void attendanceReportStudentWise(String startDate, String endDate) {
        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
        }
        System.out.println("\n--- Student-wise Attendance Report (" + startDate + " to " + endDate + ") ---");
        for (Student s : students.values()) {
            double per = getStudentAttendancePercentage(s.id, startDate, endDate);
            System.out.printf("%s (%s) : %.2f%%\n", s.name, s.id, per);
        }
    }

    // Function: Class-wise attendance summary
    void attendanceReportClassWise(String startDate, String endDate) {
        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
        }
        System.out.println("\n--- Class-wise Attendance Summary (" + startDate + " to " + endDate + ") ---");
        HashMap<String, Integer> classTotalStudents = new HashMap<>();
        HashMap<String, Double> classTotalPercentage = new HashMap<>();
        for (Student s : students.values()) {
            String cls = s.className;
            double per = getStudentAttendancePercentage(s.id, startDate, endDate);
            if (!classTotalStudents.containsKey(cls)) {
                classTotalStudents.put(cls, 0);
                classTotalPercentage.put(cls, 0.0);
            }
            classTotalStudents.put(cls, classTotalStudents.get(cls) + 1);
            classTotalPercentage.put(cls, classTotalPercentage.get(cls) + per);
        }
        for (String cls : classTotalStudents.keySet()) {
            int count = classTotalStudents.get(cls);
            double totalPer = classTotalPercentage.get(cls);
            double avg = (count == 0) ? 0 : totalPer / count;
            System.out.printf("Class %s : Average Attendance = %.2f%%\n", cls, avg);
        }
    }

    // Function: Create exam
    void createExam(String examId, String examType, String className, String subject, String date) {
        // Check for duplicate examId
        for (Exam e : exams) {
            if (e.examId.equals(examId)) {
                System.out.println("Exam ID already exists!");
                return;
            }
        }
        if (!isValidDate(date)) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
        }
        exams.add(new Exam(examId, examType, className, subject, date));
        saveExams();
        System.out.println("Exam created.");
    }

    // Function: Enter marks for a student
    void enterMarks(String studentId, String examId, double obtainedMarks) {
        if (!students.containsKey(studentId)) {
            System.out.println("Student not found.");
            return;
        }
        if (obtainedMarks < 0 || obtainedMarks > 100) {
            System.out.println("Marks must be between 0 and 100.");
            return;
        }
        // Check if marks already exist for this student and exam
        boolean updated = false;
        for (int i = 0; i < marks.size(); i++) {
            Mark m = marks.get(i);
            if (m.studentId.equals(studentId) && m.examId.equals(examId)) {
                m.marksObtained = obtainedMarks;
                updated = true;
                break;
            }
        }
        if (!updated) {
            marks.add(new Mark(studentId, examId, obtainedMarks));
        }
        saveMarks();
        System.out.println("Marks " + (updated ? "updated" : "entered") + ".");
    }

    // Function: Get grade based on percentage
    String getGrade(double percentage) {
        if (percentage >= 90) return "A+";
        else if (percentage >= 80) return "A";
        else if (percentage >= 70) return "B";
        else if (percentage >= 60) return "C";
        else if (percentage >= 50) return "D";
        else return "F";
    }

    // Function: Generate student report card
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
        // Get all exams for this student's class
        for (Exam exam : exams) {
            if (exam.className.equals(s.className)) {
                double obtained = -1;
                for (Mark m : marks) {
                    if (m.studentId.equals(studentId) && m.examId.equals(exam.examId)) {
                        obtained = m.marksObtained;
                        break;
                    }
                }
                if (obtained >= 0) {
                    double percent = obtained;
                    String grade = getGrade(percent);
                    System.out.printf("Exam: %s | Subject: %s | Marks: %.2f | Grade: %s\n", exam.examType, exam.subject, obtained, grade);
                } else {
                    System.out.printf("Exam: %s | Subject: %s | Marks: Not entered\n", exam.examType, exam.subject);
                }
            }
        }
        System.out.println("=================================\n");
    }

    // Function: Class performance report for a specific exam
    void classPerformanceReport(String examId) {
        Exam targetExam = null;
        for (Exam e : exams) {
            if (e.examId.equals(examId)) {
                targetExam = e;
                break;
            }
        }
        if (targetExam == null) {
            System.out.println("Exam not found.");
            return;
        }
        System.out.println("\n--- Class Performance for Exam: " + targetExam.examType + " (" + targetExam.subject + ") ---");
        for (Student s : students.values()) {
            if (s.className.equals(targetExam.className)) {
                double obtainedMarks = -1;
                for (Mark m : marks) {
                    if (m.studentId.equals(s.id) && m.examId.equals(examId)) {
                        obtainedMarks = m.marksObtained;
                        break;
                    }
                }
                if (obtainedMarks >= 0) {
                    System.out.printf("%s (%s) : %.2f | Grade: %s\n", s.name, s.id, obtainedMarks, getGrade(obtainedMarks));
                } else {
                    System.out.printf("%s (%s) : Marks not entered\n", s.name, s.id);
                }
            }
        }
    }

    // Function: Pay student fee
    void payStudentFee(String studentId, double amount) {
        if (!students.containsKey(studentId)) {
            System.out.println("Student not found.");
            return;
        }
        Student s = students.get(studentId);
        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }
        double outstanding = s.getOutstanding();
        if (amount > outstanding) {
            System.out.println("Cannot pay more than outstanding. Outstanding: " + outstanding);
            return;
        }
        s.feePaid += amount;
        saveStudents();
        System.out.println("Fee paid. New outstanding: " + s.getOutstanding());
    }

    // Function: Pay teacher salary
    void payTeacherSalary(String teacherId, double amount) {
        if (!teachers.containsKey(teacherId)) {
            System.out.println("Teacher not found.");
            return;
        }
        Teacher t = teachers.get(teacherId);
        double remaining = t.getRemainingSalary();
        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }
        if (amount > remaining) {
            System.out.println("Cannot pay more than remaining salary. Remaining: " + remaining);
            return;
        }
        t.salaryPaid += amount;
        saveTeachers();
        System.out.println("Salary paid to " + t.name + ". Total paid: " + t.salaryPaid);
    }

    // Function: Add other expense
    void addExpense(String name, double amount, String date) {
        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }
        if (!isValidDate(date)) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
        }
        expenses.add(new Expense(name, amount, date));
        saveExpenses();
        System.out.println("Expense added.");
    }

    // Function: Calculate total fees collected
    double getTotalFeesCollected() {
        double total = 0;
        for (Student s : students.values()) {
            total += s.feePaid;
        }
        return total;
    }

    // Function: Calculate total salaries paid
    double getTotalSalariesPaid() {
        double total = 0;
        for (Teacher t : teachers.values()) {
            total += t.salaryPaid;
        }
        return total;
    }

    // Function: Calculate total expenses
    double getTotalExpenses() {
        double total = 0;
        for (Expense e : expenses) {
            total += e.amount;
        }
        return total;
    }

    // Function: Financial report
    void financialReport() {
        double fees = getTotalFeesCollected();
        double salaries = getTotalSalariesPaid();
        double expenses = getTotalExpenses();
        double revenue = fees - salaries;
        double netProfit = fees - (salaries + expenses);
        System.out.println("\n========== FINANCIAL REPORT ==========");
        System.out.printf("Total Fees Collected: %.2f\n", fees);
        System.out.printf("Total Salaries Paid: %.2f\n", salaries);
        System.out.printf("Total Other Expenses: %.2f\n", expenses);
        System.out.printf("Net Revenue (Fees - Salaries): %.2f\n", revenue);
        System.out.printf("Net Profit (Fees - Salaries - Expenses): %.2f\n", netProfit);
        System.out.println("=======================================\n");
    }

    // Function: Fee reminders - list outstanding students with alert message
    void feeReminders() {
        System.out.println("\n--- Fee Reminders & Due Alerts ---");
        boolean any = false;
        for (Student s : students.values()) {
            double outstanding = s.getOutstanding();
            if (outstanding > 0) {
                any = true;
                System.out.println("ALERT: Student " + s.name + " (ID: " + s.id + ") has outstanding fee of " + outstanding);
            }
        }
        if (!any) {
            System.out.println("No outstanding fees.");
        }
    }

    // Function: Promotion - move all students to next class, with fee carry forward option
    void promoteStudents(boolean carryFee, boolean resetTotalFee, double newTotalFee) {
        HashMap<String, Student> newStudents = new HashMap<>();
        for (Student s : students.values()) {
            int currentClass = Integer.parseInt(s.className);
            if (currentClass == 10) {
                // graduate - do not carry forward
                continue;
            }
            int nextClass = currentClass + 1;
            String newClassName = String.valueOf(nextClass);
            String newSection = "A"; // reset to A by default
            double newFeePaid = carryFee ? s.feePaid : 0;
            double newTotal = resetTotalFee ? newTotalFee : s.totalFee;
            Student promoted = new Student(s.id, s.name, newClassName, newSection, s.parentContact, newTotal, newFeePaid);
            newStudents.put(s.id, promoted);
        }
        students.clear();
        students.putAll(newStudents);
        rebuildClassSectionMap();
        saveStudents();
        System.out.println("Promotion completed. Students moved to next class. Fee carry forward: " + (carryFee ? "Yes" : "No"));
        if (resetTotalFee) {
            System.out.println("Total fee reset to: " + newTotalFee);
        } else {
            System.out.println("Total fee unchanged.");
        }
    }

    // Function: Search student by name (contains)
    void searchStudentByName(String name) {
        boolean found = false;
        for (Student s : students.values()) {
            if (s.name.toLowerCase().contains(name.toLowerCase())) {
                System.out.printf("ID: %s | Name: %s | Class: %s-%s\n", s.id, s.name, s.className, s.section);
                found = true;
            }
        }
        if (!found) System.out.println("No student found with name containing: " + name);
    }

    // Function: Search student by class
    void searchStudentByClass(String className) {
        boolean found = false;
        for (Student s : students.values()) {
            if (s.className.equals(className)) {
                System.out.printf("ID: %s | Name: %s | Section: %s\n", s.id, s.name, s.section);
                found = true;
            }
        }
        if (!found) System.out.println("No students in class: " + className);
    }

    // Function: Search student by roll number (ID)
    void searchStudentByRoll(String roll) {
        if (students.containsKey(roll)) {
            Student s = students.get(roll);
            System.out.printf("ID: %s | Name: %s | Class: %s-%s\n", s.id, s.name, s.className, s.section);
        } else {
            System.out.println("Student not found with ID: " + roll);
        }
    }

    // Function: Filter attendance by date range and student name
    void filterAttendanceByDateAndName(String startDate, String endDate, String studentName) {
        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
        }
        System.out.println("\n--- Attendance Filter (Date: " + startDate + " to " + endDate + ", Name: " + studentName + ") ---");
        for (AttendanceRecord ar : attendanceRecords) {
            if (ar.date.compareTo(startDate) >= 0 && ar.date.compareTo(endDate) <= 0) {
                Student s = students.get(ar.studentId);
                if (s != null && s.name.toLowerCase().contains(studentName.toLowerCase())) {
                    System.out.printf("Student: %s (%s) | Date: %s | Status: %s\n", s.name, ar.studentId, ar.date, ar.status);
                }
            }
        }
    }

    // Function: Teacher login validation
    Teacher teacherLogin(String username, String password) {
        for (Teacher t : teachers.values()) {
            if (t.username.equals(username) && t.password.equals(password)) {
                return t;
            }
        }
        return null;
    }
}

// ------------------------------ MAIN APPLICATION ------------------------------

public class SchoolCRM {
    static HashMap<String, School> schoolMap = new HashMap<>();
    static Scanner scanner = new Scanner(System.in);
    static School currentSchool = null;
    static Teacher loggedTeacher = null;

    public static void main(String[] args) {
        loadAllSchoolsCredentials();
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

    // Function: Load all school credentials from file
    static void loadAllSchoolsCredentials() {
        File f = new File("school_credentials.txt");
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0];
                    String pass = parts[1];
                    School school = new School(name, pass);
                    schoolMap.put(name, school);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading school credentials: " + e.getMessage());
        }
    }

    // Function: Save school credentials (called when registering)
    static void saveSchoolCredential(String schoolName, String password) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("school_credentials.txt", true))) {
            pw.println(schoolName + "," + password);
        } catch (IOException e) {
            System.out.println("Error saving credential.");
        }
    }

    // Function: Save all schools data
    static void saveAllSchools() {
        for (School s : schoolMap.values()) {
            s.saveAllData();
        }
    }

    // Function: Register new school
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

    // Function: Login to school (admin or teacher)
    static void login() {
        System.out.print("School Name: ");
        String name = scanner.nextLine();
        System.out.print("Admin Password (leave blank for teacher login): ");
        String pass = scanner.nextLine();
        School school = schoolMap.get(name);
        if (school == null) {
            System.out.println("School not found.");
            return;
        }
        if (!pass.isEmpty() && pass.equals(school.adminPassword)) {
            currentSchool = school;
            loggedTeacher = null;
            System.out.println("Admin login successful. Welcome to " + name);
            return;
        }
        // Try teacher login
        System.out.print("Enter Teacher Username: ");
        String uname = scanner.nextLine();
        System.out.print("Enter Teacher Password: ");
        String upass = scanner.nextLine();
        Teacher t = school.teacherLogin(uname, upass);
        if (t != null) {
            currentSchool = school;
            loggedTeacher = t;
            System.out.println("Teacher login successful. Welcome " + t.name);
        } else {
            System.out.println("Invalid admin password or teacher credentials.");
        }
    }

    // ---------- ADMIN MENU ----------
    static void showAdminMenu() {
        System.out.println("\n===== " + currentSchool.schoolName + " (Admin Dashboard) =====");
        System.out.println("1. Student Management");
        System.out.println("2. Teacher Management");
        System.out.println("3. Attendance Management");
        System.out.println("4. Exam & Marks Management");
        System.out.println("5. Fee & Salary Management");
        System.out.println("6. Expense Tracking");
        System.out.println("7. Reports");
        System.out.println("8. Fee Reminders");
        System.out.println("9. Promotion (Next Class)");
        System.out.println("10. Search & Filter");
        System.out.println("11. Logout");
        System.out.print("Choice: ");
        String ch = scanner.nextLine();
        switch (ch) {
            case "1": adminStudentMenu(); break;
            case "2": adminTeacherMenu(); break;
            case "3": adminAttendanceMenu(); break;
            case "4": adminExamMenu(); break;
            case "5": adminFeeSalaryMenu(); break;
            case "6": adminExpenseMenu(); break;
            case "7": adminReportsMenu(); break;
            case "8": currentSchool.feeReminders(); break;
            case "9": adminPromotionMenu(); break;
            case "10": adminSearchMenu(); break;
            case "11":
                currentSchool.saveAllData();
                currentSchool = null;
                loggedTeacher = null;
                System.out.println("Logged out.");
                break;
            default: System.out.println("Invalid choice.");
        }
    }

    static void adminStudentMenu() {
        while (true) {
            System.out.println("\n--- Student Management ---");
            System.out.println("1. Add Student");
            System.out.println("2. Edit Student");
            System.out.println("3. Remove Student");
            System.out.println("4. View All Students");
            System.out.println("5. View Students by Class & Section");
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
                    continue;
                }
                System.out.print("New Name: "); String name = scanner.nextLine();
                System.out.print("New Class (1-10): "); String cls = scanner.nextLine();
                System.out.print("New Section (A/B/C): "); String sec = scanner.nextLine();
                System.out.print("New Parent Contact: "); String contact = scanner.nextLine();
                System.out.print("New Total Fee: "); double fee = Double.parseDouble(scanner.nextLine());
                currentSchool.editStudent(id, name, cls, sec, contact, fee);
            } else if (opt.equals("3")) {
                System.out.print("Student ID: "); String id = scanner.nextLine();
                currentSchool.removeStudent(id);
            } else if (opt.equals("4")) {
                currentSchool.viewAllStudents();
            } else if (opt.equals("5")) {
                System.out.print("Class (1-10): "); String cls = scanner.nextLine();
                System.out.print("Section (A/B/C): "); String sec = scanner.nextLine();
                currentSchool.viewStudentsByClassSection(cls, sec);
            } else if (opt.equals("6")) {
                break;
            } else {
                System.out.println("Invalid.");
            }
        }
    }

    static void adminTeacherMenu() {
        while (true) {
            System.out.println("\n--- Teacher Management ---");
            System.out.println("1. Add Teacher");
            System.out.println("2. Remove Teacher");
            System.out.println("3. View All Teachers");
            System.out.println("4. Search Teacher by Subject");
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
                break;
            } else {
                System.out.println("Invalid.");
            }
        }
    }

    static void adminAttendanceMenu() {
        while (true) {
            System.out.println("\n--- Attendance Management ---");
            System.out.println("1. Mark Attendance (Student ID, Date, Present/Absent)");
            System.out.println("2. Student-wise Attendance Report (with date range)");
            System.out.println("3. Class-wise Attendance Summary (with date range)");
            System.out.println("4. Back");
            System.out.print("Choice: ");
            String opt = scanner.nextLine();
            if (opt.equals("1")) {
                System.out.print("Student ID: "); String sid = scanner.nextLine();
                System.out.print("Date (YYYY-MM-DD): "); String dt = scanner.nextLine();
                System.out.print("Status (Present/Absent): "); String st = scanner.nextLine();
                currentSchool.markAttendance(sid, dt, st);
            } else if (opt.equals("2")) {
                System.out.print("Start Date (YYYY-MM-DD): "); String sd = scanner.nextLine();
                System.out.print("End Date (YYYY-MM-DD): "); String ed = scanner.nextLine();
                currentSchool.attendanceReportStudentWise(sd, ed);
            } else if (opt.equals("3")) {
                System.out.print("Start Date (YYYY-MM-DD): "); String sd = scanner.nextLine();
                System.out.print("End Date (YYYY-MM-DD): "); String ed = scanner.nextLine();
                currentSchool.attendanceReportClassWise(sd, ed);
            } else if (opt.equals("4")) {
                break;
            } else {
                System.out.println("Invalid.");
            }
        }
    }

    static void adminExamMenu() {
        while (true) {
            System.out.println("\n--- Exam & Marks Management ---");
            System.out.println("1. Create Exam");
            System.out.println("2. Enter Marks for Student");
            System.out.println("3. Generate Student Report Card");
            System.out.println("4. Class Performance Report (by Exam ID)");
            System.out.println("5. Back");
            System.out.print("Choice: ");
            String opt = scanner.nextLine();
            if (opt.equals("1")) {
                System.out.print("Exam ID: "); String eid = scanner.nextLine();
                System.out.print("Exam Type (Test/Mid Term/Final Term): "); String type = scanner.nextLine();
                System.out.print("Class (1-10): "); String cls = scanner.nextLine();
                System.out.print("Subject: "); String sub = scanner.nextLine();
                System.out.print("Date (YYYY-MM-DD): "); String dt = scanner.nextLine();
                currentSchool.createExam(eid, type, cls, sub, dt);
            } else if (opt.equals("2")) {
                System.out.print("Student ID: "); String sid = scanner.nextLine();
                System.out.print("Exam ID: "); String eid = scanner.nextLine();
                System.out.print("Marks (0-100): "); double marks = Double.parseDouble(scanner.nextLine());
                currentSchool.enterMarks(sid, eid, marks);
            } else if (opt.equals("3")) {
                System.out.print("Student ID: "); String sid = scanner.nextLine();
                currentSchool.generateReportCard(sid);
            } else if (opt.equals("4")) {
                System.out.print("Exam ID: "); String eid = scanner.nextLine();
                currentSchool.classPerformanceReport(eid);
            } else if (opt.equals("5")) {
                break;
            } else {
                System.out.println("Invalid.");
            }
        }
    }

    static void adminFeeSalaryMenu() {
        while (true) {
            System.out.println("\n--- Fee & Salary Management ---");
            System.out.println("1. Pay Student Fee");
            System.out.println("2. Pay Teacher Salary");
            System.out.println("3. Financial Report (Revenue, Profit)");
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
                break;
            } else {
                System.out.println("Invalid.");
            }
        }
    }

    static void adminExpenseMenu() {
        System.out.print("Expense Name: ");
        String name = scanner.nextLine();
        System.out.print("Amount: ");
        double amt = Double.parseDouble(scanner.nextLine());
        System.out.print("Date (YYYY-MM-DD): ");
        String dt = scanner.nextLine();
        currentSchool.addExpense(name, amt, dt);
    }

    static void adminReportsMenu() {
        while (true) {
            System.out.println("\n--- Reports ---");
            System.out.println("1. Financial Report");
            System.out.println("2. Student-wise Attendance Report (with date range)");
            System.out.println("3. Class-wise Attendance Summary");
            System.out.println("4. View All Students (sorted)");
            System.out.println("5. Back");
            System.out.print("Choice: ");
            String opt = scanner.nextLine();
            if (opt.equals("1")) {
                currentSchool.financialReport();
            } else if (opt.equals("2")) {
                System.out.print("Start Date (YYYY-MM-DD): "); String sd = scanner.nextLine();
                System.out.print("End Date (YYYY-MM-DD): "); String ed = scanner.nextLine();
                currentSchool.attendanceReportStudentWise(sd, ed);
            } else if (opt.equals("3")) {
                System.out.print("Start Date (YYYY-MM-DD): "); String sd = scanner.nextLine();
                System.out.print("End Date (YYYY-MM-DD): "); String ed = scanner.nextLine();
                currentSchool.attendanceReportClassWise(sd, ed);
            } else if (opt.equals("4")) {
                currentSchool.viewAllStudents();
            } else if (opt.equals("5")) {
                break;
            } else {
                System.out.println("Invalid.");
            }
        }
    }

    static void adminPromotionMenu() {
        System.out.print("Carry forward paid fee? (yes/no): ");
        String carryAns = scanner.nextLine();
        boolean carry = carryAns.equalsIgnoreCase("yes");
        System.out.print("Reset total fee to a new value? (yes/no): ");
        String resetAns = scanner.nextLine();
        boolean reset = resetAns.equalsIgnoreCase("yes");
        double newTotalFee = 0;
        if (reset) {
            System.out.print("Enter new total fee amount for promoted students: ");
            newTotalFee = Double.parseDouble(scanner.nextLine());
        }
        currentSchool.promoteStudents(carry, reset, newTotalFee);
    }

    static void adminSearchMenu() {
        while (true) {
            System.out.println("\n--- Search & Filter ---");
            System.out.println("1. Search Student by Name");
            System.out.println("2. Search Student by Class");
            System.out.println("3. Search Student by Roll Number (ID)");
            System.out.println("4. Filter Attendance by Date Range & Student Name");
            System.out.println("5. Back");
            System.out.print("Choice: ");
            String opt = scanner.nextLine();
            if (opt.equals("1")) {
                System.out.print("Name (partial): "); String nm = scanner.nextLine();
                currentSchool.searchStudentByName(nm);
            } else if (opt.equals("2")) {
                System.out.print("Class (1-10): "); String cls = scanner.nextLine();
                currentSchool.searchStudentByClass(cls);
            } else if (opt.equals("3")) {
                System.out.print("Roll Number (ID): "); String rid = scanner.nextLine();
                currentSchool.searchStudentByRoll(rid);
            } else if (opt.equals("4")) {
                System.out.print("Start Date (YYYY-MM-DD): "); String sd = scanner.nextLine();
                System.out.print("End Date (YYYY-MM-DD): "); String ed = scanner.nextLine();
                System.out.print("Student Name (partial): "); String nm = scanner.nextLine();
                currentSchool.filterAttendanceByDateAndName(sd, ed, nm);
            } else if (opt.equals("5")) {
                break;
            } else {
                System.out.println("Invalid.");
            }
        }
    }

    // ---------- TEACHER MENU ----------
    static void showTeacherMenu() {
        System.out.println("\n===== " + currentSchool.schoolName + " (Teacher: " + loggedTeacher.name + ") =====");
        System.out.println("1. Mark Attendance");
        System.out.println("2. Enter Exam Marks");
        System.out.println("3. View Student Report Card");
        System.out.println("4. View Class Performance Report");
        System.out.println("5. View Attendance Reports");
        System.out.println("6. Logout");
        System.out.print("Choice: ");
        String ch = scanner.nextLine();
        switch (ch) {
            case "1": teacherMarkAttendance(); break;
            case "2": teacherEnterMarks(); break;
            case "3": teacherViewReportCard(); break;
            case "4": teacherClassPerformance(); break;
            case "5": teacherAttendanceReports(); break;
            case "6":
                currentSchool.saveAllData();
                currentSchool = null;
                loggedTeacher = null;
                System.out.println("Logged out.");
                break;
            default: System.out.println("Invalid choice.");
        }
    }

    static void teacherMarkAttendance() {
        System.out.print("Student ID: "); String sid = scanner.nextLine();
        System.out.print("Date (YYYY-MM-DD): "); String dt = scanner.nextLine();
        System.out.print("Status (Present/Absent): "); String st = scanner.nextLine();
        currentSchool.markAttendance(sid, dt, st);
    }

    static void teacherEnterMarks() {
        System.out.print("Exam ID: "); String eid = scanner.nextLine();
        System.out.print("Student ID: "); String sid = scanner.nextLine();
        System.out.print("Marks (0-100): "); double marks = Double.parseDouble(scanner.nextLine());
        currentSchool.enterMarks(sid, eid, marks);
    }

    static void teacherViewReportCard() {
        System.out.print("Student ID: "); String sid = scanner.nextLine();
        currentSchool.generateReportCard(sid);
    }

    static void teacherClassPerformance() {
        System.out.print("Exam ID: "); String eid = scanner.nextLine();
        currentSchool.classPerformanceReport(eid);
    }

    static void teacherAttendanceReports() {
        System.out.print("Start Date (YYYY-MM-DD): "); String sd = scanner.nextLine();
        System.out.print("End Date (YYYY-MM-DD): "); String ed = scanner.nextLine();
        currentSchool.attendanceReportStudentWise(sd, ed);
        currentSchool.attendanceReportClassWise(sd, ed);
    }
}