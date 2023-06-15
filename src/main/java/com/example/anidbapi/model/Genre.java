package com.example.anidbapi.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Genre {

    @Id
    private String id;

    private String name;

}
