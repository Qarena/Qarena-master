package projects.projects.qarena;

import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by HP on 06-Aug-17.
 */

public class ProfileQuizRecyclerAdapter extends RecyclerView.Adapter<ProfileQuizRecyclerAdapter.MyViewHolder> {

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);
        }
    }


    public ProfileQuizRecyclerAdapter() {

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_page_small, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //TODO
    }

    @Override
    public int getItemCount() {
        return 10;
    }
}
