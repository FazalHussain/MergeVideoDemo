package com.fazal.mergevideosdemo.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fazal.mergevideosdemo.repo.VideoRepo
import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler
import nl.bravobit.ffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import java.lang.Exception
import javax.inject.Inject

/**
 * Video View Model Class which is lifecycle aware
 *
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
 * @param application The application instance privided by dagger
 * @param videoRepo The [VideoRepo] instance provide by dagger
 */
class VideoViewModel
@Inject constructor(val application: Application,
                    val videoRepo: VideoRepo) : ViewModel() {

    private val _merged_video = MutableLiveData<Boolean>()

    val isVideoMerged: LiveData<Boolean>
        get() = _merged_video


    private val _video_blured = MutableLiveData<Boolean>()

    val isVideoBlured: LiveData<Boolean>
        get() = _video_blured


    private val _video_color_adjusted = MutableLiveData<Boolean>()

    val isVideoColorAdjusted: LiveData<Boolean>
        get() = _video_color_adjusted

    /**
     * Merge the video file
     *
     * @param video1Path The path of first video that is going to merge
     * @param video2Path The path of second video that is going to merge
     *
     */
    fun mergeVideoFiles(video1Path: String, video2Path: String) {
        val ffmpeg = videoRepo.getFFMPEGInstance()
        if (ffmpeg?.isSupported!!) {
            try {
                val cmd = videoRepo.getFFMPEGMergeCommand(video1Path, video2Path)
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
                        loadFilters()
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
     * Load the filters
     */
    private fun loadFilters() {
        val videoURL = "${application.cacheDir}/outputs.mp4"
        blurVideo(videoURL)
        colorCorrectionVideo(videoURL)
    }

    /**
     * Blur the video
     *
     * @param videoPath the video url.
     */
    fun blurVideo(videoPath: String) {
        val ffmpeg = videoRepo.getFFMPEGInstance()
        if (ffmpeg?.isSupported!!) {
            try {
                val cmd = videoRepo.getBlurFilterCommand(videoPath)
                ffmpeg.execute(cmd, object : ExecuteBinaryResponseHandler() {
                    override fun onStart() {

                    }
                    override fun onProgress(message: String) {

                    }
                    override fun onFailure(message: String) {
                        setVideoBluredValue(false)
                        println("VideoViewModel: video is unable to blured")
                    }
                    override fun onSuccess(message: String) {
                        setVideoBluredValue(true)
                        println("VideoViewModel: video blurred success")
                    }
                    override fun onFinish() {

                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Set the color correction of the video file
     *
     * @param videoPath the video url
     */
    private fun colorCorrectionVideo(videoPath: String) {
        val ffmpeg = videoRepo.getFFMPEGInstance()
        if (ffmpeg?.isSupported!!) {
            try {
                val cmd = videoRepo.getColorCorrectionFilterCommand(videoPath)
                ffmpeg.execute(cmd, object : ExecuteBinaryResponseHandler() {
                    override fun onStart() {

                    }
                    override fun onProgress(message: String) {

                    }
                    override fun onFailure(message: String) {
                        setVideoColorCorrectionValue(false)
                        println("VideoViewModel: video color correction filter is not applied")
                    }
                    override fun onSuccess(message: String) {
                        setVideoColorCorrectionValue(true)
                        println("VideoViewModel: video color correction success")
                    }
                    override fun onFinish() {

                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Write Video
     *
     * @param rawVideoID The video id that is in the raw folder
     * @param filename The filename of the video
     */
    fun writeVideo(rawVideoID: Int, filename: String) {
        videoRepo.writeVideo(rawVideoID, filename)
    }

    /**
     * Set the Merged Video [MutableLiveData] value
     *
     * @param isMerged indicate that video has been merged success or failed
     */
    private fun setMergedVideoValue(isMerged: Boolean) {
        if (_merged_video.value == isMerged) {
            return
        }

        _merged_video.value = isMerged
    }


    /**
     * Set the Blured Video [MutableLiveData] value
     *
     * @param isBlurred indicate that video has been blurred success or failed
     */
    private fun setVideoBluredValue(isBlurred: Boolean) {
        if (_video_blured.value == isBlurred) return

        _video_blured.value = isBlurred
    }


    /**
     * Set the color adjustment Video [MutableLiveData] value
     *
     * @param isMerged indicate that video has been adjusted success or failed
     */
    private fun setVideoColorCorrectionValue(isColorAdjusted: Boolean) {
        if (_video_color_adjusted.value == isColorAdjusted) return

        _video_color_adjusted.value = isColorAdjusted
    }


}