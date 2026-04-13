package main.courses;

public enum CourseType {

    FREEDIVER_COURSE        ("Freediver Course",                2, "5–6 hours", new String[]{
        "We'll deep dive into physics and physiology, understanding the urge to breathe and professional equipment setup. In the water, we'll focus on buoyancy testing, duck-dive refinement, and finding your 'line' body position, with dives up to 12m.",
        "We'll cover Pranayamas (yogic breathing) for deep relaxation, safety, rescue procedures, and buddy protocols — the true home of freediving. In the water, you'll apply your safety skills and reach comfortable dives up to 20m at your own pace."
    }),
    PRIVATE_FREEDIVER_COURSE("Private Freediver Course",        1, "5–6 hours", new String[]{
        "A completely tailored learning experience designed around you, with full attention across theory and water sessions."
    }),
    ADVANCE_FREEDIVER_COURSE("Advance Freediver Course",        3, "5–6 hours", new String[]{
        "We'll focus on relaxation, breathing techniques, and extending your breath-hold in a calm, controlled way.",
        "We'll work on improving your freefall, efficiency in the water, and refining your equalisation techniques.",
        "This is where everything comes together — applying your technique, relaxation, and equalisation at depth, always at your own pace and within your comfort zone."
    }),
    DISCOVER_FREEDIVING     ("Discover Freediving",             1, "5–6 hours", new String[]{
        "We'll start with an intro to freediving disciplines, Boyle's Law, and equipment familiarization, focusing on relaxation and feeling the water. In the water, you'll practice efficient breathing, duck-dives, and kicking, with guided dives up to 12m — zero pressure."
    }),
    STATIC_APNEA            ("Static Apnea",                    1, "5–6 hours", new String[]{
        "We'll cover advanced relaxation and mind-control techniques along with training routines for long-term breath-hold improvement. In the water, guided static sessions will focus on body position and extending your comfort zone with full instructor support."
    }),
    EXPEDITION              ("Expedition",                      1, "3–4 hours", new String[]{
        "A beautiful guided dive at one selected spot, exploring caves, swim-throughs, and marine life depending on the day's conditions."
    }),
    COACHING                ("Coaching",                        1, "3–4 hours", new String[]{
        "A personalised in-water session focused on your goals, with direct feedback to help you progress with confidence and ease."
    }),
    TRAINING                ("Training",                        1, "3–4 hours", new String[]{
        "A relaxed buddy training session where you dive together, support each other, and build confidence through shared practice in the water."
    }),
    PRIVATE_ADVENTURES      ("Private Freediving Adventures",   1, "3–4 hours", new String[]{
        "A fully personalised experience at one chosen location, combining relaxed diving, exploration, and enjoyment at your own pace."
    }),
    UNKNOWN                 (null,                              1, "",           null);

    public final String calendarName;
    public final int days;
    public final String duration;
    public final String[] dayDescriptions;

    CourseType(String calendarName, int days, String duration, String[] dayDescriptions) {
        this.calendarName = calendarName;
        this.days = days;
        this.duration = duration;
        this.dayDescriptions = dayDescriptions;
    }

    public String getDayDescription(int dayIndex, String fallback) {
        if (dayDescriptions != null && dayIndex < dayDescriptions.length) return dayDescriptions[dayIndex];
        return fallback;
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
