package com.isep.vibelink.dao;

import com.isep.vibelink.domain.node.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendDao extends Neo4jRepository<User,Long> {

    @Query("""
            match (user:User) where user.account=$account
            match ((user)-[:Follow]->()-[:Follow]->(p)) return distinct p""")
    List<User> byFriend(@Param("account") String account);

    @Query("""
            match (user:User) where user.account=$account
            match (user)-[:Like]->(hobbies)
            match (users)-[:Like]->(hobbies) return users""")
    List<User> byHobby(@Param("account") String account);

    @Query("""
            match (user:User) where user.account=$account
            match (user)-[:Publish]->(shares)
            match (shares)-[:Praised]->(users) return users""")
    List<User> byShare(String account);
}
