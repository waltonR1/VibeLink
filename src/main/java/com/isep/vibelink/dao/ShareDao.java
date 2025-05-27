package com.isep.vibelink.dao;

import com.isep.vibelink.domain.node.Share;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface ShareDao extends Neo4jRepository<Share,Long> {

    @Query("match (user:User) where user.account=$account\n" +
            "create (share:Share{publishAccount:$account,publisher:$publisher,publisherImg:$publisherimg," +
            "title:$title,content:$content,relatedHobby:$related_hobby,hobbyId:$hobbyid,imgUrl:$imgurl,time:$time,address:$address})\n" +
            "create (user)-[publish:Publish]->(share) return share")
    Share publishShare(String account, String publisher, String publisherImg,
                       String title,String content, String relatedHobby,Long hobbyId, String imgUrl, String address, String time);

    @Query("match (user:User) where user.account=$account\n" +
            "match (share:Share) where ID(share)=$shareid\n" +
            "match (user)-[publish:Publish]->(share) delete share,publish return count(publish)")
    Integer deleteShareById(String account, Long shareid);

    @Query("match (user:User) where user.account=$account \n" +
            "match ((user)-[:Publish]->(share)) return share")
    List<Share> getShareByAccount(@Param("account") String account);

    @Query("match (user:User) where user.account=$account\n" +
            "match (user)-[:Follow]->(friends)-[:Publish]->(shares) return shares")
    ArrayList<Share> getFriendShares(@Param("account") String account);

    @Query("match (user:User) where user.account=$account\n" +
            "match (user)-[:Like]->(hobbies)\n" +
            "match (share:Share) where share.relatedHobby in hobbies.hName return share")
    ArrayList<Share> recommendByHobby(@Param("account") String account);
}
