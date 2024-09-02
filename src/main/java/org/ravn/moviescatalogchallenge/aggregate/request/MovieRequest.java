package org.ravn.moviescatalogchallenge.aggregate.request;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
public class MovieRequest {
    private String name;
    private Date releaseDate;
    private String synopsis;
    private List<String> categories;
}
