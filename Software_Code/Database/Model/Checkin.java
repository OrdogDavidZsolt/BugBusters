package Software_Code.Database.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "checkin")
public class Checkin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private LocalDateTime checkinTime = LocalDateTime.now();

    public Checkin() {}
    public Checkin(Card card, Course course) {
        this.card = card;
        this.course = course;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public Card getCard() { return card; }

    public void setCard(Card card) { this.card = card; }

    public Course getCourse() { return course; }

    public void setCourse(Course course) { this.course = course; }

    public LocalDateTime getCheckinTime() { return checkinTime; }

    public void setCheckinTime(LocalDateTime checkinTime) { this.checkinTime = checkinTime; }

    @Override
    public String toString() {
        return "CheckIn{" +
                "id=" + id +
                ", card=" + card +
                ", course=" + course +
                ", checkinTime=" + checkinTime +
                '}';
    }
}
