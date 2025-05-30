package com.isep.vibelink.domain.node;

import com.isep.vibelink.domain.BaseNode;
import com.isep.vibelink.domain.relationship.Follow;
import com.isep.vibelink.domain.relationship.Like;
import com.isep.vibelink.domain.relationship.Publish;
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
    @Property(name = "imgUrl")
    private String imgUrl;

    @Relationship(type = "Follow", direction = Relationship.Direction.OUTGOING)
    private List<Follow> followings;

    // 用户被其他用户关注（INCOMING 表示别人关注当前用户）
    @Relationship(type = "Follow", direction = Relationship.Direction.INCOMING)
    private List<Follow> followers;

    @Relationship(type = "Like")
    private List<Like> myHobbies;

    @Relationship(type = "Publish", direction = Relationship.Direction.OUTGOING)
    private List<Publish> myShares;
}
