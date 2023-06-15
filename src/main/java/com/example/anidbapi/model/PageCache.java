package com.example.anidbapi.model;

import com.example.anidbapi.core.CacheableMongoDocument;
import com.example.anidbapi.core.TTLField;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document
@Data
public class PageCache implements CacheableMongoDocument<PageCache>, Serializable {

    @Id
    private String id;
    private final String searchQuery;
    private final int pageIndex;
    private final boolean hasNextPage;
    private List<InfoAnimeCache> infoAnimeCache = new ArrayList<>(0);
    @TTLField(name = "createdAtIndex", expireAfterSeconds = 300)
    private Date createdAt = new Date();

}
