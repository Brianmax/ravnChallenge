package org.ravn.moviescatalogchallenge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.ravn.moviescatalogchallenge.aggregate.request.MovieRequest;
import org.ravn.moviescatalogchallenge.aggregate.response.MovieResponse;
import org.ravn.moviescatalogchallenge.aggregate.response.UserResponse;
import org.ravn.moviescatalogchallenge.entity.Categorie;
import org.ravn.moviescatalogchallenge.entity.Movie;
import org.ravn.moviescatalogchallenge.entity.User;

import java.util.List;

@Mapper
public interface MovieMapper {
    MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);

    @Mapping(target="name", source = "movieRequest.name")
    @Mapping(target = "releaseDate", source = "movieRequest.releaseDate")
    @Mapping(target = "synopsis", source = "movieRequest.synopsis")
    @Mapping(target = "categories", source = "categories")
    @Mapping(target = "user", source = "user")
    Movie movieRequestToMovie(MovieRequest movieRequest, List<Categorie> categories, User user);

    @Mapping(target = "name", source = "movie.name")
    @Mapping(target = "releaseDate", source = "movie.releaseDate")
    @Mapping(target = "synopsis", source = "movie.synopsis")
    @Mapping(target = "categories", source = "categories")
    @Mapping(target = "user", source = "userResponse")
    MovieResponse movieToMovieResponse(Movie movie, List<String> categories, UserResponse userResponse);
}
