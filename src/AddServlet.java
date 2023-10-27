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

@WebServlet(name = "AddServlet", urlPatterns = "/api/add")
public class AddServlet extends HttpServlet {

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
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String movie_id = request.getParameter("movie_id");
        String user = (String) session.getAttribute("user");
        if (movie_id==null || user==null){
            response.setStatus(401);
            return;
        }

        JsonObject responseJsonObject = new JsonObject();
        JsonObject modified = null;
        PrintWriter out = response.getWriter();
        String message = "";

        boolean hasAdded = false;
        ArrayList<Merchandise> previousItems = (ArrayList<Merchandise>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new ArrayList<Merchandise>();
        }
        // Log to localhost log
        request.getServletContext().log("Add: getting " + previousItems.size() + " items");
        try (Connection conn = dataSource.getConnection()) {
            for (Merchandise item : previousItems){
                if (item.mid.equals(movie_id)){
                    item.quantity ++;
                    hasAdded = true;
                    message = "quantity successfully added one";
                    modified = item.toJson();
                    break;
                }
            }
            if (!hasAdded){
                String query = "select * from movies as m, ratings as r where m.id=r.movieId and m.id=?";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, movie_id);
                ResultSet rs = statement.executeQuery();

                // Iterate through each row of rs
                while (rs.next()) {
                    Merchandise newItem = new Merchandise(movie_id, rs.getString("title"), Float.valueOf(rs.getString("rating")), (short) 1);
                    previousItems.add(newItem);
                    message = "new item has been added to cart";
                    hasAdded = true;
                    modified = newItem.toJson();
                }
            }
            if (hasAdded){
                session.setAttribute("previousItems",previousItems);
                responseJsonObject.add("addedItem",modified);
            }
            else{
                message = "no such movie";
            }
            response.setContentType("application/json");
            responseJsonObject.addProperty("message",message);
            // write all the data into the jsonObject
            out.write(responseJsonObject.toString());
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