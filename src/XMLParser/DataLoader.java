package XMLParser;

import jakarta.servlet.ServletConfig;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class DataLoader {
    private int movieCount = 0;
    private int starCount = 0;
    private int genreCount = 0;

    public int getMovieCount() {
        return movieCount;
    }

    public void setMovieCount(int movieCount) {
        this.movieCount = movieCount;
    }

    public int getStarCount() {
        return starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public int getGenreCount() {
        return genreCount;
    }

    public void setGenreCount(int genreCount) {
        this.genreCount = genreCount;
    }

    public void insertMovies(List<Movie> movies) {
        String insertMovieSql = "INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?);";

        try (Connection conn = DatabaseUtility.getConnection()) {
            PreparedStatement statement_movies = conn.prepareStatement(insertMovieSql);


            String getMaxGenreIdSql = "SELECT MAX(genreId) AS max_id FROM genres_in_movies;";
            PreparedStatement statement_getMax = conn.prepareStatement(getMaxGenreIdSql);
            ResultSet maxId_result = statement_getMax.executeQuery();
            int maxId = 0;
            while (maxId_result.next()){
                maxId = maxId_result.getInt("max_id");
            }

            String addGenreSql = "INSERT INTO genres (id, name) VALUES (?, ?);";
            PreparedStatement statement_add = conn.prepareStatement(addGenreSql);
            // Update genres_in_movies
            String insertGenresInMoviesSql = "INSERT INTO genres_in_movies (genreId, movieId) VALUES (?,?);";
            PreparedStatement statement_genresInMovies = conn.prepareStatement(insertGenresInMoviesSql);
            Map<String,Integer> genreAdded = new HashMap<>();
            for (Movie movie : movies) {
                statement_movies.setString(1, movie.getId());
                statement_movies.setString(2, movie.getTitle());
                statement_movies.setInt(3, movie.getYear());
                try{
                    statement_movies.setString(4, movie.getDirectors().get(0));
                } catch (IndexOutOfBoundsException e){
                    statement_movies.setString(4, "");
                }

                movieCount ++;
                statement_movies.addBatch();
                // Check if genre exists
                String getExistGenresSql = "SELECT id, name FROM genres WHERE name = ?;";
                PreparedStatement statement_exist = conn.prepareStatement(getExistGenresSql);
                statement_exist.setString(1, movie.getCat());

                try (ResultSet rs = statement_exist.executeQuery()){
                    //System.out.println(!rs.next() && !genreAdded.containsKey(movie.getCat()));
                    Boolean result = rs.next();
                    if (!result && !genreAdded.containsKey(movie.getCat())){
                        maxId ++;
                        genreAdded.put(movie.getCat(),maxId);
                        statement_add.setInt(1,maxId);
                        statement_add.setString(2,movie.getCat());
                        genreCount ++;
                        statement_add.addBatch();

                        statement_genresInMovies.setInt(1,maxId);
                        statement_genresInMovies.setString(2,movie.getId());
                        statement_genresInMovies.addBatch();
                    } else {
                        if (result){
                            statement_genresInMovies.setInt(1,rs.getInt("id"));
                        } else {
                            //System.out.println(movie.getCat());
                            statement_genresInMovies.setInt(1,genreAdded.get(movie.getCat()));
                        }
                        statement_genresInMovies.setString(2,movie.getId());
                        statement_genresInMovies.addBatch();
                    }
                }

            }

            statement_movies.executeBatch();
            System.out.println("Inserted " + movieCount + " movies.");
            statement_add.executeBatch();
            System.out.println("Inserted " + genreCount + " genres.");
            statement_genresInMovies.executeBatch();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertStars(List<Star> stars, List<Cast> casts, List<Movie> movies) {

        String insertStarsSql = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?);";
        String insertStarsInMoviesSql = "INSERT INTO stars_in_movies (starId, movieId) VALUES(?,?);";
        try (Connection conn = DatabaseUtility.getConnection()) {
            PreparedStatement statement_stars = conn.prepareStatement(insertStarsSql);
            PreparedStatement statement_starsInMovies = conn.prepareStatement(insertStarsInMoviesSql);
            int oldId = 0;
            String newId = "";
            // Execute a query to find the max id
            String sql = "SELECT id AS max_id FROM stars ORDER BY id DESC LIMIT 1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                oldId = Integer.parseInt(rs.getString("max_id").substring(2));
                newId = "nm" + String.valueOf(oldId+1);
            }
            rs.close();
            stmt.close();

            Map<String, String> castMap = new HashMap<>();
            for (Cast cast : casts) {
                for (String name:cast.getStage_name()){
                    castMap.put(name,cast.getFilm_id());
                }
            }
            for (Star star : stars){
                //newId = "nm" + curr_cast_id+1;

                statement_stars.setString(1, newId);

                //curr_cast_id ++;
                statement_stars.setString(2, star.getFirst_name()+" "+star.getLast_name());
                statement_stars.setInt(3,star.getDob());
                starCount ++;
                //statement_stars.addBatch();
                statement_stars.addBatch();
                if (castMap.get(star.getStagename())!=null){

                    // Check if movieId is valid in db
                    String movie_id = castMap.get(star.getStagename());
                    String checkMovieIdSql = "SELECT * from movies WHERE id = ?;";
                    PreparedStatement checkMovie = conn.prepareStatement(checkMovieIdSql);
                    checkMovie.setString(1,movie_id);
                    ResultSet rs_movie = checkMovie.executeQuery();
                    if (rs_movie.next()){
                        statement_starsInMovies.setString(1,newId);
                        statement_starsInMovies.setString(2,castMap.get(star.getStagename()));
                        statement_starsInMovies.addBatch();
                    }
                    //statement_starsInMovies.addBatch();
                }
                oldId++;
                newId = "nm" + (oldId+1);
            }
            stmt.close();
            statement_stars.executeBatch();
            statement_starsInMovies.executeBatch();
            System.out.println("Inserted " + starCount + " stars.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Assume the parsers have been run and we have the lists of movies, casts, and stars
        SAXParserMovies myMovies = new SAXParserMovies();
        myMovies.runUtils();
        List<Movie> movies = myMovies.myMovies; // This would come from your movie parser
        SAXParserCasts myCasts = new SAXParserCasts();
        myCasts.runUtils();
        List<Cast> casts = myCasts.myCasts; // This would come from your cast parser
        SAXParserStars myStars = new SAXParserStars();
        myStars.runUtils();
        List<Star> stars = myStars.myStars; // This would come from your star parser

        DataLoader dataLoader = new DataLoader();

        dataLoader.insertMovies(movies);
        //dataLoader.insertCasts(casts);
        dataLoader.insertStars(stars, casts, movies);

    }
}
