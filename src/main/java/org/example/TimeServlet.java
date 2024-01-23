package org.example;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {

    private String initTime;
    private DateTimeFormatter FORMATTER = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        String timezone = req.getParameter("timezone");

        if (timezone == null){
            timezone = "UTC";
        }

        initTime = ZonedDateTime.now(ZoneId.of(timezone)).format(FORMATTER);

        PrintWriter out = resp.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + initTime + "  " + timezone + "</h1>");
        out.println("</html></body>");

    }

    @Override
    public void destroy() {
        initTime = null;
    }
}
