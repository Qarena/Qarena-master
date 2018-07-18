package projects.projects.qarena.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import projects.projects.qarena.FirstActivity;
import projects.projects.qarena.ProfileActivity;
import projects.projects.qarena.R;
import projects.projects.qarena.helper.SQLiteHandler;
import projects.projects.qarena.helper.SessionManager;

public class ViewUploadedQuizListActivity extends AppCompatActivity {

    private static final String TAG = ViewUploadedQuizListActivity.class.getSimpleName();

    private SessionManager session;
    private SQLiteHandler db;
    //private String user_id;

    //private ProgressDialog pDialog;
    private ListView listView;
    private ArrayList<Pair<String,String>> filesMap;
    private ArrayList<String> fileNamesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_quiz_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Uploaded Quizzes");
        }

        /*pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);*/

        //-------------------------------------------------------
        db = new SQLiteHandler(getApplicationContext());

        // session manager checks is user is logged in or not
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        //user_id = session.getUserId();

        /*HashMap<String, String> user = db.getUserDetails();
        uid = user.get("user_id");*/

        filesMap = db.getFilesWithDuplicates();

        //--------------------------------------------------------

        if (filesMap.size() == 0) {
            Toast.makeText(this, "No Quiz file uploaded yet...", Toast.LENGTH_SHORT).show();
            finish();
        }

        else {
            //Creating an ArrayList of keys by passing the keySet
            fileNamesList = getFileNamesList();

            listView = (ListView) findViewById(R.id.listView);
            // Set an item click listener for ListView
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get the selected item text from ListView
                    String selectedItem = (String) parent.getItemAtPosition(position);
                    Log.d(TAG, selectedItem);

                    File file = new File(filesMap.get(position).second);

                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(Uri.fromFile(file), ProfileActivity.quizFileUploadDocType);
                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    Intent intent = Intent.createChooser(target, "Open selected file with");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Instruct the user to install a ppt reader here, or something
                        Toast.makeText(getApplicationContext(), "No ppt reader app found on " +
                                "your device!!! :( Please install it first...", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout
                    .simple_list_item_1, fileNamesList);
            listView.setAdapter(adapter);
        }
    }

    private void logoutUser() {
        session.setLogin(false, null);

        db.deleteUsers();

        // Launching the FirstActivity
        Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
        startActivity(intent);
        finish();
    }

    private ArrayList<String> getFileNamesList(){
        ArrayList<String> fileNames = new ArrayList<>();
        for(Pair<String,String> pair: filesMap){
            fileNames.add(pair.first);
        }
        return fileNames;
    }

    /*private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
*/
}
