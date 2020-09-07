package com.pratham.foundation.services.youtube_extractor;

import android.util.SparseArray;

public class VideoMeta {

    private static final String IMAGE_BASE_URL = "http://i.ytimg.com/vi/";

    private String videoId;
    private String title;
    private String shortDescript;

    private String author;
    private String channelId;

    private long videoLength;
    private long viewCount;

    private boolean isLiveStream;

    protected VideoMeta(String videoId, String title, String author, String channelId,
                        long videoLength, long viewCount, boolean isLiveStream, String shortDescript) {
        this.videoId = videoId;
        this.title = title;
        this.author = author;
        this.channelId = channelId;
        this.videoLength = videoLength;
        this.viewCount = viewCount;
        this.isLiveStream = isLiveStream;
        this.shortDescript = shortDescript;
    }

    // 120 x 90
    public String getThumbUrl() {
        return IMAGE_BASE_URL + videoId + "/default.jpg";
    }

    // 320 x 180
    public String getMqImageUrl() {
        return IMAGE_BASE_URL + videoId + "/mqdefault.jpg";
    }

    // 480 x 360
    public String getHqImageUrl() {
        return IMAGE_BASE_URL + videoId + "/hqdefault.jpg";
    }

    // 640 x 480
    public String getSdImageUrl() {
        return IMAGE_BASE_URL + videoId + "/sddefault.jpg";
    }

    // Max Res
    public String getMaxResImageUrl() {
        return IMAGE_BASE_URL + videoId + "/maxresdefault.jpg";
    }

    public String getVideoId() {
        return videoId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getChannelId() {
        return channelId;
    }

    public boolean isLiveStream() {
        return isLiveStream;
    }

    /**
     * The video length in seconds.
     */
    public long getVideoLength() {
        return videoLength;
    }

    public long getViewCount() {
        return viewCount;
    }

    public String getShortDescription() {
        return shortDescript;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VideoMeta videoMeta = (VideoMeta) o;

        if (videoLength != videoMeta.videoLength) return false;
        if (viewCount != videoMeta.viewCount) return false;
        if (isLiveStream != videoMeta.isLiveStream) return false;
        if (videoId != null ? !videoId.equals(videoMeta.videoId) : videoMeta.videoId != null)
            return false;
        if (title != null ? !title.equals(videoMeta.title) : videoMeta.title != null) return false;
        if (author != null ? !author.equals(videoMeta.author) : videoMeta.author != null)
            return false;
        return channelId != null ? channelId.equals(videoMeta.channelId) : videoMeta.channelId == null;

    }

    @Override
    public int hashCode() {
        int result = videoId != null ? videoId.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (channelId != null ? channelId.hashCode() : 0);
        result = 31 * result + (int) (videoLength ^ (videoLength >>> 32));
        result = 31 * result + (int) (viewCount ^ (viewCount >>> 32));
        result = 31 * result + (isLiveStream ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VideoMeta{" +
                "videoId='" + videoId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", channelId='" + channelId + '\'' +
                ", videoLength=" + videoLength +
                ", viewCount=" + viewCount +
                ", isLiveStream=" + isLiveStream +
                '}';
    }

    private static final int YOUTUBE_ITAG_251 = 251;    // webm - stereo, 48 KHz 160 Kbps (opus)
    private static final int YOUTUBE_ITAG_250 = 250;    // webm - stereo, 48 KHz 64 Kbps (opus)
    private static final int YOUTUBE_ITAG_249 = 249;    // webm - stereo, 48 KHz 48 Kbps (opus)
    private static final int YOUTUBE_ITAG_171 = 171;    // webm - stereo, 48 KHz 128 Kbps (vortis)
    private static final int YOUTUBE_ITAG_141 = 141;    // mp4a - stereo, 44.1 KHz 256 Kbps (aac)
    private static final int YOUTUBE_ITAG_140 = 140;    // mp4a - stereo, 44.1 KHz 128 Kbps (aac)
    private static final int YOUTUBE_ITAG_43 = 43;      // webm - stereo, 44.1 KHz 128 Kbps (vortis)
    private static final int YOUTUBE_ITAG_22 = 22;      // mp4 - stereo, 44.1 KHz 192 Kbps (aac)
    private static final int YOUTUBE_ITAG_18 = 18;      // mp4 - stereo, 44.1 KHz 96 Kbps (aac)
    private static final int YOUTUBE_ITAG_36 = 36;      // mp4 - stereo, 44.1 KHz 32 Kbps (aac)
    private static final int YOUTUBE_ITAG_17 = 17;      // mp4 - stereo, 44.1 KHz 24 Kbps (aac)

    public YtFile getBestStream(SparseArray<YtFile> ytFiles) {
//        Log.e(TAG, "ytFiles: " + ytFiles);
        if (ytFiles.get(YOUTUBE_ITAG_141) != null) {
            return ytFiles.get(YOUTUBE_ITAG_141);
        } else if (ytFiles.get(YOUTUBE_ITAG_140) != null) {
            return ytFiles.get(YOUTUBE_ITAG_140);
        } else if (ytFiles.get(YOUTUBE_ITAG_251) != null) {
            return ytFiles.get(YOUTUBE_ITAG_251);
        } else if (ytFiles.get(YOUTUBE_ITAG_250) != null) {
            return ytFiles.get(YOUTUBE_ITAG_250);
        } else if (ytFiles.get(YOUTUBE_ITAG_249) != null) {
            return ytFiles.get(YOUTUBE_ITAG_249);
        } else if (ytFiles.get(YOUTUBE_ITAG_171) != null) {
            return ytFiles.get(YOUTUBE_ITAG_171);
        } else if (ytFiles.get(YOUTUBE_ITAG_18) != null) {
            return ytFiles.get(YOUTUBE_ITAG_18);
        } else if (ytFiles.get(YOUTUBE_ITAG_22) != null) {
            return ytFiles.get(YOUTUBE_ITAG_22);
        } else if (ytFiles.get(YOUTUBE_ITAG_43) != null) {
            return ytFiles.get(YOUTUBE_ITAG_43);
        } else if (ytFiles.get(YOUTUBE_ITAG_36) != null) {
            return ytFiles.get(YOUTUBE_ITAG_36);
        }
        return ytFiles.get(YOUTUBE_ITAG_17);
    }
}