package com.tensquare.qa.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.tensquare.qa.pojo.Problem;
import org.springframework.data.jpa.repository.Query;

/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface ProblemDao extends JpaRepository<Problem,String>,JpaSpecificationExecutor<Problem>{

    //原生sql @Query(value = "select * From tb_problem where id in (select problemid from tb_pl where labelid=?1)  order by replytime desc", nativeQuery = true)
    //jqpl方式, 表名-> 类名 列名->属性名
    // ?1 第一个参数的值
    @Query(value = "select p From Problem p where id in (select problemid from Pl where labelid=?1) order by replytime desc")
    Page<Problem> newlist(String labelId, Pageable pageable);

    @Query(value = "select p From Problem p where id in (select problemid from Pl where labelid=?1) order by reply desc")
    Page<Problem> hotlist(String labelId, Pageable pageable);

    @Query(value = "select p From Problem p where id in (select problemid from Pl where labelid=?1) and reply=0 order by createtime desc")
    Page<Problem> waitlist(String labelId, Pageable pageable);
}
