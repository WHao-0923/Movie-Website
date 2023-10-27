import com.google.gson.Gson;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "GenresServlet", urlPatterns = "/api/genres")
public class GenresServlet extends HttpServlet {
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
        response.setContentType("application/json"); // Response mime type
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
        PrintWriter out = response.getWriter();
        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = null;

            String query = "SELECT id,name FROM genres;";
            PreparedStatement stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            List<List<String>> genres = new ArrayList<>();
            while (rs.next()) {
                List<String> myTuple = new ArrayList<>();
                myTuple.add(rs.getString("id"));
                myTuple.add(rs.getString("name"));
                genres.add(myTuple);
            }
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new Gson().toJson(genres));
            // Set response status to 200 (OK)
            response.setStatus(200);
            rs.close();
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

        }
    }
}
