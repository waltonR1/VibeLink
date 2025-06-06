package com.isep.vibelink.dao;

import com.isep.vibelink.domain.node.Hobby;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HobbyDAO extends Neo4jRepository<Hobby, Long> {
    /**
     * 查询图数据库中所有的兴趣（Hobby）节点
     *
     * @return 所有兴趣节点组成的列表
     */
    @Query("MATCH (hobby:Hobby) RETURN hobby")
    List<Hobby> getAllHobbies();


    /**
     * 创建一个新的兴趣（Hobby）节点
     *
     * @param hName 兴趣名称（如“阅读”、“旅行”）
     * @param hType 兴趣类型（如“文学”、“运动”）
     * @return 新创建的兴趣节点
     */
    @Query("CREATE (hobby:Hobby {hName: $hName, hType: $hType}) RETURN hobby")
    Hobby addHobby(@Param("hName") String hName, @Param("hType") String hType);


    /**
     * 删除指定 ID 的兴趣节点
     *
     * @param id Hobby 节点 ID
     * @return true 表示成功删除，false 表示节点不存在
     */
    @Query("MATCH (hobby:Hobby) WHERE ID(hobby) = $id DELETE hobby RETURN COUNT(*) > 0")
    Boolean deleteWithId(@Param("id") Long id);


    /**
     * 根据 Neo4j 内部 ID 获取兴趣节点
     *
     * @param id 节点的内部数据库 ID（不是业务字段）
     * @return 对应的兴趣节点，如果不存在则返回 null
     */
    @Query("MATCH (hobby:Hobby) WHERE ID(hobby) = $id RETURN hobby")
    Hobby getHobbyById(@Param("id") Long id);


    /**
     * 根据 Hobby 的 Neo4j 内部 ID 更新其名称和类型
     *
     * @param id    节点内部 ID（Neo4j 系统生成）
     * @param hName 新的兴趣名称
     * @param hType 新的兴趣类型
     * @return 更新后的兴趣节点（Hobby）
     */
    @Query("""
                MATCH (hobby:Hobby)
                WHERE ID(hobby) = $id
                SET hobby.hName = $hName, hobby.hType = $hType
                RETURN hobby
            """)
    Hobby fixHobby(@Param("id") Long id,
                   @Param("hName") String hName,
                   @Param("hType") String hType);


    /**
     * 根据兴趣名称使用正则表达式进行模糊搜索
     *
     * @param hName 包含在兴趣名称中的关键字（区分大小写,例如：".*读书.*" 匹配包含“读书”的兴趣）
     * @return 匹配的兴趣列表
     */
    @Query("MATCH (hobby:Hobby) WHERE hobby.hName=~$hName RETURN hobby")
    List<Hobby> searchHobbyByName(@Param("hName") String hName);


    /**
     * 查询指定用户所喜欢的兴趣列表
     *
     * @param account 用户账号
     * @return 该用户所连接的所有 Hobby 节点
     */
    @Query("""
                MATCH (:User {account: $account})-[:Like]->(hobby:Hobby)
                RETURN hobby
            """)
    List<Hobby> getMyHobby(@Param("account") String account);

}