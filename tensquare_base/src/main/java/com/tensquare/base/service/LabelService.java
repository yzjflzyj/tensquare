package com.tensquare.base.service;

import com.tensquare.base.dao.LabelDao;
import com.tensquare.base.pojo.Label;
import entity.Result;
import exception.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import util.IdWorker;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LabelService {

    @Autowired
    private LabelDao labelDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 添加
     * @param label
     */
    public void add(Label label) {
        // 生成id
        label.setId(idWorker.nextId() + "");
        // 保存到数据库
        label.setFans(0l);
        labelDao.save(label);
    }

    /**
     * 通过编号查询
     * @param labelId
     * @return
     */
    public Label findById(String labelId) {
        Optional<Label> optional = labelDao.findById(labelId);
        if(!optional.isPresent()){
            // 不存在数据
            throw new MyException("没有找到相应的数据,id不存在");
        }
        return labelDao.findById(labelId).get();
    }

    /**
     * 更新
     * @param label
     */
    public void update(Label label) {
        // 确保label有id
        labelDao.save(label);
    }

    /**
     * 通过编号删除
     * @param labelId
     */
    public void deleteById(String labelId) {
        labelDao.deleteById(labelId);
    }

    /**
     * 查询所有
     * @return
     */
    public List<Label> findAll() {
        return labelDao.findAll();
    }

    /**
     * 条件查询
     * @param paraMap
     * @return
     */
    public List<Label> search(Map<String,Object> paraMap) {
        return labelDao.findAll(createSpecification(paraMap));
    }

    /**
     * 分页条件查询
     * @param paraMap
     * @param page
     * @param size
     * @return
     */
    public Page<Label> findPage(Map<String,Object> paraMap, int page, int size) {
        Specification<Label> spec = createSpecification(paraMap);
        // springdataJPa 分页页码 从0开始
        // 分页对象
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return labelDao.findAll(spec, pageRequest);
    }

    private Specification<Label> createSpecification(Map<String,Object> paraMap){
        return new Specification<Label>() {
            @Override
            public Predicate toPredicate(Root<Label> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                //root => 要查询的对象，from表, 哪些列
                //query, 定义select 内容 from 表 where 条件
                // criteriaBuilder, 构建where后的条件
                String labelname = (String)paraMap.get("labelname");
                // 所有的条件
                List<Predicate> conditions = new ArrayList<Predicate>();
                if(!StringUtils.isEmpty(labelname)){
                    // 需要这个条件,
                    // 条件是哪个字段的，root表,
                    Predicate predicate = cb.like(root.get("labelname").as(String.class), "%" + labelname + "%");
                    conditions.add(predicate);
                }
                String state = (String)paraMap.get("state");
                if(!StringUtils.isEmpty(state)){
                    // 需要这个条件,
                    // 条件是哪个字段的，root表,
                    Predicate predicate = cb.equal(root.get("state").as(String.class), state);
                    conditions.add(predicate);
                }
                // 多条件查询, 并且的关系
                Predicate and = cb.and(conditions.toArray(new Predicate[]{}));

                return and;
            }
        };
    }
}
