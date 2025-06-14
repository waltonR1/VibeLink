package com.isep.vibelink.controller;

import com.isep.vibelink.dao.PraiseDAO;
import com.isep.vibelink.util.ResponseInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 点赞相关接口控制器，用于用户对动态点赞和取消点赞
 */
@Controller
public class PraiseController {
    private final PraiseDAO praiseDAO;

    /**
     * 构造函数注入依赖
     */
    public PraiseController(PraiseDAO praiseDAO) {
        this.praiseDAO = praiseDAO;
    }

    /**
     * 用户为动态点赞（如果已点赞则只返回点赞数）
     *
     * @param account 用户账号
     * @param shareId 动态 ID
     * @return 点赞后的点赞总数
     */
    @PostMapping("/praised/thumbsUp")
    @ResponseBody
    public ResponseInfo<Long> praiseShare(@RequestParam("account") String account, @RequestParam("shareId") Integer shareId) {
        Integer hasPraised = praiseDAO.isPraised(shareId, account);
        Long totalPraised = praiseDAO.getPraisedNumber(shareId);

        if (hasPraised > 0) {
            return ResponseInfo.success("Already praised", totalPraised);
        }

        Long relationshipId = praiseDAO.praisedIt(account, shareId);
        boolean success = (relationshipId != null);
        return success ? ResponseInfo.success("Praise successful", totalPraised+1) : ResponseInfo.fail("Praise failed");
    }


    /**
     * 用户取消对动态的点赞
     *
     * @param account 用户账号
     * @param shareId 动态 ID
     * @return 点赞后的最新总数
     */
    @PostMapping("/praised/unThumbsUp")
    @ResponseBody
    public ResponseInfo<Long> unPraiseShare(@RequestParam("account") String account, @RequestParam("shareId") Integer shareId) {

        Integer affected = praiseDAO.cancelPraised(account, shareId);
        Long totalPraised = praiseDAO.getPraisedNumber(shareId);
        return affected > 0 ? ResponseInfo.success("Cancel praise successful", totalPraised) : ResponseInfo.fail("Cancel praise failed");
    }
}
