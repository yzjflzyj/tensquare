package com.tensquare.friend.controller;

import com.tensquare.friend.service.FriendService;
import entity.Result;
import entity.StatusCode;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private FriendService friendService;

    /**
     * /like/{friendid}/{type}
     * 添加好友或非好友
     */
    @PutMapping("/like/{friendid}/{type}")
    public Result like(@PathVariable String friendid, @PathVariable int type){
        // 获取登陆用户的Id
        String loginUserId = getLoginUserId();
        if(null == loginUserId){
            return new Result(false, StatusCode.ACCESSERROR,"没有权限");
        }

        // 添加好友
        if(1 == type){
           friendService.like(loginUserId,friendid);
            return Result.success("添加好友成功");
        }else if(2 == type){
            // 添加非好友
            friendService.unlike(loginUserId,friendid);
            return Result.success("添加非好友成功");
        }
        return new Result(false, StatusCode.ACCESSERROR,"没有权限");
    }

    /**
     * /{friendid}
     * 删除好友
     */
    @DeleteMapping("/{friendid}")
    public Result deleteFriend(@PathVariable String friendid){
        // 获取登陆用户的Id
        String loginUserId = getLoginUserId();
        if(null == loginUserId){
            return new Result(false, StatusCode.ACCESSERROR,"没有权限");
        }
        friendService.deleteFriend(loginUserId,friendid);
        return Result.success("删除好友成功");
    }

    @Autowired
    private HttpServletRequest req;

    private String getLoginUserId(){
        Claims claims = (Claims) req.getAttribute("user_claims");
        if(null != claims){
            return claims.getId();
        }
        return null;
    }
}
