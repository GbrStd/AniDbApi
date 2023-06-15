package com.example.anidbapi.model;

import com.example.anidbapi.core.CacheableMongoDocument;
import com.example.anidbapi.core.TTLField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InfoAnimeCache implements CacheableMongoDocument<InfoAnimeCache>, Serializable {

    @Id
    private String id;
    private final long animeId;
    private final String name;
    private final String imageUrl;
    private final Double rating;
    private final String type;
    private final String numEps;
    private final String airedDate;
    private final String endedDate;
    @TTLField(name = "createdAtIndex", expireAfterSeconds = 300)
    private Date createdAt = new Date();

}
