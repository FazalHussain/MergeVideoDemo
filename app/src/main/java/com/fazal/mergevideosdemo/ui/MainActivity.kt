package com.fazal.mergevideosdemo.ui

import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fazal.mergevideosdemo.BaseApplication
import com.fazal.mergevideosdemo.R
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import javax.inject.Inject

/**
 * Main Activity
 */
class MainActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory : ViewModelProvider.Factory

    lateinit var viewModel: VideoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(VideoViewModel::class.java)

        mergeVideos()
        subscribeObservers()
        setBlurFilterClick()
        setNoFilterClick()
        setContrasFilterClick()
    }

    /**
     * Set No Filter Click Listener
     */
    private fun setNoFilterClick() {
        no_filters.setOnClickListener { playVideo("$cacheDir/outputs.mp4") }
    }

    /**
     * Set Blur Filter Click Listener
     */
    private fun setBlurFilterClick() {
        blur_filters.setOnClickListener {
            val blurFile = File("${cacheDir}/outputs_blur.mp4")
            // Check if blur file exists play the video with blur filter
            if (blurFile.exists()) {
                playVideo(blurFile.path)
            }
        }
    }

    /**
     * Set Contras Filter Click Listener
     */
    private fun setContrasFilterClick() {
        contras_filters.setOnClickListener {
            val contrasFile = File("${cacheDir}/outputs_contras.mp4")
            // Check if contras file exists play the video with contras filter
            if (contrasFile.exists()) {
                playVideo(contrasFile.path)
            }
        }
    }

    /**
     * Subscribe the observer to listen the changes in livedata and update the UI
     */
    private fun subscribeObservers() {
        viewModel.isVideoMerged.observe(this, Observer {isVideoMerged ->
            if (isVideoMerged) {
                playVideo("$cacheDir/outputs.mp4")
            }
        })

        viewModel.isVideoBlured.observe(this, Observer {isVideoBlured ->
            blur_filters.visibility = if (isVideoBlured) View.VISIBLE else View.INVISIBLE
            if (isVideoBlured) {
                playVideo("$cacheDir/outputs_blur.mp4")
            }
        })

        viewModel.isVideoColorAdjusted.observe(this, Observer {isVideoColorAdjusted ->
            contras_filters.visibility = if (isVideoColorAdjusted) View.VISIBLE else View.INVISIBLE
            if (isVideoColorAdjusted) {
                playVideo("$cacheDir/outputs_contras.mp4")
            }
        })
    }

    /**
     * Play the video with the given URL
     *
     * @param url The path of video
     */
    private fun playVideo(url: String) {
        videoView.visibility = View.VISIBLE
        val video: Uri = Uri.parse(url)
        videoView.setVideoURI(video)
        videoView.setOnPreparedListener{ mp ->
            mp.isLooping = true
            videoView.start()
        }

    }

    /**
     * Merge the video using Ffmpeg
     */
    private fun mergeVideos() {
        // Write video from raw folder to our cache directory
        viewModel.writeVideo(R.raw.video1, "video1.mp4")
        viewModel.writeVideo(R.raw.video2, "video2.mp4")
        val videoURL1 = "$cacheDir/video1.mp4"
        val videoURL2 = "$cacheDir/video2.mp4"
        // Check if cache directory has merged video. If merged video file not exisits then
        // Merge the video file other wise just play it
        val outputFile = File("${cacheDir}/outputs.mp4")
        if (!outputFile.exists()) {
            blur_filters.visibility = View.INVISIBLE
            contras_filters.visibility = View.INVISIBLE
            viewModel.mergeVideoFiles(videoURL1, videoURL2)
        } else {
            playVideo(outputFile.path)
        }
    }

    override fun inject() {
        (application as BaseApplication).mainComponent()
            .inject(this)
    }
}