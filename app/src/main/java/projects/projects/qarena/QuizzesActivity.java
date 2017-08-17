package projects.projects.qarena;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.TextViewCompat;
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

import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import projects.projects.qarena.helper.SQLiteHandler;
import projects.projects.qarena.helper.SessionManager;

public class QuizzesActivity extends AppCompatActivity {
    ViewPager viewPager;
    SessionManager session;
    SQLiteHandler db;
    private static final String TAG = QuizzesActivity.class.getSimpleName();
    String uid = new String();
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

        ArrayList<QuizEntity> quizzes=new ArrayList<>();
        //TODO get the details of quizzes and store in this ArrayList
        //..............Example................
        QuizEntity quiz=new QuizEntity();
        quiz.title="Animal Kingdom";
        quiz.age_res="18 to 25 Years";
        quiz.category="Animals/Species";
        quiz.level="State";
        quiz.mode=1;
        quiz.organizer_id="rickyBuoy";
        quiz.status=0;
        quiz.max_part=50;
        quiz.address="Rath-Tala, Belaghariya, Dhakuria, Rabindrasarovar Area, Kolkata, West Bengal 700058";
        quiz.shortAddress="Nazrul Mancha";
        quiz.description="A quiz on animals. A quiz on animals. A quiz on animals. A quiz on animals. A quiz on animals.";
        quiz.picUrl="https://s-media-cache-ak0.pinimg.com/736x/a7/45/ab/a745abb8099992acd9af3e809e565be2--large-posters-movie-tv.jpg";
        quiz.price="INR 50";
        quiz.time_to="12:00 AM";
        quiz.time_from="10:00 AM";
        quizzes.add(quiz);
      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabQuiz);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),CreateEvent.class));
                Snackbar.make(view, "Create Quiz", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


        viewPager=(ViewPager) findViewById(R.id.content_pager);
        Adapter adapter=new Adapter(quizzes);
        viewPager.setAdapter(adapter);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item1:
                        startActivity(new Intent(QuizzesActivity.this,ProfileActivity.class));
                        finish();
                        break;
                    case R.id.item2:
                        startActivity(new Intent(QuizzesActivity.this,QuizzesActivity.class));
                        finish();
                        break;
                    case R.id.item3:
                        startActivity(new Intent(QuizzesActivity.this,CreateEvent.class));
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

    private class Adapter extends PagerAdapter {
        ArrayList<QuizEntity> quizzes;
        public Adapter(ArrayList<QuizEntity> quizzes){
            this.quizzes=quizzes;
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
            LayoutInflater inflater=((Activity)context).getLayoutInflater();
            View layout=inflater.inflate(R.layout.quiz_page,container,false);
            layout.findViewById(R.id.right).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem((viewPager.getCurrentItem()+1)%quizzes.size());
                }
            });
            layout.findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem((viewPager.getCurrentItem()-1)%quizzes.size());
                }
            });

            ((TextView)layout.findViewById(R.id.title)).setText(quizzes.get(position).title);
            ((TextView)layout.findViewById(R.id.organizer)).setText(quizzes.get(position).organizer_id);
            ((TextView)layout.findViewById(R.id.description)).setText(quizzes.get(position).description);
            ((TextView)layout.findViewById(R.id.who_can_apply)).setText(quizzes.get(position).age_res);
            ((TextView)layout.findViewById(R.id.quiz_price)).setText(quizzes.get(position).price);
            ((TextView)layout.findViewById(R.id.quiz_address)).setText(quizzes.get(position).shortAddress);
            ((TextView)layout.findViewById(R.id.quiz_time_from)).setText(quizzes.get(position).time_from);
            ((TextView)layout.findViewById(R.id.quiz_time_to)).setText(quizzes.get(position).time_to);
            if(quizzes.get(position).status!=2)
                ((ImageButton)layout.findViewById(R.id.btStatus)).setImageResource(R.drawable.ic_isonline_true);

            //link to profile page of organizer
            layout.findViewById(R.id.organizer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO REGISTER
                    Intent i=new Intent(getApplicationContext(),ProfileActivity.class);
                    i.putExtra("person_id",quizzes.get(position).organizer_id);
                   startActivity(i);
                }
            });

            //more  info button
            layout.findViewById(R.id.quiz_prizes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO REGISTER

                    showPrizes();
                }
            });
            //register button
            layout.findViewById(R.id.quiz_register).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO REGISTER
                    String[] vals= quizzes.get(position).age_res.split(" ");
                    int age_to=Integer.parseInt(vals[0]);
                    int age_from=Integer.parseInt(vals[2]);

                }
            });


            Log.d("pic url",quizzes.get(position).picUrl );
            Glide.with(QuizzesActivity.this)
                    .load(quizzes.get(position).picUrl).centerCrop()
                    .into((ImageView) layout.findViewById(R.id.quiz_pic));
            layout.findViewById(R.id.quiz_locate).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String url="http://maps.google.co.in/maps?q="+ URLEncoder.encode(quizzes.get(position).address,"utf-8");
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
    }
    public void showPrizes() {
        DialogFragment newFragment = new DialogBoxWithStuff();

        newFragment.show(getFragmentManager(),"PRIZES");  //.show(getSupportFragmentManager(), "missiles");
    }


    //--------------------------------------ONLINE STUFF--------------------------------------------
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
        startActivity(intent);
        finish();
    }
    //----------------------------------------------------------------------------------------------

}
