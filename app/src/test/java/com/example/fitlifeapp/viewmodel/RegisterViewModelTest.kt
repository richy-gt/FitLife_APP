package com.example.fitlifeapp.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.fitlifeapp.data.local.SessionManager
import com.example.fitlifeapp.data.local.UserPreferences
import com.example.fitlifeapp.data.remote.ApiService
import com.example.fitlifeapp.data.remote.dto.RegisterRequest
import com.example.fitlifeapp.data.remote.dto.RegisterResponse
import com.example.fitlifeapp.data.remote.dto.RegisteredUser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
class RegisterViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: RegisterViewModel
    private lateinit var mockApiService: ApiService
    private lateinit var mockSessionManager: SessionManager
    private lateinit var mockUserPreferences: UserPreferences
    private lateinit var mockApplication: Application

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockApiService = mockk()
        mockSessionManager = mockk(relaxed = true)
        mockUserPreferences = mockk(relaxed = true)
        mockApplication = mockk(relaxed = true)

        viewModel = RegisterViewModel(
            app = mockApplication,
            apiService = mockApiService,
            sessionManager = mockSessionManager,
            userPreferences = mockUserPreferences
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `register con campos vacíos debe mostrar error`() = runTest {
        viewModel.register("", "")
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Por favor completa todos los campos", state.errorMessage)
            assertFalse(state.isSuccess)
        }
    }

    @Test
    fun `register exitoso debe actualizar estado y guardar datos`() = runTest {
        val request = RegisterRequest("test@example.com", "password123", "Test User")
        val response = RegisterResponse("Success", RegisteredUser("1", "Test User", "test@example.com"), "token")

        coEvery { mockApiService.register(request) } returns Response.success(response)

        viewModel.register(request.email, request.password, request.name)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isSuccess)
            assertNull(state.errorMessage)
            assertFalse(state.isLoading)
        }

        coVerify { mockSessionManager.saveToken("token") }
        coVerify { mockUserPreferences.saveUserEmail("test@example.com") }
        coVerify { mockUserPreferences.saveLoginState(true) }
    }

    @Test
    fun `register con error 400 debe mostrar mensaje de correo ya registrado`() = runTest {
        val request = RegisterRequest("test@example.com", "password123", "Test User")
        val httpException = HttpException(Response.error<RegisterResponse>(400, "Bad Request".toResponseBody()))

        coEvery { mockApiService.register(request) } throws httpException

        viewModel.register(request.email, request.password, request.name)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Correo ya registrado o datos inválidos", state.errorMessage)
            assertFalse(state.isSuccess)
        }
    }

    @Test
    fun `register con error de red debe mostrar mensaje de sin conexión`() = runTest {
        val request = RegisterRequest("test@example.com", "password123", "Test User")
        coEvery { mockApiService.register(request) } throws IOException()

        viewModel.register(request.email, request.password, request.name)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Sin conexión a Internet", state.errorMessage)
        }
    }

    @Test
    fun `register con error 500 debe mostrar mensaje de error del servidor`() = runTest {
        val request = RegisterRequest("test@example.com", "password123", "Test User")
        val httpException = HttpException(
            Response.error<RegisterResponse>(
                500,
                "Internal Server Error".toResponseBody()
            )
        )

        coEvery { mockApiService.register(request) } throws httpException

        viewModel.register(request.email, request.password, request.name)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Error en el servidor", state.errorMessage)
            assertFalse(state.isSuccess)
            assertFalse(state.isLoading)
        }

        coVerify(exactly = 0) { mockSessionManager.saveToken(any()) }
        coVerify(exactly = 0) { mockUserPreferences.saveLoginState(true) }
    }

    @Test
    fun `register con respuesta exitosa pero sin token debe manejar error`() = runTest {
        val request = RegisterRequest("test@example.com", "password123", "Test User")
        val response = RegisterResponse(
            message = "Success",
            user = RegisteredUser("1", "Test User", "test@example.com"),
            token = null
        )

        coEvery { mockApiService.register(request) } returns Response.success(response)

        viewModel.register(request.email, request.password, request.name)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isSuccess)
            assertFalse(state.isLoading)
        }

        coVerify { mockSessionManager.saveToken("") }
        coVerify { mockUserPreferences.saveUserEmail("test@example.com") }
        coVerify { mockUserPreferences.saveLoginState(true) }
    }
}
