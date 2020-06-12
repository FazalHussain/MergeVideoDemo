package com.fazal.mergevideosdemo.main

import com.fazal.mergevideosdemo.ui.MainActivity
import dagger.Subcomponent

/**
 * Main Sub Component
 *  annotated with [MainScope] so that it will be live on auth scope
 *
 *  [Subcomponent.Factory] is work like the builder pattern but it is more efficient
 */
@MainScope
@Subcomponent(
    modules = [
        MainModule::class,
        MainViewModelModule::class,
        MainFragmentsModule::class
    ])
interface MainComponent {

    @Subcomponent.Factory
    interface Factory {

        fun create() : MainComponent
    }

    // Add Main activity in dependecy graph to provide dependency
    fun inject(mainActivity: MainActivity)
}