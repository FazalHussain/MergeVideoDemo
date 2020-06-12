package com.fazal.mergevideosdemo

import com.fazal.mergevideosdemo.main.MainComponent
import dagger.Module

/**
 * This class is responsible to create association between n sub component
 */
@Module(
    subcomponents = [
        MainComponent::class
    ])
class SubComponentsModule