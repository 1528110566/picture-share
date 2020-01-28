package com.hf6z.controller.pic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hf6z.aop.AddLikeNum;
import com.hf6z.aop.AddReadNum;
import com.hf6z.aop.PicInfoChanged;
import com.hf6z.model.User;
import com.hf6z.service.PicService;
import com.hf6z.service.RedisService;
import com.hf6z.vo.PictureVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hf6z.controller.config.Constant.HOT_PIC_NUM;
import static com.hf6z.controller.config.Constant.UPLOAD_PIC_PATH;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-13 16:55
 */
@Controller
@RequestMapping("/pic")
public class PicController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PicController.class);

    @Resource
    private PicService picService;
    @Resource
    private RedisService redisService;

    @RequestMapping("/getHotPic")
    @ResponseBody
    public JSONObject hotPic(HttpSession session) {
//        System.out.println("getHotPic");
        JSONObject object = new JSONObject();
        if (session.getAttribute("user") == null) {
            object.put("hotPic", "");
        } else {
            JSONArray array = new JSONArray();
            array.addAll(picService.getHotPicByReadNum(HOT_PIC_NUM));
            object.put("hotPic", array);
            LOGGER.info("[" + ((User) session.getAttribute("user")).getUsername() + "]获取hotPic");
        }
        return object;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(@RequestParam("pic") MultipartFile[] files, HttpSession session) {
        if (null == files || files.length == 0) {
            return "upload-fail";
        }
        int flag = 0;
        List<String> fileLocations = new ArrayList<>(files.length);
        for (MultipartFile file : files) {
            try {
                String originFileName = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
                String filePath = UPLOAD_PIC_PATH + originFileName + "_" + System.currentTimeMillis() + ".jpg";
                fileLocations.add(filePath);
                file.transferTo(new File(filePath));
                LOGGER.info("[" + ((User) session.getAttribute("user")).getUsername() + "]上传图片 " + filePath);
            } catch (Exception e) {
                LOGGER.error("图片上传失败，请检查是否登录！" + Arrays.toString(e.getStackTrace()));
                session.setAttribute("errorMsg", Arrays.toString(e.getStackTrace()));
                for (String location : fileLocations) {
                    File temp = new File(location);
                    if (temp.exists()) {
                        temp.delete();
                    }
                }
                flag = 1;
                break;
            }
        }
        if (flag == 0) {
            picService.saveToDB(fileLocations, session);
            session.setAttribute("upload_num", files.length);
            return "upload-ok";
        } else {
            return "upload-fail";
        }
    }

    @RequestMapping(value = "/toUpload")
    public String toUpload() {
        return "redirect:/upload";
    }


    @RequestMapping("/sourcePic/{id}")
    @AddReadNum
    @PicInfoChanged
    public String getSourcePic(@PathVariable int id, Model model, HttpSession session) {
        // 先从缓存中查找
        PictureVO pictureVO = redisService.getPictureVoById(id);
        User user = (User) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("status", "fail");
        } else {
            if (pictureVO == null) {
                // 缓存中找不到再从数据中查找
                pictureVO = picService.getPicVOById(id);
                LOGGER.warn("[{}]请求[{}]，导致出现缓存穿透", user.getUsername(), id);
            }
            if (pictureVO == null) {
                model.addAttribute("status", "fail");
            } else {
                model.addAttribute("type", "source");
                model.addAttribute("pic", pictureVO);
                setModel(model, id);
                LOGGER.info("[{}]加载[{}]原图", user.getUsername(), id);
            }
        }
        return "detail-pic";
    }

    @RequestMapping("/zippedPic/{id}")
    @AddReadNum
    @PicInfoChanged
    public String getZippedPic(@PathVariable int id, Model model, HttpSession session) {
        // 先从缓存中查找
        PictureVO pictureVO = redisService.getPictureVoById(id);
        User user = (User) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("status", "fail");
        } else {
            if (pictureVO == null) {
                // 缓存中找不到再从数据中查找
                pictureVO = picService.getPicVOById(id);
                LOGGER.warn("[{}]请求[{}]，导致出现缓存穿透", user.getUsername(), id);
            }
            if (pictureVO == null) {
                model.addAttribute("status", "fail");
            } else {
                model.addAttribute("type", "zipped");
                model.addAttribute("pic", pictureVO);
                setModel(model, id);
                LOGGER.info("[{}]加载[{}]压缩图", user.getUsername(), id);
            }
        }
        return "detail-pic";
    }

    private void setModel(Model model, int id) {
        int nextId = picService.getNextId(id);
        if (nextId == 0 || nextId == id) {
            model.addAttribute("hasNext", "no");
        } else {
            model.addAttribute("hasNext", "yes");
        }
        int preId = picService.getPreId(id);
        if (preId == 0 || preId == id) {
            model.addAttribute("hasPre", "no");
        } else {
            model.addAttribute("hasPre", "yes");
        }
    }

    @RequestMapping("/like/{id}")
    @ResponseBody
    @AddLikeNum
    @PicInfoChanged
    public JSONObject like(@PathVariable int id, Model model, HttpSession session) {
        JSONObject object = new JSONObject();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            object.put("status", "ok");
            object.put("id", id);
        } else {
            model.addAttribute("status", "fail");
            object.put("status", "fail");
        }
        return object;
    }

    @RequestMapping("/modifyDesc")
    @PicInfoChanged
    public String modifyDesc(@RequestParam("id") int id, Model model, @RequestParam("picDesc") String desc,
                             HttpServletRequest request, HttpSession session) {
        assert desc != null;
        assert id != 0;
        User user = (User) session.getAttribute("user");
        if (user != null) {
            redisService.updateVO(id, desc);
            LOGGER.info("[{}]修改图片[{}]的描述", user.getUsername(), id);
        }
        String ref = request.getHeader("Referer");
        if (ref.contains("source")) {
            return "redirect:/pic/sourcePic/" + id;
        } else {
            return "redirect:/pic/zippedPic/" + id;
        }
    }

    @RequestMapping("/getNext/{id}")
    @ResponseBody
    public JSONObject getNext(@PathVariable int id, HttpServletRequest request, HttpSession session) {
        JSONObject object = new JSONObject();
        if (session.getAttribute("user") == null) {
            object.put("status", "fail");
            return object;
        }
        int nextId = picService.getNextId(id);
        String ref = request.getHeader("Referer");
        if (ref != null && ref.contains("source")) {
            object.put("link", "sourcePic/" + nextId);
        } else {
            object.put("link", "zippedPic/" + nextId);
        }
        return object;
    }

    @RequestMapping("/getPre/{id}")
    @ResponseBody
    public JSONObject getPre(@PathVariable int id, HttpServletRequest request, HttpSession session) {
        JSONObject object = new JSONObject();
        if (session.getAttribute("user") == null) {
            object.put("status", "fail");
            return object;
        }
        int nextId = picService.getPreId(id);
        String ref = request.getHeader("Referer");
        if (ref != null && ref.contains("source")) {
            object.put("link", "sourcePic/" + nextId);
        } else {
            object.put("link", "zippedPic/" + nextId);
        }
        return object;
    }

    @RequestMapping("/delete/{id}")
    @ResponseBody
    public JSONObject delete(@PathVariable int id, HttpSession session, HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        JSONObject object = new JSONObject();
        if (user == null) {
            object.put("status", "fail");
            return object;
        }
        if (picService.deleteById(id)) {
            LOGGER.info("[{}]删除[{}]", user.getUsername(), id);
            return getNext(id, request, session);
        } else {
            LOGGER.info("[{}]删除不存在的[{}]，造成缓存穿透", user.getUsername(), id);
            String ref = request.getHeader("Referer");
            if (ref != null && ref.contains("source")) {
                object.put("link", "sourcePic/" + id);
            } else {
                object.put("link", "zippedPic/" + id);
            }
            return object;
        }
    }

    @RequestMapping("/uploadRank")
    public String uploadRank() {
        return "uploadRank";
    }

    @RequestMapping("/getUploadRank")
    @ResponseBody
    public JSONObject getUploadRank(HttpSession session) {
        JSONObject object = new JSONObject();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            JSONArray array = new JSONArray();
            array.addAll(picService.getUploadRank());
            object.put("rank", array);
            object.put("status", "ok");
            LOGGER.info("[{}]获取上传排行榜", user.getUsername());
        } else {
            object.put("status", "fail");
        }
        return object;
    }
}
