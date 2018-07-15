package projects.projects.qarena.models;

/**
 * Created by HP on 06-Aug-17.
 */
public class QuizEntity {
    public String picUrl;
    public String title;
    public String quizId;
    public String description;
    public String age_res;
    public String level;
    public int mode;
    public int status;
    public int max_part;
    public String category;
    public String time_to, time_from;
    public String organizer_id;
    public String address;
    public String shortAddress;
    public String prize;
    public String price;

    public QuizEntity(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public QuizEntity() {

    }

    public QuizEntity(String title, String quizId, String description) {
        this.title = title;
        this.quizId = quizId;
        this.description = description;
    }

    public void setQuizId(String quizId) {

        this.quizId = quizId;
    }

    public String getQuizId() {

        return quizId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {

        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {

        return description;
    }
}
