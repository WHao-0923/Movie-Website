import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(true);
        String user = (String) session.getAttribute("user");

        if (user == null) {
            // Which means the user is never seen before
            response.setStatus(401);
        }
        else {
            response.setContentType("application/json"); // Response mime type

            // Retrieve parameter id from url request.
            String id = request.getParameter("movie_id");

            // The log message can be found in localhost log
            request.getServletContext().log("getting movie id: " + id);

            // Output stream to STDOUT
            PrintWriter out = response.getWriter();

            // Get a connection from dataSource and let resource manager close the connection after usage.
            try (Connection conn = dataSource.getConnection()) {
                // Get a connection from dataSource

                // Construct a query with parameter represented by "?"
                String query = "SELECT *,count(*) as movies_played from stars as s, stars_in_movies as sim, movies as m, ratings as r, stars_in_movies as s2 " +
                        "where m.id = sim.movieId and sim.starId = s.id and r.movieId=m.id and m.id = ? and sim.starId=s2.starId group by sim.starId order by movies_played desc, s.name asc";

                String query_for_genres = "SELECT * from movies as m, genres_in_movies as gim, genres as g " +
                        "where gim.genreId=g.id and gim.movieId=m.id and m.id = ? order by g.name";

                // Declare our statement
                PreparedStatement statement = conn.prepareStatement(query);
                PreparedStatement s2 = conn.prepareStatement(query_for_genres);

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                statement.setString(1, id);
                s2.setString(1,id);

                // Perform the query
                ResultSet rs = statement.executeQuery();
                ResultSet rs2 = s2.executeQuery();

                JsonObject mainObject = new JsonObject();
                JsonArray genreArray = new JsonArray();
                JsonArray starArray = new JsonArray();
                Boolean mainInfo = false;

                // Iterate through each row of rs
                while (rs.next()) {

                    if (!mainInfo){
                        String movieId = rs.getString("movieId");
                        String movieTitle = rs.getString("title");
                        String movieYear = rs.getString("year");
                        String movieDirector = rs.getString("director");
                        String rating = rs.getString("rating");
                        mainObject.addProperty("movie_id", movieId);
                        mainObject.addProperty("movie_title", movieTitle);
                        mainObject.addProperty("movie_year", movieYear);
                        mainObject.addProperty("movie_director", movieDirector);
                        mainObject.addProperty("movie_rating",rating);
                        mainInfo = true;
                    }

                    String starId = rs.getString("starId");
                    String starName = rs.getString("s.name");

                    // Create a JsonObject based on the data we retrieve from rs

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("star_id", starId);
                    jsonObject.addProperty("star_name", starName);

                    starArray.add(jsonObject);
                }
                rs.close();
                statement.close();

                // Iterate through each row of rs2
                while (rs2.next()) {
                    String genreId = rs2.getString("genreId");
                    String genreName = rs2.getString("g.name");

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("genre_id",genreId);
                    jsonObject.addProperty("genre",genreName);

                    genreArray.add(jsonObject);
                }
                rs2.close();
                s2.close();

                mainObject.add("genres",genreArray);
                mainObject.add("stars",starArray);

                // Write JSON string to output
                out.write(mainObject.toString());

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
        // Always remember to close db connection after usage. Here it's done by try-with-resources
    }

}