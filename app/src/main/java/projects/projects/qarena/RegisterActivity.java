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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import projects.projects.qarena.app.AppConfig;
import projects.projects.qarena.app.AppController;
import projects.projects.qarena.helper.SQLiteHandler;
import projects.projects.qarena.helper.SessionManager;

/**
 * Created by Arka Bhowmik on 1/2/2017.
 */
public class RegisterActivity extends Activity implements View.OnClickListener {
    ProgressDialog pDialog;
    SessionManager session;
    SQLiteHandler db;
    public Bitmap dp;
    ImageButton dpView;
    private int PICK_IMAGE_REQUEST = 1;
    String TAG = RegisterActivity.class.getSimpleName();
    EditText etEmail, etPassword, etCpassword, etCity, etState, etCountry, etFname, etLname, etUid, etDob;
    Button submit;

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail=(EditText)findViewById(R.id.etEmail);
        etUid=(EditText)findViewById(R.id.etUid);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etCpassword = (EditText) findViewById(R.id.etCpassword);
        etDob = (EditText) findViewById(R.id.etDob);
        etCity = (EditText) findViewById(R.id.etCity);
        etCountry = (EditText) findViewById(R.id.etCountry);
        etState = (EditText) findViewById(R.id.etState);
        etFname = (EditText) findViewById(R.id.etFname);
        etLname = (EditText) findViewById(R.id.etLname);
        dpView = (ImageButton) findViewById(R.id.updateDp);
        dpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        String date = i + "-" + (i1 + 1) + "-" + i2;
                        etDob.setText(date);
                    }
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        etDob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    datePickerDialog.show();
                }
            }
        });

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(this);

        SessionManager session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    ProfileActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onClick(View v) {
        String email, cpassword, password, dob, fname, lname, uid, city, country, state;

        email = etEmail.getText().toString().trim();
        uid = etUid.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        cpassword = etCpassword.getText().toString().trim();
        country = etCountry.getText().toString().trim();
        city = etCity.getText().toString().trim();
        state = etState.getText().toString().trim();
        dob = etDob.getText().toString().trim();
        fname = etFname.getText().toString().trim();
        lname = etLname.getText().toString().trim();
        // Progress dialog


        if (v.getId() == R.id.submit) {
            if (email.isEmpty()) {
                Toast.makeText(this.getApplicationContext(), "Email Cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(this.getApplicationContext(), "Password Cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (uid.isEmpty()) {
                Toast.makeText(this.getApplicationContext(), "User_ID Cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if ((!cpassword.equals(password) )|| cpassword.isEmpty()) {
                Toast.makeText(this.getApplicationContext(), "Passwords don't match !", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isConnected()) {
                Toast.makeText(getApplicationContext(), "Not connected to the internet!", Toast.LENGTH_SHORT).show();
            } else {

                registerUser(uid, email, password, dob, country, state, city, fname, lname, dp);
            }

        }

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     */
    private void registerUser(final String uid, final String email,
                              final String password, final String dob, final String country,
                              final String state,
                              final String city, final String fname,
                              final String lname,final Bitmap bitmap) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();
        VolleyMultipartRequest volleyMultipartRequest=new VolleyMultipartRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(new String(response.data));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite

                        JSONObject user = jObj.getJSONObject("formdata");
                        String email = user.getString("email");
                        String uid = user.getString("user_id");
                        String password = user.getString("password");
                        String dob = user.getString("dob");
                        String country = user.getString("country");
                        String state = user.getString("state");
                        String city = user.getString("city");
                        String firstname = user.getString("first_name");
                        String lastname = user.getString("last_name");

                        //Inserting row in users table
                        db.addUser(uid, email, password,dob,country,state,city,firstname,lastname);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                RegisterActivity.this,
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
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", uid);
                params.put("email", email);
                params.put("password", password);
                params.put("dob", dob);
                params.put("country", country);
                params.put("state", state);
                params.put("city", city);
                params.put("first_name", fname);
                params.put("last_name", lname);

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String,DataPart> params=new HashMap<>();
                long imagename =System.currentTimeMillis();
                params.put("pro_pic",new DataPart(imagename+".png",getFileDataFromDrawable(bitmap)));
                return params;

            }
        };
        {
            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            volleyMultipartRequest.setRetryPolicy(policy);
            AppController.getInstance().addToRequestQueue(volleyMultipartRequest, tag_string_req);
        }

        // Adding request to request queue
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
//---------------------------------------------------------------------


   /* public String getStringImage(Bitmap bmp) {
        if(bmp==null)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();

        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        // System.out.println("BABE:  " + encodedImage);
        return encodedImage;
    }*/

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                dp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //encoding image to string
                //Setting the Bitmap to ImageView
                dpView.setImageBitmap(dp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public  byte[] getFileDataFromDrawable(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,80,byteArrayOutputStream);
        return  byteArrayOutputStream.toByteArray();
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        if(realImage==null)
            return null;
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


}
