package org.ravn.moviescatalogchallenge.aggregate.response;

import lombok.Getter;
import lombok.Setter;
import org.ravn.moviescatalogchallenge.aggregate.request.BaseMovieRequest;

import java.sql.Date;

@Getter
@Setter
public class MovieResponse extends BaseMovieRequest {
    private String createdBy;
    private Date createdAt;
    private Date updatedAt;
    private String updatedBy;
    private boolean deleted;
    private Date deletedAt;
    private String deletedBy;
}
