package com.tensquare.spit.service;

import com.tensquare.spit.dao.SpitDao;
import com.tensquare.spit.pojo.Spit;
import exception.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import util.IdWorker;

import java.util.Date;

@Service
public class SpitService {

    @Autowired
    private SpitDao spitDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void add(Spit spit) {
        spit.set_id(idWorker.nextId() + "");
        spit.setPublishtime(new Date());
        spit.setComment(0);
        spit.setShare(0);
        spit.setVisits(0);
        spit.setThumbup(0);
        // 不可见，没有审核的
        spit.setState("0");
        // 判断是否有上级ID，如果有意为上级的吐槽的评论数要++
        if(!StringUtils.isEmpty(spit.getParentid())){
            //吐槽的评论数要++
            Query query = new Query();
            // Criteria {_id:'4'}
            query.addCriteria(Criteria.where("_id").is(spit.getParentid()));

            Update update = new Update();
            // thumbup {$inc:{comment:NumberInt(-1000)}}
            update.inc("comment",1);
            mongoTemplate.updateFirst(query, update, "spit");
        }
        spitDao.save(spit);
    }

    public Spit findById(String spitId) {
        return spitDao.findById(spitId).get();
    }

    public void update(Spit spit) {
        spitDao.save(spit);
    }

    public void deleteById(String spitId) {
        spitDao.deleteById(spitId);
    }

    /**
     * 查询吐槽评论信息
     * @param parentid
     * @param page
     * @param size
     * @return
     */
    public Page<Spit> commentList(String parentid, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return spitDao.findByParentid(parentid, pageRequest);
    }

    /**
     * 点赞
     * @param spitId
     */
    public void thumbup(String spitId) {
        String key = "userId_spit_" + spitId;
        // 判断是否重复点赞
        if(null != redisTemplate.opsForValue().get(key)){
            throw new MyException("不能重复点赞");
        }
        //db.spit.update({_id:'4'},{$inc:{visits:NumberInt(-1000)}})
        // query 条件
        // update 更新的值
        // collectionName: 集合的名称, 表名 spit
        Query query = new Query();
        // Criteria {_id:'4'}
        query.addCriteria(Criteria.where("_id").is(spitId));

        Update update = new Update();
        // thumbup {$inc:{thumbup:NumberInt(-1000)}}
        update.inc("thumbup",1);
        mongoTemplate.updateFirst(query, update, "spit");

        // 存入redis
        redisTemplate.opsForValue().set(key,"1");
    }
}
