import java.io.*;
import java.util.*;

class Student {
    String id;
    String name;
    String className;
    String section;
    String parentContact;
    double totalFee;
    double feePaid;

    Student(String id, String name, String className, String section,
            String parentContact, double totalFee, double feePaid) {
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

    double getPaymentPercentage() {
        return (feePaid * 100.0) / totalFee;
    }

    String getPaymentStatus() {
        double outstanding = getOutstanding();
        if (outstanding <= 0) {
            return "PAID";
        } else if (outstanding == totalFee) {
            return "NOT PAID";
        } else {
            return "PARTIAL";
        }
    }

    String toCSV() {
        return id + "," + name + "," + className + "," + section + ","
                + parentContact + "," + totalFee + "," + feePaid;
    }

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

    Teacher(String id, String name, String subject, String username,
            String password, double salary, double salaryPaid) {
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
        return id + "," + name + "," + subject + "," + username + ","
                + password + "," + salary + "," + salaryPaid;
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

class AttendanceRecord {
    String studentId;
    String date;
    String status;

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

class MarksRecord {
    String studentId;
    String examId;
    double marksObtained;

    MarksRecord(String studentId, String examId, double marksObtained) {
        this.studentId = studentId;
        this.examId = examId;
        this.marksObtained = marksObtained;
    }

    String toCSV() {
        return studentId + "," + examId + "," + marksObtained;
    }

    static MarksRecord fromCSV(String line) {
        String[] parts = line.split(",");
        return new MarksRecord(parts[0], parts[1], Double.parseDouble(parts[2]));
    }
}

class School {
    String schoolName;
    String adminPassword;
    String folderPath;

    HashMap<String, Student> students = new HashMap<>();
    HashMap<String, Teacher> teachers = new HashMap<>();

    ArrayList<AttendanceRecord> attendanceList = new ArrayList<>();
    ArrayList<MarksRecord> marksList = new ArrayList<>();
    ArrayList<Exam> examList = new ArrayList<>();
    ArrayList<Expense> expenseList = new ArrayList<>();

    School(String schoolName, String adminPassword) {
        this.schoolName = schoolName;
        this.adminPassword = adminPassword;
        this.folderPath = "SchoolData_" + schoolName;
        createFolder();
        loadAllData();
    }

    void createFolder() {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    String getFilePath(String fileName) {
        return folderPath + "/" + fileName;
    }

    boolean isValidDate(String date) {
        if (date == null) return false;
        if (date.length() != 10) return false;
        if (date.charAt(4) != '-' || date.charAt(7) != '-') return false;
        return true;
    }

    boolean isValidClassSection(String className, String section) {
        try {
            int classNumber = Integer.parseInt(className);
            if (classNumber >= 1 && classNumber <= 10) {
                if (section.equals("A") || section.equals("B") || section.equals("C")) {
                    return true;
                }
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    String getGrade(double percentage) {
        if (percentage >= 90) return "A+";
        else if (percentage >= 80) return "A";
        else if (percentage >= 70) return "B";
        else if (percentage >= 60) return "C";
        else if (percentage >= 50) return "D";
        else return "F";
    }

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

    void loadAttendance() {
        File file = new File(getFilePath("attendance.csv"));
        if (!file.exists()) return;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    AttendanceRecord record = AttendanceRecord.fromCSV(line);
                    attendanceList.add(record);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading attendance: " + e.getMessage());
        }
    }

    void saveAttendance() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(getFilePath("attendance.csv")));
            for (AttendanceRecord record : attendanceList) {
                writer.println(record.toCSV());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving attendance: " + e.getMessage());
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
                    Exam e = Exam.fromCSV(line);
                    examList.add(e);
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

    void loadMarks() {
        File file = new File(getFilePath("marks.csv"));
        if (!file.exists()) return;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    MarksRecord record = MarksRecord.fromCSV(line);
                    marksList.add(record);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading marks: " + e.getMessage());
        }
    }

    void saveMarks() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(getFilePath("marks.csv")));
            for (MarksRecord record : marksList) {
                writer.println(record.toCSV());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving marks: " + e.getMessage());
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
                    Expense e = Expense.fromCSV(line);
                    expenseList.add(e);
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
    //   NEW FEE MANAGEMENT FUNCTIONS
    // ============================================================

    void viewStudentsWithOutstandingFees() {
        ArrayList<Student> defaulters = new ArrayList<>();

        for (Student s : students.values()) {
            if (s.getOutstanding() > 0) {
                defaulters.add(s);
            }
        }

        if (defaulters.isEmpty()) {
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("|     No students with outstanding fees!              |");
            System.out.println("|     All students have paid their fees.              |");
            System.out.println("|─────────────────────────────────────────────────────|");
            return;
        }

        // Sort by outstanding amount (highest first)
        for (int i = 0; i < defaulters.size() - 1; i++) {
            for (int j = 0; j < defaulters.size() - i - 1; j++) {
                if (defaulters.get(j).getOutstanding() < defaulters.get(j + 1).getOutstanding()) {
                    Student temp = defaulters.get(j);
                    defaulters.set(j, defaulters.get(j + 1));
                    defaulters.set(j + 1, temp);
                }
            }
        }

        double totalOutstanding = 0;
        for (Student s : defaulters) {
            totalOutstanding += s.getOutstanding();
        }

        System.out.println("|─────────────────────────────────────────────────────────────────────────────────|");
        System.out.println("|                    STUDENTS WITH OUTSTANDING FEES                                |");
        System.out.println("|─────────────────────────────────────────────────────────────────────────────────|");
        System.out.printf("| Total Defaulters: %-56d |\n", defaulters.size());
        System.out.printf("| Total Outstanding Amount: Rs. %-43.2f |\n", totalOutstanding);
        System.out.println("|─────────────────────────────────────────────────────────────────────────────────|");
        System.out.println("| ID        | Name                       | Class    | Total Fee | Paid      | Outstanding | Status    |");
        System.out.println("|─────────────────────────────────────────────────────────────────────────────────|");

        for (Student s : defaulters) {
            String status = s.getPaymentStatus();
            System.out.printf("| %-8s | %-25s | %-8s | Rs. %-7.2f | Rs. %-7.2f | Rs. %-9.2f | %-9s |\n",
                    s.id, truncate(s.name, 25), s.className + "-" + s.section,
                    s.totalFee, s.feePaid, s.getOutstanding(), status);
        }
        System.out.println("|─────────────────────────────────────────────────────────────────────────────────|");
    }

    void viewStudentsWithFullPayment() {
        ArrayList<Student> paidStudents = new ArrayList<>();

        for (Student s : students.values()) {
            if (s.getOutstanding() <= 0) {
                paidStudents.add(s);
            }
        }

        if (paidStudents.isEmpty()) {
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("|     No students have fully paid their fees!         |");
            System.out.println("|─────────────────────────────────────────────────────|");
            return;
        }

        double totalCollected = 0;
        for (Student s : paidStudents) {
            totalCollected += s.feePaid;
        }

        System.out.println("|─────────────────────────────────────────────────────────────────────────────|");
        System.out.println("|                    STUDENTS WITH FULL PAYMENT                                 |");
        System.out.println("|─────────────────────────────────────────────────────────────────────────────|");
        System.out.printf("| Total Students (Fully Paid): %-52d |\n", paidStudents.size());
        System.out.printf("| Total Fee Collected: Rs. %-46.2f |\n", totalCollected);
        System.out.println("|─────────────────────────────────────────────────────────────────────────────|");
        System.out.println("| ID        | Name                       | Class    | Total Fee | Paid        |");
        System.out.println("|─────────────────────────────────────────────────────────────────────────────|");

        for (Student s : paidStudents) {
            System.out.printf("| %-8s | %-25s | %-8s | Rs. %-7.2f | Rs. %-10.2f |\n",
                    s.id, truncate(s.name, 25), s.className + "-" + s.section, s.totalFee, s.feePaid);
        }
        System.out.println("|─────────────────────────────────────────────────────────────────────────────|");
    }

    void viewStudentsWithPartialPayment() {
        ArrayList<Student> partialStudents = new ArrayList<>();

        for (Student s : students.values()) {
            double outstanding = s.getOutstanding();
            if (outstanding > 0 && outstanding < s.totalFee) {
                partialStudents.add(s);
            }
        }

        if (partialStudents.isEmpty()) {
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("|     No students with partial payments!              |");
            System.out.println("|─────────────────────────────────────────────────────|");
            return;
        }

        System.out.println("|─────────────────────────────────────────────────────────────────────────────────|");
        System.out.println("|                    STUDENTS WITH PARTIAL PAYMENTS                                |");
        System.out.println("|─────────────────────────────────────────────────────────────────────────────────|");
        System.out.printf("| Total Students (Partial): %-54d |\n", partialStudents.size());
        System.out.println("|─────────────────────────────────────────────────────────────────────────────────|");
        System.out.println("| ID        | Name                       | Class    | Total Fee | Paid      | Remaining  | % Paid     |");
        System.out.println("|─────────────────────────────────────────────────────────────────────────────────|");

        for (Student s : partialStudents) {
            double percentage = s.getPaymentPercentage();
            System.out.printf("| %-8s | %-25s | %-8s | Rs. %-7.2f | Rs. %-7.2f | Rs. %-8.2f | %6.2f%%   |\n",
                    s.id, truncate(s.name, 25), s.className + "-" + s.section,
                    s.totalFee, s.feePaid, s.getOutstanding(), percentage);
        }
        System.out.println("|─────────────────────────────────────────────────────────────────────────────────|");
    }

    void viewCompleteFeeStatus() {
        if (students.isEmpty()) {
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("|     No students found in the system!                |");
            System.out.println("|─────────────────────────────────────────────────────|");
            return;
        }

        int fullyPaid = 0;
        int partial = 0;
        int notPaid = 0;
        double totalFeeCollected = 0;
        double totalFeeExpected = 0;

        for (Student s : students.values()) {
            totalFeeCollected += s.feePaid;
            totalFeeExpected += s.totalFee;

            if (s.getOutstanding() <= 0) {
                fullyPaid++;
            } else if (s.feePaid == 0) {
                notPaid++;
            } else {
                partial++;
            }
        }

        double collectionPercentage = (totalFeeCollected * 100.0) / totalFeeExpected;

        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.println("|                    COMPLETE FEE STATUS REPORT                   |");
        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.printf("| Total Students: %-67d |\n", students.size());
        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.printf("| Fully Paid Students    : %-3d (%.1f%% of total) %-30s |\n",
                fullyPaid, (fullyPaid * 100.0) / students.size(), "");
        System.out.printf("| Partial Payment Students: %-3d (%.1f%% of total) %-30s |\n",
                partial, (partial * 100.0) / students.size(), "");
        System.out.printf("| No Payment Students     : %-3d (%.1f%% of total) %-30s |\n",
                notPaid, (notPaid * 100.0) / students.size(), "");
        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.printf("| Total Fee Expected      : Rs. %-51.2f |\n", totalFeeExpected);
        System.out.printf("| Total Fee Collected     : Rs. %-51.2f |\n", totalFeeCollected);
        System.out.printf("| Total Outstanding       : Rs. %-51.2f |\n", totalFeeExpected - totalFeeCollected);
        System.out.printf("| Collection Percentage   : %.2f%% %-48s |\n", collectionPercentage, "");
        System.out.println("|─────────────────────────────────────────────────────────────────|");

        if (collectionPercentage < 50) {
            System.out.println("| ⚠ WARNING: Fee collection is below 50%%!                              |");
        } else if (collectionPercentage < 75) {
            System.out.println("| ⚠ Notice: Fee collection is below 75%%. Consider reminders.           |");
        } else if (collectionPercentage >= 90) {
            System.out.println("| ✓ Excellent: Fee collection is above 90%%!                             |");
        }
        System.out.println("|─────────────────────────────────────────────────────────────────|");
    }

    void viewFeeStatusByClass(String className) {
        boolean classExists = false;
        int fullyPaid = 0;
        int partial = 0;
        int notPaid = 0;
        double totalClassFee = 0;
        double totalClassPaid = 0;

        System.out.println("|─────────────────────────────────────────────────────────────────────────────|");
        System.out.println("|                    FEE STATUS FOR CLASS " + className + "                                 |");
        System.out.println("|─────────────────────────────────────────────────────────────────────────────|");
        System.out.println("| ID        | Name                       | Section | Paid      | Outstanding | Status    |");
        System.out.println("|─────────────────────────────────────────────────────────────────────────────|");

        for (Student s : students.values()) {
            if (s.className.equals(className)) {
                classExists = true;
                totalClassFee += s.totalFee;
                totalClassPaid += s.feePaid;

                if (s.getOutstanding() <= 0) {
                    fullyPaid++;
                } else if (s.feePaid == 0) {
                    notPaid++;
                } else {
                    partial++;
                }

                System.out.printf("| %-8s | %-25s | %-7s | Rs. %-7.2f | Rs. %-9.2f | %-9s |\n",
                        s.id, truncate(s.name, 25), s.section, s.feePaid, s.getOutstanding(), s.getPaymentStatus());
            }
        }

        if (!classExists) {
            System.out.println("| No students found in class " + className + "!                                    |");
        } else {
            System.out.println("|─────────────────────────────────────────────────────────────────────────────|");
            System.out.printf("| Class Summary:                                                                 |\n");
            System.out.printf("| Fully Paid: %d | Partial: %d | Not Paid: %d                                   |\n", fullyPaid, partial, notPaid);
            System.out.printf("| Total Expected: Rs. %-8.2f | Collected: Rs. %-8.2f | Outstanding: Rs. %-8.2f |\n",
                    totalClassFee, totalClassPaid, totalClassFee - totalClassPaid);
        }
        System.out.println("|─────────────────────────────────────────────────────────────────────────────|");
    }

    void sendFeeRemindersToDefaulters() {
        ArrayList<Student> defaulters = new ArrayList<>();

        for (Student s : students.values()) {
            if (s.getOutstanding() > 0) {
                defaulters.add(s);
            }
        }

        if (defaulters.isEmpty()) {
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("|     No defaulters found! All fees are paid.         |");
            System.out.println("|─────────────────────────────────────────────────────|");
            return;
        }

        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.println("|                    FEE REMINDERS SENT                           |");
        System.out.println("|─────────────────────────────────────────────────────────────────|");

        for (Student s : defaulters) {
            System.out.printf("| 📧 Reminder sent to: %-25s (Parent: %-12s) | Outstanding: Rs. %-8.2f |\n",
                    truncate(s.name, 25), s.parentContact, s.getOutstanding());
        }
        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.printf("| Total reminders sent: %-57d |\n", defaulters.size());
        System.out.println("|─────────────────────────────────────────────────────────────────|");
    }

    void markAttendanceByClass(String className, String section, String date, Scanner scanner) {
        if (!isValidDate(date)) {
            System.out.println("| Invalid date format.                              |");
            return;
        }

        if (!isValidClassSection(className, section)) {
            System.out.println("| Invalid class or section.                         |");
            return;
        }

        ArrayList<Student> classStudents = new ArrayList<>();
        for (Student s : students.values()) {
            if (s.className.equals(className) && s.section.equals(section)) {
                classStudents.add(s);
            }
        }

        // Sort students by ID
        for (int i = 0; i < classStudents.size() - 1; i++) {
            for (int j = 0; j < classStudents.size() - i - 1; j++) {
                if (classStudents.get(j).id.compareTo(classStudents.get(j + 1).id) > 0) {
                    Student temp = classStudents.get(j);
                    classStudents.set(j, classStudents.get(j + 1));
                    classStudents.set(j + 1, temp);
                }
            }
        }

        if (classStudents.isEmpty()) {
            System.out.println("| No students in this class.                        |");
            return;
        }

        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|     MARKING ATTENDANCE                               |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| Class: " + className + "-" + section + " | Date: " + date + "                 |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| Enter P for Present, A for Absent:                  |");
        System.out.println("|─────────────────────────────────────────────────────|");

        int presentCount = 0;

        for (int i = 0; i < classStudents.size(); i++) {
            Student s = classStudents.get(i);
            String status = null;

            while (status == null) {
                System.out.print("| " + s.name + " (ID: " + s.id + ") - P/A: ");
                String input = scanner.nextLine().trim().toUpperCase();

                if (input.equals("P")) {
                    status = "Present";
                } else if (input.equals("A")) {
                    status = "Absent";
                } else {
                    System.out.println("| Invalid input! Please type P or A only.        |");
                }
            }

            boolean updated = false;
            for (int j = 0; j < attendanceList.size(); j++) {
                AttendanceRecord record = attendanceList.get(j);
                if (record.studentId.equals(s.id) && record.date.equals(date)) {
                    record.status = status;
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                attendanceList.add(new AttendanceRecord(s.id, date, status));
            }

            if (status.equals("Present")) {
                presentCount++;
            }
        }

        saveAttendance();
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| ✓ Attendance Recorded Successfully!                 |");
        System.out.println("| Present: " + presentCount + " | Absent: " + (classStudents.size() - presentCount) + "                        |");
        System.out.println("|─────────────────────────────────────────────────────|");
    }

    void attendanceReportAllStudents(String startDate, String endDate) {
        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            System.out.println("| Invalid date format. Use YYYY-MM-DD.               |");
            return;
        }

        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.println("|              ATTENDANCE REPORT - ALL STUDENTS                    |");
        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.println("| Period: " + startDate + " to " + endDate + "                                   |");
        System.out.println("|─────────────────────────────────────────────────────────────────|");

        for (Student s : students.values()) {
            int totalDays = countTotalDays(s.id, startDate, endDate);
            int presentDays = countPresentDays(s.id, startDate, endDate);
            double percentage = 0;

            if (totalDays > 0) {
                percentage = (presentDays * 100.0) / totalDays;
            }

            System.out.printf("| %-25s (ID: %-8s) : %6.2f%% %34s |\n",
                    truncate(s.name, 25), s.id, percentage, "");
        }
        System.out.println("|─────────────────────────────────────────────────────────────────|");
    }

    void attendanceReportByClass(String startDate, String endDate) {
        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            System.out.println("| Invalid date format. Use YYYY-MM-DD.               |");
            return;
        }

        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|         CLASS-WISE ATTENDANCE REPORT                |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| Period: " + startDate + " to " + endDate + "                 |");
        System.out.println("|─────────────────────────────────────────────────────|");

        HashMap<String, Integer> classCount = new HashMap<>();
        HashMap<String, Double> classTotalPercent = new HashMap<>();

        for (Student s : students.values()) {
            int totalDays = countTotalDays(s.id, startDate, endDate);
            int presentDays = countPresentDays(s.id, startDate, endDate);
            double percentage = 0;

            if (totalDays > 0) {
                percentage = (presentDays * 100.0) / totalDays;
            }

            if (!classCount.containsKey(s.className)) {
                classCount.put(s.className, 0);
                classTotalPercent.put(s.className, 0.0);
            }

            classCount.put(s.className, classCount.get(s.className) + 1);
            classTotalPercent.put(s.className, classTotalPercent.get(s.className) + percentage);
        }

        for (String cls : classCount.keySet()) {
            int count = classCount.get(cls);
            double average = classTotalPercent.get(cls) / count;
            System.out.printf("| Class %-3s : %6.2f%% (Average Attendance) %24s |\n", cls, average, "");
        }
        System.out.println("|─────────────────────────────────────────────────────|");
    }

    int countTotalDays(String studentId, String startDate, String endDate) {
        int count = 0;
        for (int i = 0; i < attendanceList.size(); i++) {
            AttendanceRecord record = attendanceList.get(i);
            if (record.studentId.equals(studentId)) {
                if (record.date.compareTo(startDate) >= 0 && record.date.compareTo(endDate) <= 0) {
                    count++;
                }
            }
        }
        return count;
    }

    int countPresentDays(String studentId, String startDate, String endDate) {
        int count = 0;
        for (int i = 0; i < attendanceList.size(); i++) {
            AttendanceRecord record = attendanceList.get(i);
            if (record.studentId.equals(studentId) && record.status.equals("Present")) {
                if (record.date.compareTo(startDate) >= 0 && record.date.compareTo(endDate) <= 0) {
                    count++;
                }
            }
        }
        return count;
    }

    void filterAttendance(String startDate, String endDate, String studentName) {
        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            System.out.println("| Invalid date format. Use YYYY-MM-DD.               |");
            return;
        }

        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.println("|                    FILTERED ATTENDANCE                          |");
        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.println("| Period: " + startDate + " to " + endDate + " | Name: " + studentName + "               |");
        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.println("| Student Name                    | ID          | Date       | Status    |");
        System.out.println("|─────────────────────────────────────────────────────────────────|");

        for (int i = 0; i < attendanceList.size(); i++) {
            AttendanceRecord record = attendanceList.get(i);
            if (record.date.compareTo(startDate) >= 0 && record.date.compareTo(endDate) <= 0) {
                Student s = students.get(record.studentId);
                if (s != null && s.name.toLowerCase().contains(studentName.toLowerCase())) {
                    System.out.printf("| %-30s | %-11s | %-10s | %-9s |\n",
                            truncate(s.name, 30), s.id, record.date, record.status);
                }
            }
        }
        System.out.println("|─────────────────────────────────────────────────────────────────|");
    }

    void createExam(String examId, String examType, String className, String subject, String date) {
        for (int i = 0; i < examList.size(); i++) {
            Exam e = examList.get(i);
            if (e.examId.equals(examId)) {
                System.out.println("| Exam ID already exists!                             |");
                return;
            }
        }

        if (!isValidDate(date)) {
            System.out.println("| Invalid date format. Use YYYY-MM-DD.               |");
            return;
        }

        examList.add(new Exam(examId, examType, className, subject, date));
        saveExams();

        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|           EXAM CREATED SUCCESSFULLY                  |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| ID: " + examId + " | Type: " + examType + "                           |");
        System.out.println("| Class: " + className + " | Subject: " + subject + "                        |");
        System.out.println("| Date: " + date + "                                         |");
        System.out.println("|─────────────────────────────────────────────────────|");
    }

    void enterMarks(String studentId, String examId, double obtainedMarks) {
        if (!students.containsKey(studentId)) {
            System.out.println("| Student not found.                                 |");
            return;
        }

        if (obtainedMarks < 0 || obtainedMarks > 100) {
            System.out.println("| Marks must be between 0 and 100.                   |");
            return;
        }

        boolean found = false;
        for (int i = 0; i < marksList.size(); i++) {
            MarksRecord record = marksList.get(i);
            if (record.studentId.equals(studentId) && record.examId.equals(examId)) {
                record.marksObtained = obtainedMarks;
                found = true;
                break;
            }
        }

        if (!found) {
            marksList.add(new MarksRecord(studentId, examId, obtainedMarks));
        }

        saveMarks();

        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|            MARKS ENTERED SUCCESSFULLY                |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| Student ID: " + studentId + "                                    |");
        System.out.println("| Exam ID: " + examId + "                                         |");
        System.out.println("| Marks: " + obtainedMarks + "                                          |");
        System.out.println("|─────────────────────────────────────────────────────|");
    }

    void undoLastMarks() {
        if (marksList.isEmpty()) {
            System.out.println("| No marks to undo.                                  |");
            return;
        }

        MarksRecord removed = marksList.remove(marksList.size() - 1);
        saveMarks();

        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|                 UNDO SUCCESSFUL                      |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| Removed: " + removed.studentId + "                                   |");
        System.out.println("| Exam: " + removed.examId + "                                        |");
        System.out.println("| Marks: " + removed.marksObtained + "                                      |");
        System.out.println("|─────────────────────────────────────────────────────|");
    }

    double findMarks(String studentId, String examId) {
        for (int i = 0; i < marksList.size(); i++) {
            MarksRecord record = marksList.get(i);
            if (record.studentId.equals(studentId) && record.examId.equals(examId)) {
                return record.marksObtained;
            }
        }
        return -1;
    }

    void generateReportCard(String studentId) {
        if (!students.containsKey(studentId)) {
            System.out.println("| Student not found.                                 |");
            return;
        }

        Student s = students.get(studentId);

        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.println("|                         REPORT CARD                               |");
        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.println("| School: " + schoolName + "                                                 |");
        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.printf("| %-30s : %-31s |\n", "Student Name", s.name);
        System.out.printf("| %-30s : %-31s |\n", "Student ID", s.id);
        System.out.printf("| %-30s : %-31s |\n", "Class", s.className + "-" + s.section);
        System.out.printf("| %-30s : %-31s |\n", "Parent Contact", s.parentContact);
        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.printf("| %-30s : Rs. %-28.2f |\n", "Fee Paid", s.feePaid);
        System.out.printf("| %-30s : Rs. %-28.2f |\n", "Outstanding", s.getOutstanding());
        System.out.printf("| %-30s : %-31s |\n", "Payment Status", s.getPaymentStatus());
        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.printf("| %-32s | %-10s | %-10s |\n", "Exam & Subject", "Marks", "Grade");
        System.out.println("|─────────────────────────────────────────────────────────────────|");

        for (int i = 0; i < examList.size(); i++) {
            Exam exam = examList.get(i);
            if (exam.className.equals(s.className)) {
                double obtained = findMarks(studentId, exam.examId);
                if (obtained >= 0) {
                    System.out.printf("| %-32s | %-10.2f | %-10s |\n",
                            exam.examType + " - " + exam.subject, obtained, getGrade(obtained));
                } else {
                    System.out.printf("| %-32s | %-10s | %-10s |\n",
                            exam.examType + " - " + exam.subject, "N/A", "N/A");
                }
            }
        }
        System.out.println("|─────────────────────────────────────────────────────────────────|");
    }

    void classPerformanceReport(String examId) {
        Exam targetExam = null;

        for (int i = 0; i < examList.size(); i++) {
            Exam e = examList.get(i);
            if (e.examId.equals(examId)) {
                targetExam = e;
                break;
            }
        }

        if (targetExam == null) {
            System.out.println("| Exam not found.                                     |");
            return;
        }

        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.println("|                   CLASS PERFORMANCE REPORT                       |");
        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.println("| Exam: " + targetExam.examType + " (" + targetExam.subject + ")                                         |");
        System.out.println("| Class: " + targetExam.className + "                                                      |");
        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.printf("| %-30s | %-10s | %-10s |\n", "Student Name", "Marks", "Grade");
        System.out.println("|─────────────────────────────────────────────────────────────────|");

        for (Student s : students.values()) {
            if (s.className.equals(targetExam.className)) {
                double obtained = findMarks(s.id, examId);
                if (obtained >= 0) {
                    System.out.printf("| %-30s | %-10.2f | %-10s |\n",
                            truncate(s.name, 30), obtained, getGrade(obtained));
                } else {
                    System.out.printf("| %-30s | %-10s | %-10s |\n",
                            truncate(s.name, 30), "N/A", "N/A");
                }
            }
        }
        System.out.println("|─────────────────────────────────────────────────────────────────|");
    }

    void addStudent(String id, String name, String className, String section,
                    String parentContact, double totalFee) {
        if (students.containsKey(id)) {
            System.out.println("| Student ID already exists!                          |");
            return;
        }

        if (!isValidClassSection(className, section)) {
            System.out.println("| Invalid class (1-10) or section (A/B/C)!            |");
            return;
        }

        if (totalFee <= 0) {
            System.out.println("| Total fee must be more than 0!                      |");
            return;
        }

        Student newStudent = new Student(id, name, className, section, parentContact, totalFee, 0);
        students.put(id, newStudent);
        saveStudents();

        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|           STUDENT ADDED SUCCESSFULLY                 |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| ID: " + id + " | Name: " + name + "                           |");
        System.out.println("| Class: " + className + "-" + section + "                                     |");
        System.out.println("| Total Fee: Rs. " + totalFee + "                                  |");
        System.out.println("|─────────────────────────────────────────────────────|");
    }

    void removeStudent(String id) {
        if (!students.containsKey(id)) {
            System.out.println("| Student not found.                                 |");
            return;
        }

        Student s = students.get(id);
        students.remove(id);
        saveStudents();

        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|           STUDENT REMOVED SUCCESSFULLY               |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| ID: " + id + " | Name: " + s.name + "                           |");
        System.out.println("|─────────────────────────────────────────────────────|");
    }

    void editStudent(String id, String newName, String newClass, String newSection,
                     String newContact, double newTotalFee) {
        if (!students.containsKey(id)) {
            System.out.println("| Student not found.                                 |");
            return;
        }

        if (!isValidClassSection(newClass, newSection)) {
            System.out.println("| Invalid class or section.                         |");
            return;
        }

        Student s = students.get(id);

        if (newTotalFee < s.feePaid) {
            System.out.println("| New total fee cannot be less than already paid: " + s.feePaid + " |");
            return;
        }

        s.name = newName;
        s.className = newClass;
        s.section = newSection;
        s.parentContact = newContact;
        s.totalFee = newTotalFee;

        saveStudents();

        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|           STUDENT UPDATED SUCCESSFULLY               |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| ID: " + id + " | Name: " + newName + "                           |");
        System.out.println("|─────────────────────────────────────────────────────|");
    }

    void viewAllStudents() {
        if (students.isEmpty()) {
            System.out.println("| No students found.                                 |");
            return;
        }

        System.out.println("|─────────────────────────────────────────────────────────────────────────────────────────|");
        System.out.println("|                                      ALL STUDENTS                                        |");
        System.out.println("|─────────────────────────────────────────────────────────────────────────────────────────|");
        System.out.println("| ID        | Name                       | Class      | Fee Paid     | Outstanding | Status    |");
        System.out.println("|─────────────────────────────────────────────────────────────────────────────────────────|");

        for (Student s : students.values()) {
            System.out.printf("| %-8s | %-25s | %-10s | Rs. %-9.2f | Rs. %-9.2f | %-9s |\n",
                    s.id, truncate(s.name, 25), s.className + "-" + s.section,
                    s.feePaid, s.getOutstanding(), s.getPaymentStatus());
        }
        System.out.println("|─────────────────────────────────────────────────────────────────────────────────────────|");
    }

    void viewStudentsByClass(String className, String section) {
        boolean found = false;

        System.out.println("|─────────────────────────────────────────────────────────────────────────|");
        System.out.println("|              STUDENTS IN CLASS " + className + "-" + section + "                              |");
        System.out.println("|─────────────────────────────────────────────────────────────────────────|");
        System.out.println("| ID        | Name                       | Parent Contact | Outstanding | Status    |");
        System.out.println("|─────────────────────────────────────────────────────────────────────────|");

        for (Student s : students.values()) {
            if (s.className.equals(className) && s.section.equals(section)) {
                System.out.printf("| %-8s | %-25s | %-14s | Rs. %-9.2f | %-9s |\n",
                        s.id, truncate(s.name, 25), s.parentContact, s.getOutstanding(), s.getPaymentStatus());
                found = true;
            }
        }

        if (!found) {
            System.out.println("| No students in this class-section.                                   |");
        }
        System.out.println("|─────────────────────────────────────────────────────────────────────────|");
    }

    void searchStudentByName(String name) {
        boolean found = false;

        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.println("|                 SEARCH RESULTS - NAME: " + name + "                          |");
        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.println("| ID        | Name                       | Class      | Outstanding | Status    |");
        System.out.println("|─────────────────────────────────────────────────────────────────|");

        for (Student s : students.values()) {
            if (s.name.toLowerCase().contains(name.toLowerCase())) {
                System.out.printf("| %-8s | %-25s | %-10s | Rs. %-9.2f | %-9s |\n",
                        s.id, truncate(s.name, 25), s.className + "-" + s.section, s.getOutstanding(), s.getPaymentStatus());
                found = true;
            }
        }

        if (!found) {
            System.out.println("| No student found with name: " + name + "                                   |");
        }
        System.out.println("|─────────────────────────────────────────────────────────────────|");
    }

    void searchStudentByClass(String className) {
        boolean found = false;

        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.println("|                  STUDENTS IN CLASS " + className + "                               |");
        System.out.println("|─────────────────────────────────────────────────────────────────|");
        System.out.println("| ID        | Name                       | Section | Outstanding | Status    |");
        System.out.println("|─────────────────────────────────────────────────────────────────|");

        for (Student s : students.values()) {
            if (s.className.equals(className)) {
                System.out.printf("| %-8s | %-25s | %-7s | Rs. %-9.2f | %-9s |\n",
                        s.id, truncate(s.name, 25), s.section, s.getOutstanding(), s.getPaymentStatus());
                found = true;
            }
        }

        if (!found) {
            System.out.println("| No students in class: " + className + "                                      |");
        }
        System.out.println("|─────────────────────────────────────────────────────────────────|");
    }

    void searchStudentById(String rollId) {
        if (students.containsKey(rollId)) {
            Student s = students.get(rollId);
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("|                  STUDENT DETAILS                    |");
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.printf("| %-20s : %-31s |\n", "ID", s.id);
            System.out.printf("| %-20s : %-31s |\n", "Name", s.name);
            System.out.printf("| %-20s : %-31s |\n", "Class", s.className + "-" + s.section);
            System.out.printf("| %-20s : %-31s |\n", "Parent Contact", s.parentContact);
            System.out.printf("| %-20s : Rs. %-28.2f |\n", "Total Fee", s.totalFee);
            System.out.printf("| %-20s : Rs. %-28.2f |\n", "Fee Paid", s.feePaid);
            System.out.printf("| %-20s : Rs. %-28.2f |\n", "Outstanding", s.getOutstanding());
            System.out.printf("| %-20s : %-31s |\n", "Payment Status", s.getPaymentStatus());
            System.out.println("|─────────────────────────────────────────────────────|");
        } else {
            System.out.println("| Student not found with ID: " + rollId + "                             |");
        }
    }

    void addTeacher(String id, String name, String subject, String username,
                    String password, double salary) {
        if (teachers.containsKey(id)) {
            System.out.println("| Teacher ID already exists!                          |");
            return;
        }

        for (Teacher t : teachers.values()) {
            if (t.username.equals(username)) {
                System.out.println("| Username already taken!                            |");
                return;
            }
        }

        for (Teacher t : teachers.values()) {
            if (t.subject.equalsIgnoreCase(subject)) {
                System.out.println("| A teacher already teaches: " + subject + "                     |");
                return;
            }
        }

        Teacher newTeacher = new Teacher(id, name, subject, username, password, salary, 0);
        teachers.put(id, newTeacher);
        saveTeachers();

        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|           TEACHER ADDED SUCCESSFULLY                |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| ID: " + id + " | Name: " + name + "                           |");
        System.out.println("| Subject: " + subject + "                                        |");
        System.out.println("|─────────────────────────────────────────────────────|");
    }

    void removeTeacher(String id) {
        if (!teachers.containsKey(id)) {
            System.out.println("| Teacher not found.                                 |");
            return;
        }

        Teacher t = teachers.get(id);
        teachers.remove(id);
        saveTeachers();

        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|           TEACHER REMOVED SUCCESSFULLY              |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| ID: " + id + " | Name: " + t.name + "                           |");
        System.out.println("|─────────────────────────────────────────────────────|");
    }

    void viewAllTeachers() {
        if (teachers.isEmpty()) {
            System.out.println("| No teachers found.                                 |");
            return;
        }

        System.out.println("|─────────────────────────────────────────────────────────────────────────|");
        System.out.println("|                           ALL TEACHERS                                   |");
        System.out.println("|─────────────────────────────────────────────────────────────────────────|");
        System.out.println("| ID        | Name                 | Subject        | Salary      | Paid       |");
        System.out.println("|─────────────────────────────────────────────────────────────────────────|");

        for (Teacher t : teachers.values()) {
            System.out.printf("| %-8s | %-19s | %-14s | Rs. %-8.2f | Rs. %-8.2f |\n",
                    t.id, truncate(t.name, 19), truncate(t.subject, 14), t.salary, t.salaryPaid);
        }
        System.out.println("|─────────────────────────────────────────────────────────────────────────|");
    }

    void searchTeacherBySubject(String subject) {
        boolean found = false;

        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|           TEACHERS FOR SUBJECT: " + subject + "                    |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| ID        | Name                                     |");
        System.out.println("|─────────────────────────────────────────────────────|");

        for (Teacher t : teachers.values()) {
            if (t.subject.equalsIgnoreCase(subject)) {
                System.out.printf("| %-8s | %-35s |\n", t.id, truncate(t.name, 35));
                found = true;
            }
        }

        if (!found) {
            System.out.println("| No teacher found for subject: " + subject + "                       |");
        }
        System.out.println("|─────────────────────────────────────────────────────|");
    }

    Teacher teacherLogin(String username, String password) {
        for (Teacher t : teachers.values()) {
            if (t.username.equals(username) && t.password.equals(password)) {
                return t;
            }
        }
        return null;
    }

    void payStudentFee(String studentId, double amount) {
        if (!students.containsKey(studentId)) {
            System.out.println("| Student not found.                                 |");
            return;
        }

        Student s = students.get(studentId);

        if (amount <= 0) {
            System.out.println("| Amount must be more than 0.                        |");
            return;
        }

        double outstanding = s.getOutstanding();

        if (amount > outstanding) {
            System.out.println("| Cannot pay more than outstanding: " + outstanding + "                |");
            return;
        }

        s.feePaid = s.feePaid + amount;
        saveStudents();

        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|            FEE PAYMENT SUCCESSFUL                   |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| Student: " + s.name + "                             |");
        System.out.println("| Amount Paid: Rs. " + amount + "                     |");
        System.out.println("| Remaining: Rs. " + s.getOutstanding() + "           |");
        System.out.println("| Status: " + s.getPaymentStatus() + "                |");
        System.out.println("|─────────────────────────────────────────────────────|");
    }

    void payTeacherSalary(String teacherId, double amount) {
        if (!teachers.containsKey(teacherId)) {
            System.out.println("| Teacher not found.                                 |");
            return;
        }

        Teacher t = teachers.get(teacherId);
        double remaining = t.getRemainingSalary();

        if (amount <= 0) {
            System.out.println("| Amount must be more than 0.                                |");
            return;
        }

        if (amount > remaining) {
            System.out.println("| Cannot pay more than remaining salary: " + remaining + "   |");
            return;
        }

        t.salaryPaid = t.salaryPaid + amount;
        saveTeachers();

        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|           SALARY PAYMENT SUCCESSFUL                 |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| Teacher: " + t.name + "                             |");
        System.out.println("| Amount Paid: Rs. " + amount + "                     |");
        System.out.println("| Total Paid: Rs. " + t.salaryPaid + "                |");
        System.out.println("|─────────────────────────────────────────────────────|");
    }

    void addExpense(String name, double amount, String date) {
        if (amount <= 0) {
            System.out.println("| Amount must be more than 0.                        |");
            return;
        }

        if (!isValidDate(date)) {
            System.out.println("| Invalid date format. Use YYYY-MM-DD.               |");
            return;
        }

        expenseList.add(new Expense(name, amount, date));
        saveExpenses();

        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|            EXPENSE ADDED SUCCESSFULLY               |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| Name: " + name + "                                  |");
        System.out.println("| Amount: Rs. " + amount + "                          |");
        System.out.println("| Date: " + date + "                                  |");
        System.out.println("|─────────────────────────────────────────────────────|");
    }

    void financialReport() {
        double totalFeeCollected = 0;
        double totalSalaryPaid = 0;
        double totalExpenses = 0;

        for (Student s : students.values()) {
            totalFeeCollected = totalFeeCollected + s.feePaid;
        }

        for (Teacher t : teachers.values()) {
            totalSalaryPaid = totalSalaryPaid + t.salaryPaid;
        }

        for (int i = 0; i < expenseList.size(); i++) {
            Expense e = expenseList.get(i);
            totalExpenses = totalExpenses + e.amount;
        }

        double netRevenue = totalFeeCollected - totalSalaryPaid;
        double netProfit = totalFeeCollected - totalSalaryPaid - totalExpenses;

        System.out.println("|─────────────────────────────────────────────────────────────|");
        System.out.println("|                 FINANCIAL REPORT - " + schoolName + "       |");
        System.out.println("|─────────────────────────────────────────────────────────────|");
        System.out.printf("| %-30s : Rs. %-22.2f |\n", "Total Fees Collected", totalFeeCollected);
        System.out.printf("| %-30s : Rs. %-22.2f |\n", "Total Salaries Paid", totalSalaryPaid);
        System.out.printf("| %-30s : Rs. %-22.2f |\n", "Total Other Expenses", totalExpenses);
        System.out.println("|─────────────────────────────────────────────────────────────|");
        System.out.printf("| %-30s : Rs. %-22.2f |\n", "Net Revenue", netRevenue);
        System.out.printf("| %-30s : Rs. %-22.2f |\n", "Net Profit", netProfit);
        System.out.println("|─────────────────────────────────────────────────────────────|");
    }

    void feeReminders() {
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|                  FEE REMINDERS                      |");
        System.out.println("|─────────────────────────────────────────────────────|");
        boolean anyOutstanding = false;

        for (Student s : students.values()) {
            if (s.getOutstanding() > 0) {
                System.out.printf("| ⚠ %-25s (ID: %-8s) owes: Rs. %-8.2f | Status: %-6s |\n",
                        truncate(s.name, 25), s.id, s.getOutstanding(), s.getPaymentStatus());
                anyOutstanding = true;
            }
        }

        if (!anyOutstanding) {
            System.out.println("| ✓ No outstanding fees. All students have paid!  |");
        }
        System.out.println("|─────────────────────────────────────────────────────|");
    }

    void promoteStudents(boolean carryFee, boolean resetFee, double newTotalFee) {
        HashMap<String, Student> updatedStudents = new HashMap<>();

        for (Student s : students.values()) {
            int currentClass = Integer.parseInt(s.className);

            if (currentClass == 10) {
                System.out.println("| Graduated: " + s.name + "                                   |");
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
            updatedStudents.put(s.id, promoted);
        }

        students.clear();
        students.putAll(updatedStudents);
        saveStudents();

        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|              PROMOTION COMPLETED                    |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| All students promoted to next class                 |");
        System.out.println("| Class 10 students have graduated                    |");
        System.out.println("|─────────────────────────────────────────────────────|");
    }

    private String truncate(String str, int maxLength) {
        if (str.length() > maxLength) {
            return str.substring(0, maxLength - 3) + "...";
        }
        return str;
    }
}

public class SchoolCRM {
    static HashMap<String, School> schoolMap = new HashMap<>();
    static Scanner scanner = new Scanner(System.in);
    static School currentSchool = null;
    static Teacher loggedTeacher = null;

    public static void main(String[] args) {
        loadSchoolCredentials();
        printWelcomeScreen();

        while (true) {
            if (currentSchool == null) {
                showMainMenu();
            } else {
                if (loggedTeacher == null) {
                    showAdminMenu();
                } else {
                    showTeacherMenu();
                }
            }
        }
    }

    static void printWelcomeScreen() {
        System.out.println("|─────────────────────────────────────────────────────────────|");
        System.out.println("|     WELCOME TO SCHOOL MANAGEMENT SYSTEM                     |");
        System.out.println("|─────────────────────────────────────────────────────────────|");
    }
    static void showMainMenu() {
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|                     MAIN MENU                       |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| 1. Login to School                                  |");
        System.out.println("| 2. Register New School                              |");
        System.out.println("| 3. Exit                                             |");
        System.out.println("| for testing: admin,admin                            |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.print("| Choice: ");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            login();
        } else if (choice.equals("2")) {
            registerSchool();
        } else if (choice.equals("3")) {
            saveAllSchools();
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("|                   GOODBYE!                          |");
            System.out.println("|     Thank you for using School Management System    |");
            System.out.println("|─────────────────────────────────────────────────────|");
            System.exit(0);
        } else {
            System.out.println("| Invalid choice.                                      |");
        }
    }

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
        for (School school : schoolMap.values()) {
            school.saveAllData();
        }
    }

    static void registerSchool() {
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|                REGISTER NEW SCHOOL                  |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.print("| Enter School Name: ");
        String name = scanner.nextLine();

        if (schoolMap.containsKey(name)) {
            System.out.println("| School already exists!                              |");
            System.out.println("|─────────────────────────────────────────────────────|");
            return;
        }

        System.out.print("| Set Admin Password: ");
        String pass = scanner.nextLine();

        School newSchool = new School(name, pass);
        schoolMap.put(name, newSchool);
        saveSchoolCredential(name, pass);

        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| School registered successfully!                     |");
        System.out.println("|─────────────────────────────────────────────────────|");
    }

    static void login() {
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|                      LOGIN                          |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.print("| School Name: ");
        String name = scanner.nextLine();

        School school = schoolMap.get(name);
        if (school == null) {
            System.out.println("| School not found.                                   |");
            System.out.println("|─────────────────────────────────────────────────────|");
            return;
        }

        System.out.print("| Admin Password (leave blank for teacher login): ");
        String pass = scanner.nextLine();

        if (!pass.isEmpty() && pass.equals(school.adminPassword)) {
            currentSchool = school;
            loggedTeacher = null;
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("| Admin login successful!                             |");
            System.out.println("| Welcome to " + name + "                             |");
            System.out.println("|─────────────────────────────────────────────────────|");
            return;
        }

        System.out.print("| Teacher Username: ");
        String username = scanner.nextLine();
        System.out.print("| Teacher Password: ");
        String tpass = scanner.nextLine();

        Teacher t = school.teacherLogin(username, tpass);

        if (t != null) {
            currentSchool = school;
            loggedTeacher = t;
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("| Teacher login successful!                           |");
            System.out.println("| Welcome " + t.name + "                              |");
            System.out.println("|─────────────────────────────────────────────────────|");
        } else {
            System.out.println("| Invalid credentials.                                |");
            System.out.println("|─────────────────────────────────────────────────────|");
        }
    }

    static void showAdminMenu() {
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|     " + currentSchool.schoolName + " - Admin Panel  |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| 1.  Student Management                               |");
        System.out.println("| 2.  Teacher Management                               |");
        System.out.println("| 3.  Attendance                                       |");
        System.out.println("| 4.  Exams & Marks                                    |");
        System.out.println("| 5.  Fee & Salary                                     |");
        System.out.println("| 6.  Add Expense                                      |");
        System.out.println("| 7.  Reports                                          |");
        System.out.println("| 8.  Fee Reminders                                    |");
        System.out.println("| 9.  Promote Students                                 |");
        System.out.println("| 10. Search & Filter                                  |");
        System.out.println("| 11. Fee Management Reports                           |");
        System.out.println("| 12. Logout                                           |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.print("| Choice: ");

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
            adminFeeManagementMenu();
        } else if (ch.equals("12")) {
            currentSchool.saveAllData();
            currentSchool = null;
            loggedTeacher = null;
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("|              LOGGED OUT SUCCESSFULLY                |");
            System.out.println("|─────────────────────────────────────────────────────|");
        } else {
            System.out.println("| Invalid choice.                                      |");
        }
    }

    static void adminFeeManagementMenu() {
        boolean running = true;

        while (running) {
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("|              FEE MANAGEMENT REPORTS                 |");
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("| 1. View Students with Outstanding Fees              |");
            System.out.println("| 2. View Students with Full Payment                  |");
            System.out.println("| 3. View Students with Partial Payment               |");
            System.out.println("| 4. View Complete Fee Status Report                  |");
            System.out.println("| 5. View Fee Status by Class                         |");
            System.out.println("| 6. Send Fee Reminders to Defaulters                 |");
            System.out.println("| 7. Back                                             |");
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.print("| Choice: ");

            String opt = scanner.nextLine();

            if (opt.equals("1")) {
                currentSchool.viewStudentsWithOutstandingFees();
            } else if (opt.equals("2")) {
                currentSchool.viewStudentsWithFullPayment();
            } else if (opt.equals("3")) {
                currentSchool.viewStudentsWithPartialPayment();
            } else if (opt.equals("4")) {
                currentSchool.viewCompleteFeeStatus();
            } else if (opt.equals("5")) {
                System.out.print("| Enter Class (1-10): ");
                String cls = scanner.nextLine();
                currentSchool.viewFeeStatusByClass(cls);
            } else if (opt.equals("6")) {
                currentSchool.sendFeeRemindersToDefaulters();
            } else if (opt.equals("7")) {
                running = false;
            } else {
                System.out.println("| Invalid choice.                                      |");
            }
        }
    }

    static void adminStudentMenu() {
        boolean running = true;

        while (running) {
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("|                STUDENT MANAGEMENT                   |");
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("| 1. Add Student                                      |");
            System.out.println("| 2. Edit Student                                     |");
            System.out.println("| 3. Remove Student                                   |");
            System.out.println("| 4. View All Students                                |");
            System.out.println("| 5. View by Class & Section                          |");
            System.out.println("| 6. Back                                             |");
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.print("| Choice: ");

            String opt = scanner.nextLine();

            if (opt.equals("1")) {
                System.out.print("| ID: ");
                String id = scanner.nextLine();
                System.out.print("| Name: ");
                String name = scanner.nextLine();
                System.out.print("| Class (1-10): ");
                String cls = scanner.nextLine();
                System.out.print("| Section (A/B/C): ");
                String sec = scanner.nextLine();
                System.out.print("| Parent Contact: ");
                String contact = scanner.nextLine();
                System.out.print("| Total Fee: ");
                double fee = Double.parseDouble(scanner.nextLine());
                currentSchool.addStudent(id, name, cls, sec, contact, fee);

            } else if (opt.equals("2")) {
                System.out.print("| Student ID: ");
                String id = scanner.nextLine();
                if (!currentSchool.students.containsKey(id)) {
                    System.out.println("| Student not found.                                 |");
                } else {
                    System.out.print("| New Name: ");
                    String name = scanner.nextLine();
                    System.out.print("| New Class (1-10): ");
                    String cls = scanner.nextLine();
                    System.out.print("| New Section (A/B/C): ");
                    String sec = scanner.nextLine();
                    System.out.print("| New Parent Contact: ");
                    String contact = scanner.nextLine();
                    System.out.print("| New Total Fee: ");
                    double fee = Double.parseDouble(scanner.nextLine());
                    currentSchool.editStudent(id, name, cls, sec, contact, fee);
                }

            } else if (opt.equals("3")) {
                System.out.print("| Student ID: ");
                String id = scanner.nextLine();
                currentSchool.removeStudent(id);

            } else if (opt.equals("4")) {
                currentSchool.viewAllStudents();

            } else if (opt.equals("5")) {
                System.out.print("| Class (1-10): ");
                String cls = scanner.nextLine();
                System.out.print("| Section (A/B/C): ");
                String sec = scanner.nextLine();
                currentSchool.viewStudentsByClass(cls, sec);

            } else if (opt.equals("6")) {
                running = false;
            } else {
                System.out.println("| Invalid choice.                                      |");
            }
        }
    }

    static void adminTeacherMenu() {
        boolean running = true;

        while (running) {
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("|                TEACHER MANAGEMENT                   |");
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("| 1. Add Teacher                                      |");
            System.out.println("| 2. Remove Teacher                                   |");
            System.out.println("| 3. View All Teachers                                |");
            System.out.println("| 4. Search by Subject                                |");
            System.out.println("| 5. Back                                             |");
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.print("| Choice: ");

            String opt = scanner.nextLine();

            if (opt.equals("1")) {
                System.out.print("| ID: ");
                String id = scanner.nextLine();
                System.out.print("| Name: ");
                String name = scanner.nextLine();
                System.out.print("| Subject: ");
                String sub = scanner.nextLine();
                System.out.print("| Username: ");
                String uname = scanner.nextLine();
                System.out.print("| Password: ");
                String pwd = scanner.nextLine();
                System.out.print("| Monthly Salary: ");
                double sal = Double.parseDouble(scanner.nextLine());
                currentSchool.addTeacher(id, name, sub, uname, pwd, sal);

            } else if (opt.equals("2")) {
                System.out.print("| Teacher ID: ");
                String id = scanner.nextLine();
                currentSchool.removeTeacher(id);

            } else if (opt.equals("3")) {
                currentSchool.viewAllTeachers();

            } else if (opt.equals("4")) {
                System.out.print("| Subject: ");
                String sub = scanner.nextLine();
                currentSchool.searchTeacherBySubject(sub);

            } else if (opt.equals("5")) {
                running = false;
            } else {
                System.out.println("| Invalid choice.                                      |");
            }
        }
    }

    static void adminAttendanceMenu() {
        boolean running = true;

        while (running) {
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("|                  ATTENDANCE                         |");
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("| 1. Mark Attendance (Class & Section)                |");
            System.out.println("| 2. Student-wise Report                              |");
            System.out.println("| 3. Class-wise Report                                |");
            System.out.println("| 4. Back                                             |");
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.print("| Choice: ");

            String opt = scanner.nextLine();

            if (opt.equals("1")) {
                System.out.print("| Class (1-10): ");
                String cls = scanner.nextLine();
                System.out.print("| Section (A/B/C): ");
                String sec = scanner.nextLine();
                System.out.print("| Date (YYYY-MM-DD): ");
                String dt = scanner.nextLine();
                currentSchool.markAttendanceByClass(cls, sec, dt, scanner);

            } else if (opt.equals("2")) {
                System.out.print("| Start Date (YYYY-MM-DD): ");
                String sd = scanner.nextLine();
                System.out.print("| End Date (YYYY-MM-DD): ");
                String ed = scanner.nextLine();
                currentSchool.attendanceReportAllStudents(sd, ed);

            } else if (opt.equals("3")) {
                System.out.print("| Start Date (YYYY-MM-DD): ");
                String sd = scanner.nextLine();
                System.out.print("| End Date (YYYY-MM-DD): ");
                String ed = scanner.nextLine();
                currentSchool.attendanceReportByClass(sd, ed);

            } else if (opt.equals("4")) {
                running = false;
            } else {
                System.out.println("| Invalid choice.                                      |");
            }
        }
    }

    static void adminExamMenu() {
        boolean running = true;

        while (running) {
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("|                  EXAMS & MARKS                      |");
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("| 1. Create Exam                                      |");
            System.out.println("| 2. Enter Marks                                     |");
            System.out.println("| 3. Undo Last Marks Entry                           |");
            System.out.println("| 4. Generate Report Card                            |");
            System.out.println("| 5. Class Performance Report                        |");
            System.out.println("| 6. Back                                             |");
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.print("| Choice: ");

            String opt = scanner.nextLine();

            if (opt.equals("1")) {
                System.out.print("| Exam ID: ");
                String eid = scanner.nextLine();
                System.out.print("| Type (Test/Mid Term/Final Term): ");
                String type = scanner.nextLine();
                System.out.print("| Class (1-10): ");
                String cls = scanner.nextLine();
                System.out.print("| Subject: ");
                String sub = scanner.nextLine();
                System.out.print("| Date (YYYY-MM-DD): ");
                String dt = scanner.nextLine();
                currentSchool.createExam(eid, type, cls, sub, dt);

            } else if (opt.equals("2")) {
                System.out.print("| Student ID: ");
                String sid = scanner.nextLine();
                System.out.print("| Exam ID: ");
                String eid = scanner.nextLine();
                System.out.print("| Marks (0-100): ");
                double m = Double.parseDouble(scanner.nextLine());
                currentSchool.enterMarks(sid, eid, m);

            } else if (opt.equals("3")) {
                currentSchool.undoLastMarks();

            } else if (opt.equals("4")) {
                System.out.print("| Student ID: ");
                String sid = scanner.nextLine();
                currentSchool.generateReportCard(sid);

            } else if (opt.equals("5")) {
                System.out.print("| Exam ID: ");
                String eid = scanner.nextLine();
                currentSchool.classPerformanceReport(eid);

            } else if (opt.equals("6")) {
                running = false;
            } else {
                System.out.println("| Invalid choice.                                      |");
            }
        }
    }

    static void adminFeeSalaryMenu() {
        boolean running = true;

        while (running) {
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("|                  FEE & SALARY                       |");
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("| 1. Pay Student Fee                                  |");
            System.out.println("| 2. Pay Teacher Salary                               |");
            System.out.println("| 3. Financial Report                                 |");
            System.out.println("| 4. Back                                             |");
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.print("| Choice: ");

            String opt = scanner.nextLine();

            if (opt.equals("1")) {
                System.out.print("| Student ID: ");
                String sid = scanner.nextLine();
                System.out.print("| Amount: ");
                double amt = Double.parseDouble(scanner.nextLine());
                currentSchool.payStudentFee(sid, amt);

            } else if (opt.equals("2")) {
                System.out.print("| Teacher ID: ");
                String tid = scanner.nextLine();
                System.out.print("| Amount: ");
                double amt = Double.parseDouble(scanner.nextLine());
                currentSchool.payTeacherSalary(tid, amt);

            } else if (opt.equals("3")) {
                currentSchool.financialReport();

            } else if (opt.equals("4")) {
                running = false;
            } else {
                System.out.println("| Invalid choice.                                      |");
            }
        }
    }

    static void adminAddExpense() {
        System.out.print("| Expense Name: ");
        String name = scanner.nextLine();
        System.out.print("| Amount: ");
        double amt = Double.parseDouble(scanner.nextLine());
        System.out.print("| Date (YYYY-MM-DD): ");
        String dt = scanner.nextLine();
        currentSchool.addExpense(name, amt, dt);
    }

    static void adminReportsMenu() {
        boolean running = true;

        while (running) {
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("|                    REPORTS                          |");
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("| 1. Financial Report                                 |");
            System.out.println("| 2. Student Attendance Report                        |");
            System.out.println("| 3. Class-wise Attendance Report                     |");
            System.out.println("| 4. View All Students                                |");
            System.out.println("| 5. Back                                             |");
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.print("| Choice: ");

            String opt = scanner.nextLine();

            if (opt.equals("1")) {
                currentSchool.financialReport();
            } else if (opt.equals("2")) {
                System.out.print("| Start Date (YYYY-MM-DD): ");
                String sd = scanner.nextLine();
                System.out.print("| End Date (YYYY-MM-DD): ");
                String ed = scanner.nextLine();
                currentSchool.attendanceReportAllStudents(sd, ed);
            } else if (opt.equals("3")) {
                System.out.print("| Start Date (YYYY-MM-DD): ");
                String sd = scanner.nextLine();
                System.out.print("| End Date (YYYY-MM-DD): ");
                String ed = scanner.nextLine();
                currentSchool.attendanceReportByClass(sd, ed);
            } else if (opt.equals("4")) {
                currentSchool.viewAllStudents();
            } else if (opt.equals("5")) {
                running = false;
            } else {
                System.out.println("| Invalid choice.                                      |");
            }
        }
    }

    static void adminPromotionMenu() {
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|                PROMOTE STUDENTS                      |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.print("| Carry forward paid fee? (yes/no): ");
        String ans1 = scanner.nextLine();
        boolean carryFee = ans1.equalsIgnoreCase("yes");

        System.out.print("| Reset total fee to new amount? (yes/no): ");
        String ans2 = scanner.nextLine();
        boolean resetFee = ans2.equalsIgnoreCase("yes");

        double newFee = 0;
        if (resetFee) {
            System.out.print("| New total fee amount: ");
            newFee = Double.parseDouble(scanner.nextLine());
        }

        currentSchool.promoteStudents(carryFee, resetFee, newFee);
    }

    static void adminSearchMenu() {
        boolean running = true;

        while (running) {
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("|                 SEARCH & FILTER                     |");
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("| 1. Search Student by Name                           |");
            System.out.println("| 2. Search Student by Class                          |");
            System.out.println("| 3. Search Student by ID                             |");
            System.out.println("| 4. Filter Attendance by Date & Name                 |");
            System.out.println("| 5. Back                                             |");
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.print("| Choice: ");

            String opt = scanner.nextLine();

            if (opt.equals("1")) {
                System.out.print("| Name: ");
                String nm = scanner.nextLine();
                currentSchool.searchStudentByName(nm);
            } else if (opt.equals("2")) {
                System.out.print("| Class (1-10): ");
                String cls = scanner.nextLine();
                currentSchool.searchStudentByClass(cls);
            } else if (opt.equals("3")) {
                System.out.print("| Student ID: ");
                String rid = scanner.nextLine();
                currentSchool.searchStudentById(rid);
            } else if (opt.equals("4")) {
                System.out.print("| Start Date (YYYY-MM-DD): ");
                String sd = scanner.nextLine();
                System.out.print("| End Date (YYYY-MM-DD): ");
                String ed = scanner.nextLine();
                System.out.print("| Student Name: ");
                String nm = scanner.nextLine();
                currentSchool.filterAttendance(sd, ed, nm);
            } else if (opt.equals("5")) {
                running = false;
            } else {
                System.out.println("| Invalid choice.                                      |");
            }
        }
    }

    static void showTeacherMenu() {
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("|"+ currentSchool.schoolName + " - Teacher: " + loggedTeacher.name + "|");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.println("| 1. Mark Attendance (Class & Section)                |");
        System.out.println("| 2. Enter Exam Marks                                 |");
        System.out.println("| 3. View Student Report Card                         |");
        System.out.println("| 4. Class Performance Report                         |");
        System.out.println("| 5. Attendance Reports                               |");
        System.out.println("| 6. Logout                                           |");
        System.out.println("|─────────────────────────────────────────────────────|");
        System.out.print("| Choice: ");

        String ch = scanner.nextLine();

        if (ch.equals("1")) {
            System.out.print("| Class (1-10): ");
            String cls = scanner.nextLine();
            System.out.print("| Section (A/B/C): ");
            String sec = scanner.nextLine();
            System.out.print("| Date (YYYY-MM-DD): ");
            String dt = scanner.nextLine();
            currentSchool.markAttendanceByClass(cls, sec, dt, scanner);

        } else if (ch.equals("2")) {
            System.out.print("| Exam ID: ");
            String eid = scanner.nextLine();
            System.out.print("| Student ID: ");
            String sid = scanner.nextLine();
            System.out.print("| Marks (0-100): ");
            double m = Double.parseDouble(scanner.nextLine());
            currentSchool.enterMarks(sid, eid, m);

        } else if (ch.equals("3")) {
            System.out.print("| Student ID: ");
            String sid = scanner.nextLine();
            currentSchool.generateReportCard(sid);

        } else if (ch.equals("4")) {
            System.out.print("| Exam ID: ");
            String eid = scanner.nextLine();
            currentSchool.classPerformanceReport(eid);

        } else if (ch.equals("5")) {
            System.out.print("| Start Date (YYYY-MM-DD): ");
            String sd = scanner.nextLine();
            System.out.print("| End Date (YYYY-MM-DD): ");
            String ed = scanner.nextLine();
            currentSchool.attendanceReportAllStudents(sd, ed);
            currentSchool.attendanceReportByClass(sd, ed);

        } else if (ch.equals("6")) {
            currentSchool.saveAllData();
            currentSchool = null;
            loggedTeacher = null;
            System.out.println("|─────────────────────────────────────────────────────|");
            System.out.println("|              LOGGED OUT SUCCESSFULLY                |");
            System.out.println("|─────────────────────────────────────────────────────|");

        } else {
            System.out.println("| Invalid choice.                                      |");
        }
    }
}