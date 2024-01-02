import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/tastebud/AllergyCheckServlet")
public class AllergyCheckServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String allergies = request.getParameter("allergies");
        String food = request.getParameter("food");

        boolean isSafe = true;

        // Send response to the client
        response.getWriter().write(Boolean.toString(isSafe));
    }
}
