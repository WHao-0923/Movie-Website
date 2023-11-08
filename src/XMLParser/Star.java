package XMLParser;

import java.util.Objects;

public class Star {
    private String stagename;
    private String starid;
    private String last_name;
    private String first_name;
    private int dob;

    public Star() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Star star = (Star) o;
        return Objects.equals(last_name, star.last_name) && Objects.equals(first_name, star.first_name)
                && Objects.equals(dob, star.dob);
        // Include other properties in comparison if necessary
    }

    @Override
    public int hashCode() {
        return Objects.hash(stagename);
    }
    public String getStagename() {
        return stagename;
    }

    public void setStagename(String stagename) {
        this.stagename = stagename;
    }

    public String getStarid() {
        return starid;
    }

    public void setStarid(String starid) {
        this.starid = starid;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public int getDob() {
        return dob;
    }

    public void setDob(int dob) {
        this.dob = dob;
    }

    @Override
    public String toString() {
        return "Star{" +
                "stagename='" + stagename + '\'' +
                ", starid='" + starid + '\'' +
                ", last_name='" + last_name + '\'' +
                ", first_name='" + first_name + '\'' +
                ", dob=" + dob +
                '}';
    }
}
