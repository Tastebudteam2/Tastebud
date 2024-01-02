import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/tastebud/RecipeServlet")
public class RecipeServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve user input from the HTML form
        String userInput = request.getParameter("userInput");

        String aiResponse = YourGPT3IntegrationClass.getResponse(userInput);

        // Send the AI response back to the HTML page
        response.getWriter().write(aiResponse);
    }
}
