package org.ravn.moviescatalogchallenge.aggregate.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String email;
    private String password;
}
