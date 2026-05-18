import java.util.*;

class Student {

    private String name;
    private int roll;

    Student(String name, int roll) {
        this.name = name;
        this.roll = roll;
    }

    void display() {
        System.out.println("Name : " + name);
        System.out.println("Roll : " + roll);
    }
}

class CollegeStudent extends Student {

    private String branch;

    CollegeStudent(String name, int roll, String branch) {
        super(name, roll);
        this.branch = branch;
    }

    void showBranch() {
        System.out.println("Branch : " + branch);
    }
}

public class Main {

    public static void main(String[] args) {

        CollegeStudent s1 =
                new CollegeStudent("Vishal", 101, "CSE");

        s1.display();
        s1.showBranch();
    }
}