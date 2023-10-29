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

class Merchandise {
    public String mid;
    public String title;
    public float price;
    public short quantity;

    public Merchandise(String movieId,String t, float p, short q) {
        mid=movieId;
        title = t;
        price= p;
        quantity=q;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("movie_id", mid);
        jsonObject.addProperty("title", title);
        jsonObject.addProperty("price", price);
        jsonObject.addProperty("quantity", quantity);
        return jsonObject;
    }
}


/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {

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
        response.setContentType("application/json"); // Response mime type

        JsonObject responseJsonObject = new JsonObject();
        PrintWriter out = response.getWriter();

        ArrayList<Merchandise> previousItems = (ArrayList<Merchandise>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new ArrayList<Merchandise>();
        }
        // Log to localhost log
        request.getServletContext().log("ViewCart: " + previousItems.size() + " items");
        JsonArray previousItemsJsonArray = new JsonArray();
        double total = 0;
        for (Merchandise item:previousItems){
           total += item.quantity*item.price;
           previousItemsJsonArray.add(item.toJson());
        }
        responseJsonObject.add("previousItems", previousItemsJsonArray);
        responseJsonObject.addProperty("total",(float)Math.round(total * 100d) / 100d);

        // write all the data into the jsonObject
        out.write(responseJsonObject.toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Response mime type

        // Read request body
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String data = buffer.toString();

        // Parse JSON
        JsonObject json = JsonParser.parseString(data).getAsJsonObject();
        String movie_id = json.getAsJsonPrimitive("movie_id").getAsString();
        String actionType = json.getAsJsonPrimitive("type").getAsString();
        short num = Short.valueOf(json.getAsJsonPrimitive("num").getAsString());

        request.getServletContext().log("Cart: num "+num+" "+movie_id+" "+actionType);

        HttpSession session = request.getSession();
        String message = "";

        // get the previous items in a ArrayList
        ArrayList<Merchandise> previousItems = (ArrayList<Merchandise>) session.getAttribute("previousItems");
        session.setAttribute("referrer","cart");
        if (previousItems == null) {
            previousItems = new ArrayList<Merchandise>();
            session.setAttribute("previousItems", previousItems);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousItems) {
                Merchandise removeItem = null;
                for (Merchandise item : previousItems){
                    if (item.mid.equals(movie_id)){
                        if (actionType.equals("add")){
                            item.quantity += num;
                        } else if (actionType.equals("decrease")) {
                            if (item.quantity-num <= 0){
                                actionType = "delete";
                            }
                            else{
                                item.quantity -= num;
                            }
                        }
                        if (actionType.equals("delete")){
                            removeItem = item;
                        }
                        break;
                    }
                }
                previousItems.remove(removeItem);
            }
        }
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("actionDone", actionType);
        response.getWriter().write(responseJsonObject.toString());
    }
}