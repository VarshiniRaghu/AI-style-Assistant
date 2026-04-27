package com.smartstyle.ui.screens.history

import com.smartstyle.data.repository.OutfitRepository
import com.smartstyle.domain.model.OutfitAnalysis
import com.smartstyle.util.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: OutfitRepository
    private lateinit var viewModel: HistoryViewModel

    private val fakeHistory = listOf(
        OutfitAnalysis(
            id = 1L,
            imageUri = "/data/analyses/1.jpg",
            assessment = "Stylish and modern.",
            suggestions = listOf("Add accessories"),
            tags = listOf("casual", "summer"),
            mlKitLabels = listOf("Shirt"),
            timestamp = 2000L
        ),
        OutfitAnalysis(
            id = 2L,
            imageUri = "/data/analyses/2.jpg",
            assessment = "Classic formal look.",
            suggestions = listOf("Try a bolder tie"),
            tags = listOf("formal", "black"),
            mlKitLabels = listOf("Jacket"),
            timestamp = 1000L
        )
    )

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        every { repository.getHistory() } returns flowOf(fakeHistory)
        viewModel = HistoryViewModel(repository)
    }

    @Test
    fun history_emitsRepositoryData() = runTest {
        assertEquals(fakeHistory, viewModel.history.value)
    }

    @Test
    fun history_emptyWhenRepositoryReturnsEmpty() {
        every { repository.getHistory() } returns flowOf(emptyList())
        viewModel = HistoryViewModel(repository)
        assertTrue(viewModel.history.value.isEmpty())
    }

    @Test
    fun deleteAnalysis_callsRepositoryWithCorrectId() = runTest {
        viewModel.deleteAnalysis(1L)
        coVerify { repository.deleteAnalysis(1L) }
    }

    @Test
    fun deleteAnalysis_differentIds_eachDelegatedToRepository() = runTest {
        viewModel.deleteAnalysis(1L)
        viewModel.deleteAnalysis(2L)
        coVerify { repository.deleteAnalysis(1L) }
        coVerify { repository.deleteAnalysis(2L) }
    }
}
