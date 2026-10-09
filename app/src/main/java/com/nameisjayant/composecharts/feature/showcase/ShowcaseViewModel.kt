package com.nameisjayant.composecharts.feature.showcase

import androidx.lifecycle.viewModelScope
import com.nameisjayant.composecharts.core.mvi.MviViewModel
import com.nameisjayant.composecharts.data.ChartSampleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ShowcaseViewModel @Inject constructor(
    private val repository: ChartSampleRepository,
) : MviViewModel<ShowcaseState, ShowcaseIntent, ShowcaseEffect>(ShowcaseState()) {

    init {
        onIntent(ShowcaseIntent.Load)
    }

    override fun handleIntent(intent: ShowcaseIntent) {
        when (intent) {
            ShowcaseIntent.Load -> load()
            is ShowcaseIntent.OpenSample -> sendEffect(ShowcaseEffect.NavigateToSample(intent.id))
        }
    }

    private fun load() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val samples = repository.samples()
            setState { copy(isLoading = false, samples = samples) }
        }
    }
}
