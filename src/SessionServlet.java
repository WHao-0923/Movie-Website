import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

// Declaring a WebServlet called SessionServlet, which maps to url "/session"
@WebServlet(name = "SessionServlet", urlPatterns = "/session")
public class SessionServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String title = "Session Tracking Example";

        // Get a instance of current session on the request
        HttpSession session = request.getSession(true);

        String heading;

        // Retrieve data named "accessCount" from session, which count how many times the user requested before
        Integer accessCount = (Integer) session.getAttribute("accessCount");

        if (accessCount == null) {
            // Which means the user is never seen before
            accessCount = 0;
            heading = "Welcome, New-Comer";
        } else {
            // Which means the user has requested before, thus user information can be found in the session
            heading = "Welcome Back";
            accessCount++;
        }

        // Update the new accessCount to session, replacing the old value if existed
        session.setAttribute("accessCount", accessCount);

        out.println("<html><head><title>" + title + "</title></head>\n" +

                "<body bgcolor=\"#FDF5E6\">\n" +
                "<h1 ALIGN=\"center\">" + heading + "</h1>\n" +  // Set the greeting heading generated before
                "<h2>Information on Your Session:</H2>\n" +
                // Create a <table>
                "<table border=1 align=\"center\">\n" +
                "  <tr bgcolor=\"#FFAD00\">\n" +  // Create a <tr> (table row)
                "    <th>Info Type<th>Value\n" +  // Create two <th>s (table header)

                // Create a <tr> (table row)
                "  <tr>\n" +
                "    <td>ID\n" +  // Create the first <td> (table data) in <tr>, which corresponding to the first column
                "    <td>" + session.getId() + "\n" +   // Create the second <td> (table data) in <tr>, which
                // corresponding to the second column

                // Repeat for more table rows and data.
                "  <tr>\n" +
                "    <td>Creation Time\n" +
                "    <td>" +
                new Date(session.getCreationTime()) + "\n" +
                "  <tr>\n" +
                "    <td>Time of Last Access\n" +
                "    <td>" +
                new Date(session.getLastAccessedTime()) + "\n" +
                "  <tr>\n" +
                "    <td>Number of Previous Accesses\n" +
                "    <td>" + accessCount + "\n" +
                "  </tr>" +
                "</table>\n");

        // The following two statements show how to retrieve parameters in the request. The URL format is something like:
        // http://localhost:8080/cs122b-fall21-project2-session-example/Session?myname=Chen%20Li
        String myName = request.getParameter("myname");
        if (myName != null)
            out.println("Hey " + myName + "<br><br>");

        out.println("</body></html>");
    }
}
