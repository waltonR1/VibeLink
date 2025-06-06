package com.isep.vibelink.controller;

import com.isep.vibelink.dao.FollowDAO;
import com.isep.vibelink.dao.RecommendDAO;
import com.isep.vibelink.dao.UserDAO;
import com.isep.vibelink.domain.node.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户推荐控制器，基于朋友、兴趣和分享推荐可能认识的人
 */
@Controller
public class RecommendUserController {
    private final FollowDAO followDAO;
    private final RecommendDAO recommendDAO;
    private final UserDAO userDAO;

    /**
     * 构造函数注入依赖
     */
    public RecommendUserController(FollowDAO followDAO, RecommendDAO recommendDAO, UserDAO userDAO) {
        this.followDAO = followDAO;
        this.recommendDAO = recommendDAO;
        this.userDAO = userDAO;
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

        /* ---------- 1. 三张榜单 ---------- */
        List<User> byFriend = recommendDAO.byFriend(user.getAccount());
        List<User> byShare = recommendDAO.byShare(user.getAccount());
        List<User> byHobby = recommendDAO.byHobby(user.getAccount());

        /* ---------- 2. 权重表 ---------- */
        Map<String, Integer> weight = switch (source) {
            case "byFriend" -> Map.of("friend", 5, "share", 3, "hobby", 1);
            case "byShare"  -> Map.of("friend", 3, "share", 5, "hobby", 1);
            case "byHobby"  -> Map.of("friend", 3, "share", 1, "hobby", 5);
            default         -> Map.of("friend", 3, "share", 2, "hobby", 1);
        };

        /* ---------- 3. 计算总分 ---------- */
        Map<User, Integer> scoreMap = new HashMap<>();
        byFriend.forEach(u -> scoreMap.merge(u, weight.get("friend"), Integer::sum));
        byShare .forEach(u -> scoreMap.merge(u, weight.get("share"),  Integer::sum));
        byHobby .forEach(u -> scoreMap.merge(u, weight.get("hobby"),  Integer::sum));

        /* ---------- 4. 过滤：排除自己 & 已关注 ---------- */
        List<User> myFollowing = followDAO.getMyFollowing(user.getAccount());

        // 方便 contains 判断
        Set<String> myFollowingAccount = myFollowing.stream()
                .map(User::getAccount)
                .collect(Collectors.toSet());

        /* ---------- 5. 按分排序，取前 10 ---------- */
        List<User> recommends = scoreMap.entrySet().stream()
                .filter(e -> {                       // 排除自己 / 已关注
                    String acc = e.getKey().getAccount();
                    return !acc.equals(user.getAccount())
                            && !myFollowingAccount.contains(acc);
                })
                .sorted((a, b) -> b.getValue() - a.getValue()) // 倒序
                .limit(9)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        /* ---------- 6. 兜底：若为空则随机推荐 ---------- */
        if (recommends.isEmpty()) {
            List<User> allUsers = userDAO.getAllUser();           // ← 调用图数据库接口
            Collections.shuffle(allUsers);                        // 打乱顺序
            recommends = allUsers.stream()
                    .filter(u -> !u.getAccount().equals(user.getAccount()) &&
                            !myFollowingAccount.contains(u.getAccount()))
                    .limit(10)
                    .collect(Collectors.toList());
        }

        int followingCount = myFollowing.size();
        int followerCount = followDAO.getPeopleWhoFollowMe(user.getAccount()).size();

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
     * 基于点赞互动推荐
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
