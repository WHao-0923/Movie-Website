import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cast {
    private String film_id;
    private List<String> stage_name = new ArrayList<>();

    public Cast() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cast cast = (Cast) o;
        return Objects.equals(film_id, cast.film_id) && Objects.equals(stage_name, cast.stage_name);
        // Include other properties in comparison if necessary
    }

    @Override
    public int hashCode() {
        return Objects.hash(film_id,stage_name);
    }
    public void setStage_name(List<String> stage_name) {
        this.stage_name = stage_name;
    }

    public String getFilm_id() {
        return film_id;
    }

    public void setFilm_id(String film_id) {
        this.film_id = film_id;
    }

    public List<String> getStage_name() {
        return stage_name;
    }


    @Override
    public String toString() {
        return "Cast{" +
                "film_id='" + film_id + '\'' +
                ", stage_name='" + stage_name + '\'' +
                '}';
    }
}
