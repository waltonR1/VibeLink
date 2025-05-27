package com.isep.vibelink.domain.node;

import com.isep.vibelink.domain.BaseNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@EqualsAndHashCode(callSuper = true)
@Data
@Node("Hobby")
public class Hobby extends BaseNode {
    @Property(name = "hName")
    private String hName;
    @Property(name = "hType")
    private String hType;
    public Hobby(String hName) {
        this.hName = hName;
    }
}
