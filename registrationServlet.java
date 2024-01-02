import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/tastebud/RegistrationServlet")
public class RegistrationServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Extract user registration data from the request
        String username = request.getParameter("Username");
        String email = request.getParameter("email");
        String password = request.getParameter("psw");


        // Redirect to a success page or send a response to the client
        response.sendRedirect("registration-success.html");
    }
}
