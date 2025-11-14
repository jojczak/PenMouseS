package pl.jojczak.penmouses.screen.manual

import android.content.Context
import androidx.lifecycle.ViewModel
import com.halilibo.richtext.commonmark.CommonmarkAstNodeParser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pl.jojczak.penmouses.core.common.types.ManualPageType
import java.io.BufferedReader
import javax.inject.Inject

@HiltViewModel
class ManualViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _state: MutableStateFlow<ManualViewState> = MutableStateFlow(ManualViewState())
    val state: StateFlow<ManualViewState> = _state.asStateFlow()

    fun onViewAction(viewAction: ManualAction) = when (viewAction) {
        is ManualUserAction.ChangePage -> loadManualPage(viewAction.page, viewAction.isDarkMode)
    }

    private fun loadManualPage(
        pageType: ManualPageType,
        isDarkMode: Boolean
    ) {
        val pageContent = context.resources
            .openRawResource(manualPageData.getValue(pageType).fileId)
            .bufferedReader()
            .use(BufferedReader::readText)
            .replace(THEME_PLACEHOLDER, if (isDarkMode) THEME_DARK else THEME_LIGHT)

        val markdownNode = CommonmarkAstNodeParser().parse(pageContent)

        _state.update {
            ManualViewState(
                page = pageType,
                markdownNode = markdownNode
            )
        }
    }

    companion object {
        private const val THEME_PLACEHOLDER = "{theme}"
        private const val THEME_LIGHT = "light"
        private const val THEME_DARK = "dark"
    }
}