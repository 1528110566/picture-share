package com.picshare.aop;

import com.picshare.model.User;
import com.picshare.service.RedisService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-16 23:33
 */
@Component
@Aspect
public class ReadNumAop {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadNumAop.class);

    @Pointcut("@annotation(com.picshare.aop.AddReadNum)")
    public void readNumAdd() {
    }

    @Resource
    private RedisService redisService;

    @After("readNumAdd()")
    public void redisAdd(JoinPoint joinPoint) {
        Model model = (Model) joinPoint.getArgs()[1];
        if (model.getAttribute("status") == null
                || !("fail").equals(model.getAttribute("status"))) {
            int id = (int) joinPoint.getArgs()[0];
            String username = ((User) ((HttpSession) joinPoint.getArgs()[2]).getAttribute("user")).getUsername();
            redisService.addReadNum(id, username);
        }
    }
}
