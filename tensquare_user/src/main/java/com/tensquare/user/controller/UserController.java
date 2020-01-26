package com.tensquare.user.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tensquare.user.pojo.Admin;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.tensquare.user.pojo.User;
import com.tensquare.user.service.UserService;

import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 控制器层
 *
 * @author Administrator
 */
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", userService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", userService.findById(id));
    }


    /**
     * 分页+多条件查询
     *
     * @param searchMap 查询条件封装
     * @param page      页码
     * @param size      页大小
     * @return 分页结果
     */
    @RequestMapping(value = "/search/{page}/{size}", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap, @PathVariable int page, @PathVariable int size) {
        Page<User> pageList = userService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<User>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", userService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param user
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody User user) {
        userService.add(user);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param user
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody User user, @PathVariable String id) {
        user.setId(id);
        userService.update(user);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id, HttpServletRequest req) {
        // 判断这个登陆用户的是否有管理员的角色
        Claims claims = (Claims) req.getAttribute("admin_claims");
        if (null == claims || !"admin".equals(claims.get("roles"))) {
            return new Result(false, StatusCode.ACCESSERROR, "没有权限");
        }
        // 有权限
        userService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * /sendsms/{mobile}
     * 发送手机验证码
     */
    @PostMapping("/sendsms/{mobile}")
    public Result sendsms(@PathVariable String mobile) {
        userService.sendSms(mobile);
        return Result.success("验证码发送成功");
    }

    /**
     * /register/{code}
     * 注册用户
     */
    @PostMapping("/register/{code}")
    public Result register(@PathVariable String code, @RequestBody User user) {
        userService.register(code, user);
        return Result.success("注册成功");
    }

    /**
     * /login
     * 登陆
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> paramMap) {
        User user = userService.loginCheck(paramMap);
        if (null == user) {
            return new Result(false, StatusCode.LOGINERROR, "用户名或密码不存在");
        }
        // 成功签发token
        String token = jwtUtil.createJWT(user.getId(), user.getNickname(), "user");
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("name", user.getNickname());
        resultMap.put("token", token);
        resultMap.put("avatar", user.getAvatar());
        return Result.success("登陆成功", resultMap);
    }

    /**
     * 更新用户的粉丝数
     */
    @PutMapping("/updateFans/{userid}/{x}")
    public void updateFans(@PathVariable String userid, @PathVariable int x) {
        userService.updateFans(userid, x);
    }

    /**
     * 更新用户的关注数
     */
    @PutMapping("/updateFollow/{userid}/{x}")
    public void updateFollow(@PathVariable String userid, @PathVariable int x) {
        userService.updateFollow(userid, x);
    }

}
