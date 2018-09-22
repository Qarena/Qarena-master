package projects.projects.qarena;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import projects.projects.qarena.app.AppConfig;
import projects.projects.qarena.app.VolleySingleton;
import projects.projects.qarena.helper.SQLiteHandler;
import projects.projects.qarena.helper.SessionManager;
import projects.projects.qarena.models.QuizEntity;

public class QuizzesActivity extends AppCompatActivity {
    ViewPager viewPager;
    SessionManager session;
    SQLiteHandler db;
    private static final String TAG = QuizzesActivity.class.getSimpleName();
    String uid = new String();
    ArrayList<QuizEntity> quizzes;
    Adapter adapter;
    ProgressDialog dialog;
    private TextView sortOption;
    private String city = "Kolkata";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizzes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //------------------------SESSION---------------------
        db = new SQLiteHandler(getApplicationContext());

        // session manager checks is user is logged in or not
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("user_id");
        //---------------------------------------------------

        quizzes = new ArrayList<>();

        getAllQuizzes();

        sortOption = (TextView) findViewById(R.id.sort_option);
        sortOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] cities = new String[]{"Gurgaon", "Kolkata", "Bhubaneswar",
                        "Delhi", "Bangalore", "Mumbai", "Chennai", "Coimbatore", "Jaipur", "Ahmedabad", "Thiruvananthapuram"};
                new AlertDialog.Builder(QuizzesActivity.this)
                        .setTitle("Choose City")
                        .setItems(cities,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        city = cities[i];
                                        sortOption.setText(city);
                                        getAllQuizzes();
                                    }
                                })
                        .create().show();
            }
        });


      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabQuiz);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),CreateEvent.class));
                Snackbar.make(view, "Create Quiz", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


        viewPager = (ViewPager) findViewById(R.id.content_pager);
        adapter = new Adapter(quizzes);
        viewPager.setAdapter(adapter);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1:
                        startActivity(new Intent(QuizzesActivity.this, ProfileActivity.class));
                        finish();
                        break;
                    case R.id.item2:
                        startActivity(new Intent(QuizzesActivity.this, QuizzesActivity.class));
                        finish();
                        break;
                    case R.id.item3:
                        startActivity(new Intent(QuizzesActivity.this, CreateQuizEventActivity.class));
                        finish();
                        break;
                    case R.id.item4:
                        logoutUser();
                        break;
                    /*case R.id.item5:
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

    void getAllQuizzes() {
        quizzes.clear();

        dialog = new ProgressDialog(QuizzesActivity.this);
        dialog.setIndeterminate(true);
        dialog.setMessage("Loading all quizzes near you");
        dialog.show();

        final RequestQueue requestQueue = VolleySingleton.getRequestQueue(this);
        requestQueue.start();

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_ADVANCED_SEARCH_QUIZ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        requestQueue.stop();
                        Log.d(TAG, "Get all quizzes response " + response);

                        try {
                            JSONArray results = (new JSONObject(response)).getJSONArray("results");

                            for (int i = 0; i < results.length(); i++) {
                                JSONObject obj = results.getJSONObject(i);
                                QuizEntity quiz = new QuizEntity();

                                quiz.title = obj.getString("title");
                                quiz.age_res = obj.getString("age_range_from") + " to " + obj.getString("age_range_to") + " Years";
                                quiz.category = obj.getString("category");
                                quiz.level = obj.getString("level");
                                //quiz.mode = 1;
                                //quiz.organizer_id = "rickyBuoy";
                                //quiz.status = 0;
                                quiz.max_part = Integer.parseInt(obj.getString("participant_count_max"));
                                quiz.address = obj.getString("gps");
                                quiz.shortAddress = obj.getString("address");
                                quiz.description = obj.getString("description");
                                quiz.picUrl = AppConfig.URL_ImageEndpoint + obj.getString("cpl_name");
                                quiz.price = "INR " + obj.getString("price");
                                quiz.time_to = getDate(obj.getString("datetime_from"));
                                quiz.time_from = getDate(obj.getString("datetime_to"));
                                quiz.prize = obj.getString("prizes");

                                quizzes.add(quiz);
                            }
                            adapter.notifyDataSetChanged();//

                        } catch (JSONException e) {
                            e.printStackTrace();
                            adapter.notifyDataSetChanged();//
                        }
                        //adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestQueue.stop();
                dialog.dismiss();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("city", city);
                params.put("datetime_from", "2017-01-01 00:00");//TODO change it send
                // datetime_to for a quiz...
                return params;
            }
        };
        requestQueue.add(request);
    }

    private String getDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date testDate = null;

        try {
            testDate = sdf.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM hh:mm a");
        String newFormat = formatter.format(testDate);
        return newFormat;
    }

    private class Adapter extends PagerAdapter {
        ArrayList<QuizEntity> quizzes;

        public Adapter(ArrayList<QuizEntity> quizzes) {
            this.quizzes = quizzes;
        }

        @Override
        public int getCount() {
            return quizzes.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Context context = QuizzesActivity.this;
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            View layout = inflater.inflate(R.layout.quiz_page, container, false);

            layout.findViewById(R.id.right).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem((viewPager.getCurrentItem() + 1) % quizzes.size());
                }
            });

            layout.findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem((viewPager.getCurrentItem() - 1) % quizzes.size());
                }
            });

            ((TextView) layout.findViewById(R.id.title)).setText(quizzes.get(position).title);
            ((TextView) layout.findViewById(R.id.organizer)).setText(quizzes.get(position).organizer_id);
            ((TextView) layout.findViewById(R.id.description)).setText(quizzes.get(position).description);
            ((TextView) layout.findViewById(R.id.quiz_price)).setText(quizzes.get(position).price);
            ((TextView) layout.findViewById(R.id.quiz_address)).setText(quizzes.get(position).shortAddress);
            ((TextView) layout.findViewById(R.id.quiz_time_from)).setText(quizzes.get(position).time_from);
            ((TextView) layout.findViewById(R.id.quiz_time_to)).setText(quizzes.get(position).time_to);
            ((TextView) layout.findViewById(R.id.quiz_category)).setText(quizzes.get(position).category);
            ((TextView) layout.findViewById(R.id.quiz_level)).setText(quizzes.get(position).level);

            if (quizzes.get(position).status != 2)
                ((ImageButton) layout.findViewById(R.id.btStatus)).setImageResource(R.drawable.ic_isonline_true);

            //link to profile page of organizer
            layout.findViewById(R.id.organizer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                    i.putExtra("person_id", quizzes.get(position).organizer_id);
                    startActivity(i);
                }
            });

            //more  info button
            layout.findViewById(R.id.quiz_prizes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog dialog = new AlertDialog.Builder(QuizzesActivity.this)
                            .setView(((LayoutInflater) QuizzesActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.more_info_layout, null, false))
                            .setPositiveButton("Okay", null)
                            .create();
                    dialog.show();
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    ((TextView) dialog.findViewById(R.id.more_info_prize_1)).setText(quizzes.get(position).prize.split(";")[0]);
                    ((TextView) dialog.findViewById(R.id.more_info_prize_2)).setText(quizzes.get(position).prize.split(";")[1]);
                    ((TextView) dialog.findViewById(R.id.more_info_prize_3)).setText(quizzes.get(position).prize.split(";")[2]);
                    ((TextView) dialog.findViewById(R.id.more_info_age)).setText(quizzes.get(position).age_res);
                }
            });

            //register button
            layout.findViewById(R.id.quiz_register).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] vals = quizzes.get(position).age_res.split(" ");
                    int age_to = Integer.parseInt(vals[0]);
                    int age_from = Integer.parseInt(vals[2]);
                    startActivity(new Intent(QuizzesActivity.this, QuizRegisterActivity.class));
                }
            });

            Log.d("pic url", quizzes.get(position).picUrl);

            Glide.with(QuizzesActivity.this)
                    .load(quizzes.get(position).picUrl).centerCrop()
                    .into((ImageView) layout.findViewById(R.id.quiz_pic));

            layout.findViewById(R.id.quiz_locate).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String url = "http://maps.google.co.in/maps?q=" + URLEncoder.encode(quizzes.get(position).address, "utf-8");
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        QuizzesActivity.this.startActivity(intent);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });

            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            if (quizzes.contains((View) object)) {
                return quizzes.indexOf((View) object);
            } else {
                return POSITION_NONE;
            }
        }
    }

    public void showPrizes() {
        DialogFragment newFragment = new DialogBoxWithStuff();
        newFragment.show(getFragmentManager(), "PRIZES");//.show(getSupportFragmentManager(), "missiles");
    }

    //--------------------------------------ONLINE STUFF--------------------------------------------
    private void logoutUser() {
        session.setLogin(false, null);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
        startActivity(intent);
        finish();
    }
    //----------------------------------------------------------------------------------------------

}
