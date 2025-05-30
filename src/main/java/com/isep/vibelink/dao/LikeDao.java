package com.isep.vibelink.dao;

import com.isep.vibelink.domain.relationship.Like;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeDao extends Neo4jRepository<Like, Long> {
    /**
     * 用户点赞一个兴趣（建立双向 Like 关系）
     *
     * @param account 用户账号
     * @param hobbyId 兴趣节点的 Neo4j 内部 ID
     * @return 实际新创建的关系数量（0~2）
     */
    @Query("""
                match (user:User) where user.account=$account
                match (hobby:Hobby) where ID(hobby)=$hobbyId
                create (user)-[like:Like]->(hobby)
                create (user)<-[liked:Like]-(hobby)
                return count(liked)+count(like)
            """)
    Integer likeHobby(@Param("account") String account, @Param("hobbyId") Long hobbyId);


    /**
     * 取消用户对某个兴趣的喜欢（删除双向 Like 关系）
     *
     * @param account 用户账号
     * @param hobbyId Hobby 节点的内部 ID
     * @return 实际删除的 Like 关系数量（0～2）
     */
    @Query("""
                match (user:User) where user.account=$account
                match (hobby:Hobby) where ID(hobby)=$hobbyId
                match (user)-[like:Like]-(hobby)
                delete like
                return count(like)
            """)
    Integer unlikeHobby(@Param("account") String account, @Param("hobbyId") Long hobbyId);
}
