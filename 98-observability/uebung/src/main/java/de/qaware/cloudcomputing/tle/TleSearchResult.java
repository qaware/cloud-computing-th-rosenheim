package de.qaware.cloudcomputing.tle;

import lombok.Data;

import javax.json.bind.annotation.JsonbProperty;
import java.util.List;

@Data
public class TleSearchResult {

    @JsonbProperty("@context")
    String context;

    @JsonbProperty("@id")
    String id;

    @JsonbProperty("@type")
    String type;

    int totalItems;

    List<TleMember> member;
}
