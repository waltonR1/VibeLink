package com.isep.vibelink.controller;

import com.isep.vibelink.dao.FollowDao;
import com.isep.vibelink.dao.RecommendDao;
import com.isep.vibelink.dao.ShareDao;
import com.isep.vibelink.dao.UserDao;
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

/**
 * 用户控制器，用于处理用户相关功能，如登录、注册、主页展示等。
 */
@Controller
public class UserController {
    private final UserDao userDao;
    private final FollowDao followDao;
    private final ShareDao shareDao;
    private final RecommendDao recommendDao;

    /**
     * 构造函数注入依赖
     */
    public UserController(UserDao userDao,
                          FollowDao followDao,
                          ShareDao shareDao,
                          RecommendDao recommendDao) {
        this.userDao = userDao;
        this.followDao = followDao;
        this.shareDao = shareDao;
        this.recommendDao = recommendDao;
    }


    /**
     * 截取列表前 length 个不为当前用户的用户，避免推荐到自己
     *
     * @param list    原始用户列表
     * @param length  最大推荐数量
     * @param account 当前用户账号
     * @return 去除自身后的推荐用户集合
     */
    public static Set<User> subList(List<User> list, int length, String account) {
        Set<User> res = new HashSet<>();
        for (int i = 0; i < Math.min(list.size(), length); i++) {
            if (!list.get(i).getAccount().equals(account)) {
                res.add(list.get(i));
            }
        }
        return res;
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
        User user = userDao.checkUser(account, password);
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

        Long followingCount = followDao.howManyIFollow(user.getAccount());
        Long followerCount = followDao.howManyPeopleFollowMe(user.getAccount());
        // 读取动态内容
        List<Share> shares = shareDao.getFriendShares(user.getAccount());
        // 读取推荐用户列表
        List<User> byFriend = recommendDao.byFriend(user.getAccount());
        List<User> byShare = recommendDao.byShare(user.getAccount());
        List<User> byHobby = recommendDao.byHobby(user.getAccount());

        paramMap.put("byFriend", subList(byFriend, 3, user.getAccount()));
        paramMap.put("byShare", subList(byShare, 3, user.getAccount()));
        paramMap.put("byHobby", subList(byHobby, 3, user.getAccount()));

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
        User user = userDao.getUserByAccount(account);
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
        List<User> users = userDao.getAllUser();
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
        User user = userDao.getUserByAccount(account);
        if (user != null) {
            map.put("msg", "This account already exists, please log in");
            return "login";
        }

        user = userDao.addUser(account, password, nickname, age, gender, email, address);
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
        boolean deleted = userDao.deleteUserByAccount(account);
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
        Long result = userDao.fixInfo(account, nickname, age, address, email);
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
        Long result = userDao.fixPass(account, password);
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
        Long result = userDao.fixImg(account, imgUrl);
        return result > 0 ? ResponseInfo.success("Modified successfully", result) : ResponseInfo.fail("Modification Failure");

    }
}
