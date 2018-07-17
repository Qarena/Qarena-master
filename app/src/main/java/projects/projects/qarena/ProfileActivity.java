package projects.projects.qarena;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import projects.projects.qarena.activities.ViewUploadedQuizListActivity;
import projects.projects.qarena.app.AppConfig;
import projects.projects.qarena.app.AppController;
import projects.projects.qarena.helper.SQLiteHandler;
import projects.projects.qarena.helper.SessionManager;
import projects.projects.qarena.models.QuizEntity;

public class ProfileActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    PersonLite p;

    RecyclerView quizRecycler;
    ProfileQuizRecyclerAdapter adapter;
    ArrayList<QuizEntity> dataModelArrayList;

    private TextView tvName, tvPoints, tvLocation, tvDob, tvEmail;
    ImageView proPic;
    SessionManager session;
    SQLiteHandler db;

    private static final String TAG = ProfileActivity.class.getSimpleName();
    String uid = new String();
    String user_id;
    private TextView sortOption;
    private String city = "Kolkata";//TODO
    RatingBar ratingBar;

    public static final int REQUEST_EXTERNAL_PERMISSION_CODE = 666;
    private byte[] bytes;
    public static String quizFileUploadDocType = "application/pdf";//TODO change to ppt

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ratingBar = (RatingBar) findViewById(R.id.ratingbar);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvName = (TextView) findViewById(R.id.tvName);
        tvPoints = (TextView) findViewById(R.id.tvPoints);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        proPic = (ImageView) findViewById(R.id.profile_pic);
        sortOption = (TextView) findViewById(R.id.sort_option);

        sortOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] cities = new String[]{"Gurgaon","Kolkata", "Bhubaneswar", "Delhi", "Bangalore", "Mumbai", "Chennai", "Coimbatore", "Jaipur", "Ahmedabad", "Thiruvananthapuram"};
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Choose City")
                        .setItems(cities,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        city = cities[i];
                                        sortOption.setText(city);
                                        loadQuiz(city);
                                    }
                                })
                        .create().show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabi);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AccountEdit.class));
            }
        });

        //------------------------------------------------------------------------------------------
        db = new SQLiteHandler(getApplicationContext());

        // session manager checks is user is logged in or not
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        user_id = session.getUserId();

        //Toast.makeText(this, "The saved user id"+user_id, Toast.LENGTH_SHORT).show();
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("user_id");
        //------------------------------------------------------------------------------------------
        loadProfile(user_id);


        /*ImageLoader ir = VolleySingleton.getInstance().getImageLoader();
        String imageUrl = AppConfig.URL_DP + "//" + user_id + ".png";//TODO use correct url...
        ir.get(imageUrl, new com.android.volley.toolbox.ImageLoader.ImageListener() {
            @Override
            public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer response, boolean isImmediate) {
                // proPic.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error retrieving image!", Toast.LENGTH_SHORT).show();
            }
        });*/


        quizRecycler = (RecyclerView) findViewById(R.id.quiz_recycler);
        loadQuiz(city);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1:
                        startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                        finish();
                        break;
                    case R.id.item2:
                        startActivity(new Intent(ProfileActivity.this, QuizzesActivity.class));
                        finish();
                        break;
                    case R.id.item3:
                        startActivity(new Intent(ProfileActivity.this, CreateQuizEventActivity.class));
                        finish();
                        break;
                    /*case R.id.item4:
                        logoutUser();
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        /*tvUName = (TextView) findViewById(R.id.tvUName);
        tvUEmail = (TextView) findViewById(R.id.tvUEmail);
        loginDP=(ImageView)findViewById(R.id.loginDP);
        tvUName.setText(uid);
        tvUEmail.setText(email);
        ImageLoader ir = VolleySingleton.getInstance().getImageLoader();
        String imageUrl= AppConfig.URL_DP+"//"+uid+".png";
        ir.get(imageUrl, new com.android.volley.toolbox.ImageLoader.ImageListener() {
            @Override
            public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer response, boolean isImmediate) {
                loginDP.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        drawer.closeDrawer(GravityCompat.START);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

        }
        if (id == R.id.action_search) {
            Intent i = new Intent(getApplicationContext(), SearchResultsActivity.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.action_view_uploaded_quiz) {
            Intent i = new Intent(getApplicationContext(), ViewUploadedQuizListActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        session.setLogin(false, null);

        db.deleteUsers();

        // Launching the FirstActivity
        Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadQuiz(final String city) {
        String tag_string_quiz = "req_quiz";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                AppConfig.URL_Load_QUIZ, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Loading Quiz Response: " + response.toString());
                hideDialog();
                //Toast.makeText(ProfileActivity.this, response, Toast.LENGTH_SHORT).show();

                JSONArray quizArray;
                try {
                    dataModelArrayList = new ArrayList<>();
                    quizArray = new JSONArray(response);

                    for (int i = 0; i < quizArray.length(); i++) {
                        JSONObject quizDetail = quizArray.getJSONObject(i);
                        QuizEntity quizEntity = new QuizEntity();
                        quizEntity.setTitle(quizDetail.getString("title"));
                        //Toast.makeText(ProfileActivity.this,quizDetail.getString("title") , Toast.LENGTH_SHORT).show();
                        quizEntity.setDescription(quizDetail.getString("description"));
                        quizEntity.setQuizId(quizDetail.getString("quiz_id"));
                        //Toast.makeText(ProfileActivity.this,quizDetail.getString("description") , Toast.LENGTH_SHORT).show();

                        dataModelArrayList.add(quizEntity);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Toast.makeText(ProfileActivity.this, dataModelArrayList.toString(), Toast.LENGTH_SHORT).show();
                quizRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                adapter = new ProfileQuizRecyclerAdapter(ProfileActivity.this, dataModelArrayList);
                quizRecycler.setAdapter(adapter);

                //TODO
                /*click listener on a list item here in the recycler view to get to the
                CreateQuizEventActivity, with all the prefilled values gotten from the
                        URL_QuizDetails backend url endpoint, with the help of the quiz_id*/
                addListenersToRecycler();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to url
                Map<String, String> params = new HashMap<>();
                params.put("city", city);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_quiz);
    }

    private void addListenersToRecycler(){
        quizRecycler.addOnItemTouchListener(new RecyclerTouchListener(this,
                quizRecycler, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
//                Toast.makeText(ProfileActivity.this, "Single Click on position        :"+position,
//                        Toast.LENGTH_SHORT).show();
                QuizEntity selectedQuiz = adapter.getQuizEntity(position);
                addUpdateActivity(selectedQuiz);
            }

            @Override
            public void onLongClick(View view, int position) {
//                        Toast.makeText(ProfileActivity.this, "Long press on position :"+position,
//                                Toast.LENGTH_LONG).show();
            }
        }));
    }

    private void addUpdateActivity(QuizEntity quizEntity){
        Intent intent = new Intent(this, CreateQuizEventActivity.class);
        intent.putExtra(CreateQuizEventActivity.IS_UPDATING, true);
        intent.putExtra(CreateQuizEventActivity.QUIZ_ID, quizEntity.getQuizId());
        startActivity(intent);
    }

    private void loadProfile(final String uid) {

        // Tag used to cancel the request
        String tag_string_req = "req_profile";

        pDialog.setMessage("Loading Profile ...");
        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Loading Profile Response: " + response.toString());
                hideDialog();

                //Toast.makeText(ProfileActivity.this, response, Toast.LENGTH_SHORT).show();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);

                    tvName.setText(jsonObject.getString("first_name") + "  "
                            + jsonObject.getString("last_name"));
                    tvEmail.setText(jsonObject.getString("email"));
                    tvPoints.setText("Points :  " + jsonObject.getString("points"));
                    tvLocation.setText(jsonObject.getString("city") + " | "
                            + jsonObject.getString("current_state") + " | " + jsonObject.getString("country"));

                    String url = AppConfig.URL_ImageEndpoint + jsonObject.getString("pro_pic");

                    Glide.with(ProfileActivity.this).load(url).into(proPic);
                    ratingBar.setRating(Float.parseFloat(jsonObject.getString("rating")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", uid);
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void sendFriendRequest(final String uid, final String fid, final String type) {
        // Tag used to cancel the request
        String tag_string_req = "req_friend";

        pDialog.setMessage("Sending Request ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FRIENDS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String errorMsg = jObj.getString("message");
                    if (!error) {
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();

                        // Launch ProfileActivity
                        Intent intent = new Intent(
                                ProfileActivity.this,
                                ProfileActivity.class);
                        intent.putExtra("fid", fid);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error message
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
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
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("friend_id", fid);
                params.put("type", type);
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    public void uploadQuiz(View v) {
        showFileChooser();
    }

    private void showFileChooser() {
        Toast.makeText(this, "Please choose a quiz ppt document to upload...",
                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.setType(quizFileUploadDocType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);//

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a file to upload"),
                    1);//
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager app first...",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String getFilePathByUri(Context context, Uri uri) {
        String filepath = "";
        File file;

        if (uri.getScheme().toString().compareTo("content") == 0) {

            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{android.provider.MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.ORIENTATION},
                    null, null, null);

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String mImagePath = cursor.getString(column_index);//
            cursor.close();
            filepath = mImagePath;
            //cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        } else if (uri.getScheme().compareTo("file") == 0) {

            try {
                file = new File(new URI(uri.toString()));
                if (file.exists())
                    filepath = file.getAbsolutePath();

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            filepath = uri.getPath();
        }
        return filepath;
    }

    /**
     * Method used for encode the file to base64 binary format
     *
     * @param file
     * @return encoded file format
     */
    private String encodeFileToBase64Binary(File file) {
        String encodedfile = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            bytes = new byte[(int) file.length()];
            fileInputStreamReader.read(bytes);
            encodedfile = Base64.encodeBase64(bytes).toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return encodedfile;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1 && data != null && data.getData() != null) {
            Uri selectedFileURI = data.getData();


            //String path = data.getData().getPath();//useless

            final String filePath = getFilePathByUri(getApplicationContext(), selectedFileURI);
            //String path3 = FilePath.getPath(getApplicationContext(), selectedFileURI);//same as
            //the above

            if (filePath == null) {
                Toast.makeText(this, "Please move your selected file to a storage " +
                        "location on your device first & then retry...", Toast.LENGTH_LONG).show();

            } else if (!filePath.contains(".pdf")) {//TODO change to ppt
                showFileChooser();

            } else {
                //try {
                final File file = new File(filePath);
                encodeFileToBase64Binary(file);//lowered the targetSdkVersion to 22 to avoid runtime
                // permissions...


                // Tag used to cancel the request
                String tag_string_upload = "req_upload";

                pDialog = new ProgressDialog(ProfileActivity.this);
                pDialog.setIndeterminate(true);
                pDialog.setMessage("Uploading...");
                pDialog.show();


                VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                        AppConfig.URL_QuizUpload, new Response.Listener<NetworkResponse>() {

                    @Override
                    public void onResponse(NetworkResponse response) {
                        Log.d(TAG, "Quiz file upload network res status code: " + response
                                .statusCode);
                        hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(new String(response.data));
                            Log.d(TAG, "jObj : " + jObj);

                            boolean error = jObj.getBoolean("error");

                            if (!error)
                                //Inserting row in fileNames table
                                db.addFiles(new Date().toString(), filePath, file.getName());
                            else
                                Toast.makeText(getApplicationContext(), "Oops!!! Something went " +
                                        "wrong... Please retry...", Toast.LENGTH_SHORT).show();

                            String error_message = jObj.getString("message");
                            Log.d(TAG, error_message);
                            Toast.makeText(getApplicationContext(), error_message, Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "Quiz file upload err msg: " + error.getMessage());
                                //requestQueue.stop();
                                pDialog.dismiss();

                                if (error.networkResponse.statusCode == 413)
                                    Toast.makeText(getApplicationContext(), "Selected quiz file " +
                                                    "is too big in size... Please select a " +
                                                    "smaller file & retry...",
                                            Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(getApplicationContext(), "Some network issue " +
                                            "occurred while uploading the selected file... Please" +
                                            " retry in sometime...", Toast
                                            .LENGTH_SHORT).show();
                            }
                        })
                {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("user_id", user_id);
                        return params;
                    }

                    @Override
                    protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                        Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                        params.put("ppt_file", new DataPart(file.getName(), bytes, quizFileUploadDocType));
                        return params;
                    }
                };
                {
                    int socketTimeout = 30000;
                    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy
                            .DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);//
                    volleyMultipartRequest.setRetryPolicy(policy);
                    AppController.getInstance().addToRequestQueue(volleyMultipartRequest, tag_string_upload);
                }
            }
        }
    }

    public static interface ClickListener {
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clickListener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clickListener){

            this.clickListener=clickListener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clickListener!=null){
                        clickListener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clickListener!=null && gestureDetector.onTouchEvent(e)){
                clickListener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
