package io.drogue.iot.rodney.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Root element of the IBM Watson speech-to-text result.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Results {
    List<Result> results;

    @JsonProperty("result_index")
    int resultIndex;

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
