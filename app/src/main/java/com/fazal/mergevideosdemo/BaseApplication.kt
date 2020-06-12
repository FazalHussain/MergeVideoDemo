package com.fazal.mergevideosdemo

import android.app.Application
import com.fazal.mergevideosdemo.main.MainComponent

class BaseApplication : Application() {

    lateinit var appComponent: AppComponent

    private var mainComponent: MainComponent? = null

    override fun onCreate() {
        super.onCreate()
        initAppComponent()
    }

    fun initAppComponent(){
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
    }

    /**
     * Release The Main Component When Main Activity Destroy
     */
    fun releaseMainComponent(){
        mainComponent = null
    }

    /**
     * Create the main component if it is not created
     *
     * @return The [MainComponent] instance
     */
    fun mainComponent(): MainComponent {
        if(mainComponent == null){
            mainComponent = appComponent.mainComponent().create()
        }
        return mainComponent as MainComponent
    }
}