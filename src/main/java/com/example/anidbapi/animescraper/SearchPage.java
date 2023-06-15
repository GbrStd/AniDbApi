package com.example.anidbapi.animescraper;

import lombok.Data;
import lombok.Value;

@Data
@Value
public class SearchPage {
    PageResult[] pageResults;
    int currentPage;
    boolean hasNextPage;
}
