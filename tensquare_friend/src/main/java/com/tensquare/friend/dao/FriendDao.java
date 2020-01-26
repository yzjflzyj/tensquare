package com.tensquare.friend.dao;

import com.tensquare.friend.pojo.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FriendDao extends JpaRepository<Friend,String> {

    @Query("select count(f) from Friend f where userid=?1 and friendid=?2")
    int selectCount(String userid, String friendid);

    @Query("update Friend set islike=?3 where userid=?1 and friendid=?2")
    @Modifying
    void updateLike(String userid, String friendid, String s);

    @Query("delete from Friend where userid=?1 and friendid=?2")
    @Modifying
    void remove(String userid, String friendid);
}
