package projects.projects.qarena;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import projects.projects.qarena.app.AppConfig;
import projects.projects.qarena.app.AppController;
import projects.projects.qarena.helper.SQLiteHandler;
import projects.projects.qarena.helper.SessionManager;

/**
 * Created by Arka Bhowmik on 2/22/2017.
 */
public class QuizEventActivity extends AppCompatActivity  {

    ProgressDialog pDialog;
    SessionManager session;
    SQLiteHandler db;

    String uid = new String();
    String email = new String();

    public Bitmap dp;
    ImageView dpView;
    private int PICK_IMAGE_REQUEST = 1;

    String TAG = AccountEdit.class.getSimpleName();
    EditText etQname, etQid, etAddress, etDescp, etPartNum,
            etQFee, etAgeTo, etAgeFrom, etDateTo, etDateFrom, etQuiz_descp;
    TextView submit,winner;
    //int flag = 0;
    Calendar myCalendar;
    public static final String IS_UPDATING = "IsUpdating";
    public static final String QUIZ_ID = "QuizId";
    private boolean isUpdate = false;
    private String quizId = "";
    private QuizEntity quizEntity = new QuizEntity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        etQid = (EditText) findViewById(R.id.etQid);
        etQname = (EditText) findViewById(R.id.etQname);
        etAddress = (EditText) findViewById(R.id.etQuiz_address);
        etDescp = (EditText) findViewById(R.id.etQuiz_descp);
        etAgeFrom=(EditText)findViewById(R.id.etAge_from);
        etAgeTo=(EditText)findViewById(R.id.etAge_to);
        etPartNum=(EditText) findViewById(R.id.etQmaxPart);
        etQFee=(EditText)findViewById(R.id.etQfee);
        winner=(TextView)findViewById(R.id.winnertxt);

        //etDateTo.setOnClickListener(this) ;
        //etDateFrom.setOnClickListener(this);
        dpView = (ImageView) findViewById(R.id.quizDp);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        submit = (TextView) findViewById(R.id.submitQuiz);
        myCalendar = Calendar.getInstance();
        etDateFrom=(EditText)findViewById(R.id.etQdate_from);
        etDateTo=(EditText)findViewById(R.id.etQdate_to);
        //etTimeTo;

        final EditText etTimeFrom=(EditText)findViewById(R.id.etQTime_from);
        etTimeFrom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(QuizEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etTimeFrom.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                //Toast.makeText(QuizEventActivity.this, etTimeFrom.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

       final EditText etTimeTo=(EditText)findViewById(R.id.etTime_to);
        etTimeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(QuizEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etTimeTo.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                //Toast.makeText(QuizEventActivity.this, etTimeTo.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        final DatePickerDialog.OnDateSetListener datefrom = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelDateFrom();
            }

        };

        final DatePickerDialog.OnDateSetListener dateto = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelDateTo();
            }

        };

        etDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(QuizEventActivity.this, dateto, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etDateFrom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(QuizEventActivity.this, datefrom, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        updateLabelDateFrom();
        updateLabelDateTo();

        //submit.setOnClickListener(this);
        findViewById(R.id.quiz_locater).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String url = "http://maps.google.co.in/maps?q=" + URLEncoder.encode(etAddress.getText().toString(), "utf-8");
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    QuizEventActivity.this.startActivity(intent);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        dpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.getMenu().findItem(R.id.item4).setVisible(false);
        navigationView.getMenu().findItem(R.id.item5).setVisible(false);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1:
                        startActivity(new Intent(QuizEventActivity.this, ProfileActivity.class));
                        finish();
                        break;
                    case R.id.item2:
                        startActivity(new Intent(QuizEventActivity.this, QuizzesActivity.class));
                        finish();
                        break;
                    case R.id.item3:
                        startActivity(new Intent(QuizEventActivity.this, QuizEventActivity.class));
                        finish();
                        break;
                    /*case R.id.item4:
                        break;
                    case R.id.item5:
                        break;*/
                }
                return true;
            }
        });

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        //-----------------------------------------------------------------------------
        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager checks is user is logged in or not
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("user_id");
        email = user.get("email");

        etQid.setText(uid);
        //----------------------------------------------------------------------------

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String useridQ=session.getUserId();
                String qTitle=etQname.getText().toString();
                String qDescp=etDescp.getText().toString();
                String partnum=etPartNum.getText().toString();
                String qFrom=etDateFrom.getText().toString()+" "+etTimeFrom.getText().toString();
                String qTo=etDateTo.getText().toString()+" "+etTimeTo.getText().toString();
                String address=etAddress.getText().toString();
                String fee=etQFee.getText().toString();
                String ageTo=etAgeTo.getText().toString();
                String ageFrom=etAgeFrom.getText().toString();

                if(isUpdate)
                    updateQuizEvent(useridQ,
                            qTitle,
                            qDescp,
                            partnum,
                            qFrom,
                            qTo,
                            address,
                            fee,ageTo,
                            ageFrom);
                else
                    createQuizEvent(useridQ,
                                    qTitle,
                                    qDescp,
                                    partnum,
                                    qFrom,
                                    qTo,
                                    address,
                                    fee,ageTo,
                                    ageFrom);

                String textone=useridQ+" "+
                        qTitle+" "+qDescp+" "+
                        partnum+" "+
                        qFrom+" "+qTo
                        +" "+
                        address+" "+
                        fee+" "+
                        ageTo+" "+
                        ageFrom;

                winner.setText(textone);
            }
        });

        checkForUpdate();
    }

//--------------------------------------UTILITIES---------------------------------------------------------

    private void checkForUpdate(){
        isUpdate = getIntent().getBooleanExtra(IS_UPDATING, false);
        changeLabels();
        if (isUpdate) {
            quizId = getIntent().getStringExtra(QUIZ_ID);
            fetchQuiz();
        }
    }

    private void changeLabels(){
        if (isUpdate){
            getSupportActionBar().setTitle("Update Quiz Event");
        } else {
            getSupportActionBar().setTitle("Create Quiz Event");
        }

    }

    private void loadQuizDetails(){
        etAddress.setText(quizEntity.getAddress());
        etDateFrom.setText(quizEntity.getTime_from());
        etDateTo.setText(quizEntity.getTime_to());
        etDescp.setText(quizEntity.getDescription());
        etQname.setText(quizEntity.getTitle());
        etAgeFrom.setText(quizEntity.getAge_res());
        etAgeTo.setText(quizEntity.getAge_to());
        etQFee.setText(quizEntity.getPrice());
        etPartNum.setText(String.valueOf(quizEntity.getMax_part()));
    }

    private void fetchQuiz(){

        // Tag used to cancel the request
        String tag_string_req = "req_fetch_quiz_event";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_QuizDetails, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Quiz event fetch res: " + response.toString());
                hideDialog();

                JSONObject jObj;
                try {
                    jObj = new JSONObject(response);

                    String description = jObj.getString("description");
                    String address = jObj.getString("address");
                    String prizes = jObj.getString("prizes");
                    String age_range_from = jObj.getString("age_range_from");
                    String age_range_to = jObj.getString("age_range_to");
                    String category = jObj.getString("category");
                    String title = jObj.getString("title");
                    double price = jObj.getDouble("price");
                    String datetime_from = jObj.getString("datetime_from");
                    String datetime_to = jObj.getString("datetime_to");
                    String level = jObj.getString("level");
                    String quizId = jObj.getInt("quiz_id") + "";
                    String userId = jObj.getInt("user_id") + "";
                    String state = jObj.getString("state");
                    String city = jObj.getString("city");
                    int max_count = jObj.getInt("participant_count_max");
                    String cpl_name = jObj.getString("cpl_name");

                    quizEntity.setAddress(address);
                    quizEntity.setAge_res(age_range_from);
                    quizEntity.setCategory(category);
                    quizEntity.setDescription(description);
                    quizEntity.setLevel(level);
                    quizEntity.setPrice(price+"");
                    quizEntity.setPrize(prizes);
                    quizEntity.setTitle(title);
                    quizEntity.setTime_from(datetime_from);
                    quizEntity.setTime_to(datetime_to);
                    quizEntity.setQuizId(quizId);
                    quizEntity.setOrganizer_id(userId);
                    quizEntity.setShortAddress(city + "," + state);
                    quizEntity.setMax_part(max_count);
                    quizEntity.setPicUrl(cpl_name);
                    quizEntity.setAge_to(age_range_to);

                    loadQuizDetails();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Quiz event updation err: " + error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to url
                Map<String, String> params = new HashMap<String, String>();
                params.put("quiz_id", quizId);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void updateLabelDateFrom() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        etDateFrom.setText(sdf.format(myCalendar.getTime()));
        //Toast.makeText(this, etDateFrom.getText(), Toast.LENGTH_SHORT).show();
    }

    private void updateLabelDateTo() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        etDateTo.setText(sdf.format(myCalendar.getTime()));
        //Toast.makeText(this, etDateTo.getText(), Toast.LENGTH_SHORT).show();
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
//------------------------------------------------------------------------------------------------------------------------
    private void updateQuizEvent(final String uid, final String title, final String description,
                                 final String partc_count, final String date_from,
                                 final String date_to,final String address,final String price,
                                 final String age_range_to, final String age_range_from) {

        // Tag used to cancel the request
        String tag_string_req = "req_update_quiz_event";

        pDialog.setMessage("Updating quiz event...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.PUT,
                AppConfig.URL_UPDATE_QUIZ, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Quiz event updation res: " + response.toString());
                hideDialog();

                JSONObject jObj;
                try {
                    jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        Toast.makeText(getApplicationContext(), "Successfully update quiz " +
                                "event...", Toast.LENGTH_LONG).show();

                        // Launch FirstActivity
                        Intent intent = new Intent(
                                QuizEventActivity.this,
                                FirstActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // Error occurred in creation. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Quiz event updation err: " + error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                // Posting params to url
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", title);//name
                params.put("description", description);
                params.put("participant_count_max", partc_count);
                params.put("datetime_from", date_from);
                params.put("datetime_to", date_to);
                params.put("price", price);//participation cost
                params.put("age_range_to", age_range_to);
                params.put("age_range_from", age_range_from);
                params.put("address", address);//

                params.put("user_id", uid);//imp...
                params.put("category","Cars");//TODO code not in place...
                params.put("level","Local");//TODO code not in place...

                params.put("gps","0");//?
                params.put("maps_link","");

                params.put("state","Gurgaon");//?
                params.put("city","Kolkata");


                params.put("prizes","20");
                params.put("cplarge","");//cash prize 2...?
                params.put("cpsmall","");//cash prize 1...?

                params.put("quiz_id", quizId);//newly added...
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void createQuizEvent(final String uid, final String title, final String description,
                            final String partc_count, final String date_from,
                            final String date_to,final String address,final String price,
                            final String age_range_to, final String age_range_from) {

        // Tag used to cancel the request
        String tag_string_req = "req_create_quiz_event";

        pDialog.setMessage("Creating quiz event...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CREATE_QUIZ, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Quiz event creation res: " + response.toString());
                hideDialog();

                JSONObject jObj;
                try {
                    jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        Toast.makeText(getApplicationContext(), "Successfully created a quiz " +
                                        "event...", Toast.LENGTH_LONG).show();

                        // Launch FirstActivity
                        Intent intent = new Intent(
                                QuizEventActivity.this,
                                FirstActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // Error occurred in creation. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Quiz event creation err: " + error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                // Posting params to url
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", title);//name
                params.put("description", description);
                params.put("participant_count_max", partc_count);
                params.put("datetime_from", date_from);
                params.put("datetime_to", date_to);
                params.put("price", price);//participation cost
                params.put("age_range_to", age_range_to);
                params.put("age_range_from", age_range_from);
                params.put("address", address);//

                params.put("user_id", uid);//imp...
                params.put("category","Cars");//TODO code not in place...
                params.put("level","Local");//TODO code not in place...

                params.put("gps","0");//?
                params.put("maps_link","");

                params.put("state","Gurgaon");//?
                params.put("city","Kolkata");


                params.put("prizes","20");
                params.put("cplarge","");//cash prize 2...?
                params.put("cpsmall","");//cash prize 1...?

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void logoutUser() {
        session.setLogin(false,null);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
        startActivity(intent);
        finish();
    }


    //--------------IMAGE UPLOAD---------------------------------------------------------------------

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an image"),
                PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                dp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                dpView.setImageBitmap(dp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());
        ;
        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    //-----------------------------------------ONLINE STUFF--------------------------------------------------

}

