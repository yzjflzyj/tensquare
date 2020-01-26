package com.tensquare.friend.service;

import com.tensquare.friend.client.UserClient;
import com.tensquare.friend.dao.FriendDao;
import com.tensquare.friend.dao.NoFriendDao;
import com.tensquare.friend.pojo.Friend;
import com.tensquare.friend.pojo.NoFriend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FriendService {

    @Autowired
    private FriendDao friendDao;

    @Autowired
    private NoFriendDao noFriendDao;

    @Autowired
    private UserClient userClient;

    /**
     * 添加好友
     * @param loginUserId
     * @param friendid
     */
    @Transactional
    public void like(String loginUserId, String friendid) {
        //1. 插入好友表一条记录
        Friend friend = new Friend();
        friend.setUserid(loginUserId);
        friend.setFriendid(friendid);
        friend.setIslike("0"); // 单方喜欢
        //2. 判断对方是否也是喜欢我, 通过统计个数（userid=friendid, friendid=loginUserId)
        int count = friendDao.selectCount(friendid,loginUserId);
        if(count > 0) {
            //3. 如果对方也喜欢我，更新它的islike为1 相互喜欢
            friendDao.updateLike(friendid, loginUserId,"1");
            friend.setIslike("1");
        }
        friendDao.save(friend);
        // 我关注了对方，我的关注数要+1
        userClient.updateFollow(loginUserId,1);
        // 我成为了对方的粉丝，对方的粉丝数要+1
        userClient.updateFans(friendid,1);
    }

    /**
     * 添加非好友
     * @param loginUserId
     * @param friendid
     */
    public void unlike(String loginUserId, String friendid) {
        NoFriend noFriend = new NoFriend();
        noFriend.setUserid(loginUserId);
        noFriend.setFriendid(friendid);
        noFriendDao.save(noFriend);
    }

    /**
     * 删除好友
     * @param loginUserId
     * @param friendid
     */
    @Transactional
    public void deleteFriend(String loginUserId, String friendid) {
        friendDao.remove(loginUserId, friendid);
        // 更新对方的islike=0
        friendDao.updateLike(friendid,loginUserId,"0");

        // 添加对方为非好友
        NoFriend noFriend = new NoFriend();
        noFriend.setUserid(loginUserId);
        noFriend.setFriendid(friendid);
        noFriendDao.save(noFriend);

        // 我的关注要减1
        userClient.updateFollow(loginUserId,-1);
        // 对方的粉丝数也要减1
        userClient.updateFans(friendid, -1);
    }
}
