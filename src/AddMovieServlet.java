import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/addMovie")
public class AddMovieServlet extends HttpServlet {

    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbMaster");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

//         Read request body
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String data = buffer.toString();

        // Parse JSON
        JsonObject json = JsonParser.parseString(data).getAsJsonObject();
        String title = json.getAsJsonPrimitive("title").getAsString();
        String starName = json.getAsJsonPrimitive("starName").getAsString();
        String genre = json.getAsJsonPrimitive("genre").getAsString();
        String director = json.getAsJsonPrimitive("director").getAsString();
        int year = json.getAsJsonPrimitive("year").getAsInt();

        HttpSession session = request.getSession();
        String message = "";
        PrintWriter out = response.getWriter();

        if (session.getAttribute("employee") == null) {
            message = "should log in";
            response.setStatus(401);
        }
        else{
            try (Connection conn = dataSource.getConnection()) {
                PreparedStatement st = conn.prepareStatement("call add_movie(?,?,?,?,?);");
                st.setString(1,title);
                st.setInt(2,year);
                st.setString(3,director);
                st.setString(4,starName);
                st.setString(5,genre);
                request.getServletContext().log(st.toString());
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    message = rs.getString("message");
                }
                request.getServletContext().log(message);

            }
            catch (Exception e) {
                // Write error message JSON object to output
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("errorMessage", e.getMessage());
                out.write(jsonObject.toString());

                // Log error to localhost log
                request.getServletContext().log("Error:", e);
                // Set response status to 500 (Internal Server Error)
                response.setStatus(500);
            }
        }
        response.setContentType("application/json");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message",message);
        out.write(jsonObject.toString());
    }
}