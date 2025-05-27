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

    @Query("""
            match (user:User) where user.account=$account
            create (share:Share{publishAccount:$account,publisher:$publisher,publisherImg:$publisherImg,
            title:$title,content:$content,relatedHobby:$related_hobby,hobbyId:$hobbyId,imgUrl:$imgUrl,time:$time,address:$address})
            create (user)-[publish:Publish]->(share) return share""")
    Share publishShare(String account, String publisher, String publisherImg,
                       String title,String content, String relatedHobby,Long hobbyId, String imgUrl, String address, String time);

    @Query("""
            match (user:User) where user.account=$account
            match (share:Share) where ID(share)=$shareId
            match (user)-[publish:Publish]->(share) delete share,publish return count(publish)""")
    Integer deleteShareById(String account, Long shareId);

    @Query("""
            match (user:User) where user.account=$account
            match ((user)-[:Publish]->(share)) return share""")
    List<Share> getShareByAccount(@Param("account") String account);

    @Query("""
            match (user:User) where user.account=$account
            match (user)-[:Follow]->(friends)-[:Publish]->(shares) return shares""")
    ArrayList<Share> getFriendShares(@Param("account") String account);

    @Query("""
            match (user:User) where user.account=$account
            match (user)-[:Like]->(hobbies)
            match (share:Share) where share.relatedHobby in hobbies.hName return share""")
    ArrayList<Share> recommendByHobby(@Param("account") String account);
}
