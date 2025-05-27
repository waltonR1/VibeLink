package com.isep.vibelink.domain.relationship;

import com.isep.vibelink.domain.node.Share;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@Data
@RelationshipProperties
public class Publish {
    @Id
    @GeneratedValue
    private Long id;
    @TargetNode
    private Share share;
}
