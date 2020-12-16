package io.drogue.iot.rodney.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {
    private List<Alternative> alternatives;

    @JsonProperty("final")
    private boolean fin;

    public void setAlternatives(List<Alternative> alternatives) {
        this.alternatives = alternatives;
    }

    public List<Alternative> getAlternatives() {
        return alternatives;
    }

    public void setFinal(boolean fin) {
        this.fin = fin;
    }

    public boolean isFinal() {
        return fin;
    }
}
