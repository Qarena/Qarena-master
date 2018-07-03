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
import android.os.Environment;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import projects.projects.qarena.app.AppConfig;
import projects.projects.qarena.app.AppController;
import projects.projects.qarena.app.VolleySingleton;
import projects.projects.qarena.helper.SQLiteHandler;
import projects.projects.qarena.helper.SessionManager;

public class ProfileActivity extends AppCompatActivity  {

    private String jsonResponse;//
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
    private String city = "Kolkata";
    RatingBar ratingBar;

    private String file_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ratingBar=(RatingBar)findViewById(R.id.ratingbar);
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
                final String[] cities = new String[]{"Kolkata", "Jaynagar", "Raniganj", "Bhubaneswar", "Delhi", "Bangalore", "Mumbai", "Chennai", "Coimbatore", "Jaipur", "Ahmedabad", "Thiruvananthapuram"};
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
                System.out.println("hi i am not working");
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
        user_id= session.getUserId();
        //Toast.makeText(this, "The saved user id"+user_id, Toast.LENGTH_SHORT).show();
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("user_id");
        //------------------------------------------------------------------------------------------
        loadProfile(user_id);


        ImageLoader ir = VolleySingleton.getInstance().getImageLoader();
        String imageUrl = AppConfig.URL_DP + "//" + uid + ".png";
        ir.get(imageUrl, new com.android.volley.toolbox.ImageLoader.ImageListener() {
            @Override
            public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer response, boolean isImmediate) {
               // proPic.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error retrieving image!", Toast.LENGTH_SHORT).show();
            }
        });


        quizRecycler = (RecyclerView) findViewById(R.id.quiz_recycler);
        loadQuiz("Kolkata");
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1:
                        startActivity(new Intent(ProfileActivity.this,ProfileActivity.class));
                        finish();
                        break;
                    case R.id.item2:
                        startActivity(new Intent(ProfileActivity.this,QuizzesActivity.class));
                        finish();
                        break;
                    case R.id.item3:
                        startActivity(new Intent(ProfileActivity.this, CreateEventActivity.class));
                        finish();
                        break;
                    case R.id.item4:
                        logoutUser();
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

        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        session.setLogin(false,null);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadQuiz(final String city){
        String tag_string_quiz = "req_quiz";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                AppConfig.URL_QUIZ, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Loading Response: " + response.toString());
                hideDialog();
                //Toast.makeText(ProfileActivity.this, response, Toast.LENGTH_SHORT).show();
                JSONArray quizArray=null;
                try {
                    dataModelArrayList=new ArrayList<>();
                    quizArray=new JSONArray(response);
                    for (int i=0;i<quizArray.length();i++){
                        JSONObject quizDetail=quizArray.getJSONObject(i);
                        QuizEntity quizEntity=new QuizEntity();
                        quizEntity.setTitle(quizDetail.getString("title"));
                        //Toast.makeText(ProfileActivity.this,quizDetail.getString("title") , Toast.LENGTH_SHORT).show();
                        quizEntity.setDescription(quizDetail.getString("description"));
                        //Toast.makeText(ProfileActivity.this,quizDetail.getString("description") , Toast.LENGTH_SHORT).show();

                        dataModelArrayList.add(quizEntity);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(ProfileActivity.this, dataModelArrayList.toString(), Toast.LENGTH_SHORT).show();
                quizRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

                adapter = new ProfileQuizRecyclerAdapter(ProfileActivity.this,dataModelArrayList);
                quizRecycler.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                //Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("city", city);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_quiz);
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
                Log.d(TAG, "Loading Response: " + response.toString());
                hideDialog();
                //Toast.makeText(ProfileActivity.this, response, Toast.LENGTH_SHORT).show();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    tvName.setText(jsonObject.getString("first_name")+"  "
                            +jsonObject.getString("last_name"));
                    tvEmail.setText(jsonObject.getString("email"));
                    tvPoints.setText("Points :  "+jsonObject.getString("points"));
                    tvLocation.setText(jsonObject.getString("city")+" | "
                         +jsonObject.getString("current_state")+" | "+jsonObject.getString("country"));
                    String url="http://35.198.203.61/utilities/image?file_name="+jsonObject.getString("pro_pic");
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
                        // Launch login activity
                        Intent intent = new Intent(
                                ProfileActivity.this,
                                ProfileActivity.class);
                        intent.putExtra("fid", fid);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        //Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
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


    public void upload(View v){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//TODO restrict to ppt or pdf only... application/ppt,application/pdf
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    1);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 1 && data != null && data.getData() != null) {

                Uri selectedFileURI = data.getData();


                //InputStream is = getContentResolver().openInputStream(selectedFileURI);


                final String docFilePath = getFileNameByUri(this, selectedFileURI);


                /*final String selectedFilePath = FilePath.getPath(this,selectedFileURI);
                Log.i(TAG,"Selected File Path:" + selectedFilePath);*/
                /*if(selectedFilePath != null && !selectedFilePath.equals("")){
                    tvFileName.setText(selectedFilePath);
                }else{
                    Toast.makeText(this,"Cannot upload file to server",Toast.LENGTH_SHORT).show();
                }*/


                File file = new File(selectedFileURI.getPath().toString());
                Log.d("akash", "File : " + file.getName());

                String uploadedFileName = file.getName().toString();
                StringTokenizer tokens = new StringTokenizer(uploadedFileName, ":");
                Log.d("akash", "tokens : " + tokens);

                /*String first = tokens.nextToken();
                Log.d("akash", "first : " + first);*/

                file_1 = tokens.nextToken().trim();
                Log.d("akash", "file_1 : " + file_1);//
                //txt_file_name_1.setText(file_1);


                //do the network call here on a new thread instead of the async task...
                pDialog = new ProgressDialog(ProfileActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage("Please wait ...");
                showDialog();


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(AppConfig.UPLOAD_URL);//TODO change to
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setReadTimeout(10000);
                            conn.setConnectTimeout(15000);
                            conn.setRequestMethod("POST");
                            conn.setUseCaches(false);
                            conn.setDoInput(true);
                            conn.setDoOutput(true);

                            File file1 = new File(Environment.getExternalStorageDirectory(),
                                    docFilePath);

                            FileBody fileBody1 = new FileBody(file1);

                            MultipartEntity reqEntity = new MultipartEntity(
                                    HttpMultipartMode.BROWSER_COMPATIBLE);
                            reqEntity.addPart("file1", fileBody1);

                            conn.setRequestProperty("Connection", "Keep-Alive");
                            conn.addRequestProperty("Content-length", reqEntity.getContentLength()+"");
                            conn.addRequestProperty(reqEntity.getContentType().getName(), reqEntity.getContentType().getValue());

                            OutputStream os = conn.getOutputStream();
                            reqEntity.writeTo(os);
                            os.close();
                            conn.connect();

                            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                readStream(conn.getInputStream());
                            }

                        } catch (MalformedURLException e) {
                            Log.e(TAG, "MalformedURLException: " + e.getMessage());
                        } catch (ProtocolException e) {
                            Log.e(TAG, "ProtocolException: " + e.getMessage());
                        } catch (IOException e) {
                            Log.e(TAG, "IOException: " + e.getMessage());
                        } catch (NullPointerException e) {
                            Log.e(TAG, "NullPointerException: " + e.getMessage());
                        }catch (Exception e) {
                            Log.e(TAG, "Exception: " + e.getMessage());
                        }
                    }
                }).start();

                hideDialog();
                //return null;
            }
        }

    // get file path
    private String getFileNameByUri(Context context, Uri uri)
    {
        String filepath = "";//default fileName
        //Uri filePathUri = uri;
        File file;

        if (uri.getScheme().toString().compareTo("content") == 0)
        {
            Cursor cursor = context.getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.ORIENTATION }, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            cursor.moveToFirst();

            String mImagePath = cursor.getString(column_index);
            cursor.close();
            filepath = mImagePath;

        }
        else
        if (uri.getScheme().compareTo("file") == 0)
        {
            try
            {
                file = new File(new URI(uri.toString()));
                if (file.exists())
                    filepath = file.getAbsolutePath();

            }
            catch (URISyntaxException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
        {
            filepath = uri.getPath();
        }
        return filepath;
    }

    /*private class PostDataAsyncTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            Log.d("onPreExecute", "inside onPreExecute");

            pDialog = new ProgressDialog(ProfileActivity.this);//
            pDialog.setCancelable(false);
            pDialog.setMessage("Please wait ...");
            showDialog();
        }

        @Override
        protected String doInBackground(String... strings) {
            *//*try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("https://10.0.2.2/test/file.php");


                file1 = new File(Environment.getExternalStorageDirectory(),
                        file_1);
                fileBody1 = new FileBody(file1);

                MultipartEntity reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);
                reqEntity.addPart("file1", fileBody1);


                httpPost.setEntity(reqEntity);

                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity resEntity = response.getEntity();

                if (resEntity != null) {
                    final String responseStr = EntityUtils.toString(resEntity)
                            .trim();
                    Log.v(TAG, "Response: " + responseStr);

                }*//*

                //String response = null;

                Log.d("doInBackground", "inside doInBackground");

                try {
                    URL url = new URL(AppConfig.UPLOAD_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setUseCaches(false);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    File file1 = new File(Environment.getExternalStorageDirectory(),
                            file_1);

                    FileBody fileBody1 = new FileBody(file1);

                    MultipartEntity reqEntity = new MultipartEntity(//deprecated...
                            HttpMultipartMode.BROWSER_COMPATIBLE);
                    reqEntity.addPart("file1", fileBody1);

                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.addRequestProperty("Content-length", reqEntity.getContentLength()+"");
                    conn.addRequestProperty(reqEntity.getContentType().getName(), reqEntity.getContentType().getValue());

                    OutputStream os = conn.getOutputStream();
                    reqEntity.writeTo(conn.getOutputStream());
                    os.close();
                    conn.connect();

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        return readStream(conn.getInputStream());
                    }

                } catch (MalformedURLException e) {
                    Log.e(TAG, "MalformedURLException: " + e.getMessage());
                } catch (ProtocolException e) {
                    Log.e(TAG, "ProtocolException: " + e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, "IOException: " + e.getMessage());
                } catch (NullPointerException e) {
                    Log.e(TAG, "NullPointerException: " + e.getMessage());
                }catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            hideDialog();
            Log.d("onPostExecute", "RESULT : " + result);
        }
    }*/

    private static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }
}
