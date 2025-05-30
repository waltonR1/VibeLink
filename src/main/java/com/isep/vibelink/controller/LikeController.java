package com.isep.vibelink.controller;

import com.isep.vibelink.dao.LikeDao;
import com.isep.vibelink.util.ResponseInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户对兴趣点赞和取消点赞的控制器
 */
@Controller
public class LikeController {
    private final LikeDao likeDao;

    /**
     * 构造函数注入依赖
     */
    public LikeController(LikeDao likeDao) {
        this.likeDao = likeDao;
    }


    /**
     * 用户点赞一个兴趣
     *
     * @param account  用户账号
     * @param hobbyId  兴趣 ID
     * @return 响应信息：是否添加成功
     */
    @PostMapping("/user/like")
    @ResponseBody
    public ResponseInfo<Integer> likeHobby(@RequestParam("account") String account, @RequestParam("hobbyId") Long hobbyId){
        Integer result=likeDao.likeHobby(account,hobbyId);
        boolean success = (result == 2);
        String msg = success ? "Hobby liked successfully" : "Failed to like hobby";
        return new ResponseInfo<>(msg, success, result);
    }


    /**
     * 用户取消点赞一个兴趣
     *
     * @param account  用户账号
     * @param hobbyId  兴趣 ID
     * @return 响应信息：是否删除成功
     */
    @PostMapping("/user/unlike")
    @ResponseBody
    public ResponseInfo<Integer> unlikeHobby(@RequestParam("account") String account, @RequestParam("hobbyId") Long hobbyId) {
        Integer result = likeDao.unlikeHobby(account, hobbyId);
        boolean success = (result > 0);
        String msg = success ? "Hobby unliked successfully" : "Failed to unlike hobby";
        return new ResponseInfo<>(msg, success, result);
    }
}
