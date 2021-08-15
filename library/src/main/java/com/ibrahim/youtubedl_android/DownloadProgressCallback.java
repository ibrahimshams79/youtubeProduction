package com.ibrahim.youtubedl_android;

public interface DownloadProgressCallback {
    void onProgressUpdate(float progress, long etaInSeconds);
}
