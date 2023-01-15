package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${community.path.upload}")
    private String uploadPath;

    @Autowired
    private UserService userService;
    @Autowired
    HostHolder hostHolder;

    @Value("${community.path.domain}")
    private String domain;

    @GetMapping("/setting")
    public String getSettingPage() {
        return "/site/setting";
    }

    /**
     * 更新用户头像
     * @param headerImage 图片信息
     * @param model
     * @return
     */
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model){
        // 1. 判断是否传入图片
        if (headerImage == null) {
            model.addAttribute("error", "你没有选择图片!");
            return "/site/setting";
        }

        // 获取图片名字
        String filename = headerImage.getOriginalFilename();
        // 取后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确!");
            return "/site/setting";
        }

        // 生成图片名
        filename = CommunityUtil.generateUUID() + suffix;
        // 文件存放路径
        File file = new File(uploadPath + "/" + filename);
        try {
            headerImage.transferTo(file);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }

        // 更新用户头像路径
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headUrl);

        return "redirect:/index";
    }

    /**
     * 获取用户头像
     * @param fileName 图片名
     * @param response
     */
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) throws IOException {
        // 1. 服务器取出图片
        // 2. 将图片返回给前端
        fileName = uploadPath + "/" + fileName;
        // 文件后缀名
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);

        try (
                // 输入流
                FileInputStream fis = new FileInputStream(fileName);
                // 输出流
                OutputStream os = response.getOutputStream();
        ) {
            // 暂存
            byte[] buffer = new byte[1024];
            int  b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0 , b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户密码
     * @param oldPassword
     * @param newPassword
     * @param model
     * @return
     */
    @PostMapping("/changePw")
    public String changePassword(String oldPassword, String newPassword, Model model){
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.changePassword(user, oldPassword, newPassword);

        // 修改密码成功
        if (map == null || map.isEmpty()) {
            return "redirect:/index";
        } else {
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            return "/site/setting";
        }
    }
}
