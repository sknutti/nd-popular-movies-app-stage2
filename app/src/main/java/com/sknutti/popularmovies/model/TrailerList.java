package com.sknutti.popularmovies.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sknutti on 8/6/15.
 */
public class TrailerList {
    private Integer id;
    private List<Trailer> results = new ArrayList<Trailer>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The results
     */
    public List<Trailer> getTrailers() {
        return results;
    }

    /**
     *
     * @param results
     * The results
     */
    public void setTrailers(List<Trailer> results) {
        this.results = results;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
