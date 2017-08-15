package hu.ait.courseinformer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.ait.courseinformer.R;
import hu.ait.courseinformer.data.Course;
import io.realm.Realm;
import io.realm.RealmResults;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder>{

    private Context context;
    private List<Course> courseList;
    private Realm realmCourse;

    public CourseAdapter(Context context) {
        this.context = context;

        realmCourse = Realm.getDefaultInstance();

        RealmResults<Course> courseResults = realmCourse.where(Course.class).findAll();

        courseList = new ArrayList<>();

        for (int i = 0; i < courseResults.size(); i++) {
            courseList.add(courseResults.get(i));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View courseView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_row, parent, false);

        return new ViewHolder(courseView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = courseList.get(position).getDep() + " " + courseList.get(position).getCrn();
        holder.tvName.setText(name);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public void addCourse(String name) {
        String dep = name.split(" ")[0];
        String crn = name.split(" ")[1];

        realmCourse.beginTransaction();
        Course course = realmCourse.createObject(Course.class);
        course.setDep(dep);
        course.setCrn(crn);
        realmCourse.commitTransaction();

        courseList.add(course);
        notifyItemInserted(courseList.size()-1);
    }

    public void deleteAll() {

        realmCourse.beginTransaction();
        realmCourse.deleteAll();
        realmCourse.commitTransaction();

        courseList.clear();
        notifyDataSetChanged();
    }

    public void closeRealm() {
        realmCourse.close();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName;

        public ViewHolder(View courseView) {
            super(courseView);

            tvName = (TextView) courseView.findViewById(R.id.tvName);
        }
    }
}
