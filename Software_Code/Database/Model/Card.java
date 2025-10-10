package Software_Code.Database.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "card")
public class Card {
    @Id
    private String cardId;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;

    public Card() {}
    public Card(String cardId, Student student) {
        this.cardId = cardId;
        this.student = student;
    }

    public String getCardId() { return cardId; }

    public void setCardId(String cardId) { this.cardId = cardId; }

    public Student getStudent() { return student; }

    public void setStudent(Student student) { this.student = student; }

    @Override
    public String toString() {
        return "Card{" +
                "cardId='" + cardId + '\'' +
                ", student=" + student +
                '}';
    }
}
