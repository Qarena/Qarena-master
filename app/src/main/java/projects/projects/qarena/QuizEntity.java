package projects.projects.qarena;

/**
 * Created by HP on 06-Aug-17.
 */
public class QuizEntity {
    String picUrl;
    String title;
    String quizId;
    String description;
    String age_res, age_to;
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

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getAge_res() {
        return age_res;
    }

    public void setAge_res(String age_res) {
        this.age_res = age_res;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMax_part() {
        return max_part;
    }

    public void setMax_part(int max_part) {
        this.max_part = max_part;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTime_to() {
        return time_to;
    }

    public void setTime_to(String time_to) {
        this.time_to = time_to;
    }

    public String getTime_from() {
        return time_from;
    }

    public void setTime_from(String time_from) {
        this.time_from = time_from;
    }

    public String getOrganizer_id() {
        return organizer_id;
    }

    public void setOrganizer_id(String organizer_id) {
        this.organizer_id = organizer_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAge_to() {
        return age_to;
    }

    public void setAge_to(String age_to) {
        this.age_to = age_to;
    }
}
