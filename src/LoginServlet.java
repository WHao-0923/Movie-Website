import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import jakarta.servlet.http.HttpSession;
import org.jasypt.util.password.StrongPasswordEncryptor;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedbexample,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // Use http GET
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json"); // Response mime type
        PrintWriter out = response.getWriter();

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
        String email = json.getAsJsonPrimitive("email").getAsString();
        String password = json.getAsJsonPrimitive("password").getAsString();
        String gRecaptchaResponse = json.getAsJsonPrimitive("gRecaptchaResponse").getAsString();

        request.getServletContext().log("gRecaptchaResponse=" + gRecaptchaResponse);

        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            request.getServletContext().log("Recaptcha success");
        } catch (Exception e) {
            response.setStatus(401);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            return;
        }

        request.getServletContext().log("logging in:" + email + " " +password);

        try(Connection conn = dataSource.getConnection()) {
            // 3. 数据库查询
            // Construct a query with parameter represented by "?"
            String query = "select * from customers where email=?";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, email);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            // 4. 检查凭证
            if (rs.next()&&new StrongPasswordEncryptor().checkPassword(password,rs.getString("password") )) {
                // TODO: It's better to hash and salt passwords, and compare the hashed values
                // 5. 处理登录
                response.setStatus(200);
                request.getSession().setAttribute("user", email);
                request.getSession().setAttribute("uid",rs.getString("id"));
            } else {
                response.setStatus(401);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("errorMessage", "Invalid email or password");
                out.write(jsonObject.toString());
            }
        } catch (Exception e) {
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
