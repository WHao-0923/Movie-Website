import jakarta.servlet.ServletConfig;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DataLoader {
//    private DataSource dataSource;
//
//    public void init(ServletConfig config) {
//        try {
//            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
//        } catch (NamingException e) {
//            e.printStackTrace();
//        }
//    }
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
            conn.setAutoCommit(false);
            statement_movies.executeBatch();
            System.out.println("Inserted " + movieCount + " movies.");
            statement_add.executeBatch();
            conn.commit();
            System.out.println("Inserted " + genreCount + " genres.");
            statement_genresInMovies.executeBatch();
            conn.commit();


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
            long time1 = System.nanoTime();
            String sql = "SELECT id AS max_id FROM stars ORDER BY id DESC LIMIT 1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                oldId = Integer.parseInt(rs.getString("max_id").substring(2));
                newId = "nm" + String.valueOf(oldId+1);
            }
            rs.close();
            //stmt.close();
            //System.out.println("Time 1: "+String.valueOf((System.nanoTime()-time1)/1000));
            long time2 = System.nanoTime();
            String checkMovieIdSql = "SELECT id FROM movies WHERE id = ?;";
            PreparedStatement checkMovie = conn.prepareStatement(checkMovieIdSql);
            Map<String, Set<String>> castMap = new HashMap<>();
            for (Cast cast : casts) {
                checkMovie.setString(1, cast.getFilm_id());
                ResultSet rs_movie = checkMovie.executeQuery();
                if (rs_movie.next()) {
                    for (String name : cast.getStage_name()) {
                        if (castMap.containsKey(name)) {
                            castMap.get(name).add(cast.getFilm_id());
                        } else {
                            castMap.put(name, new HashSet<>());
                            castMap.get(name).add(cast.getFilm_id());
                        }
                    }
                }
            }
            //System.out.println("Time 2: "+String.valueOf((System.nanoTime()-time2)/1000));

            for (Star star : stars) {
                //newId = "nm" + curr_cast_id+1;
                starCount++;
                statement_stars.setString(1, newId);
                statement_stars.setString(2, star.getFirst_name() + " " + star.getLast_name());
                statement_stars.setInt(3, star.getDob());
                statement_stars.addBatch();
                if (castMap.containsKey(star.getStagename())) {
                    for (String movie_id : castMap.get(star.getStagename())) {
                        statement_starsInMovies.setString(1, newId);
                        statement_starsInMovies.setString(2, movie_id);
                        statement_starsInMovies.addBatch();
                    }
                }

                oldId++;
                newId = "nm" + (oldId + 1);
            }



            stmt.close();
            //long time3 = System.nanoTime();

            conn.setAutoCommit(false);
            statement_stars.executeBatch();
            statement_starsInMovies.executeBatch();
            conn.commit();
//
            //System.out.println("Time 3: "+String.valueOf((System.nanoTime()-time3)/1000));


            System.out.println("Inserted " + starCount + " stars.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        long startTime = System.nanoTime();
//        // Assume the parsers have been run and we have the lists of movies, casts, and stars
//        SAXParserMovies myMovies = new SAXParserMovies();
//        myMovies.runUtils();
//        List<Movie> movies = myMovies.myMovies; // This would come from your movie parser
//        SAXParserCasts myCasts = new SAXParserCasts();
//        myCasts.runUtils();
//        List<Cast> casts = myCasts.myCasts; // This would come from your cast parser
//        SAXParserStars myStars = new SAXParserStars();
//        myStars.runUtils();
//        List<Star> stars = myStars.myStars; // This would come from your star parser
        long startTime = System.nanoTime();
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        SAXParserMovies myMovies = new SAXParserMovies();
        SAXParserCasts myCasts = new SAXParserCasts();
        SAXParserStars myStars = new SAXParserStars();
        // Runnable task for parsing movies
        Runnable parseMoviesTask = () -> {
            myMovies.runUtils();
        };

        // Runnable task for parsing casts
        Runnable parseCastsTask = () -> {
            myCasts.runUtils();
        };

        // Runnable task for parsing stars
        Runnable parseStarsTask = () -> {
            myStars.runUtils();
        };

        // Submit tasks to the executor service
        executorService.submit(parseMoviesTask);
        executorService.submit(parseCastsTask);
        executorService.submit(parseStarsTask);

        // Shutdown the executor and wait for tasks to complete
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.MINUTES)) {
                // Optional: handle the case where parsing takes more than 60 minutes
                executorService.shutdownNow(); // Force shutdown
            }
        } catch (InterruptedException e) {
            // Current thread was interrupted while waiting
            executorService.shutdownNow();
            Thread.currentThread().interrupt(); // Preserve interrupt status
        }
        long endTime = System.nanoTime();

        // Calculate elapsed time in milliseconds
        long parserTime = (endTime - startTime) / 1_000_000;
        System.out.println("The parsers use " + String.valueOf(parserTime) + " ms.");

        // At this point, all parsers have completed
        // Now you can access the parsed data
        List<Movie> movies = myMovies.myMovies;
        List<Cast> casts = myCasts.myCasts;
        List<Star> stars = myStars.myStars;

        DataLoader dataLoader = new DataLoader();
        long time3 = System.nanoTime();
        dataLoader.insertMovies(movies);
        //System.out.println("Time 3: "+String.valueOf((System.nanoTime()-time3)/1000));
        long time4 = System.nanoTime();
        dataLoader.insertStars(stars,casts,movies);
        //System.out.println("Time 4: "+String.valueOf((System.nanoTime()-time4)/1000));

        //dataLoader.insertCasts(casts);
        long totalTime = System.nanoTime();
        long totalProgramTime = (totalTime - startTime) / 1_000_000;
        System.out.println("The program took " + String.valueOf(totalProgramTime) + " ms.");

    }
}
