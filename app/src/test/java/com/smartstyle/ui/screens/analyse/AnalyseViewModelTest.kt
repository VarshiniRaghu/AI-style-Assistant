package com.smartstyle.ui.screens.analyse

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.smartstyle.data.repository.OutfitRepository
import com.smartstyle.domain.model.OutfitAnalysis
import com.smartstyle.util.MainDispatcherRule
import com.smartstyle.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyseViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: OutfitRepository
    private lateinit var context: Context
    private lateinit var viewModel: AnalyseViewModel

    private val fakeBitmap = mockk<Bitmap>(relaxed = true)
    private val fakeUri = mockk<Uri> { io.mockk.every { toString() } returns "content://fake/uri" }

    private val fakeAnalysis = OutfitAnalysis(
        assessment = "Great casual look.",
        suggestions = listOf("Add a belt", "Try lighter shoes"),
        tags = listOf("casual", "blue", "summer"),
        mlKitLabels = listOf("Jeans", "Shirt")
    )

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        context = mockk(relaxed = true)
        viewModel = AnalyseViewModel(repository, context)
    }

    @Test
    fun initialState_isIdle() {
        assertTrue(viewModel.uiState.value is AnalyseUiState.Idle)
    }

    @Test
    fun analyzeOutfit_whenIdle_doesNothing() = runTest {
        viewModel.analyzeOutfit()
        assertTrue(viewModel.uiState.value is AnalyseUiState.Idle)
    }

    @Test
    fun analyzeOutfit_onSuccess_emitsSuccessState() = runTest {
        coEvery { repository.analyzeOutfit(any(), any()) } returns Result.Success(fakeAnalysis)
        viewModel.setState(AnalyseUiState.ImageReady(fakeBitmap, fakeUri))

        viewModel.analyzeOutfit()

        val state = viewModel.uiState.value
        assertTrue(state is AnalyseUiState.Success)
        assertEquals(fakeAnalysis, (state as AnalyseUiState.Success).analysis)
    }

    @Test
    fun analyzeOutfit_onError_emitsErrorState() = runTest {
        coEvery { repository.analyzeOutfit(any(), any()) } returns
                Result.Error(Exception("Network error"))
        viewModel.setState(AnalyseUiState.ImageReady(fakeBitmap, fakeUri))

        viewModel.analyzeOutfit()

        val state = viewModel.uiState.value
        assertTrue(state is AnalyseUiState.Error)
        assertEquals("Network error", (state as AnalyseUiState.Error).message)
    }

    @Test
    fun analyzeOutfit_onErrorWithNoMessage_emitsDefaultMessage() = runTest {
        coEvery { repository.analyzeOutfit(any(), any()) } returns
                Result.Error(Exception())
        viewModel.setState(AnalyseUiState.ImageReady(fakeBitmap, fakeUri))

        viewModel.analyzeOutfit()

        assertEquals("Analysis failed", (viewModel.uiState.value as AnalyseUiState.Error).message)
    }

    @Test
    fun saveAnalysis_callsRepositoryAndMarksSaved() = runTest {
        viewModel.setState(AnalyseUiState.Success(fakeAnalysis, fakeBitmap, saved = false))

        viewModel.saveAnalysis()

        coVerify { repository.saveAnalysis(fakeAnalysis, fakeBitmap) }
        assertTrue((viewModel.uiState.value as AnalyseUiState.Success).saved)
    }

    @Test
    fun saveAnalysis_whenAlreadySaved_doesNotCallRepositoryAgain() = runTest {
        viewModel.setState(AnalyseUiState.Success(fakeAnalysis, fakeBitmap, saved = true))

        viewModel.saveAnalysis()

        coVerify(exactly = 0) { repository.saveAnalysis(any(), any()) }
    }

    @Test
    fun saveAnalysis_whenNotSuccess_doesNothing() = runTest {
        viewModel.saveAnalysis()
        coVerify(exactly = 0) { repository.saveAnalysis(any(), any()) }
    }

    @Test
    fun reset_setsStateToIdle() = runTest {
        viewModel.setState(AnalyseUiState.Success(fakeAnalysis, fakeBitmap))

        viewModel.reset()

        assertTrue(viewModel.uiState.value is AnalyseUiState.Idle)
    }
}
