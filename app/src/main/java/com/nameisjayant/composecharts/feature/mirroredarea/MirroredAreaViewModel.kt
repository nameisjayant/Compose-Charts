package com.nameisjayant.composecharts.feature.mirroredarea

import com.nameisjayant.composecharts.core.mvi.MviViewModel
import com.nameisjayant.composecharts.data.MirroredAreaSampleData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MirroredAreaViewModel @Inject constructor() :
    MviViewModel<MirroredAreaState, MirroredAreaIntent, MirroredAreaEffect>(MirroredAreaState()) {

    override fun handleIntent(intent: MirroredAreaIntent) {
        when (intent) {
            MirroredAreaIntent.Shuffle -> setState {
                MirroredAreaState(
                    upper = MirroredAreaSampleData.randomUpper(Random.Default),
                    lower = MirroredAreaSampleData.randomLower(Random.Default),
                    intensityPercent = MirroredAreaSampleData.randomIntensity(Random.Default),
                )
            }
            MirroredAreaIntent.Reset -> setState { MirroredAreaState() }
        }
    }
}
