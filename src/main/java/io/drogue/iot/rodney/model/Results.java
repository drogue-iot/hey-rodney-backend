package io.drogue.iot.rodney.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Root element of the IBM Watson speech-to-text result.
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class Results {
    private List<Result> results;

    @JsonProperty("result_index")
    private int resultIndex;

    public void setResultIndex(int resultIndex) {
        this.resultIndex = resultIndex;
    }

    public int getResultIndex() {
        return resultIndex;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public List<Result> getResults() {
        return results;
    }
}
