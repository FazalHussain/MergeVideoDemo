package com.fazal.mergevideosdemo.repo

import android.app.Application
import com.fazal.mergevideosdemo.R
import nl.bravobit.ffmpeg.FFmpeg
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject

/**
 * Video Merge Repository
 *
 * @param application The application instance that we are providing
 * at the time of creation of an app component
 */
class VideoMergeRepo @Inject constructor(val application: Application) {

    /**
     * Fetch the instance of [FFmpeg]
     *
     * @return The [FFmpeg] instance
     */
    fun getFFMPEGInstance(): FFmpeg? {
        return FFmpeg.getInstance(application)
    }

    /**
     * Fetch FFMPEG Video Merging Command
     *
     * @param video1Path the path of video1
     * @param video2Path the path of video2
     *
     * @return the [FFmpeg] Video Merge command array
     */
    fun getFFMPEGCommand(video1Path: String, video2Path: String): Array<String> {
        return arrayOf(

            "-i", video1Path, "-i", video2Path,
            "-filter_complex",
            "[0:v]pad=iw*2:ih[int];[int][1:v]overlay=W/2:0[vid]",
            "-map", "[vid]",
            "-c:v", "libx264",
            "-crf", "23",
            "-preset", "veryfast",
            "${application.cacheDir}/outputs.mp4"


            /*"-i", video1Path, "-i", video2Path,
            "-filter_complex",
            "[0:v]pad=iw*2:ih[int];" +
                    "[int][1:v]overlay=W/2:0[vid]",
            "-map", "[vid]",
            "-c:v", "libx264", "-crf", "23", "/data/user/0/com.fazal.mergevideosdemo/cache/output.mp4"*/
        )
    }

    /**
     * Write Video in cache directory
     *
     * @param id The raw id of video
     * @param name The file name
     */
    fun writeVideo(id: Int, name: String) {
        val fileName = "${application.cacheDir}/${name}"
        val file = File(fileName)
        if (file.exists()) return
        val `in`: InputStream = application.resources.openRawResource(id)
        val out = FileOutputStream(fileName)
        val buff = ByteArray(1024)
        var read = 0

        try {
            while (`in`.read(buff).also { read = it } > 0) {
                out.write(buff, 0, read)
            }
        } finally {
            `in`.close()
            out.close()
        }
    }


}