package com.tensquare.search.controller;

import com.tensquare.search.pojo.Article;
import com.tensquare.search.service.ArticleSearchService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.core.support.ReactiveRepositoryFactorySupport;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("/article")
public class ArticleSearchController {

    @Autowired
    private ArticleSearchService articleSearchService;

    /**
     * 添加文章到索引库中
     * @param article
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Article article){
        articleSearchService.add(article);
        return Result.success("添加成功");
    }

    /**
     * /search/{keywords}/{page}/{size}
     * 文章分页查询的检索
     */
    @GetMapping("/search/{keywords}/{page}/{size}")
    public Result search(@PathVariable String keywords, @PathVariable int page, @PathVariable int size){
        Page<Article> articlePage = articleSearchService.search(keywords, page, size);
        return Result.success("查询成功", new PageResult<Article>(articlePage.getTotalElements(), articlePage.getContent()));
    }
}
