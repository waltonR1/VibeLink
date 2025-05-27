package com.isep.vibelink.domain.node;

import com.isep.vibelink.domain.BaseNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Node("User")
public class User extends BaseNode {
    @Property(name = "account")
    private String account;
    @Property(name = "password")
    private String password;
    @Property(name = "nickname")
    private String nickname;
    @Property(name = "age")
    private int age;
    @Property(name = "gender")
    private String gender;
    @Property(name = "email")
    private String email;
    @Property(name = "address")
    private String address;
    @Property(name = "imgurl")
    private String imgurl;

    @Relationship(type = "Follow", direction = Relationship.Direction.OUTGOING)
    private List<Follow> followings;

    @Relationship(type = "Like")
    private List<Like> myhobbys;

    @Relationship(type = "Publish", direction = Relationship.Direction.OUTGOING)
    private List<Publish> myshares;
}
