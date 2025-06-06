package com.isep.vibelink.controller;

import com.isep.vibelink.dao.FollowDAO;
import com.isep.vibelink.domain.node.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * 页面路由控制器，用于渲染用户相关的界面
 */
@Controller
public class RouterController {

    private final FollowDAO followDAO;

    /**
     * 构造函数注入依赖
     */
    public RouterController(FollowDAO followDAO) {
        this.followDAO = followDAO;
    }


    /**
     * 显示当前用户的个人信息页面
     *
     * @param request HTTP 请求对象，用于获取会话中的用户
     * @param map     页面渲染参数
     * @return personInfo 视图页面
     */
    @GetMapping("/user/personInfo")
    public String personInfo(HttpServletRequest request, Map<String,Object> map){
        HttpSession session=request.getSession();
        User user=(User) session.getAttribute("user");
        if(user==null){
            return "login";
        }

        int followingCount = followDAO.getMyFollowing(user.getAccount()).size();
        int followerCount = followDAO.getPeopleWhoFollowMe(user.getAccount()).size();

        map.put("user", user);
        map.put("myFollowing", followingCount);
        map.put("follower", followerCount);

        return "personInfo";
    }
}
