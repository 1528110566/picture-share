package com.hf6z.controller.config;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hf6z.controller.config.Constant.THREAD_NAME_IN_POOL;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-15 11:20
 */
@Controller
public class GlobalConfig {
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
        return (factory -> {
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.do");
            factory.addErrorPages(error404Page);
        });
    }

    @Resource
    private ThreadFactory threadFactory;

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(
                2, 5, 10, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(), threadFactory);
    }

    @Bean
    public ThreadFactory threadFactory() {
        return new ThreadFactory() {
            private final AtomicInteger poolNumber = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(THREAD_NAME_IN_POOL + poolNumber.getAndIncrement());
                return thread;
            }
        };
    }

    @RequestMapping("/404.do")
    @ResponseBody
    public String error() {
        return "您访问的页面不存在";
    }

//    @RequestMapping("/")
//    public String firstPage() {
//        return "redirect:";
//    }

    @RequestMapping("/upload")
    public String gotoUpload() {
        return "upload";
    }
}