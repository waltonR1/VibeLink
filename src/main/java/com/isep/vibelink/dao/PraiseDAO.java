package com.isep.vibelink.dao;

import com.isep.vibelink.domain.relationship.Praised;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PraiseDAO extends Neo4jRepository<Praised, Long> {
    /**
     * 用户点赞一条动态（Share），创建 Praised 关系
     *
     * @param account 点赞者账号
     * @param shareId 被点赞的动态的 ID
     * @return 新建的 Praised 关系的 Neo4j 内部 ID
     */
    @Query("""
                MATCH (user:User) WHERE user.account = $account
                MATCH (share:Share) WHERE ID(share) = $shareId
                CREATE (share)-[praised:Praised]->(user)
                RETURN ID(praised)
            """)
    Long praisedIt(@Param("account") String account, @Param("shareId") Integer shareId);


    /**
     * 取消用户对动态的点赞（删除 Praised 关系）
     *
     * @param account 用户账号
     * @param shareId 被点赞动态的内部 ID
     * @return 删除的 Praised 关系数量（0 表示未点过赞，1 表示成功取消）
     */
    @Query("""
                MATCH (user:User) WHERE user.account = $account
                MATCH (share:Share) WHERE ID(share) = $shareId
                MATCH (share)-[praised:Praised]->(user)
                DELETE praised
                RETURN count(praised)
            """)
    Integer cancelPraised(@Param("account") String account, @Param("shareId") Integer shareId);


    /**
     * 获取某条动态被点赞的数量
     *
     * @param shareId 动态的 Neo4j 内部 ID
     * @return 点赞数量（即从该 Share 节点出发的 Praised 关系数量）
     */
    @Query("""
                MATCH (share:Share) WHERE ID(share) = $shareId
                MATCH (share)-[:Praised]->(user:User)
                RETURN count(user)
            """)
    Long getPraisedNumber(@Param("shareId") Integer shareId);


    /**
     * 查询某用户是否点赞了指定动态（返回 0 或 1）
     *
     * @param shareId 动态 ID
     * @param account 用户账号
     * @return 点赞数（理论上为 0 或 1）
     */
    @Query("""
                MATCH (share:Share) WHERE ID(share) = $shareId
                MATCH (share)-[:Praised]->(user:User) WHERE user.account = $account
                RETURN count(user)
            """)
    Integer isPraised(@Param("shareId") Integer shareId, @Param("account") String account);

}
