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
    public static String URL_QUIZ = "http://35.198.203.61/utilities/search_quiz";
    public static String URL_CREATE_QUIZ="http://35.198.203.61/utilities/create_quiz";

    public static String URL_FRIENDS = " https://qufinder.000webhostapp.com//friends.php";

    // Server user profile url
    public static String URL_PROFILE = "http://35.198.203.61/utilities/get_profile_details";

    // Server user session url
    public static String URL_SESSION = " https://qufinder.000webhostapp.com//session.php";

    //account edit url
    public static String URL_ACCOUNT = " https://qufinder.000webhostapp.com//account.php";

    //graphics directory
    public static String URL_DP = " https://qufinder.000webhostapp.com//uploads";

    public static String signallingIp="http://192.168.0.100:7800";

    public static String URL_SearchQuiz = "http://35.198.203.61/utilities/advanced_search_quiz";
    public static String URL_ImageEndpoint = "http://35.198.203.61/utilities/image?file_name=";

    public static String URL_QuizUpload = "http://35.198.203.61/utilities/set_ppt";
}
