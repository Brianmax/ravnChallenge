package org.ravn.moviescatalogchallenge.aggregate.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieCreateRequest extends BaseMovieRequest {
    private int userId;
}
