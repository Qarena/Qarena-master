package projects.projects.qarena.app;

public class AppConfig {
    // Server user login url
    // default:     https://qufinder.000webhostapp.com/
    //native:       http://192.168.0.100/qarena

    public static String BASE_URL = "https://qufinder.000webhostapp.com/";

    public static String URL_LOGIN = "http://35.198.203.61/utilities/login";

    // Server user register url
    public static String URL_REGISTER = "http://35.198.203.61/utilities/register_user";

    public static final String UPLOAD_URL = URL_REGISTER + "/uploadpic";
    public static final String GET_PICS_URL = URL_REGISTER + "/getpics";

    // Server user notification handling url
    public static String URL_NOTIFICATIONS = " https://qufinder.000webhostapp.com//notifications.php";

    // Server user friends url
    public static String URL_FRIENDS = " https://qufinder.000webhostapp.com//friends.php";

    // Server user profile url
    public static String URL_PROFILE = "http://35.198.203.61/utilities/get_profile_details";

    // Server user session url
    public static String URL_SESSION = " https://qufinder.000webhostapp.com//session.php";

    //account edit url
    public static String URL_ACCOUNT = " https://qufinder.000webhostapp.com//account.php";

    //graphics directory
    public static String URL_DP = " https://qufinder.000webhostapp.com//uploads";

    public static String signallingIp = "http://192.168.0.100:7800";

    public static String URL_ImageEndpoint = "http://35.198.203.61/utilities/image?file_name=";


    public static String URL_GMaps = "http://maps.google.co.in/maps?q=";



    public static String URL_QuizUpload = "http://35.198.203.61/utilities/set_ppt";

    //QuizEventActivity...
    public static String URL_CREATE_QUIZ = "http://35.198.203.61/utilities/create_quiz";
    //use this to create a new quiz...
    public static String URL_QuizDetails = "http://35.198.203.61/utilities/get_quiz_details";
    public static String URL_UPDATE_QUIZ = "http://35.198.203.61/utilities/create_quiz";
    //use this to update a given quiz, sending quiz_id in the PUT req...


    public static String URL_SEARCH_QUIZ = "http://35.198.203.61/utilities/search_quiz";
    //Basic or reg search in ProfileActivity, now provide an optional param user_id to get quizzes
    // filtered based on the user_id as res, along with the user_id in the res... city param is
    // also optional now, which was not intended, but we anyways use it while sending a req...

    public static String URL_ADVANCED_SEARCH_QUIZ =
            "http://35.198.203.61/utilities/advanced_search_quiz";
    //same comments as above in QuizzesActivity now, with datetime_to (quiz end date)
    // defaulting to the server time, if not sent... quizzes need to be filtered & sent based on
    // the city (optional now but needed in our case) & the datetime_to params sent to the
    // server, & compared there with the server's current timestamp for live status...


    //List of all the files uploaded by an user, using GET method...
    //public static String URL_QuizListAllFiles = "http://35.198.203.61/utilities/get_ppt?user_id=";

    //Retrieve a file uploaded, using POST method, with the help of file_name
    //public static String URL_QuizGetAFile = "http://35.198.203.61/utilities/get_ppt";
}
