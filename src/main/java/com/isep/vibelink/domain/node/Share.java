package com.isep.vibelink.domain.node;

import com.isep.vibelink.domain.BaseNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Node("Share")
public class Share extends BaseNode {

    @Property(name = "content")
    private String content; // 发表动态的内容
    @Property(name = "imgUrl")
    private String imgUrl; // 图片的url地址
    @Property(name = "time")
    private String time; // 发表动态的时间
    @Property(name = "address")
    private String address; // 发表动态的位置
    @Property(name = "title")
    private String title;

    @Property(name = "relatedHobby")
    private String relatedHobby; // 相关兴趣

    @Property(name = "hobbyId")
    private Long hobbyId;

    @Property(name = "publisher")
    private String publisher;

    @Property(name = "publisherImg")
    private String publisherImg;

    @Property(name = "publisherAccount")
    private String account;

    @Relationship(type = "Praised",direction = Relationship.Direction.OUTGOING)
    private List<User> praisedUser;
}
