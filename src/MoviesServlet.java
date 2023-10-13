import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Set response mime type
        response.setContentType("application/json");

//        // Retrieve parameter id from url request.
//        String id = request.getParameter("id");
//
//        // The log message can be found in localhost log
//        request.getServletContext().log("getting id: " + id);

        // Get the PrintWriter for writing response
        PrintWriter out = response.getWriter();

//        out.println("<html>");
//        out.println("<head><title>Fabflix</title></head>");

        try (Connection conn = dataSource.getConnection()){
//            Class.forName("com.mysql.jdbc.Driver").newInstance();
//            // create database connection
//
//            // declare statement
//            Statement statement = connection.createStatement();


            // prepare query; Sorted by rating (TBD)
//            String query = "SELECT m.title, m.year, m.director, r.rating from movies m JOIN ratings r ON m.id = r.movieId " +
//                    "ORDER BY r.rating DESC, r.numVotes DESC " +
//                    "LIMIT 20";
            String query = "SELECT m.id, m.title,m.year,m.director," +
                    "SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY gim.movieId), ',', 1) AS genre1," +
                    "SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY gim.movieId), ',', 2), ',', -1) AS genre2," +
                    "SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY gim.movieId), ',', 3), ',', -1) AS genre3," +
                    "SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(CONCAT(s.name, ':', s.id) ORDER BY sim.movieId), ',', 1), ':', 1) AS star1_name," +
                    "SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(CONCAT(s.name, ':', s.id) ORDER BY sim.movieId), ',', 1), ':', -1) AS star1_id," +
                    "SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(CONCAT(s.name, ':', s.id) ORDER BY sim.movieId), ',', -3), ':', 1) AS star2_name," +
                    "SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(CONCAT(s.name, ':', s.id) ORDER BY sim.movieId), ',', 2), ':', -1) AS star2_id," +
                    "SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(CONCAT(s.name, ':', s.id) ORDER BY sim.movieId), ',', -2), ':', 1) AS star3_name," +
                    "SUBSTRING_INDEX(SUBSTRING_INDEX(GROUP_CONCAT(CONCAT(s.name, ':', s.id) ORDER BY sim.movieId), ',', 3), ':', -1) AS star3_id," +
                    "r.rating " +
                    "FROM movies m " +
                    "LEFT JOIN ratings r ON m.id = r.movieId " +
                    "LEFT JOIN genres_in_movies gim ON m.id = gim.movieId " +
                    "LEFT JOIN genres g ON gim.genreId = g.id " +
                    "LEFT JOIN stars_in_movies sim ON m.id = sim.movieId " +
                    "LEFT JOIN stars s ON sim.starId = s.id " +
                    "GROUP BY m.id " +
                    "ORDER BY r.rating DESC;";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            //reparedStatement statement_genre1 = conn.prepareStatement(query_genre1);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            //ResultSet rs1 = statement_genre1.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Add a row for every star result
            while (rs.next()) {

                String movieId = rs.getString("id");
                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("year");
                String movieDirector = rs.getString("director");
                String movieGenre1 = rs.getString("genre1");
                String movieGenre2 = rs.getString("genre2");
                String movieGenre3 = rs.getString("genre3");
                String movieStar1Name = rs.getString("star1_name");
                String movieStar1Id = rs.getString("star1_id");
                String movieStar2Name = rs.getString("star2_name");
                String movieStar2Id = rs.getString("star2_id");
                String movieStar3Name = rs.getString("star3_name");
                String movieStar3Id = rs.getString("star3_id");
                String movieRating = rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_year", movieYear);
                jsonObject.addProperty("movie_director", movieDirector);
                jsonObject.addProperty("movie_genre1", movieGenre1);
                jsonObject.addProperty("movie_genre2", movieGenre2);
                jsonObject.addProperty("movie_genre3", movieGenre3);
                jsonObject.addProperty("movie_star1_name", movieStar1Name);
                jsonObject.addProperty("movie_star1_id", movieStar1Id);
                jsonObject.addProperty("movie_star2_name", movieStar2Name);
                jsonObject.addProperty("movie_star2_id", movieStar2Id);
                jsonObject.addProperty("movie_star3_name", movieStar3Name);
                jsonObject.addProperty("movie_star3_id", movieStar3Id);
                jsonObject.addProperty("movie_rating", movieRating);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

    }


}
