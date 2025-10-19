package pl.jojczak.penmouses.screen.manual

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ManualViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _state: MutableStateFlow<ManualViewState> = MutableStateFlow(ManualViewState())
    val state: StateFlow<ManualViewState> = _state.asStateFlow()

    init {
        loadManualPage(state.value.page)
    }

    fun onViewAction(viewAction: ManualAction) = when (viewAction) {
        is ManualUserAction.ChangeScreen -> loadManualPage(viewAction.page)
    }

    private fun loadManualPage(pageType: ManualPageType) {
        val pageContent = context
            .resources
            .openRawResource(pageType.fileId)
            .bufferedReader()
            .use { it.readText() }

        _state.update {
            ManualViewState(
                page = pageType,
                markdownContent = pageContent
            )
        }
    }
}