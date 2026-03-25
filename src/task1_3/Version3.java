package task1_3;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Базовий клас
abstract class Human implements Externalizable {
    protected String firstName;
    protected String lastName;

    // Для Externalizable обов'язковий порожній конструктор
    public Human() {}
    public Human(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(firstName);
        out.writeObject(lastName);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        firstName = (String) in.readObject();
        lastName = (String) in.readObject();
    }
}

// Предмет
class Subject implements Externalizable {
    private String name;
    private List<Teacher> teachers;

    public Subject() {
        this.teachers = new ArrayList<>();
    }
    public Subject(String name) {
        this.name = name;
        this.teachers = new ArrayList<>();
    }

    public void addTeacher(Teacher teacher) {
        if (!teachers.contains(teacher)) {
            teachers.add(teacher);
        }
    }

    public String getName() { return name; }

    @Override
    public String toString() {
        return "Предмет: " + name;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = (String) in.readObject();
        teachers = new ArrayList<>();
    }
}

// Викладач
class Teacher extends Human implements Externalizable {
    private List<Subject> subjects;

    public Teacher() {
        this.subjects = new ArrayList<>();
    }
    public Teacher(String firstName, String lastName) {
        super(firstName, lastName);
        this.subjects = new ArrayList<>();
    }

    public void addSubject(Subject subject) {
        if (!subjects.contains(subject)) {
            subjects.add(subject);
            subject.addTeacher(this);
        }
    }

    @Override
    public String toString() {
        return "Викладач: " + firstName + " " + lastName + " (викладає: " + subjects.size() + " предмети)";
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out); // Зберігаємо ім'я та прізвище
        out.writeInt(subjects.size());
        for (Subject s : subjects) {
            s.writeExternal(out);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        int count = in.readInt();
        subjects = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Subject s = new Subject();
            s.readExternal(in);
            subjects.add(s);
        }
    }
}

// Студент
class Student extends Human implements Externalizable {
    private List<Subject> subjects;
    private int score;

    public Student() {
        this.subjects = new ArrayList<>();
        this.score = 0;
    }
    public Student(String firstName, String lastName) {
        super(firstName, lastName);
        this.subjects = new ArrayList<>();
        this.score = 0;
    }

    public void addSubject(Subject subject) {
        if (!subjects.contains(subject)) {
            subjects.add(subject);
        }
    }

    public void addScore(int points) { this.score += points; }

    @Override
    public String toString() {
        return "Студент: " + firstName + " " + lastName + " | Бали: " + score;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(score);
        out.writeInt(subjects.size());
        for (Subject s : subjects) {
            s.writeExternal(out);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        score = in.readInt();
        int count = in.readInt();
        subjects = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Subject s = new Subject();
            s.readExternal(in);
            subjects.add(s);
        }
    }
}

// Навчальний план
class Curriculum implements Externalizable {
    private String name;
    private List<Subject> subjects;

    public Curriculum() {
        this.subjects = new ArrayList<>();
    }
    public Curriculum(String name) {
        this.name = name;
        this.subjects = new ArrayList<>();
    }

    public void addSubject(Subject subject) {
        subjects.add(subject);
    }

    @Override
    public String toString() {
        return "План [" + name + "], предметів: " + subjects.size();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
        out.writeInt(subjects.size());
        for (Subject s : subjects) {
            s.writeExternal(out);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = (String) in.readObject();
        int count = in.readInt();
        subjects = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Subject s = new Subject();
            s.readExternal(in);
            subjects.add(s);
        }
    }
}

// Університет
class University implements Externalizable {
    private String name;
    private List<Curriculum> curriculums;
    private List<Teacher> teachers;
    private List<Student> students;

    public University() {
        this.curriculums = new ArrayList<>();
        this.teachers = new ArrayList<>();
        this.students = new ArrayList<>();
    }
    public University(String name) {
        this.name = name;
        this.curriculums = new ArrayList<>();
        this.teachers = new ArrayList<>();
        this.students = new ArrayList<>();
    }

    public void addCurriculum(Curriculum c) { curriculums.add(c); }
    public void addTeacher(Teacher t) { teachers.add(t); }
    public void addStudent(Student s) { students.add(s); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- ").append(name).append(" ---\n");
        sb.append("Викладачі:\n");
        for (Teacher t : teachers) sb.append("  ").append(t).append("\n");
        sb.append("Студенти:\n");
        for (Student s : students) sb.append("  ").append(s).append("\n");
        return sb.toString();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);

        out.writeInt(teachers.size());
        for (Teacher t : teachers) t.writeExternal(out);

        out.writeInt(students.size());
        for (Student s : students) s.writeExternal(out);

        out.writeInt(curriculums.size());
        for (Curriculum c : curriculums) c.writeExternal(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = (String) in.readObject();

        int tCount = in.readInt();
        teachers = new ArrayList<>();
        for (int i = 0; i < tCount; i++) {
            Teacher t = new Teacher();
            t.readExternal(in);
            teachers.add(t);
        }

        int sCount = in.readInt();
        students = new ArrayList<>();
        for (int i = 0; i < sCount; i++) {
            Student s = new Student();
            s.readExternal(in);
            students.add(s);
        }

        int cCount = in.readInt();
        curriculums = new ArrayList<>();
        for (int i = 0; i < cCount; i++) {
            Curriculum c = new Curriculum();
            c.readExternal(in);
            curriculums.add(c);
        }
    }
}

// Головний клас для запуску 3 версії
public class Version3 {
    private static final String FILE_NAME = "university_v3.dat";

    public static void serializeSystem(University uni) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            uni.writeExternal(oos);
            System.out.println("\n[+] Систему успішно збережено у файл (через Externalizable): " + FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static University deserializeSystem() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            University uni = new University();
            uni.readExternal(ois);
            return uni;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        University uni = new University("Каразінський університет");

        Subject math = new Subject("Вища математика");
        Subject java = new Subject("Програмування на Java");

        Teacher teacher1 = new Teacher("Олександр", "Споров");
        teacher1.addSubject(java);

        Teacher teacher2 = new Teacher("Сергій", "Севідов");
        teacher2.addSubject(math);

        Curriculum itPlan = new Curriculum("Комп'ютерні науки");
        itPlan.addSubject(math);
        itPlan.addSubject(java);

        Student student1 = new Student("Олександра", "Малолєтова");
        student1.addSubject(java);
        student1.addSubject(math);
        student1.addScore(95);

        Student student2 = new Student("Вероніка", "Одинець");
        student2.addSubject(java);
        student2.addScore(98);

        uni.addTeacher(teacher1);
        uni.addTeacher(teacher2);
        uni.addCurriculum(itPlan);
        uni.addStudent(student1);
        uni.addStudent(student2);

        System.out.println("=== СТАН ДО СЕРІАЛІЗАЦІЇ (Версія 3) ===");
        System.out.println(uni);

        serializeSystem(uni);

        University restoredUni = deserializeSystem();

        System.out.println("\n=== СТАН ПІСЛЯ ДЕСЕРІАЛІЗАЦІЇ (Версія 3) ===");
        if (restoredUni != null) {
            System.out.println(restoredUni);
        }
    }
}