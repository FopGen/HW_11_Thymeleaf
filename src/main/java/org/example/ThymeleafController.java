package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebApplication;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/time")
public class ThymeleafController extends HttpServlet {
    private TemplateEngine engine;
    private String initTime;
    private DateTimeFormatter FORMATTER =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");;
    @Override
    public void init() throws ServletException {


    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        WebApplicationTemplateResolver resolver = new WebApplicationTemplateResolver(createApplication(req));
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");
        resolver.setCacheTTLMs(Long.valueOf(3600000L));
        resolver.setCacheable(true);

        final TemplateEngine engine = new TemplateEngine();
        engine.addTemplateResolver(resolver);


        resp.setContentType("text/html");
        String timezone = req.getParameter("timezone");

        Cookie[] cookies = req.getCookies();

        if (timezone == null){
            timezone = "UTC";
            if(cookies!=null){
                for(Cookie cookie:cookies){
                     if(cookie.getName().equals("lastTimezone")){
                     timezone = cookie.getValue();
                }
            }
            }
        }else{
            resp.addCookie(new Cookie("lastTimezone", timezone));
        }

        initTime = ZonedDateTime.now(ZoneId.of(timezone)).format(FORMATTER);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("timezone", timezone);
        params.put("initTime", initTime);


        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("queryParams", params)
        );

        engine.process("index", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }

    @Override
    public void destroy() {
        initTime = null;
    }

    private JakartaServletWebApplication createApplication(HttpServletRequest request){
        return JakartaServletWebApplication.buildApplication(request.getServletContext());
    }
}
