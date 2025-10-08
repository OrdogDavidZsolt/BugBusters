package Software_Code.Database.Model;

public class Teacher {
    private int teacher_id;
    private String name;
    private String email;

    //Constructor for retrieving from DB (with id)
    public Teacher(int id, String name, String email) {
        this.teacher_id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return teacher_id;
    }

    public void setId(int id) {
        this.teacher_id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "teacher_id=" + teacher_id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
