package com.isep.vibelink.dao;

import com.isep.vibelink.domain.node.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDAO extends Neo4jRepository<User, Long> {
    /**
     * 创建一个新用户节点
     *
     * @param account  用户账号（唯一标识）
     * @param password 密码（建议加密存储）
     * @param nickname 昵称
     * @param age      年龄
     * @param gender   性别
     * @param email    邮箱
     * @param address  地址
     * @return 创建的 User 节点对象
     */
    @Query("""
                CREATE (user:User {
                    account: $account,
                    password: $password,
                    nickname: $nickname,
                    age: $age,
                    gender: $gender,
                    email: $email,
                    address: $address
                })
                RETURN user
            """)
    User addUser(@Param("account") String account,
                 @Param("password") String password,
                 @Param("nickname") String nickname,
                 @Param("age") Integer age,
                 @Param("gender") String gender,
                 @Param("email") String email,
                 @Param("address") String address);


    /**
     * 根据账号删除用户节点，并删除该用户的所有关联关系
     *
     * @param account 用户账号
     * @return 删除是否成功（true 表示删除了用户，false 表示用户不存在）
     */
    @Query("""
                MATCH (user:User {account: $account})
                DETACH DELETE user
                RETURN COUNT(user) > 0
            """)
    Boolean deleteUserByAccount(@Param("account") String account);


    /**
     * 根据用户账号查询用户节点
     *
     * @param account 用户账号
     * @return 匹配到的用户节点，若不存在则返回 null
     */
    @Query("MATCH (user:User) WHERE user.account = $account RETURN user")
    User getUserByAccount(@Param("account") String account);


    /**
     * 登录校验：根据账号和密码查询用户（仅用于原型，生产应加密）
     *
     * @param account  用户账号
     * @param password 用户密码（明文）
     * @return 匹配的用户，若账号或密码错误返回 null
     */
    @Query("""
                MATCH (user:User)
                WHERE user.account = $account AND user.password = $password
                RETURN user
            """)
    User checkUser(@Param("account") String account, @Param("password") String password);


    /**
     * 获取图数据库中所有用户节点
     *
     * @return 所有用户列表
     */
    @Query("MATCH (user:User) RETURN user")
    List<User> getAllUser();


    /**
     * 更新指定用户的基本信息（昵称、年龄、地址、邮箱）
     *
     * @param account  用户账号（唯一标识）
     * @param nickname 新昵称
     * @param age      新年龄
     * @param address  新地址
     * @param email    新邮箱
     * @return 被更新的用户数量（1 表示成功，0 表示未找到该用户）
     */
    @Query("""
                MATCH (user:User) WHERE user.account = $account
                SET user.nickname = $nickname,
                    user.age = $age,
                    user.address = $address,
                    user.email = $email
                RETURN count(user)
            """)
    Long fixInfo(@Param("account") String account,
                 @Param("nickname") String nickname,
                 @Param("age") Integer age,
                 @Param("address") String address,
                 @Param("email") String email);


    /**
     * 修改指定用户的密码
     *
     * @param account  用户账号
     * @param password 新密码（建议在 Java 层加密后再传入）
     * @return 更新的用户数量（1 表示成功，0 表示账号不存在）
     */
    @Query("""
                MATCH (user:User) WHERE user.account = $account
                SET user.password = $password
                RETURN count(user)
            """)
    Long fixPass(@Param("account") String account,
                 @Param("password") String password);


    /**
     * 更新指定用户的头像地址（imgUrl）
     *
     * @param account 用户账号
     * @param imgUrl  新头像的 URL 地址
     * @return 更新的用户数量（1 表示成功，0 表示用户不存在）
     */
    @Query("""
                MATCH (user:User) WHERE user.account = $account
                SET user.imgUrl = $imgUrl
                RETURN count(user)
            """)
    Long fixImg(@Param("account") String account,
                @Param("imgUrl") String imgUrl);

}
