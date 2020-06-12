package com.fazal.mergevideosdemo.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fazal.mergevideosdemo.di.main.MainViewModelFactory
import com.fazal.mergevideosdemo.ui.VideoViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Main View Model Module Responsible For binding ViewModel & ViewModelFactory
 */
@Module
abstract class MainViewModelModule {

    @MainScope
    @Binds
    abstract fun bindViewModelFactory(factory: MainViewModelFactory): ViewModelProvider.Factory

    @MainScope
    @Binds
    @IntoMap
    @MainViewModelKey(VideoViewModel::class)
    abstract fun bindAuthViewModel(videoViewModel: VideoViewModel): ViewModel

}