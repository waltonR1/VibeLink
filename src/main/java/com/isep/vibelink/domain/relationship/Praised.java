package com.isep.vibelink.domain.relationship;

import com.isep.vibelink.domain.node.User;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@Data
@RelationshipProperties
public class Praised {
    @Id
    @GeneratedValue
    private Long id;
    @TargetNode
    private User user;
}
