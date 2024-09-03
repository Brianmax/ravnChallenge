package org.ravn.moviescatalogchallenge.aggregate.response;

import lombok.Getter;
import lombok.Setter;
import org.ravn.moviescatalogchallenge.aggregate.request.BaseMovieRequest;
import org.ravn.moviescatalogchallenge.aggregate.request.MovieCreateRequest;

import java.sql.Date;

@Getter
@Setter
public class MovieResponse extends BaseMovieRequest {
    private UserResponse createdBy;
    private Date createdAt;
    private Date updatedAt;
    private String updatedBy;
    private boolean deleted;
    private Date deletedAt;
    private String deletedBy;
}
