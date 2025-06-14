package com.isep.vibelink.controller;

import com.isep.vibelink.dao.FollowDAO;
import com.isep.vibelink.dao.RecommendDAO;
import com.isep.vibelink.dao.ShareDAO;
import com.isep.vibelink.dao.UserDAO;
import com.isep.vibelink.domain.node.Share;
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
import java.util.stream.Collectors;

/**
 * 用户控制器，用于处理用户相关功能，如登录、注册、主页展示等。
 */
@Controller
public class UserController {
    private final UserDAO userDAO;
    private final FollowDAO followDAO;
    private final ShareDAO shareDAO;
    private final RecommendDAO recommendDAO;

    /**
     * 构造函数注入依赖
     */
    public UserController(UserDAO userDAO,
                          FollowDAO followDAO,
                          ShareDAO shareDAO,
                          RecommendDAO recommendDAO) {
        this.userDAO = userDAO;
        this.followDAO = followDAO;
        this.shareDAO = shareDAO;
        this.recommendDAO = recommendDAO;
    }


    /**
     * 推荐用户列表：排除自己和已关注用户
     *
     * @param candidates 候选用户列表
     * @param length     最大推荐数量
     * @param account    当前用户账号
     * @return 推荐结果集合
     */
    public Set<User> filterRecommendedUsers(List<User> candidates, int length, String account) {
        // 查询当前用户关注的用户列表
        List<User> followed = followDAO.getMyFollowing(account);
        Set<String> followedAccounts = followed.stream()
                .map(User::getAccount)
                .collect(Collectors.toSet());

        Set<User> result = new HashSet<>();
        for (User user : candidates) {
            String targetAccount = user.getAccount();
            if (!account.equals(targetAccount) && !followedAccounts.contains(targetAccount)) {
                result.add(user);
            }
            if (result.size() >= length) break;
        }
        return result;
    }


    /**
     * 首页跳转判断：若已登录跳转主页面，否则跳转登录页面
     *
     * @param request  HTTP 请求对象
     * @return 登录页或主页
     */
    @GetMapping("/")
    public String index(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "login";
        } else {
            return "redirect:/content";
        }
    }


    /**
     * 登录接口
     *
     * @param account  用户账号
     * @param password 用户密码
     * @param paramMap 页面传参容器
     * @param request  HTTP 请求对象
     * @return 登录成功跳转主页，失败返回登录页
     */
    @PostMapping("/login")
    public String index(@RequestParam("account") String account,
                        @RequestParam("password") String password,
                        Map<String, Object> paramMap,
                        HttpServletRequest request) {
        User user = userDAO.checkUser(account, password);
        if (user == null) {
            paramMap.put("msg", "Wrong username or password");
            return "login";
        } else {
            request.getSession().setAttribute("user", user);
            return "redirect:content";
        }
    }


    /**
     * 主页面展示内容：推荐用户和好友动态等信息
     *
     * @param request  HTTP 请求对象
     * @param paramMap 页面传参容器
     * @return 主页面视图
     */
    @GetMapping("/content")
    public String content(HttpServletRequest request, Map<String, Object> paramMap) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "login";
        }

        Long followingCount = followDAO.howManyIFollow(user.getAccount());
        Long followerCount = followDAO.howManyPeopleFollowMe(user.getAccount());
        // 读取动态内容
        List<Share> shares = shareDAO.getFriendShares(user.getAccount());
        for (Share share : shares) {
            share.updatePraisedStatus(user.getAccount());
        }
        // 读取推荐用户列表
        List<User> byFriend = recommendDAO.byFriend(user.getAccount());
        List<User> byShare = recommendDAO.byShare(user.getAccount());
        List<User> byHobby = recommendDAO.byHobby(user.getAccount());

        paramMap.put("byFriend", filterRecommendedUsers(byFriend, 2, user.getAccount()));
        paramMap.put("byShare", filterRecommendedUsers(byShare, 2, user.getAccount()));
        paramMap.put("byHobby", filterRecommendedUsers(byHobby, 2, user.getAccount()));

        paramMap.put("shares", shares);
        paramMap.put("user", user);
        paramMap.put("myFollowing", followingCount);
        paramMap.put("follower", followerCount);
        return "content";
    }

    /**
     * 注销登录
     *
     * @param request HTTP 请求对象
     * @return 登录页面视图
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("user", null);
        return "login";
    }

    /**
     * 跳转到注册页
     *
     * @return 注册页视图
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }


    /**
     * 获取指定用户信息
     *
     * @param account 用户账号
     * @return 用户信息包装的响应
     */
    @GetMapping("/user/getUser")
    @ResponseBody
    public ResponseInfo<User> getUser(@RequestParam("account") String account) {
        User user = userDAO.getUserByAccount(account);
        if (user != null) {
            return ResponseInfo.success("1", user);
        }
        return ResponseInfo.success("0", null);
    }


    /**
     * 获取所有用户信息
     *
     * @return 所有用户信息列表的响应体
     */
    @GetMapping("/user/all")
    @ResponseBody
    public ResponseInfo<List<User>> getAllUser() {
        List<User> users = userDAO.getAllUser();
        return ResponseInfo.success(Integer.toString(users.size()), users);
    }


    /**
     * 注册新用户
     *
     * @param account  账号
     * @param password 密码
     * @param age      年龄
     * @param gender   性别
     * @param email    邮箱
     * @param address  地址
     * @param nickname 昵称
     * @param map      页面参数容器
     * @return 注册成功跳转主页，失败返回相应页面
     */
    @PostMapping("/user/addUser")
    public String addUser(@RequestParam("account") String account,
                          @RequestParam("password") String password,
                          @RequestParam("age") Integer age,
                          @RequestParam("gender") String gender,
                          @RequestParam("email") String email,
                          @RequestParam("address") String address,
                          @RequestParam("nickname") String nickname,
                          Map<String, Object> map) {
        User user = userDAO.getUserByAccount(account);
        if (user != null) {
            map.put("msg", "This account already exists, please log in");
            return "login";
        }

        user = userDAO.addUser(account, password, nickname, age, gender, email, address);
        if (user != null) {
            return "redirect:/content";
        }

        map.put("msg", "Registration failed, please contact the administrator");
        return "register";
    }


    /**
     * 删除指定账号的用户
     *
     * @param account 用户账号
     * @return 删除操作的响应体
     */
    @PostMapping("/user/deleteUser")
    @ResponseBody
    public ResponseInfo<Void> deleteUser(@RequestParam("account") String account) {
        boolean deleted = userDAO.deleteUserByAccount(account);
        if (deleted) {
            return ResponseInfo.success("Deleted successfully", null);
        } else {
            return ResponseInfo.fail("Deletion failed: user does not exist");
        }
    }


    /**
     * 修改用户基本信息
     *
     * @param account  用户账号
     * @param nickname 新昵称
     * @param age      新年龄
     * @param email    新邮箱
     * @param address  新地址
     * @return 更新结果的响应体
     */
    @PostMapping("/user/fixInfo")
    @ResponseBody
    public ResponseInfo<Long> fixInfo(@RequestParam("account") String account,
                                      @RequestParam("nickname") String nickname,
                                      @RequestParam("age") Integer age,
                                      @RequestParam("email") String email,
                                      @RequestParam("address") String address) {
        Long result = userDAO.fixInfo(account, nickname, age, address, email);
        return result > 0 ? ResponseInfo.success("Modified successfully", result) : ResponseInfo.fail("Modification Failure");
    }


    /**
     * 修改用户密码
     *
     * @param account  用户账号
     * @param password 新密码
     * @return 修改密码操作的响应体
     */
    @PostMapping("/user/fixPass")
    @ResponseBody
    public ResponseInfo<Long> fixPass(@RequestParam("account") String account, @RequestParam("newPass") String password) {
        Long result = userDAO.fixPass(account, password);
        return result > 0 ? ResponseInfo.success("Modified successfully", result) : ResponseInfo.fail("Modification Failure");
    }


    /**
     * 修改用户头像地址
     *
     * @param account 用户账号
     * @param imgUrl  新头像 URL
     * @return 修改头像操作的响应体
     */
    @PostMapping("/user/fixImg")
    @ResponseBody
    public ResponseInfo<Long> fixImg(@RequestParam("account") String account, @RequestParam("imgUrl") String imgUrl) {
        Long result = userDAO.fixImg(account, imgUrl);
        return result > 0 ? ResponseInfo.success("Modified successfully", result) : ResponseInfo.fail("Modification Failure");

    }

    /**
     * 主页面展示内容：推荐用户和好友动态等信息
     *
     * @param request  HTTP 请求对象
     * @param paramMap 页面传参容器
     * @return 主页面视图
     */
    @GetMapping("/publisherShare")
    public String publisherShare(@RequestParam("publisher") String publisher,HttpServletRequest request, Map<String, Object> paramMap) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "login";
        }


        System.out.println("Publisher User: " + publisher);

        Long followingCount = followDAO.howManyIFollow(user.getAccount());
        Long followerCount = followDAO.howManyPeopleFollowMe(user.getAccount());
        // 读取动态内容
        List<Share> shares = shareDAO.getShareByAccount(publisher);
        System.out.println("Shares: " + shares);

        for (Share share : shares) {
            share.updatePraisedStatus(user.getAccount());
        }

        // 读取推荐用户列表
        List<User> byFriend = recommendDAO.byFriend(user.getAccount());
        List<User> byShare = recommendDAO.byShare(user.getAccount());
        List<User> byHobby = recommendDAO.byHobby(user.getAccount());

        paramMap.put("byFriend", filterRecommendedUsers(byFriend, 3, user.getAccount()));
        paramMap.put("byShare", filterRecommendedUsers(byShare, 3, user.getAccount()));
        paramMap.put("byHobby", filterRecommendedUsers(byHobby, 3, user.getAccount()));

        paramMap.put("shares", shares);
        paramMap.put("user", user);
        paramMap.put("myFollowing", followingCount);
        paramMap.put("follower", followerCount);
        return "publisherShare";
    }
}
