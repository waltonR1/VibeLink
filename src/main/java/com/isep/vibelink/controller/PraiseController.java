package com.isep.vibelink.controller;

import com.isep.vibelink.dao.PraiseDao;
import com.isep.vibelink.util.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PraiseController {
    @Autowired
    PraiseDao praiseDao;

    @PostMapping("/praised/ThumbsUp")
    @ResponseBody
    public ResponseInfo praiseShare(
            @RequestParam("account") String account, @RequestParam("shareId") Integer shareId) {
        Integer number=praiseDao.isPraised(shareId,account);
        if(number>0) {
            Long praise_number=praiseDao.getPraisedNumber(shareId);
            return new ResponseInfo("check", true, praise_number);
        }
        Long relationshipId=praiseDao.praisedIt(account,shareId);
        Long praise_number=praiseDao.getPraisedNumber(shareId);
        return new ResponseInfo(relationshipId!=null?"success":"fail",relationshipId!=null,praise_number);
    }

    @PostMapping("/praised/unThumbsUp")
    @ResponseBody
    public ResponseInfo unPraiseShare(
            @RequestParam("account") String account,@RequestParam("shareId") Integer shareId) {
        Integer affectRows=praiseDao.cancelPraised(account,shareId);
        Long praise_number=praiseDao.getPraisedNumber(shareId);
        return new ResponseInfo(affectRows>0?"success":"fail",affectRows>0,praise_number);
    }
}
