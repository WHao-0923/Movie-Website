import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.swing.text.Style;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "MainPageServlet", urlPatterns = "/api/main_page")
public class MainPageServlet extends HttpServlet {
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

        // Check if has logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null){
            //response.sendRedirect("api/login");
            response.setCharacterEncoding("UTF-8");
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("redirect", "login.html");
            response.getWriter().write(new Gson().toJson(responseMap));
            response.setStatus(200);
            return;
        }

        // Get the PrintWriter for writing response
        PrintWriter out = response.getWriter();
        try (Connection conn = dataSource.getConnection()){
            String title = request.getParameter("title").trim();
            String year = request.getParameter("year").trim();
            String director = request.getParameter("director").trim();
            String star = request.getParameter("star").trim();
            String genre = request.getParameter("genre").trim();
            //System.out.println("star: "+ star + ", genre: " +genre);
            String page = request.getParameter("page").trim();     // current page number
            String pageSize = request.getParameter("pageSize").trim(); // number of records per page
            String fullText = request.getParameter("fullText").trim();


            ResultSet rs = null;
             if (title != null || year != null || director != null || star != null || genre != null) {
                StringBuilder sql = new StringBuilder("WITH InitialMovies AS (SELECT id,title,year,director FROM movies WHERE 1=1");
                String[] tokens = null;
                if (fullText!=null && !fullText.isEmpty()){
                    tokens = fullText.split("\\s+");
                     for (int i = 0; i < tokens.length; i++) {
                         sql.append(" AND ");
                         sql.append("MATCH(title) AGAINST (? IN BOOLEAN MODE) ");
                     }
                } else {
                    if (title != null && !title.isEmpty()) {
                        System.out.println(title);
                        if (title.equals("*")) {
                            sql.append(" AND title REGEXP '^[^a-zA-Z0-9]'");
                        } else {
                            sql.append(" AND title LIKE ?");
                        }
                    }
                    if (year != null && !year.isEmpty()) {
                        sql.append(" AND year = ?");
                    }
                    if (director != null && !director.isEmpty()) {
                        sql.append(" AND director LIKE ?");
                    }
                }

                sql.append(")");
                if ((star != null && !star.isEmpty()) || (genre != null && !genre.isEmpty())) {
                    sql.append(" Select id, title, year, director, rating, genre1_name," +
                            "genre1_id, genre2_name, genre2_id, genre3_name, genre3_id," +
                            "star1_name, star1_id, star2_name, star2_id, star3_name," +
                            "star3_id FROM (");
                }

                sql.append("SELECT " +
                        "m.id, m.title, m.year, m.director, r.rating," +
                        "COALESCE((SELECT g.name FROM genres_in_movies gim " +
                        "   JOIN genres g ON gim.genreId = g.id " +
                        "   WHERE gim.movieId = m.id " +
                        "   ORDER BY gim.movieId LIMIT 1),'') AS genre1_name," +
                        "COALESCE((SELECT g.id FROM genres_in_movies gim " +
                        "   JOIN genres g ON gim.genreId = g.id " +
                        "   WHERE gim.movieId = m.id " +
                        "   ORDER BY gim.movieId LIMIT 1),'') AS genre1_id," +
                        "COALESCE((SELECT g.name FROM genres_in_movies gim " +
                        "   JOIN genres g ON gim.genreId = g.id " +
                        "   WHERE gim.movieId = m.id " +
                        "   ORDER BY gim.movieId LIMIT 1 OFFSET 1)," +
                        "   '') AS genre2_name," +
                        "COALESCE((SELECT g.id FROM genres_in_movies gim " +
                        "   JOIN genres g ON gim.genreId = g.id " +
                        "   WHERE gim.movieId = m.id " +
                        "   ORDER BY gim.movieId LIMIT 1 OFFSET 1)," +
                        "   '') AS genre2_id," +
                        "COALESCE((SELECT g.name FROM genres_in_movies gim " +
                        "   JOIN genres g ON gim.genreId = g.id " +
                        "   WHERE gim.movieId = m.id " +
                        "   ORDER BY gim.movieId LIMIT 1 OFFSET 2)," +
                        "   '') AS genre3_name," +
                        "COALESCE((SELECT g.id FROM genres_in_movies gim " +
                        "   JOIN genres g ON gim.genreId = g.id " +
                        "   WHERE gim.movieId = m.id " +
                        "   ORDER BY gim.movieId LIMIT 1 OFFSET 2)," +
                        "   '') AS genre3_id," +
                        "COALESCE(" +
                        "   (SELECT s.name FROM stars_in_movies sim " +
                        "   JOIN stars s ON sim.starId = s.id " +
                        "   WHERE sim.movieId = m.id " +
                        "   ORDER BY sim.movieId LIMIT 1)," +
                        "   '') AS star1_name," +
                        "COALESCE(" +
                        "   (SELECT s.id FROM stars_in_movies sim " +
                        "   JOIN stars s ON sim.starId = s.id " +
                        "   WHERE sim.movieId = m.id " +
                        "   ORDER BY sim.movieId LIMIT 1)," +
                        "   '') AS star1_id," +
                        "COALESCE(" +
                        "   (SELECT s.name FROM stars_in_movies sim " +
                        "   JOIN stars s ON sim.starId = s.id " +
                        "   WHERE sim.movieId = m.id " +
                        "   ORDER BY sim.movieId LIMIT 1 OFFSET 1)," +
                        "   '') AS star2_name," +
                        "COALESCE(" +
                        "   (SELECT s.id FROM stars_in_movies sim " +
                        "   JOIN stars s ON sim.starId = s.id " +
                        "   WHERE sim.movieId = m.id " +
                        "   ORDER BY sim.movieId LIMIT 1 OFFSET 1)," +
                        "   '') AS star2_id," +
                        "COALESCE(" +
                        "   (SELECT s.name FROM stars_in_movies sim " +
                        "   JOIN stars s ON sim.starId = s.id " +
                        "   WHERE sim.movieId = m.id " +
                        "   ORDER BY sim.movieId LIMIT 1 OFFSET 2)," +
                        "   '') AS star3_name," +
                        "COALESCE(" +
                        "   (SELECT s.id FROM stars_in_movies sim " +
                        "   JOIN stars s ON sim.starId = s.id " +
                        "   WHERE sim.movieId = m.id " +
                        "   ORDER BY sim.movieId LIMIT 1 OFFSET 2)," +
                        "   '') AS star3_id " +
                        "FROM InitialMovies im " +
                        "JOIN movies m ON im.id = m.id " +
                        "LEFT JOIN ratings r ON m.id = r.movieId ");
                        //+
                        //"ORDER BY r.rating DESC;");
                if (genre != null && !genre.isEmpty()){
                    sql.append(") AS subquery WHERE genre1_name LIKE ? or " +
                            "genre2_name LIKE ? or genre3_name LIKE ?");
                }
                else if (star != null && !star.isEmpty()) {
                    sql.append(") AS subquery WHERE star1_name LIKE ? or " +
                            "star2_name LIKE ? or star3_name LIKE ?");
                }


                String sortValue = request.getParameter("sortBy").trim();
                String sortOrder = request.getParameter("sortTitle").trim();
                if (sortValue.equals("title")){
                    if (sortOrder.equals("ascasc")){
                        sql.append("ORDER BY " + sortValue + " ASC" +
                                ", rating ASC");
                    }
                    else if (sortOrder.equals("ascdesc")){
                        sql.append("ORDER BY " + sortValue + " ASC" +
                                ", rating DESC");
                    }
                    else if (sortOrder.equals("descasc")){
                        sql.append("ORDER BY " + sortValue + " DESC" +
                                ", rating ASC");
                    }
                    else {
                        sql.append("ORDER BY " + sortValue + " DESC" +
                                ", rating DESC");
                    }
                }
                else{
                    if (sortOrder.equals("ascasc")){
                        sql.append("ORDER BY " + sortValue + " ASC" +
                                ", title ASC");
                    }
                    else if (sortOrder.equals("ascdesc")){
                        sql.append("ORDER BY " + sortValue + " ASC" +
                                ", title DESC");
                    }
                    else if (sortOrder.equals("descasc")){
                        sql.append("ORDER BY " + sortValue + " DESC" +
                                ", title ASC");
                    }
                    else {
                        sql.append("ORDER BY " + sortValue + " DESC" +
                                ", title DESC");
                    }
                }
                sql.append(" LIMIT ? OFFSET ?;");


                PreparedStatement stmt = conn.prepareStatement(sql.toString());
                System.out.println(stmt);
                int index = 1;
                if (fullText!=null && !fullText.isEmpty()){
                    for (int i = 0; i < tokens.length; i++) {
                        stmt.setString(index++, tokens[i] + "*");
                    }
                    //stmt.setString(index++,fullText + "*");
                }
                if (title != null && !title.isEmpty()) {
                    if (!title.equals("*")){
                        if (title.length() == 1){
                            stmt.setString(index++,  title + "%");
                        }
                        else{
                            stmt.setString(index++,  "%" + title + "%");
                        }
                    }
                }
                if (year != null && !year.isEmpty()) {
                    stmt.setString(index++, year);
                }
                if (director != null && !director.isEmpty()) {
                    stmt.setString(index++, "%" + director + "%");
                }
                if (genre != null && !genre.isEmpty()) {
                    stmt.setString(index++, genre + "%");
                    stmt.setString(index++, genre + "%");
                    stmt.setString(index++, genre + "%");
                }
                else if (star != null && !star.isEmpty()) {
                    stmt.setString(index++, "%" + star + "%");
                    stmt.setString(index++, "%" + star + "%");
                    stmt.setString(index++, "%" + star + "%");
                }

                int offset = 0;
                if (page != null && pageSize != null){
                    offset = (Integer.valueOf(page) - 1) * Integer.valueOf(pageSize);
                }


                stmt.setInt(index++, Integer.valueOf(pageSize));
                stmt.setInt(index++, offset);

                System.out.println(stmt);

                rs = stmt.executeQuery();

                // Second query to get all the corresponding values

                List<Map<String, Object>> movies = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> movie = new HashMap<>();
                    movie.put("id", rs.getString("id"));
                    movie.put("title", rs.getString("title"));
                    movie.put("year", rs.getString("year"));
                    movie.put("director", rs.getString("director"));
                    movie.put("genre1_name", rs.getString("genre1_name"));
                    movie.put("genre1_id", rs.getString("genre1_id"));
                    movie.put("genre2_name", rs.getString("genre2_name"));
                    movie.put("genre2_id", rs.getString("genre2_id"));
                    movie.put("genre3_name", rs.getString("genre3_name"));
                    movie.put("genre3_id", rs.getString("genre3_id"));
                    movie.put("star1_name", rs.getString("star1_name"));
                    movie.put("star1_id", rs.getString("star1_id"));
                    movie.put("star2_name", rs.getString("star2_name"));
                    movie.put("star2_id", rs.getString("star2_id"));
                    movie.put("star3_name", rs.getString("star3_name"));
                    movie.put("star3_id", rs.getString("star3_id"));
                    movie.put("rating", rs.getString("rating"));
                    //movie.put("pageSize", rs.getString("pageSize"));
                    //movie.put("offset",rs.getString("offset"));

                    // Add other fields as needed
                    movies.add(movie);
                }

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                //System.out.println(session.getAttribute("jump"));

                response.getWriter().write(new Gson().toJson(movies));

                // Set response status to 200 (OK)
                response.setStatus(200);
                rs.close();


            } else {
                // Handle empty or invalid input
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("errorMessage", "Some error message");
                response.setContentType("application/json");
                response.getWriter().write(jsonObject.toString());
            }



        } catch (Exception e) {
            // Write error message JSON object to output
            e.printStackTrace();
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

