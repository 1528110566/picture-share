package com.hf6z.aop;

import com.hf6z.model.User;
import com.hf6z.service.RedisService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-18 0:03
 */
@Component
@Aspect
public class LikeNumAop {
    @Pointcut("@annotation(AddLikeNum)")
    public void likeNumAop() {
    }

    @Resource
    private RedisService redisService;

    @After("likeNumAop()")
    public void redisAdd(JoinPoint joinPoint) {
        Model model = (Model) joinPoint.getArgs()[1];
        if (model.getAttribute("status") == null
                || !("fail").equals(model.getAttribute("status"))) {
            int id = (int) joinPoint.getArgs()[0];
            String username = ((User) ((HttpSession) joinPoint.getArgs()[2]).getAttribute("user")).getUsername();
            redisService.addLikeNum(id, username);
        }
    }
}
