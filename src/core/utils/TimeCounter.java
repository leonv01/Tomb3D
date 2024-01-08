package core.utils;

public class TimeCounter {
    private int seconds;
    private int minutes;
    private int milliseconds;

    public TimeCounter() {
        seconds = 0;
        minutes = 0;
        milliseconds = 0;
    }

    public void update() {
        milliseconds++;
        if (milliseconds == 100) {
            seconds++;
            milliseconds = 0;
        }
        if (seconds == 60) {
            minutes++;
            seconds = 0;
        }
    }

    public String getTime() {
        return String.format("%02d:%02d:%02d", minutes, seconds, milliseconds);
    }
}
