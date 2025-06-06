package com.isep.vibelink.dao;

import com.isep.vibelink.domain.node.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//follower ——关注——> following
@Repository
public interface FollowDAO extends Neo4jRepository<User, Long> {
    /**
     * 创建一条关注关系（follower -> following）
     *
     * @param follower  发起关注的用户账号（例如当前用户）
     * @param following 被关注的用户账号
     * @return 创建的关注关系的 Neo4j 内部 ID（关系 ID）
     */
    @Query("""
                MATCH (a:User),(b:User)
                WHERE a.account = $follower AND b.account = $following
                CREATE (a)-[follow:Follow]->(b)
                RETURN ID(follow)
            """)
    Long createFollow(@Param("follower") String follower, @Param("following") String following);


    /**
     * 取消关注操作（即删除一条关注关系）
     *
     * @param follower  发起取消关注的用户账号
     * @param following 被取消关注的用户账号
     * @return 被删除的关注关系数量（通常为 0 或 1）
     */
    @Query("""
                MATCH (user:User {account: $follower})-[r:Follow]->(user2:User {account: $following})
                DELETE r
                RETURN count(r)
            """)
    Long deleteRelation(@Param("follower") String follower, @Param("following") String following);


    /**
     * 查询指定用户关注的所有用户列表
     *
     * @param myAccount 当前用户账号
     * @return 当前用户所关注的所有用户（去重）
     */
    @Query("""
                MATCH (me:User {account: $myAccount})-[:Follow]->(following:User)
                RETURN DISTINCT following
            """)
    List<User> getMyFollowing(@Param("myAccount") String myAccount);


    /**
     * 查询所有关注当前用户的人（粉丝列表）
     *
     * @param myAccount 当前用户账号
     * @return 关注了当前用户的所有用户（粉丝）
     */
    @Query("""
                MATCH (me:User {account: $myAccount})<-[:Follow]-(follower:User)
                RETURN follower
            """)
    List<User> getPeopleWhoFollowMe(@Param("myAccount") String myAccount);


    /**
     * 查询有多少人关注当前用户（粉丝数量）
     *
     * @param myAccount 当前用户账号
     * @return 粉丝数量（关注该用户的用户数）
     */
    @Query("""
                MATCH (:User {account: $myAccount})<-[:Follow]-(follower:User)
                RETURN count(follower)
            """)
    Long howManyPeopleFollowMe(@Param("myAccount") String myAccount);


    /**
     * 查询当前用户关注的用户数量（关注数）
     *
     * @param myAccount 当前用户账号
     * @return 当前用户关注的其他用户数量
     */
    @Query("""
                MATCH (:User {account: $myAccount})-[:Follow]->(following:User)
                RETURN count(following)
            """)
    Long howManyIFollow(@Param("myAccount") String myAccount);


    /**
     * 查询与当前用户互相关注的用户列表（即“我的朋友”）
     *
     * @param myAccount 当前用户账号
     * @return 所有与当前用户互相关注的用户
     */
    @Query("""
                MATCH (:User {account: $myAccount})-[:Follow]->(friend:User)-[:Follow]->(:User {account: $myAccount})
                RETURN DISTINCT friend
            """)
    List<User> getMyFriends(@Param("myAccount") String myAccount);


    /**
     * 判断一个用户是否关注了另一个用户
     *
     * @param follower  发起关注的用户账号
     * @param following 被关注的用户账号
     * @return 如果存在关注关系，返回 true；否则返回 false
     */
    @Query("""
                MATCH (:User {account: $follower})-[:Follow]->(:User {account: $following})
                RETURN count(*) > 0
            """)
    Boolean isFollow(@Param("follower") String follower, @Param("following") String following);

}
