package com.isep.vibelink.dao;

import com.isep.vibelink.domain.relationship.Praised;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PraiseDao extends Neo4jRepository<Praised,Long> {

    @Query("""
            match (user:User) where user.account=$account
            match (share:Share) where ID(share)=$shareId
            create (share)-[praised:Praised]->(user) return ID(praised)""")
    Long praisedIt(String account, Integer shareId);

    @Query("""
            match (user:User) where user.account=$account
            match (share:Share) where ID(share)=$shareId
            match (share)-[praised:Praised]->(user) delete praised return count(praised)""")
    Integer cancelPraised(String account, Integer shareId);

    @Query("""
            match (share:Share) where ID(share)=$shareId
            match (share)-[:Praised]->(user) return count(user)""")
    Long getPraisedNumber(Integer shareId);

    @Query("""
            match (share:Share) where ID(share)=$shareId
            match (share)-[:Praised]->(user) where user.account=$account return count(user)""")
    Integer isPraised(Integer shareId, String account);
}
