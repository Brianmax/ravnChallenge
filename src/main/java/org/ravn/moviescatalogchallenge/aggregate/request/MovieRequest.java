package org.ravn.moviescatalogchallenge.aggregate.request;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
public class MovieRequest {
    protected String name;
    protected Date releaseDate;
    protected String synopsis;
    protected List<String> categories;
    private int userId;
}
