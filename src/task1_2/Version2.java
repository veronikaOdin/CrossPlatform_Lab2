package task1_2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Базовий клас
abstract class Human implements Serializable {
    protected String firstName;
    protected String lastName;

    public Human() {}
    public Human(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}

// ПРЕДМЕТ БІЛЬШЕ НЕ СЕРІАЛІЗУЄТЬСЯ (немає implements Serializable)
class Subject {
    private String name;
    private transient List<Teacher> teachers;

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
    public void setName(String name) { this.name = name; }
    public List<Teacher> getTeachers() { return teachers; }
    public void setTeachers(List<Teacher> teachers) { this.teachers = teachers; }

    @Override
    public String toString() {
        return "Предмет: " + name;
    }
}

// Викладач
class Teacher extends Human implements Serializable {
    // Поле transient, бо Subject не серіалізується
    private transient List<Subject> subjects;

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

    public List<Subject> getSubjects() { return subjects; }
    public void setSubjects(List<Subject> subjects) { this.subjects = subjects; }

    @Override
    public String toString() {
        return "Викладач: " + firstName + " " + lastName + " (викладає: " + subjects.size() + " предмети)";
    }

    // Ручне збереження предметів
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(subjects.size());
        for (Subject s : subjects) {
            out.writeObject(s.getName());
        }
    }

    // Ручне відновлення предметів
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int size = in.readInt();
        subjects = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            subjects.add(new Subject((String) in.readObject()));
        }
    }
}

// Студент
class Student extends Human implements Serializable {
    // Поля transient за умовою
    private transient List<Subject> subjects;
    private transient int score;

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

    public List<Subject> getSubjects() { return subjects; }
    public void setSubjects(List<Subject> subjects) { this.subjects = subjects; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    @Override
    public String toString() {
        return "Студент: " + firstName + " " + lastName + " | Бали: " + score;
    }

    // Ручне збереження (Шифрування балів)
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(score + 100); // Найпростіше шифрування (+100)
        out.writeInt(subjects.size());
        for (Subject s : subjects) {
            out.writeObject(s.getName());
        }
    }

    // Ручне відновлення (Дешифрування балів)
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        score = in.readInt() - 100; // Розшифрування (-100)
        int size = in.readInt();
        subjects = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            subjects.add(new Subject((String) in.readObject()));
        }
    }
}

// Навчальний план
class Curriculum implements Serializable {
    private String name;
    private transient List<Subject> subjects;

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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Subject> getSubjects() { return subjects; }
    public void setSubjects(List<Subject> subjects) { this.subjects = subjects; }

    @Override
    public String toString() {
        return "План [" + name + "], предметів: " + subjects.size();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(subjects.size());
        for (Subject s : subjects) {
            out.writeObject(s.getName());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int size = in.readInt();
        subjects = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            subjects.add(new Subject((String) in.readObject()));
        }
    }
}

// Університет
class University implements Serializable {
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
}

// Головний клас для запуску 2 версії
public class Version2 {
    private static final String FILE_NAME = "university_v2.dat";

    public static void serializeSystem(University uni) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(uni);
            System.out.println("\n[+] Систему успішно збережено у файл (з ручним шифруванням): " + FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static University deserializeSystem() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (University) ois.readObject();
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

        System.out.println("=== СТАН ДО СЕРІАЛІЗАЦІЇ (Версія 2) ===");
        System.out.println(uni);

        serializeSystem(uni);

        University restoredUni = deserializeSystem();

        System.out.println("\n=== СТАН ПІСЛЯ ДЕСЕРІАЛІЗАЦІЇ (Версія 2) ===");
        if (restoredUni != null) {
            System.out.println(restoredUni);
        }
    }
}