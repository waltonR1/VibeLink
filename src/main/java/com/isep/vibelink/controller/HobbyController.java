package com.isep.vibelink.controller;

import com.isep.vibelink.dao.FollowDao;
import com.isep.vibelink.dao.HobbyDao;
import com.isep.vibelink.dao.LikeDao;
import com.isep.vibelink.domain.node.Hobby;
import com.isep.vibelink.domain.node.User;
import com.isep.vibelink.util.ResponseInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Map;

@Controller
public class HobbyController {
    public static boolean contains(ArrayList<Hobby> hobbies, Hobby target){
        for (Hobby hobby : hobbies) {
            if (hobby.getId().equals(target.getId()))
                return true;
        }
        return false;
    }
    @Autowired
    HobbyDao hobbyDao;
    @Autowired
    LikeDao likeDao;
    @Autowired
    FollowDao followDao;

    @GetMapping("/hobby/getall")
    public String getAll(HttpServletRequest request, Map<String,Object> map){
        HttpSession session=request.getSession();
        User user=(User) session.getAttribute("user");
        if(user==null) return "login";
        ArrayList<Hobby> hobbies=(ArrayList<Hobby>) hobbyDao.getAllHobbies();
        ArrayList<Hobby> myhobbies=(ArrayList<Hobby>) hobbyDao.getMyHobby(user.getAccount());
        ArrayList<Hobby> res=new ArrayList<>();
        for (Hobby hobby : hobbies) {
            if (contains(myhobbies, hobby)) continue;
            res.add(hobby);
        }
        Integer following_num=followDao.getMyFollowing(user.getAccount()).size();
        Integer follower_num=followDao.getPeopleWhoFollowMe(user.getAccount()).size();
        map.put("myFollowing",following_num);
        map.put("follower",follower_num);
        map.put("hobbies",res);
        map.put("myhobbies",myhobbies);
        map.put("user",user);
        return "hobbies";
    }

    @PostMapping("/hobby/addHobby")
    @ResponseBody
    public ResponseInfo addHobby(@RequestParam("hName") String hName,
                                 @RequestParam("hType") String hType){
        Hobby hobby=hobbyDao.addHobby(hName, hType);
        if(hobby==null){
            return new ResponseInfo("fail",false,null);
        }
        return new ResponseInfo("success",true,hobby);
    }

    @PostMapping("/hobby/delete")
    @ResponseBody
    public ResponseInfo deleteHobby(@RequestParam("id") Long id){
        Hobby hobby=hobbyDao.deleteWithId(id);
        return new ResponseInfo("success",true,hobby);
    }

    @GetMapping("/hobby/search")
    @ResponseBody
    public ResponseInfo search(@RequestParam("hName") String hName){
        ArrayList<Hobby> hobbies=(ArrayList<Hobby>) hobbyDao.searchHobbyByName(".*"+hName+".*");
        return new ResponseInfo("",true,hobbies);
    }

    @PostMapping("/hobby/fix")
    @ResponseBody
    public ResponseInfo fix(@RequestParam("id") Long id, @RequestParam("hName") String hName,
                            @RequestParam("hType") String hType){
        Hobby hobby=hobbyDao.fixHobby(id,hName,hType);
        if(hobby!=null){
            return new ResponseInfo("修改成功",true,hobby);
        }
        return new ResponseInfo("修改失败",false,null);
    }
}
