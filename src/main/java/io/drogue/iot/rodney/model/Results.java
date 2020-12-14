package io.drogue.iot.rodney.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Results {
    public List<Result> results;

    @JsonProperty("result_index")
    public int resultIndex;
}
