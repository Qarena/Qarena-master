package projects.projects.qarena;

/**
 * Created by Arka Bhowmik on 2/18/2017.
 */
public class PersonLite {
    private String id;
    private String name;
    private String email;
    private String points;
    private String country;
    private String city;
    private String state;
    private String dob;
    private String ratings;
    private Double locLat, locLong;
    private String dp;
    private int isOnline;
    //note that here we are getting the src for the image and in the imageloader we are using that src only
    public PersonLite() {
    }

    public PersonLite(String id, String name, int isonline, double Lat, double Long, String dp) {
        this.name = name;
        this.id = id;
        this.isOnline = isonline;
        this.dp = dp;
        this.locLat = Lat;
        this.locLong = Long;

    }
    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getRatings() {
        return ratings;
    }

    public PersonLite(String name, String email, String points, String country, String city, String state, String dob, String ratings) {
        this.name = name;
        this.email = email;
        this.points = points;
        this.country = country;
        this.city = city;
        this.state = state;
        this.dob = dob;
        this.ratings = ratings;
    }

    public void setRatings(String ratings) {

        this.ratings = ratings;
    }

    public String getDob() {

        return dob;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {

        return email;
    }
    public void setPoints(String points) {
        this.points = points;
    }

    public String getPoints() {

        return points;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

}

