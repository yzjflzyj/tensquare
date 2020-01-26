package com.tensquare.base.controller;

import com.tensquare.base.pojo.Label;
import com.tensquare.base.service.LabelService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/label")
// 允许跨域访问
@CrossOrigin
@RefreshScope
public class LabelController {

    @Autowired
    private LabelService labelService;

    /**
     * 添加
     */
    @PostMapping
    public Result add(@Validated @RequestBody Label label){
        // 调用业务保存
        labelService.add(label);
        return Result.success("添加成功");
    }

    /**
     * 通过编号查询
     * 数据回显
     * /{labelId}
     * 根据ID查询
     */
    @GetMapping("/{labelId}")
    public Result findById(@PathVariable String labelId){
        // 调用业务查询
        Label label = labelService.findById(labelId);
        return Result.success("查询成功",label);
    }

    /**
     * 修改
     */
    @PutMapping("/{labelId}")
    public Result update(@PathVariable String labelId, @RequestBody Label label){
        // 设置id
        label.setId(labelId);
        // 调用业务更新
        labelService.update(label);
        return Result.success("更新成功");
    }

    /**
     * 通过编号删除
     * /{labelId}
     */
    @DeleteMapping("/{labelId}")
    public Result deleteById(@PathVariable String labelId){
        labelService.deleteById(labelId);
        return Result.success("删除成功");
    }


    /**
     * 查询所有
     */
    @GetMapping
    public Result findAll(){
        return Result.success("查询成功", labelService.findAll());
    }

    /**
     * /search
     * 标签条件查询
     */
    @PostMapping("/search")
    public Result search(@RequestBody Map<String,Object> paraMap){
        List<Label> list = labelService.search(paraMap);
        return Result.success("查询成功",list);
    }

    /**
     * /search/{page}/{size}
     * 标签分页
     */
    @PostMapping("/search/{page}/{size}")
    public Result findPage(@RequestBody Map<String,Object> paraMap, @PathVariable int page, @PathVariable int size){
        Page<Label> labelPage = labelService.findPage(paraMap, page, size);
        //getTotalElements 总计录数
        // getContent 分页的结果集
        PageResult<Label> pageResult = new PageResult<Label>(labelPage.getTotalElements(), labelPage.getContent());
        return Result.success("查询成功",pageResult);
    }

    @Value("${ip}")
    private String ip;

    @GetMapping("/ip")
    public String getSmsIp(){
        return ip;
    }
}
