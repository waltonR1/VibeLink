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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    UserDao userDao;
    @Autowired
    FollowDao followDao;
    @Autowired
    ShareDao shareDao;
    @Autowired
    RecommendDao recommendDao;
    public static HashSet<User> subList(ArrayList<User> list, int length, String account){
        HashSet<User> res=new HashSet<>();
        int range=Math.min(list.size(),length);
        for(int i=0;i<range;i++){
            if(list.get(i).getAccount().equals(account)) continue;
            res.add(list.get(i));
        }
        return res;
    }
    @GetMapping("/")
    public String index(Map<String,Object> paramMap, HttpServletRequest request){
        HttpSession session=request.getSession();
        User user=(User) session.getAttribute("user");
        if(user==null){
            return "login";
        }else{
            return "redirect:/content";
        }
    }
    @PostMapping("/login")
    public String index(@RequestParam("account") String account,
                        @RequestParam("password") String password,
                        Map<String,Object> paramMap,
                        HttpServletRequest request){
        User user=userDao.checkUser(account,password);
        if(user==null){
            paramMap.put("msg","用户名或者密码错误");
            return "login";
        }else{
            HttpSession session=request.getSession();
            session.setAttribute("user",user);
            return "redirect:content";
        }
    }
    @GetMapping("/content")
    public String content(HttpServletRequest request,Map<String,Object> paramMap){
        HttpSession session=request.getSession();
        User user=(User) session.getAttribute("user");
        if(user==null)
            return "login";
        Long following_num=followDao.howManyIFollow(user.getAccount());
        Long follower_num=followDao.howManyPeopleFollowMe(user.getAccount());
        // 读取动态内容
        ArrayList<Share> shares=shareDao.getFriendShares(user.getAccount());
        // 读取推荐用户列表
        ArrayList<User> byFriend=(ArrayList<User>) recommendDao.byFriend(user.getAccount());
        ArrayList<User> byShare=(ArrayList<User>) recommendDao.byShare(user.getAccount());
        ArrayList<User> byHobby=(ArrayList<User>) recommendDao.byHobby(user.getAccount());

        paramMap.put("byFriend",subList(byFriend,3,user.getAccount()));
        paramMap.put("byShare",subList(byShare,3,user.getAccount()));
        paramMap.put("byHobby",subList(byHobby,3,user.getAccount()));

        paramMap.put("shares",shares);
        paramMap.put("user",user);
        paramMap.put("myFollowing",following_num);
        paramMap.put("follower",follower_num);
        return "content";
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        HttpSession session=request.getSession();
        session.setAttribute("user",null);
        return "login";
    }
    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @GetMapping("/user/getUser")
    @ResponseBody
    public ResponseInfo getUser(@RequestParam("account") String account){
        User user=userDao.getUserByAccount(account);
        if(user!=null){
            return new ResponseInfo("1",true,user);
        }
        return new ResponseInfo("0",true,null);
    }

    @GetMapping("/user/all")
    @ResponseBody
    public ResponseInfo getAllUser(){
        ArrayList<User> users=(ArrayList<User>) userDao.getAllUser();
        return new ResponseInfo(Integer.toString(users.size()),true,users);
    }

    @PostMapping("/user/addUser")
    public String addUser(@RequestParam("account") String account,
                          @RequestParam("password") String password,
                          @RequestParam("age") Integer age,
                          @RequestParam("gender") String gender,
                          @RequestParam("email") String email,
                          @RequestParam("address") String address,
                          @RequestParam("nickname") String nickname,
                          Map<String,Object> map){
        User user=userDao.getUserByAccount(account);
        if(user!=null){
            map.put("msg","该账号已经存在，请登录");
            return "login";
        }
        user=userDao.addUser(account,password,nickname,age,gender,email,address);
        if(user!=null) {
            return "redirect:/content";
        }
        map.put("msg","注册失败，请联系管理员");
        return "register";
    }

    @PostMapping("/user/deleteUser")
    @ResponseBody
    public ResponseInfo deleteUser(@RequestParam("account") String account){
        User user=userDao.deleteUserByAccount(account);
        return new ResponseInfo("删除成功",true,user);
    }

    @PostMapping("/user/fixInfo")
    @ResponseBody
    public ResponseInfo fixInfo(@RequestParam("account") String account,@RequestParam("nickname") String nickname,
                                @RequestParam("age") Integer age,@RequestParam("email") String email,
                                @RequestParam("address") String address){
        Long num=userDao.fixInfo(account,nickname,age,address,email);
        return new ResponseInfo(num>0?"success":"fail",num>0,num);
    }

    @PostMapping("/user/fixPass")
    @ResponseBody
    public ResponseInfo fixPass(@RequestParam("account") String account,@RequestParam("newPass") String password){
        Long num=userDao.fixPass(account,password);
        return new ResponseInfo(num>0?"success":"fail",num>0,num);
    }

    @PostMapping("/user/fixImg")
    @ResponseBody
    public ResponseInfo fixImg(@RequestParam("account") String account,@RequestParam("imgUrl") String imgUrl){
        Long num=userDao.fixImg(account,imgUrl);
        return new ResponseInfo(num>0?"success":"fail",num>0,num);
    }
}
