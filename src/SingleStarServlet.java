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
@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbSlave");
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
            String id = request.getParameter("star_id");

            // The log message can be found in localhost log
            request.getServletContext().log("getting star id: " + id);

            // Output stream to STDOUT
            PrintWriter out = response.getWriter();

            // Get a connection from dataSource and let resource manager close the connection after usage.
            try (Connection conn = dataSource.getConnection()) {

                JsonObject responseJson = new JsonObject();
                // Get a connection from dataSource
                String q = "select * from stars where id=?";
                PreparedStatement s = conn.prepareStatement(q);
                s.setString(1, id);
                ResultSet rs1 = s.executeQuery();
                while (rs1.next()){
                    String starId = rs1.getString("id");
                    String starName = rs1.getString("name");
                    String starDob = rs1.getString("birthYear");
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("star_id", starId);
                    jsonObject.addProperty("star_name", starName);
                    jsonObject.addProperty("star_dob", starDob);
                    responseJson.add("starInfo",jsonObject);
                }

                // Construct a query with parameter represented by "?"
                String query = "SELECT * from stars as s, stars_in_movies as sim, movies as m " +
                        "where m.id = sim.movieId and sim.starId = s.id and s.id = ? order by m.year desc, m.title asc";

                // Declare our statement
                PreparedStatement statement = conn.prepareStatement(query);

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                statement.setString(1, id);

                // Perform the query
                ResultSet rs = statement.executeQuery();

                JsonArray jsonArray = new JsonArray();

                // Iterate through each row of rs
                while (rs.next()) {

                    String movieId = rs.getString("movieId");
                    String movieTitle = rs.getString("title");
                    String movieYear = rs.getString("year");
                    String movieDirector = rs.getString("director");

                    // Create a JsonObject based on the data we retrieve from rs

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movie_id", movieId);
                    jsonObject.addProperty("movie_title", movieTitle);
                    jsonObject.addProperty("movie_year", movieYear);
                    jsonObject.addProperty("movie_director", movieDirector);

                    jsonArray.add(jsonObject);
                }
                rs.close();
                statement.close();
                responseJson.add("movies",jsonArray);

                // Write JSON string to output
                out.write(responseJson.toString());

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
                session.setAttribute("referrer", "single-star");
                out.close();
            }
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}
