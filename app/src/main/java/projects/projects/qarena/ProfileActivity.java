package projects.projects.qarena;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import projects.projects.qarena.app.AppConfig;
import projects.projects.qarena.app.AppController;
import projects.projects.qarena.app.VolleySingleton;
import projects.projects.qarena.helper.SQLiteHandler;
import projects.projects.qarena.helper.SessionManager;

public class ProfileActivity extends AppCompatActivity  {

    private ProgressDialog pDialog;
    Person p;

    private TextView tvName, tvStatus, tvLoc, tvDob, tvId;
    ImageView proPic;
    SessionManager session;
    SQLiteHandler db;
    private static final String TAG = ProfileActivity.class.getSimpleName();
    String uid = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
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
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("user_id");
        //------------------------------------------------------------------------------------------
        loadProfile(uid);
        tvId = (TextView) findViewById(R.id.tvId);
        tvName = (TextView) findViewById(R.id.tvName);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvLoc = (TextView) findViewById(R.id.tvLoc);
        proPic = (ImageView) findViewById(R.id.profile_pic);
        tvId.setText(uid);


        ImageLoader ir = VolleySingleton.getInstance().getImageLoader();
        String imageUrl = AppConfig.URL_DP + "//" + uid + ".png";
        ir.get(imageUrl, new com.android.volley.toolbox.ImageLoader.ImageListener() {
            @Override
            public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer response, boolean isImmediate) {
                proPic.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error retrieving image!", Toast.LENGTH_SHORT).show();
            }
        });


        RecyclerView quizRecycler = (RecyclerView) findViewById(R.id.quiz_recycler);
        ProfileQuizRecyclerAdapter adapter = new ProfileQuizRecyclerAdapter();
        quizRecycler.setAdapter(adapter);
        quizRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

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
                        startActivity(new Intent(ProfileActivity.this,CreateEvent.class));
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
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
        startActivity(intent);
        finish();
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

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String link = new String();
                    // Check for error node in json
                    if (!error) {
                        //fetch the person's details
                        JSONObject user = jObj.getJSONObject("profile");
                        p = new Person();
                        p.setName(user.getString("first_name") + " " + user.getString("last_name"));
                        if (!user.getString("curr_lat").isEmpty() && !user.getString("curr_long").isEmpty())
                            p.setLat(user.getDouble("curr_lat"));
                        p.setLong(user.getDouble("curr_long"));
                        p.setCountry(user.getString("country"));
                        p.setCity(user.getString("city"));
                        p.setState(user.getString("state"));
                        p.setDOB(user.getString("dob"));
                        p.setIsOnline(user.getInt("is_online"));
                        if (!user.getString("pro_pic").isEmpty()) {
                            p.setDp(link = AppConfig.URL_DP + user.getString("pro_pic"));
                            System.out.println(link);

                        }
                        if (p != null) {
                            tvName.setText(p.getName());
                            // tvDob.setText(tvDob.getText()+p.getDOB());
                            tvLoc.setText(p.getCity() + " | " + p.getState() + " | " + p.getCountry());


                        }


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                params.put("uid", uid);
                params.put("type", "1");
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


}
