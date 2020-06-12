package com.fazal.mergevideosdemo

import android.app.Application
import com.fazal.mergevideosdemo.main.MainComponent
import com.fazal.mergevideosdemo.ui.BaseActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * AppComponent create the depedency graph and it will be live entire lifecycler of application
 */
@Singleton
@Component(
    modules = [AppModule::class, SubComponentsModule::class]
)
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    // Add base activity in dependency graph to provide dependency
    fun inject(baseActivity: BaseActivity)

    // Save the reference point of the sub component factory  and used to create Main Component at runtime
    fun mainComponent() : MainComponent.Factory
}