package com.isep.vibelink.dao;

import com.isep.vibelink.domain.node.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendDAO extends Neo4jRepository<User, Long> {

    /**
     * 基于“朋友的朋友”推荐用户列表
     * 示例逻辑：我关注 A，A 关注 B，那么 B 是潜在推荐对象
     *
     * @param account 当前用户账号
     * @return 推荐的用户列表（去重）
     */
    @Query("""
                MATCH (user:User) WHERE user.account = $account
                MATCH (user)-[:Follow]->(:User)-[:Follow]->(p:User)
                RETURN DISTINCT p
            """)
    List<User> byFriend(@Param("account") String account);

    /**
     * 基于兴趣相似度推荐用户：找出和我有相同爱好的人（排除自己）
     *
     * @param account 当前用户账号
     * @return 推荐的用户列表（有共同兴趣）
     */
    @Query("""
                MATCH (me:User {account: $account})-[:Like]->(h:Hobby)
                MATCH (other:User)-[:Like]->(h)
                WHERE other.account <> $account
                RETURN DISTINCT other
            """)
    List<User> byHobby(@Param("account") String account);


    /**
     * 查找所有点赞了当前用户发布的动态的用户
     *
     * @param account 当前用户账号
     * @return 点赞了我发布内容的用户列表（去重）
     */
    @Query("""
                MATCH (me:User {account: $account})-[:Publish]->(:Share)-[:Praised]->(user:User)
                RETURN DISTINCT user
            """)
    List<User> byShare(@Param("account") String account);

}
