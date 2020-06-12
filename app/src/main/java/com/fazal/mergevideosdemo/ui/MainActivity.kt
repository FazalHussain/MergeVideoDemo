package com.fazal.mergevideosdemo.ui

import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fazal.mergevideosdemo.BaseApplication
import com.fazal.mergevideosdemo.R
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import javax.inject.Inject


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
        //playVideo()
    }

    private fun subscribeObservers() {
        viewModel.isVideoMerged.removeObservers(this)
        viewModel.isVideoMerged.observe(this, Observer {isVideoMerged ->
            if (isVideoMerged) {
                playVideo()
            }
        })
    }

    private fun playVideo() {
        val video: Uri =
            Uri.parse("$cacheDir/outputs.mp4")
        videoView.setVideoURI(video)
        videoView.setOnPreparedListener{ mp ->
            mp.isLooping = true
            videoView.start()
        }

    }

    private fun mergeVideos() {
        val videoURL1 = "$cacheDir/video1.mp4"
        val videoURL2 = "$cacheDir/video2.mp4"
        val outputFile = File("${cacheDir}/outputs.mp4")
        if (!outputFile.exists()) {
            viewModel.mergeVideoFiles(videoURL1, videoURL2)
        } else {
            playVideo()
        }
    }

    override fun inject() {
        (application as BaseApplication).mainComponent()
            .inject(this)
    }
}