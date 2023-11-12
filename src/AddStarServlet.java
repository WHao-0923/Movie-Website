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

@WebServlet(name = "AddStarServlet", urlPatterns = "/api/addStar")
public class AddStarServlet extends HttpServlet {

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
        String name = json.getAsJsonPrimitive("name").getAsString();
        String pre_birth = json.getAsJsonPrimitive("birth").getAsString();
        //card_number = card_number.replaceAll("(.{4})", "$1 ").trim();


        HttpSession session = request.getSession();
        String message = "";
        PrintWriter out = response.getWriter();
        JsonObject jsonObject = new JsonObject();

        if (session.getAttribute("employee") == null) {
            message = "should log in";
            response.setStatus(401);
        }
        else{
            try (Connection conn = dataSource.getConnection()) {
                PreparedStatement st = conn.prepareStatement("select max(id) from stars;");
                ResultSet rs = st.executeQuery();
                String max_id = null;
                int this_id = 0;
                while (rs.next()) {
                    max_id = rs.getString("max(id)");
                    this_id = Integer.parseInt(max_id.substring(2));
                }

                String query = "Insert into stars (name,birthYear,id) values (?,?,?)";

                PreparedStatement statement = conn.prepareStatement(query);

                statement.setString(1, name);
                if (pre_birth.isEmpty()){
                    statement.setNull(2, Types.INTEGER);
                }
                else{
                statement.setInt(2, Integer.parseInt(pre_birth));}
                statement.setString(3, max_id.substring(0, 2) + (this_id + 1));

                request.getServletContext().log(statement.toString());

                // Perform the query
                statement.execute();
                message = "Adding successful";
                jsonObject.addProperty("starId",max_id.substring(0, 2) + (this_id + 1));

            }
            catch (Exception e) {
                // Write error message JSON object to output
                jsonObject.addProperty("errorMessage", e.getMessage());
                out.write(jsonObject.toString());

                // Log error to localhost log
                request.getServletContext().log("Error:", e);
                // Set response status to 500 (Internal Server Error)
                response.setStatus(500);
            }
        }
        response.setContentType("application/json");
        jsonObject.addProperty("message",message);
        out.write(jsonObject.toString());
    }
}