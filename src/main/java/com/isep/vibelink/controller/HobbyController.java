package com.isep.vibelink.controller;

import com.isep.vibelink.dao.FollowDAO;
import com.isep.vibelink.dao.HobbyDAO;
import com.isep.vibelink.domain.node.Hobby;
import com.isep.vibelink.domain.node.User;
import com.isep.vibelink.util.ResponseInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 兴趣爱好管理控制器，处理爱好增删改查及用户喜好操作
 */
@Controller
public class HobbyController {
    private final HobbyDAO hobbyDAO;
    private final FollowDAO followDAO;

    /**
     * 构造函数注入依赖
     */
    public HobbyController(HobbyDAO hobbyDAO, FollowDAO followDAO) {
        this.hobbyDAO = hobbyDAO;
        this.followDAO = followDAO;
    }


    /**
     * 判断兴趣列表中是否包含目标兴趣
     *
     * @param hobbies 兴趣列表
     * @param target 目标兴趣
     * @return 是否包含该兴趣
     */
    public static boolean contains(List<Hobby> hobbies, Hobby target) {
        for (Hobby hobby : hobbies) {
            if (hobby.getId().equals(target.getId()))
                return true;
        }
        return false;
    }


    /**
     * 获取所有兴趣爱好（排除已关注），用于展示页面
     *
     * @param request 请求对象
     * @param map     模板数据
     * @return hobbies 视图页面
     */
    @GetMapping("/hobby/getAll")
    public String getAll(HttpServletRequest request, Map<String, Object> map) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) return "login";

        List<Hobby> allHobbies = hobbyDAO.getAllHobbies();
        List<Hobby> myHobbies = hobbyDAO.getMyHobby(user.getAccount());

        List<Hobby> res = new ArrayList<>();
        for (Hobby hobby : allHobbies) {
            if (!contains(myHobbies, hobby)) {
                res.add(hobby);
            }
        }

        int followingCount = followDAO.getMyFollowing(user.getAccount()).size();
        int followerCount = followDAO.getPeopleWhoFollowMe(user.getAccount()).size();

        map.put("myFollowing", followingCount);
        map.put("follower", followerCount);
        map.put("hobbies", res);
        map.put("myHobbies", myHobbies);
        map.put("user", user);

        return "hobbies";
    }


    /**
     * 添加一个新的兴趣爱好
     *
     * @param hName 兴趣名称
     * @param hType 兴趣类型
     * @return 添加结果
     */
    @PostMapping("/hobby/addHobby")
    @ResponseBody
    public ResponseInfo<Hobby> addHobby(@RequestParam("hName") String hName, @RequestParam("hType") String hType) {
        Hobby hobby = hobbyDAO.addHobby(hName, hType);
        if (hobby == null) {
            return ResponseInfo.fail("Failed to add hobby");
        }
        return ResponseInfo.success("Hobby added successfully", hobby);
    }


    /**
     * 删除指定 ID 的兴趣
     *
     * @param id 兴趣 ID
     * @return 删除结果
     */
    @PostMapping("/hobby/delete")
    @ResponseBody
    public ResponseInfo<Void> deleteHobby(@RequestParam("id") Long id) {
        boolean success = hobbyDAO.deleteWithId(id);
        if (success) {
            return ResponseInfo.success("Hobby deleted successfully", null);
        } else {
            return ResponseInfo.fail("Hobby does not exist or has been deleted");
        }
    }


    /**
     * 根据兴趣名称模糊搜索兴趣
     *
     * @param hName 兴趣名关键词
     * @return 匹配到的兴趣列表
     */
    @GetMapping("/hobby/search")
    @ResponseBody
    public ResponseInfo<List<Hobby>> search(@RequestParam("hName") String hName) {
        List<Hobby> hobbies = hobbyDAO.searchHobbyByName(".*" + hName + ".*");
        return ResponseInfo.success("Search completed", hobbies);
    }


    /**
     * 修改兴趣信息（名称或类型）
     *
     * @param id    兴趣 ID
     * @param hName 新名称
     * @param hType 新类型
     * @return 修改后的兴趣对象
     */
    @PostMapping("/hobby/fix")
    @ResponseBody
    public ResponseInfo<Hobby> fix(@RequestParam("id") Long id, @RequestParam("hName") String hName, @RequestParam("hType") String hType) {
        Hobby hobby = hobbyDAO.fixHobby(id, hName, hType);
        if (hobby != null) {
            return ResponseInfo.success("Hobby updated successfully", hobby);
        }
        return ResponseInfo.fail("Failed to update hobby");
    }
}
