package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/time-template")
public class ThymeleafController extends HttpServlet {
    private TemplateEngine engine;
    private String initTime;
    private DateTimeFormatter FORMATTER =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");;
    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("./templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        String timezone = req.getParameter("timezone");

        Cookie[] cookies = req.getCookies();

        if (timezone == null){
            timezone = "UTC";
            for(Cookie cookie:cookies){
                if(cookie.getName().equals("lastTimezone")){
                    timezone = cookie.getValue();
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
}
