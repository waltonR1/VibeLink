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
    @Property(name = "hname")
    private String hname;
    @Property(name = "htype")
    private String htype;
    public Hobby(String hname) {
        this.hname = hname;
    }
}
