package com.tensquare.user.service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import exception.MyException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import com.tensquare.user.dao.UserDao;
import com.tensquare.user.pojo.User;

/**
 * 服务层
 * 
 * @author Administrator
 *
 */
@Service
public class UserService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private IdWorker idWorker;

	@Autowired
	private RedisTemplate<String,String> redisTemplate;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
    private BCryptPasswordEncoder encoder;

	/**
	 * 查询全部列表
	 * @return
	 */
	public List<User> findAll() {
		return userDao.findAll();
	}

	
	/**
	 * 条件查询+分页
	 * @param whereMap
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<User> findSearch(Map whereMap, int page, int size) {
		Specification<User> specification = createSpecification(whereMap);
		PageRequest pageRequest =  PageRequest.of(page-1, size);
		return userDao.findAll(specification, pageRequest);
	}

	
	/**
	 * 条件查询
	 * @param whereMap
	 * @return
	 */
	public List<User> findSearch(Map whereMap) {
		Specification<User> specification = createSpecification(whereMap);
		return userDao.findAll(specification);
	}

	/**
	 * 根据ID查询实体
	 * @param id
	 * @return
	 */
	public User findById(String id) {
		return userDao.findById(id).get();
	}

	/**
	 * 增加
	 * @param user
	 */
	public void add(User user) {
		user.setId( idWorker.nextId()+"" );
		user.setPassword(encoder.encode(user.getPassword()));
		userDao.save(user);
	}

	/**
	 * 修改
	 * @param user
	 */
	public void update(User user) {
		userDao.save(user);
	}

	/**
	 * 删除
	 * @param id
	 */
	public void deleteById(String id) {
		userDao.deleteById(id);
	}

	/**
	 * 动态条件构建
	 * @param searchMap
	 * @return
	 */
	private Specification<User> createSpecification(Map searchMap) {

		return new Specification<User>() {

			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
                // ID
                if (searchMap.get("id")!=null && !"".equals(searchMap.get("id"))) {
                	predicateList.add(cb.like(root.get("id").as(String.class), "%"+(String)searchMap.get("id")+"%"));
                }
                // 手机号码
                if (searchMap.get("mobile")!=null && !"".equals(searchMap.get("mobile"))) {
                	predicateList.add(cb.like(root.get("mobile").as(String.class), "%"+(String)searchMap.get("mobile")+"%"));
                }
                // 密码
                if (searchMap.get("password")!=null && !"".equals(searchMap.get("password"))) {
                	predicateList.add(cb.like(root.get("password").as(String.class), "%"+(String)searchMap.get("password")+"%"));
                }
                // 昵称
                if (searchMap.get("nickname")!=null && !"".equals(searchMap.get("nickname"))) {
                	predicateList.add(cb.like(root.get("nickname").as(String.class), "%"+(String)searchMap.get("nickname")+"%"));
                }
                // 性别
                if (searchMap.get("sex")!=null && !"".equals(searchMap.get("sex"))) {
                	predicateList.add(cb.like(root.get("sex").as(String.class), "%"+(String)searchMap.get("sex")+"%"));
                }
                // 头像
                if (searchMap.get("avatar")!=null && !"".equals(searchMap.get("avatar"))) {
                	predicateList.add(cb.like(root.get("avatar").as(String.class), "%"+(String)searchMap.get("avatar")+"%"));
                }
                // E-Mail
                if (searchMap.get("email")!=null && !"".equals(searchMap.get("email"))) {
                	predicateList.add(cb.like(root.get("email").as(String.class), "%"+(String)searchMap.get("email")+"%"));
                }
                // 兴趣
                if (searchMap.get("interest")!=null && !"".equals(searchMap.get("interest"))) {
                	predicateList.add(cb.like(root.get("interest").as(String.class), "%"+(String)searchMap.get("interest")+"%"));
                }
                // 个性
                if (searchMap.get("personality")!=null && !"".equals(searchMap.get("personality"))) {
                	predicateList.add(cb.like(root.get("personality").as(String.class), "%"+(String)searchMap.get("personality")+"%"));
                }
				
				return cb.and( predicateList.toArray(new Predicate[predicateList.size()]));

			}
		};
	}

    /**
     * 发送验证码
     * @param mobile
     */
    public void sendSms(String mobile) {
	    // 防止重发
        String key = "sms_validateCode_" + mobile;
        String codeInRedis = redisTemplate.opsForValue().get(key);
        if(null != codeInRedis){
            throw new MyException("验证码已经发送过了，请注意查收");
        }
        // 生成验证码
        String validateCode = RandomStringUtils.randomNumeric(6);
        System.out.println("validateCode: " + validateCode);
        // 发给消息队列
        Map<String,String> messageMap = new HashMap<String,String>();
        messageMap.put("mobile", mobile);
        messageMap.put("validateCode", validateCode);
        rabbitTemplate.convertAndSend("","sms",messageMap);
        // 存入redis，5mins
        redisTemplate.opsForValue().set(key,validateCode,5,TimeUnit.MINUTES);

    }

    /**
     * 用户注册
     * @param code
     * @param user
     */
    public void register(String code, User user) {
        // 验证验证码
        String key = "sms_validateCode_" + user.getMobile();
        String codeInRedis = redisTemplate.opsForValue().get(key);
        if(null == codeInRedis){
            throw new MyException("请点击发送验证码");
        }
        if(!codeInRedis.equals(code)){
            throw new MyException("验证码错误");
        }
        user.setId(idWorker.nextId() + "");
        user.setFollowcount(0);
        user.setFanscount(0);
        Date date = new Date();
        user.setRegdate(date);
        user.setUpdatedate(date);
        user.setPassword(encoder.encode(user.getPassword()));
        userDao.save(user);
    }

    /**
     * 用户登陆校验
     * @param paramMap
     * @return
     */
    public User loginCheck(Map<String,String> paramMap) {
        // 通过手机号码查询数据库是否存在
        User user = userDao.findByMobile(paramMap.get("mobile"));
        if(null != user && !encoder.matches(paramMap.get("password"), user.getPassword())){
            // 比对密码
            return null;
        }
        return user;
    }

	/**
	 * 更新粉丝数
	 * @param userid
	 * @param x
	 */
	@Transactional
	public void updateFans(String userid, int x) {
		userDao.updateFans(userid, x);
    }

	/**
	 * 更新关注数
	 * @param userid
	 * @param x
	 */
	@Transactional
	public void updateFollow(String userid, int x) {
		userDao.updateFollow(userid, x);
	}
}
