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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

@WebServlet(name = "PayServlet", urlPatterns = "/api/pay")
public class PayServlet extends HttpServlet {

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
        String first_name = json.getAsJsonPrimitive("first_name").getAsString();
        String last_name = json.getAsJsonPrimitive("last_name").getAsString();
        String card_number = json.getAsJsonPrimitive("card_number").getAsString();
        String expiration = json.getAsJsonPrimitive("expiration").getAsString();

        request.getServletContext().log("Pay: customer "+first_name+" "+last_name);

        HttpSession session = request.getSession();
        String message = "";
        PrintWriter out = response.getWriter();

        // get the previous items in a ArrayList
        ArrayList<Merchandise> previousItems = (ArrayList<Merchandise>) session.getAttribute("previousItems");
        if (previousItems == null || previousItems.isEmpty()) {
            message = "cart.html";
        }
        else{
            try (Connection conn = dataSource.getConnection()){
                String query = "select * from creditcards where id=? and firstName=? and lastName=? and expiration=?";

                // Declare our statement
                PreparedStatement statement = conn.prepareStatement(query);

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                statement.setString(1, card_number);
                statement.setString(2, first_name);
                statement.setString(3, last_name);
                statement.setString(4,expiration);

                // Perform the query
                ResultSet rs = statement.executeQuery();

                if (rs.next()){
                    response.setStatus(200);
                    message = "confirmation.html";
                    UUID uuid = UUID.randomUUID();
                    String transaction_id = uuid.toString();
                    for (Merchandise item : previousItems ){
                        String q2 = "Insert into sales (customerId,movieId,quantity,sid,saleDate) values (?,?,?,?,?)";
                        PreparedStatement s = conn.prepareStatement(q2);
                        s.setString(1, card_number);
                        s.setString(2, item.mid);
                        s.setInt(3, item.quantity);
                        s.setString(4,transaction_id);
                        s.setString(5, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        s.execute();
                    }
                    request.getServletContext().log("Pay: successful "+transaction_id);
                    session.setAttribute("hasPaid",true);
                    session.setAttribute("sid",transaction_id);
                }
                else{
                    response.setStatus(401);
                    message = "incorrect information";
                }
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