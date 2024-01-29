package org.example;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.TimeZone;

@WebFilter("/time-template/*")
public class TimezoneValidateFilter extends HttpFilter {
    final static int STATUS_400 = 400;
    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {

        String timeZoneReq = req.getParameter("timezone");
        if (timeZoneReq==null){
            timeZoneReq = "UTC";
        }

        try{

            ZonedDateTime.now(ZoneId.of(timeZoneReq)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            chain.doFilter(req, resp);

        }catch(DateTimeException ex){

            resp.setStatus(STATUS_400);
            resp.setContentType("text/html");
            PrintWriter out = resp.getWriter();
            out.println("<html><body>");
            out.println("<h1>" + "Invalid timezone  --- !!!" + "</h1>");
            out.println("</html></body>");
        }

    }
}
