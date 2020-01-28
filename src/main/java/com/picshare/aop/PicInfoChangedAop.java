package com.picshare.aop;

import com.picshare.service.RedisService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.annotation.Resource;

/**
 * description:图片的某个资源改变，记录到redis，以便定时任务能将其更新到数据库
 *
 * @author Tao Zheng
 * @date 2020-01-18 0:25
 */
@Component
@Aspect
public class PicInfoChangedAop {
    @Resource
    private RedisService redisService;

    @Pointcut("@annotation(PicInfoChanged)")
    public void pointCut() {
    }

    @After("pointCut()")
    public void picInfoChanged(JoinPoint joinPoint) {
        Model model = (Model) joinPoint.getArgs()[1];
        if (model.getAttribute("status") == null
                || !("fail").equals(model.getAttribute("status"))) {
            int id = (int) joinPoint.getArgs()[0];
            redisService.addChangedPic(id);
        }
    }
}
