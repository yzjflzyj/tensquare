package com.tensquare.spit.controller;

import com.tensquare.spit.pojo.Spit;
import com.tensquare.spit.service.SpitService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/spit")
public class SpitController {

    @Autowired
    private SpitService spitService;

    /**
     * 添加
     */
    @PostMapping
    public Result add(@RequestBody Spit spit){
        spitService.add(spit);
        return Result.success("添加成功");
    }

    /**
     * 通过编号查询
     */
    @GetMapping("/{spitId}")
    public Result findById(@PathVariable String spitId){
        return Result.success("查询成功",spitService.findById(spitId));
    }

    /**
     * 修改
     */
    @PutMapping("/{spitId}")
    public Result update(@RequestBody Spit spit, @PathVariable String spitId){
        spit.set_id(spitId);
        spitService.update(spit);
        return Result.success("更新成功");
    }

    /**
     * 删除
     */
    @DeleteMapping("/{spitId}")
    public Result deleteById(@PathVariable String spitId){
        spitService.deleteById(spitId);
        return Result.success("删除成功");
    }

    /**
     * /comment/{parentid}/{page}/{size}
     * 根据上级ID查询吐槽数据（分页）
     */
    @GetMapping("/comment/{parentid}/{page}/{size}")
    public Result commentList(@PathVariable String parentid, @PathVariable int page, @PathVariable int size){
        Page<Spit> spitPage = spitService.commentList(parentid, page, size);
        return Result.success("查询成功", new PageResult<Spit>(spitPage.getTotalElements(), spitPage.getContent()));
    }

    /**
     * thumbup/{spitId}
     * 吐槽点赞
     */
    @PutMapping("/thumbup/{spitId}")
    public Result thumbup(@PathVariable String spitId){
        spitService.thumbup(spitId);
        return Result.success("点赞成功");
    }

}
