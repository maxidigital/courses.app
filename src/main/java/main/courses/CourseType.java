package main.courses;

public class CourseType {

    public static int getDays(String type) {
        if (type == null) return 1;
        switch (type) {
            case "Advance Freediver Course": return 3;
            case "Freediver Course":
            case "Private Freediver Course": return 2;
            default: return 1;
        }
    }
}
