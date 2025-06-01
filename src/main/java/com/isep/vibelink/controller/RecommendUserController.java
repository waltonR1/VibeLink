package com.isep.vibelink.controller;

import com.isep.vibelink.dao.FollowDao;
import com.isep.vibelink.dao.RecommendDao;
import com.isep.vibelink.domain.node.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

/**
 * 用户推荐控制器，基于朋友、兴趣和分享推荐可能认识的人
 */
@Controller
public class RecommendUserController {
    private final FollowDao followDao;
    private final RecommendDao recommendDao;

    /**
     * 构造函数注入依赖
     */
    public RecommendUserController(FollowDao followDao, RecommendDao recommendDao) {
        this.followDao = followDao;
        this.recommendDao = recommendDao;
    }

    /**
     * 判断某用户是否在列表中
     *
     * @param list    用户列表
     * @param target 目标用户
     * @return 是否包含该用户
     */
    @SuppressWarnings("JavaExistingMethodCanBeUsed")
    public static boolean contains(List<User> list, User target) {
        for (User user : list) {
            if (user.getId().equals(target.getId()))
                return true;
        }
        return false;
    }

    /**
     * 通用的用户推荐处理方法
     *
     * @param request 请求对象
     * @param map     页面参数容器
     * @param title   页面标题
     * @param source  数据源（byFriend/byShare/byHobby）
     * @return 渲染视图
     */
    private String recommend(HttpServletRequest request, Map<String, Object> map,
                             String title, String source) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null){
            return "login";
        }

        List<User> candidates;
        switch (source) {
            case "byFriend" -> candidates = recommendDao.byFriend(user.getAccount());
            case "byShare" -> candidates = recommendDao.byShare(user.getAccount());
            case "byHobby" -> candidates = recommendDao.byHobby(user.getAccount());
            default -> candidates = new ArrayList<>();
        }

        List<User> myFollowing = followDao.getMyFollowing(user.getAccount());
        Set<User> recommends = new HashSet<>();

        for (User candidate : candidates) {
            if (!candidate.getAccount().equals(user.getAccount()) &&
                    !contains(myFollowing, candidate)) {
                recommends.add(candidate);
            }
        }

        int followingCount = myFollowing.size();
        int followerCount = followDao.getPeopleWhoFollowMe(user.getAccount()).size();

        map.put("myFollowing", followingCount);
        map.put("follower", followerCount);
        map.put("recommends", recommends);
        map.put("user", user);
        map.put("index", "Friends Recommendation");
        map.put("title", title);
        map.put("reverse", false);

        return "userList";
    }


    /**
     * 基于朋友的朋友推荐
     *
     * @param request 请求对象
     * @param map     模板数据
     * @return 视图名称
     */
    @GetMapping("/recommend/user/byFriend")
    public String recommendFriendsByFriend(HttpServletRequest request, Map<String, Object> map) {
        return recommend(request, map, "Discover Friends", "byFriend");
    }


    /**
     * 基于点赞/评论等互动推荐
     *
     * @param request 请求对象
     * @param map     模板数据
     * @return 视图名称
     */
    @GetMapping("/recommend/user/byShare")
    public String recommendFriendsByShare(HttpServletRequest request, Map<String, Object> map) {
        return recommend(request, map, "Close Friends", "byShare");
    }

    /**
     * 基于兴趣推荐
     *
     * @param request 请求对象
     * @param map     模板数据
     * @return 视图名称
     */
    @GetMapping("/recommend/user/byHobby")
    public String recommendFriendsByHobby(HttpServletRequest request, Map<String, Object> map) {
        return recommend(request, map, "Common Interests", "byHobby");
    }
}
