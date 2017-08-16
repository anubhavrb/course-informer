package hu.ait.courseinformer.data;

import io.realm.RealmObject;

public class Course extends RealmObject {

    private String dep;
    private String crn;

    public Course() {
    }

    public Course(String dep, String crn) {
        this.dep = dep;
        this.crn = crn;
    }

    public String getDep() {
        return dep;
    }

    public void setDep(String dep) {
        this.dep = dep;
    }

    public String getCrn() {
        return crn;
    }

    public void setCrn(String crn) {
        this.crn = crn;
    }
}
