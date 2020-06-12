package com.fazal.mergevideosdemo.repo

import android.app.Application
import com.fazal.mergevideosdemo.R
import nl.bravobit.ffmpeg.FFmpeg
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject

class VideoRepo @Inject constructor(val application: Application) {

    fun getFFMPEGInstance(): FFmpeg? {
        return FFmpeg.getInstance(application)
    }

    fun getFFMPEGCommand(video1Path: String, video2Path: String): Array<String> {
        writeVideo(R.raw.video1, "video1.mp4")
        writeVideo(R.raw.video2, "video2.mp4")
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

    fun writeVideo(id: Int, name: String) {
        val fileName = "${application.cacheDir}/${name}"
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