package com.fazal.mergevideosdemo.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fazal.mergevideosdemo.repo.VideoMergeRepo
import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler
import nl.bravobit.ffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import javax.inject.Inject

/**
 * Video View Model Class which is lifecycle aware
 *
 * @param application The application instance privided by dagger
 * @param videoMergeRepo The [VideoMergeRepo] instance provide by dagger
 */
class VideoViewModel
@Inject constructor(val application: Application,
                    val videoMergeRepo: VideoMergeRepo) : ViewModel() {

    private val _merged_video = MutableLiveData<Boolean>()

    val isVideoMerged: LiveData<Boolean>
        get() = _merged_video

    /**
     * Merge the video file
     *
     * @param video1Path The path of first video that is going to merge
     * @param video2Path The path of second video that is going to merge
     *
     */
    fun mergeVideoFiles(video1Path: String, video2Path: String) {
        val ffmpeg = videoMergeRepo.getFFMPEGInstance()
        if (ffmpeg?.isSupported!!) {
            try {
                val cmd = videoMergeRepo.getFFMPEGCommand(video1Path, video2Path)
                // to execute "ffmpeg -version" command you just need to pass "-version"
                ffmpeg.execute(cmd, object : ExecuteBinaryResponseHandler() {
                    override fun onStart() {

                    }
                    override fun onProgress(message: String) {

                    }
                    override fun onFailure(message: String) {
                        setMergedVideoValue(false)
                        println("VideoViewModel: merge failed")
                    }
                    override fun onSuccess(message: String) {
                        setMergedVideoValue(true)
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

    /**
     * Write Video
     *
     * @param rawVideoID The video id that is in the raw folder
     * @param filename The filename of the video
     */
    fun writeVideo(rawVideoID: Int, filename: String) {
        videoMergeRepo.writeVideo(rawVideoID, filename)
    }

    /**
     * Set the Merged Video [MutableLiveData] value
     *
     * There is one problem with Live Data and it is the known issue
     *
     * Problem:
     *
     * LiveData publishes the data to the destination component if it is in the foreground.
     * If it’s not, it might hold the data and deliver it when that specific component comes back
     * to the foreground, like in onResume state.
     *
     * If your live data subscribe in multiple fragment you will face issues to avoid this
     * check the new value and previus value is same return from the function
     *
     */
    private fun setMergedVideoValue(isSupport: Boolean) {
        if (_merged_video.value == isSupport) {
            return
        }

        _merged_video.value = isSupport
    }


}