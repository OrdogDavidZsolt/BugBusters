package hu.bugbusters.checkinapp.backendserver.maincomponents.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import hu.bugbusters.checkinapp.backendserver.timer.Timer;

@Service
public class TimingService
{
    /**
     * Ebben az osztályban vannak az időzítéssel kapcsolatos feladatok
     */    

    private static List<Timer> timers = new ArrayList<>();

    public static void startNewTimer(int minutes, int seconds)
    {
        Timer timer = new Timer(minutes, seconds);
        timer.setTimerListener(t -> {
            System.out.println("TimingService >> A timer lejárt! " + t.getRemainingTimeFormatted());
            TimingService.timers.remove(t);
        });
        timer.start();
        timers.add(timer);
    }

    public static List<Timer> getTimers()
    {
        return TimingService.timers;
    }
}
