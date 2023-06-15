package com.example.anidbapi.animescraper;

import com.example.anidbapi.proxy.ProxyWorkerPool;
import com.example.anidbapi.utils.TimeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

public class AniDbScraper extends AnimeScraper {

    private static final String ANIDB_URL = "https://anidb.net";

    public AniDbScraper(ProxyWorkerPool proxyWorkerPool) {
        super(proxyWorkerPool);
    }

    private static String getAniDbId(String id) {
        StringBuilder digitsSb = new StringBuilder();
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            if (Character.isDigit(c)) {
                digitsSb.append(c);
            }
        }
        return digitsSb.toString();
    }

    private static List<Element> getAniDbAnimeList(Element body) throws AnimeScraperException {
        final Element animeList = body.getElementsByClass("animelist").first();

        if (animeList == null) {
            throw new AnimeScraperException("Anime list not found");
        }

        final Element tbody = animeList.getElementsByTag("tbody").first();

        if (tbody == null) {
            throw new AnimeScraperException("Tbody not found");
        }

        final List<Element> trs = tbody.getElementsByTag("tr");

        if (trs.isEmpty()) {
            throw new AnimeScraperException("No results found");
        }

        return trs;
    }

    private static String valueFromAnimeInfo(Element animeInfo, String trClass) {
        final Element type = animeInfo.getElementsByClass(trClass).first();
        if (type == null)
            return null;
        final Element td = type.getElementsByTag("td").first();
        if (td == null)
            return null;
        animeInfo.removeClass(trClass);
        return td.text();
    }

    private AnimePage getAnimePageById(long id) throws AnimeScraperException, FailedToScrapeException {
        final String url = ANIDB_URL + "/anime/%d".formatted(id);

        final String pageHtml;
        try {
            pageHtml = doRequest(url, false);
        } catch (IOException | InterruptedException e) {
            throw new FailedToScrapeException("Failed to search anime", e);
        }

        final Element body = Jsoup.parse(pageHtml).body();

        final Element main = body.getElementById("layout-main");

        if (main == null) {
            throw new AnimeScraperException("Main not found");
        }

        AnimePage animePage = new AnimePage();
        animePage.setId(id);

        final Element title = main.getElementsByTag("h1").stream()
                .filter(e -> e.hasClass("anime"))
                .findFirst()
                .orElseThrow(() -> new AnimeScraperException("Anime not found"));

        final String animeName = title.text().replace("Anime:", "").trim();
        animePage.setName(animeName);

        final Element divInfo = main.getElementsByClass("g_section info").first();

        // image
        if (divInfo != null) {
            final Element img = divInfo.getElementsByTag("img").first();
            if (img != null) {
                animePage.setImageUrl(img.attr("src"));
            }
        }

        // info
        if (divInfo != null) {
            final Element animeInfo = divInfo.getElementsByClass("pane info").first();

            if (animeInfo != null) {

                List<String> tagList = new ArrayList<>();
                final Element tags = animeInfo.getElementsByClass("tags").first();
                if (tags != null) {
                    final Element td = tags.getElementsByTag("td").first();
                    if (td != null) {
                        for (Element tagName : td.getElementsByClass("tagname")) {
                            tagList.add(tagName.text());
                        }
                    }
                }

                animePage.setType(valueFromAnimeInfo(animeInfo, "type"));
                animePage.setYear(valueFromAnimeInfo(animeInfo, "year"));
                animePage.setSeason(valueFromAnimeInfo(animeInfo, "season"));
                animePage.setGenres(tagList);

                final String rating = valueFromAnimeInfo(animeInfo, "rating");
                Double ratingValue = null;
                if (rating != null) {
                    try {
                        final String[] ratingSplit = rating.split(" ");
                        ratingValue = ratingSplit[0].equals("N/A") ? null : Double.parseDouble(ratingSplit[0]);
                    } catch (Exception ignored) {
                    }
                }
                animePage.setRating(ratingValue);

                animeInfo.getElementsByClass("group streaming").first().getElementsByTag("a").forEach(a -> {
                    final String link = a.attr("href");
                    if (link.contains("crunchyroll")) {
                        animePage.setCrunchyrollLink(link);
                    }
                });
            }
        }

        // summary
        final Element summary = main.getElementsByClass("desc").first();
        if (summary != null) {
            animePage.setSummary(summary.text());
        }

        // episodes
        List<EpisodeInfo> episodeInfoList = new ArrayList<>();
        final Element epList = main.getElementById("eplist");
        if (epList != null) {
            final Element tbody = epList.getElementsByTag("tbody").first();
            for (Element ep : tbody.getElementsByTag("tr")) {
                final Element abbr = ep.firstElementChild().getElementsByTag("abbr").first();
                if (!abbr.attr("title").equalsIgnoreCase("Regular Episode"))
                    continue;
                String epNumber = abbr.text().trim();
                String epTitle = ep.getElementsByClass("title name episode").first().text();
                String duration = ep.getElementsByClass("duration").first().text();
                String airDate = ep.getElementsByClass("date airdate").first().text();
                Elements epLinks = ep.getElementsByClass("action episode").first().getElementsByTag("a");
                String crunchyrollLink = null;
                for (Element link : epLinks) {
                    final String href = link.attr("href");
                    if (href.contains("crunchyroll.com")) {
                        crunchyrollLink = href;
                        break;
                    }
                }
                Integer epNumberInt = null;
                try {
                    epNumberInt = Integer.parseInt(epNumber);
                } catch (NumberFormatException ignored) {
                }

                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .appendPattern("dd.MM.yyyy[ HH:mm:ss]")
                        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                        .toFormatter();

                final LocalDateTime airDateParsed;
                try {
                    airDateParsed = LocalDateTime.parse(airDate, formatter);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                episodeInfoList.add(new EpisodeInfo(epNumberInt, epTitle, crunchyrollLink, airDateParsed, TimeUtils.durationFrom(duration)));
            }
        }

        animePage.setEpisodes(episodeInfoList);

        return animePage;
    }

    @Override
    public SearchPage searchAnimes(String animeName, int pageIndex) throws AnimeScraperException, FailedToScrapeException {
        boolean includeAdultContent = false;
        final String url;
        if (!includeAdultContent) {
            url = ANIDB_URL + "/anime/?adb.search=%s&do.update=1&noalias=1&orderby.name=0.1&type.movie=1&type.tvseries=1&type.tvspecial=1&page=%d".formatted(URLEncoder.encode(animeName, StandardCharsets.UTF_8), pageIndex);
        } else {
            url = ANIDB_URL + "/search/fulltext/?adb.search=%s&do.search=1&entity.animetb=1&field.titles=1&page=%d".formatted(URLEncoder.encode(animeName, StandardCharsets.UTF_8), pageIndex);
        }

        final String pageHtml;
        try {
            pageHtml = doRequest(url, false);
        } catch (IOException | InterruptedException e) {
            throw new FailedToScrapeException("Failed to search anime", e);
        }

        final Element body = Jsoup.parse(pageHtml).body();

        final List<Element> trs = getAniDbAnimeList(body);

        ArrayList<PageResult> pageResults = new ArrayList<>(trs.size());
        for (Element tr : trs) {

            final long idLong = Long.parseLong(getAniDbId(tr.id()));

            final Element name = tr.getElementsByClass("name main anime").first();
            final Element type = tr.getElementsByClass("type").first();
            final Element numEps = tr.getElementsByClass("count eps").first();
            final Element rating = tr.getElementsByClass("rating").first();
            final Element aired = tr.getElementsByClass("date airdate").first();
            final Element ended = tr.getElementsByClass("date enddate").first();

            String imageUrl = tr.getElementsByTag("source").attr("srcset");
            String nameStr = name != null ? name.text() : "";
            String typeStr = type != null ? type.text() : "";
            String numEpsStr = numEps != null ? numEps.text() : "";
            String ratingStr = rating != null ? rating.text() : "";
            String airedStr = aired != null ? aired.text() : "";
            String endedStr = ended != null ? ended.text() : "";

            Double ratingValue;
            try {
                final String[] ratingSplit = ratingStr.split(" ");
                ratingValue = ratingSplit[0].equals("N/A") ? null : Double.parseDouble(ratingSplit[0]);
            } catch (Exception e) {
                ratingValue = null;
            }

            pageResults.add(new PageResult(idLong, nameStr, imageUrl, ratingValue, typeStr, numEpsStr, airedStr, endedStr));
        }

        Element navigationBar = body.getElementsByClass("g_list jump").first();

        boolean hasNextPage = false;

        if (navigationBar != null) {
            Element nextButton = navigationBar.getElementsByClass("next").first();
            if (nextButton != null) {
                nextButton = nextButton.getElementsByTag("a").first();
            }
            hasNextPage = nextButton != null && !nextButton.isBlock();
        }

        return new SearchPage(pageResults.toArray(PageResult[]::new), pageIndex, hasNextPage);
    }

    @Override
    public AnimePage findExactAnime(String animeName) throws AnimeScraperException, FailedToScrapeException {
        final String url = ANIDB_URL + "/anime/?adb.search=%s&do.update=Search&fullmatch=1&noalias=1".formatted(URLEncoder.encode(animeName, StandardCharsets.UTF_8));

        // this search query from anidb returns a redirect to the anime page if there is an exact match,
        // so we need to get the href from the redirect
        final String pageHtml;
        try {
            pageHtml = doRequest(url, true);
        } catch (IOException | InterruptedException e) {
            throw new FailedToScrapeException("Anime not found");
        }

        final Document document = Jsoup.parse(pageHtml);

        if (!document.title().equalsIgnoreCase("302 Found"))
            throw new AnimeScraperException("Anime not found");

        final Element body = document.body();

        final Element link = body.getElementsByTag("a").first();

        if (link == null)
            throw new AnimeScraperException("Redirect link not found");

        final String redirect = link.attr("href");

        long id;
        try {
            id = Long.parseLong(redirect.substring(redirect.lastIndexOf("/") + 1));
        } catch (NumberFormatException e) {
            throw new AnimeScraperException("Failed to parse anime id");
        }

        return getAnimePageById(id);
    }

    @Override
    public SearchPage popularAnimes() throws AnimeScraperException, FailedToScrapeException {
        final String url = ANIDB_URL + "/latest/anime/popular/?do.update=1&epp=100&tvseries=1&tvspecial=1";
        final String pageHtml;
        try {
            pageHtml = doRequest(url, false);
        } catch (IOException | InterruptedException e) {
            throw new FailedToScrapeException("Failed to get popular animes", e);
        }
        final Element body = Jsoup.parse(pageHtml).body();

        final List<Element> trs = getAniDbAnimeList(body);

        ArrayList<PageResult> pageResults = new ArrayList<>(trs.size());
        for (Element tr : trs) {

            final Element thumb = tr.getElementsByClass("thumb anime").first();

            assert thumb != null;

            final Element a = thumb.getElementsByTag("a").first();

            assert a != null;

            final String id = a.attr("href");

            final long idLong = Long.parseLong(getAniDbId(id));

            final Element img = a.getElementsByTag("img").first();

            String imageThumbId = img.attr("src");

            imageThumbId = imageThumbId.substring(imageThumbId.lastIndexOf("/") + 1);
            imageThumbId = imageThumbId.substring(0, imageThumbId.indexOf("."));

            String imageUrl = "https://cdn-eu.anidb.net/images/main/%s.jpg".formatted(imageThumbId);

            final Element name = tr.getElementsByClass("name anime").first();
            final Element rating = tr.getElementsByAttributeValue("data-label", "Rating").first();

            String nameStr = name != null ? name.text() : "";
            String ratingStr = rating != null ? rating.text() : "";

            Double ratingValue;
            try {
                ratingStr = ratingStr.substring(0, ratingStr.indexOf("("));
                final String[] ratingSplit = ratingStr.split(" ");
                ratingValue = ratingSplit[0].equals("N/A") ? null : Double.parseDouble(ratingSplit[0]);
            } catch (Exception e) {
                ratingValue = null;
            }

            pageResults.add(new PageResult(idLong, nameStr, imageUrl, ratingValue, null, null, null, null));
        }

        return new SearchPage(pageResults.toArray(PageResult[]::new), 0, false);
    }

}
