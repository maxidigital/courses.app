package main.courses;

public enum CourseType {

    FREEDIVER_COURSE        ("Freediver Course",                2, "5–6 hours"),
    PRIVATE_FREEDIVER_COURSE("Private Freediver Course",        2, "5–6 hours"),
    ADVANCE_FREEDIVER_COURSE("Advance Freediver Course",        3, "5–6 hours"),
    DISCOVER_FREEDIVING     ("Discover Freediving",             1, "5–6 hours"),
    STATIC_APNEA            ("Static Apnea",                    1, "5–6 hours"),
    EXPEDITION              ("Expedition",                      1, "3–4 hours"),
    COACHING                ("Coaching",                        1, "3–4 hours"),
    TRAINING                ("Training",                        1, "3–4 hours"),
    PRIVATE_ADVENTURES      ("Private Freediving Adventures",   1, "3–4 hours"),
    UNKNOWN                 (null,                              1, "");

    public final String calendarName;
    public final int days;
    public final String duration;

    CourseType(String calendarName, int days, String duration) {
        this.calendarName = calendarName;
        this.days = days;
        this.duration = duration;
    }

    public static CourseType fromName(String name) {
        if (name == null) return UNKNOWN;
        for (CourseType ct : values()) {
            if (name.equals(ct.calendarName)) return ct;
        }
        return UNKNOWN;
    }

    /** Convenience — kept for callers that only need day count */
    public static int getDays(String name) {
        return fromName(name).days;
    }
}
