import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Movie {
    private String id;
    private String title;

    private int year;

    private String cat;

    List<String> directors = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        if (Objects.equals(id, movie.id)) return true;
        return Objects.equals(id, movie.id) && Objects.equals(title, movie.title)
            && Objects.equals(year, movie.year) && Objects.equals(cat, movie.cat);
        // Include other properties in comparison if necessary
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public List<String> getDirectors() {
        return directors;
    }

    public void setDirectors(List<String> directors) {
        this.directors = directors;
    }


    public Movie(){

    }

    public Movie(String id, String title, int year,String cat) {
        this.id  = id;
        this.title = title;
        this.year = year;
        this.cat = cat;

    }
    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Movie Details - ");
        sb.append("Id:" + getId());
        sb.append(", ");
        sb.append("Title:" + getTitle());
        sb.append(", ");
        sb.append("Year:" + getYear());
        sb.append(".");
        sb.append("Category:" + getCat());
        sb.append(", ");
        sb.append("Directors:" + getDirectors());

        return sb.toString();
    }
}
