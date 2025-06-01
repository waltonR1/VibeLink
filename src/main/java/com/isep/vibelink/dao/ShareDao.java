package com.isep.vibelink.dao;

import com.isep.vibelink.domain.node.Share;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareDao extends Neo4jRepository<Share, Long> {

    /**
     * 用户发布一条新动态（Share），并建立发布关系
     *
     * @param account      用户账号（用于匹配 User 节点）
     * @param publisher    显示用的发布者昵称
     * @param publisherImg 发布者头像 URL
     * @param title        动态标题
     * @param content      动态内容
     * @param relatedHobby 动态关联的兴趣名称
     * @param hobbyId      动态关联的兴趣 ID
     * @param imgUrl       动态封面图片 URL
     * @param address      发布地点
     * @param time         发布时间
     * @return 创建的 Share 节点对象
     */
    @Query("""
                MATCH (user:User) WHERE user.account = $account
                CREATE (share:Share {
                    publishAccount: $account,
                    publisher: $publisher,
                    publisherImg: $publisherImg,
                    title: $title,
                    content: $content,
                    relatedHobby: $relatedHobby,
                    hobbyId: $hobbyId,
                    imgUrl: $imgUrl,
                    time: $time,
                    address: $address
                })
                CREATE (user)-[:Publish]->(share)
                RETURN share
            """)
    Share publishShare(@Param("account") String account,
                       @Param("publisher") String publisher,
                       @Param("publisherImg") String publisherImg,
                       @Param("title") String title,
                       @Param("content") String content,
                       @Param("relatedHobby") String relatedHobby,
                       @Param("hobbyId") Long hobbyId,
                       @Param("imgUrl") String imgUrl,
                       @Param("address") String address,
                       @Param("time") String time);


    /**
     * 删除用户发布的一条动态（Share 节点和对应 Publish 关系）
     *
     * @param account 用户账号
     * @param shareId 被删除动态的 Neo4j 内部 ID
     * @return 删除的发布关系数量（0 表示无权限或动态不存在，1 表示删除成功）
     */
    @Query("""
                MATCH (user:User {account: $account})-[publish:Publish]->(share:Share)
                WHERE ID(share) = $shareId
                DETACH DELETE share
                RETURN count(publish)
            """)
    Integer deleteShareById(@Param("account") String account, @Param("shareId") Long shareId);


    /**
     * 获取指定用户发布的所有动态
     *
     * @param account 用户账号
     * @return 该用户发布的 Share 列表
     */
    @Query("""
                MATCH (:User {account: $account})-[:Publish]->(share:Share)
                OPTIONAL MATCH (s)-[r:Praised]->(u:User)
                RETURN share, collect(r), collect(u)
            """)
    List<Share> getShareByAccount(@Param("account") String account);


    /**
     * 查询当前用户所关注的用户发布的所有动态
     *
     * @param account 当前用户账号
     * @return 朋友发布的动态列表
     */
    @Query("""
            MATCH (:User {account: $account})-[:Follow]->(:User)-[:Publish]->(s:Share)
            OPTIONAL MATCH (s)-[r:Praised]->(u:User)
            RETURN s, collect(r), collect(u)
            """)
    List<Share> getFriendShares(@Param("account") String account);


    /**
     * 推荐与当前用户兴趣相关的动态（基于 Hobby -> Share 的匹配）
     *
     * @param account 当前用户账号
     * @return 兴趣匹配的动态列表
     */
    @Query("""
                MATCH (user:User {account: $account})-[:Like]->(h:Hobby)
                MATCH (share:Share)
                WHERE share.relatedHobby = h.hName
                OPTIONAL MATCH (s)-[r:Praised]->(u:User)
                RETURN DISTINCT share, collect(r), collect(u)
            """)
    List<Share> recommendByHobby(@Param("account") String account);

}
