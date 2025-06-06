package com.isep.vibelink.controller;

import com.isep.vibelink.dao.FollowDAO;
import com.isep.vibelink.dao.ShareDAO;
import com.isep.vibelink.dao.HobbyDAO;
import com.isep.vibelink.domain.node.Hobby;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 动态分享控制器，处理用户的动态发布、删除与展示
 */
@Controller
public class ShareController {

    private final ShareDAO shareDAO;
    private final FollowDAO followDAO;
    private final HobbyDAO hobbyDAO;

    /**
     * 构造函数注入依赖
     */
    public ShareController(ShareDAO shareDAO, FollowDAO followDAO, HobbyDAO hobbyDAO) {
        this.shareDAO = shareDAO;
        this.followDAO = followDAO;
        this.hobbyDAO = hobbyDAO;
    }

    /**
     * 用户发布动态
     *
     * @param account      发布者账号
     * @param publisher    发布者昵称
     * @param publisherImg 发布者头像
     * @param title        动态标题
     * @param content      动态正文内容
     * @param relatedHobby 相关兴趣名称
     * @param hobbyId      兴趣 ID
     * @param imgUrl       动态配图地址
     * @param address      所在地点
     * @return 响应信息：包含发布成功与否及发布的 Share 对象
     */
    @PostMapping("/share/add")
    @ResponseBody
    public ResponseInfo<Share> addShare(
            @RequestParam("account") String account,
            @RequestParam("publisher") String publisher,
            @RequestParam("publisherImg") String publisherImg,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("relatedHobby") String relatedHobby,
            @RequestParam("hobbyId") Long hobbyId,
            @RequestParam("imgUrl") String imgUrl,
            @RequestParam("address") String address) {

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Share share = shareDAO.publishShare(account, publisher, publisherImg, title, content, relatedHobby, hobbyId, imgUrl, address, time);

        return share != null
                ? ResponseInfo.success("Share published successfully", share)
                : ResponseInfo.fail("Failed to publish share");
    }


    /**
     * 删除指定动态
     *
     * @param account 用户账号（确保权限）
     * @param shareId 要删除的动态 ID
     * @return 响应信息：是否成功删除及影响的行数
     */
    @PostMapping("/share/delete")
    @ResponseBody
    public ResponseInfo<Integer> deleteShare(@RequestParam("account") String account, @RequestParam("shareId") Long shareId) {

        int affectedRows = shareDAO.deleteShareById(account, shareId);
        return affectedRows > 0
                ? ResponseInfo.success("Share deleted successfully", affectedRows)
                : ResponseInfo.fail("Failed to delete share");
    }


    /**
     * 根据用户账号获取该用户发布的所有动态
     * ⚠️ 当前仅返回 Share 列表，后续应扩展点赞数、评论数
     *
     * @param account 用户账号
     * @return 响应信息：包含该用户的所有动态列表
     */
    @GetMapping("/share/getByAccount")
    @ResponseBody
    public ResponseInfo<List<Share>> getShareByAccount(@RequestParam("account") String account) {
        List<Share> shares = shareDAO.getShareByAccount(account);
        for (Share share : shares) {
            share.updatePraisedStatus(account);
        }
        return ResponseInfo.success("Shares retrieved successfully", shares);
    }


    /**
     * 打开发布动态页面（页面跳转）
     *
     * @param request 请求对象，用于获取当前登录用户
     * @param map     视图渲染参数
     * @return 视图名称：addShare.html 或 login.html
     */
    @GetMapping("/share/publish")
    public String publishShare(HttpServletRequest request, Map<String, Object> map) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null){
            return "login";
        }

        List<Hobby> allHobbies = hobbyDAO.getMyHobby(user.getAccount());

        map.put("user", user);
        map.put("hobbies",allHobbies);
        map.put("index", "Discover");
        map.put("title", "Post Share");
        return "addShare";
    }


    /**
     * 获取好友发布的动态列表（页面）
     *
     * @param request 请求对象
     * @param map     页面参数
     * @return 视图名称：shareList.html 或 login.html
     */
    @GetMapping("/share/friend")
    public String getShareByFriend(HttpServletRequest request, Map<String, Object> map) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "login";
        }

        Long following_num = followDAO.howManyIFollow(user.getAccount());
        Long follower_num = followDAO.howManyPeopleFollowMe(user.getAccount());
        session.setAttribute("follower", follower_num);
        session.setAttribute("myFollowing", following_num);

        // 读取动态内容
        List<Share> shares = shareDAO.getFriendShares(user.getAccount());
        for (Share share : shares) {
            share.updatePraisedStatus(user.getAccount());
        }
        map.put("shares", shares);
        map.put("user", user);
        map.put("myFollowing", following_num);
        map.put("follower", follower_num);
        map.put("index", "Discover Shares");
        map.put("title", "Friends' Feed");
        return "shareList";
    }

    /**
     * 获取推荐的动态（根据兴趣标签）
     *
     * @param request 请求对象
     * @param map     页面参数
     * @return 视图名称：shareList.html 或 login.html
     */
    @GetMapping("/share/recommend")
    public String getShareByHobby(HttpServletRequest request, Map<String, Object> map) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "login";
        Long following_num = followDAO.howManyIFollow(user.getAccount());
        Long follower_num = followDAO.howManyPeopleFollowMe(user.getAccount());
        session.setAttribute("follower", follower_num);
        session.setAttribute("myFollowing", following_num);

        // 读取动态内容
        List<Share> shares = shareDAO.recommendByHobby(user.getAccount());
        for (Share share : shares) {
            share.updatePraisedStatus(user.getAccount());
        }
        map.put("shares", shares);
        map.put("user", user);
        map.put("myFollowing", following_num);
        map.put("follower", follower_num);
        map.put("index", "Discover Shares");
        map.put("title", "Friends' Feed");
        return "shareList";
    }
}
