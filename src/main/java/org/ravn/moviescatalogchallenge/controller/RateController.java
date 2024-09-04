package org.ravn.moviescatalogchallenge.controller;

import org.ravn.moviescatalogchallenge.aggregate.request.RateDto;
import org.ravn.moviescatalogchallenge.aggregate.response.ResponseBase;
import org.ravn.moviescatalogchallenge.service.RateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rate")
public class RateController {
    private final RateService rateService;

    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    @PostMapping("/save")
    ResponseBase<RateDto> rateMovie(@RequestBody RateDto rateDto) {
        return rateService.rateMovie(rateDto);
    }

    @GetMapping("/get")
    List<RateDto> getMovieRates() {
        return rateService.getMovieRates();
    }

    @DeleteMapping("/delete")
    ResponseBase<Boolean> deleteRate(@RequestParam String movieName) {
        return rateService.deleteRate(movieName);
    }
}
