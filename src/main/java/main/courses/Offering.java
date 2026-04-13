package main.courses;

public enum Offering {

    FREEDIVER_COURSE        ("Freediver Course",                ProductType.COURSE,    2, "5–6 hours", "https://freedive-mallorca.com/freediver-course-mallorca/", new String[]{
        "We'll deep dive into physics and physiology, understanding the urge to breathe and professional equipment setup. In the water, we'll focus on buoyancy testing, duck-dive refinement, and finding your 'line' body position, with dives up to 12m.",
        "We'll cover Pranayamas (yogic breathing) for deep relaxation, safety, rescue procedures, and buddy protocols — the true home of freediving. In the water, you'll apply your safety skills and reach comfortable dives up to 20m at your own pace."
    }),
    PRIVATE_FREEDIVER_COURSE("Private Freediver Course",        ProductType.COURSE,    1, "5–6 hours", "https://freedive-mallorca.com/freediver-course-mallorca/private-freediver-course/", new String[]{
        "A completely tailored learning experience designed around you, with full attention across theory and water sessions."
    }),
    ADVANCE_FREEDIVER_COURSE("Advance Freediver Course",        ProductType.COURSE,    3, "5–6 hours", "https://freedive-mallorca.com/advanced-course-mallorca/", new String[]{
        "We'll focus on relaxation, breathing techniques, and extending your breath-hold in a calm, controlled way.",
        "We'll work on improving your freefall, efficiency in the water, and refining your equalisation techniques.",
        "This is where everything comes together — applying your technique, relaxation, and equalisation at depth, always at your own pace and within your comfort zone."
    }),
    DISCOVER_FREEDIVING     ("Discover Freediving",             ProductType.COURSE,    1, "5–6 hours", "https://freedive-mallorca.com/discover-freediving-mallorca/", new String[]{
        "We'll start with an intro to freediving disciplines, Boyle's Law, and equipment familiarization, focusing on relaxation and feeling the water. In the water, you'll practice efficient breathing, duck-dives, and kicking, with guided dives up to 12m — zero pressure."
    }),
    STATIC_APNEA            ("Static Apnea",                    ProductType.COURSE,    1, "5–6 hours", "https://freedive-mallorca.com/static-course-freedive-mallorca/", new String[]{
        "We'll cover advanced relaxation and mind-control techniques along with training routines for long-term breath-hold improvement. In the water, guided static sessions will focus on body position and extending your comfort zone with full instructor support."
    }),
    EXPEDITION              ("Expedition",                      ProductType.ACTIVITY,  1, "3–4 hours", "https://freedive-mallorca.com/expeditions/", new String[]{
        "A beautiful guided dive at one selected spot, exploring caves, swim-throughs, and marine life depending on the day's conditions."
    }),
    COACHING                ("Coaching",                        ProductType.ACTIVITY,  1, "3–4 hours", "https://freedive-mallorca.com/coaching/", new String[]{
        "A personalised in-water session focused on your goals, with direct feedback to help you progress with confidence and ease."
    }),
    TRAINING                ("Training",                        ProductType.ACTIVITY,  1, "3–4 hours", "https://freedive-mallorca.com/training-session/", new String[]{
        "A relaxed buddy training session where you dive together, support each other, and build confidence through shared practice in the water."
    }),
    PRIVATE_ADVENTURES      ("Private Freediving Adventures",   ProductType.ACTIVITY,  1, "3–4 hours", "https://freedive-mallorca.com/private-freediving-adventures/", new String[]{
        "A fully personalised experience at one chosen location, combining relaxed diving, exploration, and enjoyment at your own pace."
    }),
    UNKNOWN                 (null,                              ProductType.COURSE,    1, "",           null, null);

    public final String calendarName;
    public final ProductType productType;
    public final int days;
    public final String duration;
    public final String url;
    public final String[] dayDescriptions;

    Offering(String calendarName, ProductType productType, int days, String duration, String url, String[] dayDescriptions) {
        this.calendarName = calendarName;
        this.productType = productType;
        this.days = days;
        this.duration = duration;
        this.url = url;
        this.dayDescriptions = dayDescriptions;
    }

    public String getDayDescription(int dayIndex, String fallback) {
        if (dayDescriptions != null && dayIndex < dayDescriptions.length) return dayDescriptions[dayIndex];
        return fallback;
    }

    public static Offering fromName(String name) {
        if (name == null) return UNKNOWN;
        for (Offering p : values()) {
            if (name.equals(p.calendarName)) return p;
        }
        return UNKNOWN;
    }

    public static int getDays(String name) {
        return fromName(name).days;
    }
}
