package io.drogue.iot.rodney.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Alternative {
    public double confidence;
    public String transcript;
}
