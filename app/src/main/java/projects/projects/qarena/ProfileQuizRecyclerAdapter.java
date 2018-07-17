package projects.projects.qarena;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import projects.projects.qarena.models.QuizEntity;

/**
 * Created by HP on 06-Aug-17.
 */
public class ProfileQuizRecyclerAdapter extends RecyclerView.Adapter<ProfileQuizRecyclerAdapter.MyViewHolder> {
    List<QuizEntity> dataArrayList;
    QuizEntity quizEntity;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description;

        public MyViewHolder(View view) {
            super(view);
            this.title=(TextView)view.findViewById(R.id.quizTitle);
            this.description=(TextView)view.findViewById(R.id.quizDescription);
        }
    }

    public ProfileQuizRecyclerAdapter(Context context, List<QuizEntity> dataArrayList) {
        this.dataArrayList= dataArrayList;
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_page_small, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        quizEntity=dataArrayList.get(position);
        holder.title.setText(quizEntity.getTitle());
        holder.description.setText(quizEntity.getDescription());
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public QuizEntity getQuizEntity(int position){
        return dataArrayList.get(position);
    }
}
