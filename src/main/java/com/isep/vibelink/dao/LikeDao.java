package com.isep.vibelink.dao;

import com.isep.vibelink.domain.relationship.Like;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeDao extends Neo4jRepository<Like,Long> {
    @Query("""
            match (user:User) where user.account=$account
            match (hobby:Hobby) where ID(hobby)=$hobbyId
            create (user)-[like:Like]->(hobby)
            create (user)<-[liked:Like]-(hobby) return count(liked)+count(like)""")
    Integer likeHobby(String account, Long hobbyId);

    @Query("""
            match (user:User) where user.account=$account
            match (hobby:Hobby) where ID(hobby)=$hobbyId
            match (user)-[like:Like]-(hobby) delete like return count(like)""")
    Integer unlikeHobby(String account, Long hobbyId);
}
