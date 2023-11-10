import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "DashAccessServlet", urlPatterns = "/_dashboard")
public class DashAccessServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        if (request.getSession().getAttribute("employee")==null){
            response.sendRedirect("eLogin.html");
        }
        else{
            response.sendRedirect("dashboard.html");
        }

    }

}