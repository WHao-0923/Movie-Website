import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Change this to your own mysql username and password
        String loginUser = "root";
        String loginPasswd = "password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        // Set response mime type
        response.setContentType("text/html");

        // Get the PrintWriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>Fabflix</title></head>");

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            // create database connection
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // declare statement
            Statement statement = connection.createStatement();
            // prepare query; Sorted by rating (TBD)
//            String query = "SELECT m.title, m.year, m.director, r.rating from movies m JOIN ratings r ON m.id = r.movieId " +
//                    "ORDER BY r.rating DESC, r.numVotes DESC " +
//                    "LIMIT 20";
            String query = "SELECT m.title,m.year,m.director," +
                    "SUBSTRING_INDEX(GROUP_CONCAT(g.name ORDER BY gim.movieId), ',', 3) AS genres," +
                    "SUBSTRING_INDEX(GROUP_CONCAT(s.name ORDER BY sim.movieId), ',', 3) AS stars," +
                    "r.rating " +
                    "FROM movies m " +
                    "LEFT JOIN ratings r ON m.id = r.movieId " +
                    "LEFT JOIN genres_in_movies gim ON m.id = gim.movieId " +
                    "LEFT JOIN genres g ON gim.genreId = g.id " +
                    "LEFT JOIN stars_in_movies sim ON m.id = sim.movieId " +
                    "LEFT JOIN stars s ON sim.starId = s.id " +
                    "GROUP BY m.id " +
                    "ORDER BY r.rating DESC LIMIT 20;";
            // execute query
            ResultSet resultSet = statement.executeQuery(query);

            out.println("<body>");
            out.println("<h1>MovieDB Movies</h1>");

            out.println("<table border>");

            // Add table header row
            out.println("<tr>");
            out.println("<td>title</td>");
            out.println("<td>year</td>");
            out.println("<td>director</td>");
            out.println("<td>genres</td>");
            out.println("<td>stars</td>");
            out.println("<td>rating</td>");
            out.println("</tr>");

            // Add a row for every star result
            while (resultSet.next()) {
                // get a star from result set
                String movieTitle = resultSet.getString("title");
                String movieYear = resultSet.getString("year");
                String movieDirector = resultSet.getString("director");
                String movieGenres = resultSet.getString("genres");
                String movieStars = resultSet.getString("stars");
                String movieRating = resultSet.getString("rating");

                out.println("<tr>");
                out.println("<td>" + movieTitle + "</td>");
                out.println("<td>" + movieYear + "</td>");
                out.println("<td>" + movieDirector + "</td>");
                out.println("<td>" + movieGenres + "</td>");
                out.println("<td>" + movieStars + "</td>");
                out.println("<td>" + movieRating + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("</body>");

            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            /*
             * After you deploy the WAR file through tomcat manager webpage,
             *   there's no console to see the print messages.
             * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
             *
             * To view the last n lines (for example, 100 lines) of messages you can use:
             *   tail -100 catalina.out
             * This can help you debug your program after deploying it on AWS.
             */
            request.getServletContext().log("Error: ", e);

            out.println("<body>");
            out.println("<p>");
            out.println("Exception in doGet: " + e.getMessage());
            out.println("</p>");
            out.print("</body>");
        }

        out.println("</html>");
        out.close();

    }


}
