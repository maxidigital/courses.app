package main.calendar;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import main.courses.menus.CourseStartTimeMenu;
import main.sheets.SettingsService;

public class EventDetailsParser {

    public static class Result {
        public final boolean found;
        public final String times;
        public final String locs;

        private Result(boolean found, String times, String locs) {
            this.found = found;
            this.times = times;
            this.locs = locs;
        }

        public static Result notFound() { return new Result(false, "", ""); }
        public static Result of(String times, String locs) { return new Result(true, times, locs); }
    }

    private static final Pattern BLOCK = Pattern.compile("(?s)#DETAILS#\\{(.*?)\\}");
    private static final Pattern LINE  = Pattern.compile("\\s*Day \\d+: (.+?) - (.+)");

    public static String getRawContent(String rawDescription) {
        if (rawDescription == null) return null;
        Matcher m = BLOCK.matcher(rawDescription);
        if (!m.find()) return null;
        return m.group(1).trim();
    }

    public static Result parse(String rawDescription) {
        if (rawDescription == null) return Result.notFound();

        Matcher blockMatcher = BLOCK.matcher(rawDescription);
        if (!blockMatcher.find()) return Result.notFound();

        String block = blockMatcher.group(1).trim();
        StringBuilder times = new StringBuilder();
        StringBuilder locs  = new StringBuilder();

        for (String line : block.split("\\n")) {
            Matcher m = LINE.matcher(line.trim());
            if (!m.find()) return Result.notFound();

            String timeStr = m.group(1).trim();
            String locName = m.group(2).trim();

            int timeIdx = reverseTimeIndex(timeStr);
            int locIdx  = reverseLocIndex(locName);

            if (timeIdx < 0 || locIdx < 0) return Result.notFound();

            times.append(timeIdx);
            locs.append(locIdx);
        }

        if (times.length() == 0) return Result.notFound();
        return Result.of(times.toString(), locs.toString());
    }

    private static int reverseTimeIndex(String timeStr) {
        for (int i = 0; i < 8; i++) {
            if (CourseStartTimeMenu.slotLabel(i).equals(timeStr)) return i;
        }
        return -1;
    }

    private static int reverseLocIndex(String locName) {
        List<String> names = SettingsService.getInstance().getLocationNames();
        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).equals(locName)) return i;
        }
        return -1;
    }
}
