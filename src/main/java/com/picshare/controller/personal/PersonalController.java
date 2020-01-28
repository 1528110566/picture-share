package com.picshare.controller.personal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.picshare.controller.config.Constant;
import com.picshare.model.User;
import com.picshare.service.PicService;
import com.picshare.vo.PictureVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-19 12:23
 */
@Controller
@RequestMapping("/personal")
public class PersonalController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonalController.class);
    @Resource
    private PicService picService;

    @RequestMapping("/personalPage")
    public String toPersonal() {
        return "personal";
    }

    @RequestMapping("/getAllUploadPicInfo")
    @ResponseBody
    public JSONObject personal(HttpSession httpSession) {
        JSONObject object = new JSONObject();
        User user = (User) httpSession.getAttribute("user");
        if (user != null) {
            JSONArray array = new JSONArray();
            array.addAll(list(user.getUsername()));
            object.put("status", "ok");
            object.put("pic", array);
            LOGGER.info("[{}]获取其上传的所有图片", user.getUsername());
        } else {
            object.put("status", "fail");
            object.put("pic", "");
        }
        return object;
    }

    /**
     * 因为要获取的图片很多，所以将zipped链接更改为缩略图
     * 注意：只有zipped图片有缩略图
     */
    private List<PictureVO> list(String username) {
        List<PictureVO> list = picService.getAllPicByUserName(username);
        for (PictureVO pictureVO : list) {
            String zippedLink = pictureVO.getZippedLink();
            String temp = zippedLink.substring(0, zippedLink.lastIndexOf(".")) + Constant.THUMABNAIL_PIC_PREFIX + ".jpg";
            pictureVO.setZippedLink(temp);
        }
        return list;
    }


    @RequestMapping("othersPage/{username}")
    public String getOthersUpload(@PathVariable("username") String username, HttpSession httpSession, Model model) {
        User user = (User) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("others", username);
            return "others";
        }
        return "redirect:";
    }

    @RequestMapping("/getOthers/{username}")
    @ResponseBody
    public JSONObject getOthersPic(@PathVariable("username") String username, HttpSession httpSession) {
        JSONObject object = new JSONObject();
        User user = (User) httpSession.getAttribute("user");
        if (user != null) {
            JSONArray array = new JSONArray();
            if (!"".equals(username)) {
                array.addAll(list(username));
            }
            object.put("status", "ok");
            object.put("pic", array);
            LOGGER.info("[{}]获取[{}]上传的所有图片", user.getUsername(), username);
        } else {
            object.put("status", "fail");
            object.put("pic", "");
        }
        return object;
    }
}
