package org.ravn.moviescatalogchallenge.aggregate.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MovieRequest {
    private String name;
    private String releaseDate;
    private String synopsis;
    private List<String> categories;
}
