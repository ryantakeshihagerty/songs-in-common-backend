package com.songsincommon.songsincommon.models.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class TracksResponse {
    @JsonProperty("href")
    private String href;
    @JsonProperty("items")
    private List<Item> items;
    @JsonProperty("limit")
    private int limit;
    @JsonProperty("next")
    private String next;
    @JsonProperty("offset")
    private int offset;
    @JsonProperty("previous")
    private String previous;
    @JsonProperty("total")
    private int total;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        @JsonProperty("track")
        private Track track;

        public Track getTrack() {
            return track;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Track {
        @JsonProperty("artists")
        private List<Artist> artists;

        @JsonProperty("duration_ms")
        private long durationMillis;

        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("uri")
        private String uri;

        public List<Artist> getArtist() {
            return artists;
        }

        public long getDurationMillis() {
            return durationMillis;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getUri() {
            return uri;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Artist {
        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("uri")
        private String uri;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getUri() {
            return uri;
        }
    }

    public String getHref() {
        return href;
    }

    public List<Item> getItems() {
        return items;
    }

    public int getLimit() {
        return limit;
    }

    public String getNext() {
        return next;
    }

    public int getOffset() {
        return offset;
    }

    public String getPrevious() {
        return previous;
    }

    public int getTotal() {
        return total;
    }
}
