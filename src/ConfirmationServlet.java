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
import java.util.ArrayList;

@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {

    private static final long serialVersionUID = 2L;


    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        response.setContentType("application/json"); // Response mime type

        JsonObject responseJsonObject = new JsonObject();
        PrintWriter out = response.getWriter();

        ArrayList<Merchandise> previousItems = (ArrayList<Merchandise>) session.getAttribute("previousItems");
        String sid = (String) session.getAttribute("sid");
        if (previousItems == null||sid==null) {
            response.setStatus(401);
            return;
        }
        // Log to localhost log
        request.getServletContext().log("Confirmation: " + sid);
        JsonArray previousItemsJsonArray = new JsonArray();
        double total = 0;
        for (Merchandise item:previousItems){
            total += item.quantity*item.price;
            previousItemsJsonArray.add(item.toJson());
        }
        responseJsonObject.add("previousItems", previousItemsJsonArray);
        responseJsonObject.addProperty("total",(float)Math.round(total * 100d) / 100d);
        responseJsonObject.addProperty("sid",sid);
        session.removeAttribute("previousItems");
        session.removeAttribute("sid");

        // write all the data into the jsonObject
        out.write(responseJsonObject.toString());
    }

}