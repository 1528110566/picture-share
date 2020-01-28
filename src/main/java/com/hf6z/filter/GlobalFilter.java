package com.hf6z.filter;

import com.hf6z.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-15 10:39
 */
//@Configuration
public class GlobalFilter {
//    @Bean("myFilter")
    public Filter filter() {
        return new Filter() {
            @Override
            public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {
            }

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                HttpServletRequest httpServletRequest = (HttpServletRequest) request;
                String url = httpServletRequest.getRequestURI();
                System.out.println(url);
                if (url.contains("swagger") || url.contains("pic") || url.equals("/")) {
                    chain.doFilter(request, response);
                    return;
                }
                if (url.contains("kartik-v-bootstrap-fileinput-5d9c093/")) {
                    chain.doFilter(request, response);
                    return;
                }

                User user = (User) httpServletRequest.getSession().getAttribute("user");
                if (user == null && !url.contains("common")) {
//                    System.out.println("redirect");
                    request.getRequestDispatcher("/toLogin").forward(request, response);
                } else {
                    chain.doFilter(request, response);
                }
//                if (url.startsWith("/pic") || url.equals("/") || url.equals("/common")) {
//                    if (url.contains("/uploadPic")) {
//                        if (((HttpServletRequest) request).getSession().getAttribute("user") == null) {
//                            request.getRequestDispatcher("/toLogin").forward(request, response);
//                        } else {
//                            chain.doFilter(request, response);
//                        }
//                    } else {
//                        chain.doFilter(request, response);
//                    }
//                } else {
//                    if (((HttpServletRequest) request).getSession().getAttribute("user") == null) {
//                        request.getRequestDispatcher("/toLogin").forward(request, response);
//                    } else {
//                        chain.doFilter(request, response);
//                    }
//                }
            }

            @Override
            public void destroy() {
            }
        };
    }
}
