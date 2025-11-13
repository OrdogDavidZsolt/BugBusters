package Timer;

public class Timer
{
    /**
     * Ez az osztály valósít meg minden olyan implementéciót, ami a timerekkel kapcsolatos
     */

    private final int initialMinutes;
    private final int initialSeconds;
    private final long totalMillis;

    private volatile boolean running = false;
    private long startTime;
    private Thread thread;

    public Timer(int minutes, int seconds) {
        this.initialMinutes = minutes;
        this.initialSeconds = seconds;
        this.totalMillis = (minutes * 60L + seconds) * 1000L;
    }

    public void start() {
        if (running) return;

        running = true;
        startTime = System.currentTimeMillis();

        thread = new Thread(() -> {
            try {
                while (true) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    long remaining = totalMillis - elapsed;

                    if (remaining <= 0) {
                        System.out.println("Timer ended SIGNAL!");
                        break;
                    }
                    Thread.sleep(200);  // free the CPU for 200 ms
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        thread.start();
    }

    public int getRemainingMinutes() {
        long remainingMillis = getRemainingMillis();
        return (int) (remainingMillis / 1000 / 60);
    }

    public int getRemainingSeconds() {
        long remainingMillis = getRemainingMillis();
        return (int) ((remainingMillis / 1000) % 60);
    }

    public String getRemainingTimeFormatted() {
        return String.format("%02d:%02d", getRemainingMinutes(), getRemainingSeconds());
    }

    public long getRemainingMillis() {
        if (!running) return totalMillis;
        long elapsed = System.currentTimeMillis() - startTime;
        long remaining = totalMillis - elapsed;
        return Math.max(remaining, 0);
    }
}