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

@WebServlet(name = "AutoCompleteServlet", urlPatterns = "/api/autocomplete")
public class AutoCompleteServlet extends HttpServlet {
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
        try (Connection conn = dataSource.getConnection()) {
            String[] tokens = request.getParameter("query").trim().split("\\s+");
            StringBuilder sql = new StringBuilder("SELECT id,title FROM movies WHERE ");
            for (int i = 0; i < tokens.length; i++) {
                if (i > 0) sql.append(" AND ");
                sql.append("MATCH(title) AGAINST (? IN BOOLEAN MODE)");
            }
            PreparedStatement stmt = conn.prepareStatement(sql.toString());

            for (int i = 0; i < tokens.length; i++) {
                stmt.setString(i + 1, tokens[i] + "*");
            }

            ResultSet rs = stmt.executeQuery();
            List<Map<String,String>> results = new ArrayList<>();
            while (rs.next()){
                Map<String, String> result = new HashMap<>();
                result.put("id",rs.getString("id"));
                result.put("title",rs.getString("title"));
                results.add(result);
            }
            response.setCharacterEncoding("UTF-8");

            //System.out.println(session.getAttribute("jump"));
            System.out.println(results);
            response.getWriter().write(new Gson().toJson(results));

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
        } finally {
            out.close();

        }
    }
}
