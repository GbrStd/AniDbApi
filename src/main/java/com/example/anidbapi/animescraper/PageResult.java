package com.example.anidbapi.animescraper;

import lombok.Data;
import lombok.Value;

@Data
@Value
public class PageResult {
    long id;
    String name;
    String imageUrl;
    Double rating;
    String type;
    String numEps;
    String airedDate;
    String endedDate;
}
