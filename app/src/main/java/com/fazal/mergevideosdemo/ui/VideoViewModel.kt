package com.fazal.mergevideosdemo.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fazal.mergevideosdemo.R
import com.fazal.mergevideosdemo.repo.VideoRepo
import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler
import nl.bravobit.ffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject


class VideoViewModel @Inject constructor(val application: Application, val videoRepo: VideoRepo) : ViewModel() {

    private val _merged_video = MutableLiveData<Boolean>()

    val isVideoMerged: LiveData<Boolean>
        get() = _merged_video

    fun mergeVideoFiles(video1Path: String, video2Path: String) {
        val ffmpeg = videoRepo.getFFMPEGInstance()
        if (ffmpeg?.isSupported!!) {
            try {
                val cmd = videoRepo.getFFMPEGCommand(video1Path, video2Path)
                // to execute "ffmpeg -version" command you just need to pass "-version"
                ffmpeg.execute(cmd, object : ExecuteBinaryResponseHandler() {
                    override fun onStart() {

                    }
                    override fun onProgress(message: String) {

                    }
                    override fun onFailure(message: String) {
                        setFmpegSupportValue(false)
                        println("VideoViewModel: merge failed")
                    }
                    override fun onSuccess(message: String) {
                        setFmpegSupportValue(true)
                        println("VideoViewModel: merge success")
                    }
                    override fun onFinish() {

                    }
                })
            } catch (e: FFmpegCommandAlreadyRunningException) {
                // Handle if FFmpeg is already running
            }
        } else {
            // ffmpeg is not supported
        }
    }

    fun writeVideo(rawVideoID: Int, filename: String) {
        videoRepo.writeVideo(rawVideoID, filename)
    }

    private fun setFmpegSupportValue(isSupport: Boolean) {
        if (_merged_video.value == isSupport) {
            return
        }

        _merged_video.value = isSupport
    }


}