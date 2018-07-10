package projects.projects.qarena;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import projects.projects.qarena.app.AppConfig;
import projects.projects.qarena.app.AppController;
import projects.projects.qarena.helper.SQLiteHandler;
import projects.projects.qarena.helper.SessionManager;

/*
Change Log: added register buttons
added the login feature using Volley
* */
public class FirstActivity extends AppCompatActivity implements View.OnClickListener{

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    EditText email,password;
    private static final String TAG = FirstActivity.class.getSimpleName();


    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        TextView signup = (TextView) findViewById(R.id.register);
        signup.setOnClickListener(this);
        TextView login = (TextView) findViewById(R.id.login);
        login.setOnClickListener(this);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(FirstActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void signUp() {
        startActivity(new Intent(FirstActivity.this,RegisterActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.login:
                String em=email.getText().toString();
                String pass=password.getText().toString();
                if(em.isEmpty()|| pass.isEmpty())
                    Toast.makeText(getApplicationContext(),"email/Id or Password cannot be Empty !",Toast.LENGTH_SHORT).show();
                else
                    checkLogin(em,pass);
                break;
            case R.id.register:
                signUp();
                break;

        }
    }

    //---------------------------------Login using Volley ----------------------------------------
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        // Now store the user in SQLite
                        String uid = jObj.getString("user_id");

                        JSONObject user = jObj.getJSONObject("user");
                        String email = user.getString("email");
                        String password = user.getString("password");

                        String user_id=jObj.getString("user_id");
                        //Toast.makeText(FirstActivity.this, "The user id is"+user_id, Toast.LENGTH_SHORT).show();

                        session.setLogin(true, user_id);

                        //Launch ProfileActivity
                        Intent intent = new Intent(FirstActivity.this,
                                ProfileActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "JSON error: " + e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
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

}

