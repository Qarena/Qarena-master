package projects.projects.qarena;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog pDialog;
    SessionManager session;
    SQLiteHandler db;
    String uid = new String();
    String email = new String();
    public Bitmap dp;
    ImageView dpView;
    private int PICK_IMAGE_REQUEST = 1;
    String TAG = AccountEdit.class.getSimpleName();
    EditText etQname, etQid, etAddress, etDescp, etPartNum, etQFee, etAgeTo, etAgeFrom, etDateTo, etDateFrom;
    TextView submit;
    int flag = 0;
    Calendar myCalendar = Calendar.getInstance();

    //--------------------------------------UTILITIES---------------------------------------------------------
    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if(flag==0)
        etDateFrom.setText(sdf.format(myCalendar.getTime()));
        else if(flag==1)
            etDateTo.setText(sdf.format(myCalendar.getTime()));

    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
//------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("hiiiii");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        System.out.println("hiiiii");
        etQid = (EditText) findViewById(R.id.etQid);
        etQname = (EditText) findViewById(R.id.etQname);
        etAddress = (EditText) findViewById(R.id.etQuiz_address);
        etDescp = (EditText) findViewById(R.id.etQuiz_descp);
        etAgeFrom=(EditText)findViewById(R.id.etAge_from);
        etAgeTo=(EditText)findViewById(R.id.etAge_to);
        //etDateTo.setOnClickListener(this) ;
        //etDateFrom.setOnClickListener(this);
        dpView = (ImageView) findViewById(R.id.quizDp);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        submit = (TextView) findViewById(R.id.submit);
        //submit.setOnClickListener(this);
        findViewById(R.id.quiz_locater).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String url = "http://maps.google.co.in/maps?q=" + URLEncoder.encode(etAddress.getText().toString(), "utf-8");
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    CreateEventActivity.this.startActivity(intent);
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
                        startActivity(new Intent(CreateEventActivity.this, ProfileActivity.class));
                        finish();
                        break;
                    case R.id.item2:
                        startActivity(new Intent(CreateEventActivity.this, QuizzesActivity.class));
                        finish();
                        break;
                    case R.id.item3:
                        startActivity(new Intent(CreateEventActivity.this, CreateEventActivity.class));
                        finish();
                        break;
                    case R.id.item4:
                        break;
                    case R.id.item5:
                        break;
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
    }
//--------------------------------CLICKS AWAY BABY !!!!----------------------------------------------
    @Override
    public void onClick(View v) {
        String qname, qid;

        qname = etQname.getText().toString().trim();

        // Progress dialog
        switch (v.getId()) {
            case R.id.submit:
                if(!isConnected())
                {
                    Toast.makeText(getApplicationContext(),"Not Connected to the Internet!",Toast.LENGTH_SHORT).show();
                    break;
                }
               // uploadQuiz();
                 break;
            case R.id.etQdate_from: flag=0;
                new DatePickerDialog(CreateEventActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.etQdate_to:flag=1;
                new DatePickerDialog(CreateEventActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;



        }


    }
    //----------------------------------------------------------------------------------

    private void uploadQuiz(final String uid, final Bitmap dp,
                        final String password, final String dob, final String country, final String state, final String city, final String fname, final String lname) {
        // Tag used to cancel the request
        String tag_string_req = "req_quizmake";

        pDialog.setMessage("Updating ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ACCOUNT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Edit Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        // JSONObject user = jObj.getJSONObject("user");
                        Toast.makeText(getApplicationContext(), "User successfully updated your account", Toast.LENGTH_LONG).show();
                        // Launch login activity
                        Intent intent = new Intent(
                                CreateEventActivity.this,
                                FirstActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("pro_pic", getStringImage(scaleDown(dp, 800, true)));
                params.put("password", password);
                params.put("dob", dob);
                params.put("country", country);
                params.put("state", state);
                params.put("city", city);
                params.put("first_name", fname);
                params.put("last_name", lname);
                params.put("user_id", uid);
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
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
        startActivity(intent);
        finish();
    }


    //--------------IMAGE UPLOAD---------------------------------------------------------------------

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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

