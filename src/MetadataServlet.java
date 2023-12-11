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
import java.util.ArrayList;

@WebServlet(name = "MetadataServlet", urlPatterns = "/api/metadata")
public class MetadataServlet extends HttpServlet {

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
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        response.setContentType("application/json"); // Response mime type

        JsonObject responseJsonObject = new JsonObject();
        PrintWriter out = response.getWriter();

        if ((String) session.getAttribute("employee")==null){
            response.setStatus(401);
            return;
        }

        try(Connection conn = dataSource.getConnection()) {
            String query = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, "moviedb");
            ResultSet rs = statement.executeQuery();
            JsonArray meta = new JsonArray();
            while (rs.next()){
                JsonObject col = new JsonObject();
                col.addProperty("table_name",rs.getString("TABLE_NAME"));
                col.addProperty("col",rs.getString("COLUMN_NAME"));
                col.addProperty("type",rs.getString("DATA_TYPE"));
                meta.add(col);
            }
            responseJsonObject.add("cols",meta);

        }catch (Exception e){
            responseJsonObject.addProperty("errorMessage", e.getMessage());
            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        // write all the data into the jsonObject
        out.write(responseJsonObject.toString());

    }

}