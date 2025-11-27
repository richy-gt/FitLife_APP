package com.example.fitlifeapp.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.fitlifeapp.data.remote.NutritionApiService
import com.example.fitlifeapp.data.remote.dto.Nutrient
import com.example.fitlifeapp.data.remote.dto.NutritionResponse
import com.example.fitlifeapp.data.remote.dto.TotalNutrients
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class NutritionViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: NutritionViewModel
    private lateinit var mockApiService: NutritionApiService
    private lateinit var mockApplication: Application

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockApiService = mockk()
        mockApplication = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createMockNutritionResponse(calories: Int = 165): NutritionResponse {
        return NutritionResponse(
            calories = calories,
            totalWeight = 100.0,
            totalNutrients = TotalNutrients(
                energyKcal = Nutrient("Energy", calories.toDouble(), "kcal"),
                protein = Nutrient("Protein", 31.0, "g"),
                fat = Nutrient("Fat", 3.6, "g"),
                carbs = Nutrient("Carbs", 0.0, "g"),
                fiber = Nutrient("Fiber", 0.0, "g")
            ),
            healthLabels = listOf("Low-Carb", "High-Protein")
        )
    }

    @Test
    fun `searchFood con query vacío no debe hacer nada`() = runTest(testDispatcher) {
        viewModel = NutritionViewModel(mockApplication, mockApiService)

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)
            assertNull(initialState.nutritionData)

            viewModel.searchFood("")
            advanceUntilIdle()

            // No debe haber cambios de estado
            expectNoEvents()
        }
    }

    @Test
    fun `searchFood exitoso debe actualizar estado con datos nutricionales`() = runTest(testDispatcher) {
        val mockResponse = createMockNutritionResponse()

        coEvery {
            mockApiService.getNutritionData(any(), any(), any(), any())
        } returns Response.success(mockResponse)

        viewModel = NutritionViewModel(mockApplication, mockApiService)

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)

            viewModel.searchFood("100g chicken breast")

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.error)

            advanceUntilIdle()

            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertNotNull(successState.nutritionData)
            assertNull(successState.error)

            // Verificar datos con operador Elvis para evitar errores de nullabilidad
            val data = successState.nutritionData
            assertNotNull(data)
            assertEquals(165, data?.calories ?: 0)
            assertEquals(31.0, data?.protein ?: 0.0, 0.1)
            assertEquals(3.6, data?.fat ?: 0.0, 0.1)
            assertEquals(0.0, data?.carbs ?: 0.0, 0.1)
        }
    }

    @Test
    fun `searchFood con error 404 debe mostrar mensaje de error`() = runTest(testDispatcher) {
        val httpException = HttpException(
            Response.error<NutritionResponse>(
                404,
                "Not Found".toResponseBody()
            )
        )

        coEvery {
            mockApiService.getNutritionData(any(), any(), any(), any())
        } throws httpException

        viewModel = NutritionViewModel(mockApplication, mockApiService)

        viewModel.uiState.test {
            awaitItem() // Estado inicial

            viewModel.searchFood("invalid food")

            awaitItem() // Estado loading
            advanceUntilIdle()

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertNull(errorState.nutritionData)
            assertNotNull(errorState.error)
            assertTrue(errorState.error!!.contains("Error"))
        }
    }

    @Test
    fun `searchFood sin conexión debe mostrar error de red`() = runTest(testDispatcher) {
        coEvery {
            mockApiService.getNutritionData(any(), any(), any(), any())
        } throws IOException("Network error")

        viewModel = NutritionViewModel(mockApplication, mockApiService)

        viewModel.uiState.test {
            awaitItem()

            viewModel.searchFood("chicken")

            awaitItem() // loading
            advanceUntilIdle()

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertNotNull(errorState.error)
        }
    }

    @Test
    fun `searchFood con respuesta vacía debe manejar error`() = runTest(testDispatcher) {
        coEvery {
            mockApiService.getNutritionData(any(), any(), any(), any())
        } returns Response.error(400, "Bad Request".toResponseBody())

        viewModel = NutritionViewModel(mockApplication, mockApiService)

        viewModel.uiState.test {
            awaitItem()

            viewModel.searchFood("test food")

            awaitItem() // loading
            advanceUntilIdle()

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertNotNull(errorState.error)
            assertTrue(errorState.error!!.contains("No se encontró información"))
        }
    }

    @Test
    fun `múltiples búsquedas deben actualizar el estado correctamente`() = runTest(testDispatcher) {
        val mockResponse1 = createMockNutritionResponse(165)
        val mockResponse2 = createMockNutritionResponse(200)

        coEvery {
            mockApiService.getNutritionData(any(), any(), any(), "100g chicken breast")
        } returns Response.success(mockResponse1)

        coEvery {
            mockApiService.getNutritionData(any(), any(), any(), "1 cup rice")
        } returns Response.success(mockResponse2)

        viewModel = NutritionViewModel(mockApplication, mockApiService)

        viewModel.uiState.test {
            awaitItem() // inicial

            // Primera búsqueda
            viewModel.searchFood("100g chicken breast")
            awaitItem() // loading
            advanceUntilIdle()
            val state1 = awaitItem()
            assertEquals(165, state1.nutritionData?.calories ?: 0)

            // Segunda búsqueda
            viewModel.searchFood("1 cup rice")
            awaitItem() // loading
            advanceUntilIdle()
            val state2 = awaitItem()
            assertEquals(200, state2.nutritionData?.calories ?: 0)
        }
    }

    @Test
    fun `searchFood con espacios en blanco no debe hacer nada`() = runTest(testDispatcher) {
        viewModel = NutritionViewModel(mockApplication, mockApiService)

        viewModel.uiState.test {
            val initialState = awaitItem()

            viewModel.searchFood("   ")
            advanceUntilIdle()

            // No debe haber cambios
            expectNoEvents()
        }
    }

    @Test
    fun `estado inicial debe ser correcto`() = runTest(testDispatcher) {
        viewModel = NutritionViewModel(mockApplication, mockApiService)

        viewModel.uiState.test {
            val state = awaitItem()

            assertFalse(state.isLoading)
            assertNull(state.nutritionData)
            assertNull(state.error)
        }
    }
}