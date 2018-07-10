package projects.projects.qarena;

/**
 * Created by HP on 06-Aug-17.
 */
public class QuizEntity {
    String picUrl;
    String title;
    String quizId;
    String description;
    String age_res;
    String level;
    int mode;
    int status;
    int max_part;
    String category;
    String time_to,time_from;
    String organizer_id;
    String address;
    String shortAddress;
    String prize;

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

    String price;
}
