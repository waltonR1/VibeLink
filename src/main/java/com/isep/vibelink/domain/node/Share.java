package com.isep.vibelink.domain.node;

import com.isep.vibelink.domain.BaseNode;
import com.isep.vibelink.domain.relationship.Praised;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Transient;
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
    private List<Praised> praisedUser;

    @Transient
    private boolean praisedByCurrentUser; // 页面显示是否已点赞

    public void updatePraisedStatus(String currentAccount) {
        if (praisedUser != null) {
            for (Praised p : praisedUser) {
                User u = p.getUser();
                if (u != null && currentAccount.equals(u.getAccount())) {
                    this.praisedByCurrentUser = true;
                    return;
                }
            }
        }
        this.praisedByCurrentUser = false;
    }
}
