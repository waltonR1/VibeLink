package com.isep.vibelink.controller;

import com.isep.vibelink.dao.FollowDao;
import com.isep.vibelink.domain.node.User;
import com.isep.vibelink.util.ResponseInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * 处理用户关注与粉丝相关功能的控制器
 */
@Controller
public class FollowController {
    private final FollowDao followDao;

    /**
     * 构造函数注入依赖
     */
    public FollowController(FollowDao followDao) {
        this.followDao = followDao;
    }


    /**
     * 判断某用户是否在列表中
     *
     * @param list    用户列表
     * @param target 目标用户
     * @return 是否包含该用户
     */
    public static boolean contains(List<User> list, User target) {
        for (User user : list) {
            if (user.getId().equals(target.getId()))
                return true;
        }
        return false;
    }


    /**
     * 执行关注操作
     *
     * @param follower  关注者账号
     * @param following 被关注者账号
     * @return 返回是否关注成功
     */
    @PostMapping("/follow/followIt")
    @ResponseBody
    public ResponseInfo<Long> followIt(@RequestParam("follower") String follower,
                                       @RequestParam("following") String following) {
        Long id = followDao.createFollow(follower, following);
        if (id != null) {
            return ResponseInfo.success("success", id);
        }
        return ResponseInfo.fail("fail");
    }

    /**
     * 取消关注操作
     *
     * @param follower  关注者账号
     * @param following 被关注者账号
     * @return 返回取消关注是否成功
     */
    @PostMapping("/follow/unfollow")
    @ResponseBody
    public ResponseInfo<Long> unfollow(@RequestParam("follower") String follower,
                                       @RequestParam("following") String following) {
        Long count = followDao.deleteRelation(follower, following);
        if (count == 0) {
            return ResponseInfo.fail("Unfollow failed");
        }
        return ResponseInfo.success("Unfollow successful", count);
    }

    /**
     * 获取“关注我的人”页面
     *
     * @param request 请求对象
     * @param map     模板数据
     * @return 视图名称
     */
    @GetMapping("/user/follower")
    public String getFans(HttpServletRequest request, Map<String, Object> map) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "login";
        }

        List<User> myFollowing = followDao.getMyFollowing(user.getAccount());
        List<User> followers = followDao.getPeopleWhoFollowMe(user.getAccount());

        Set<User> mutualFollows = new HashSet<>(); // 单向粉丝推荐
        Set<User> recommendations = new HashSet<>(); // 互相关注

        for (User follower : followers) {
            if (contains(myFollowing, follower)) {
                mutualFollows.add(follower);
            } else {
                recommendations.add(follower);
            }
        }

        int followingCount = myFollowing.size();
        int followerCount = followers.size();

        map.put("myFollowing", followingCount);
        map.put("follower", followerCount);
        map.put("recommends", recommendations);
        map.put("myStar", mutualFollows);
        map.put("user", user);
        map.put("title", "Followers");
        map.put("index", "Home");
        map.put("hasCategory", true);

        return "userList";
    }

    /**
     * 获取“我关注的人”页面
     *
     * @param request 请求对象
     * @param map     模板数据
     * @return 视图名称
     */
    @GetMapping("/user/myFollowing")
    public String getMyFollowing(HttpServletRequest request, Map<String, Object> map) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "login";
        }

        List<User> followingList = followDao.getMyFollowing(user.getAccount());
        int followingCount = followingList.size();
        int followerCount = followDao.getPeopleWhoFollowMe(user.getAccount()).size();

        map.put("myFollowing", followingCount);
        map.put("follower", followerCount);
        map.put("recommends", followingList);
        map.put("user", user);
        map.put("title", "Following");
        map.put("index", "Home");

        return "myFollowing";
    }
}
