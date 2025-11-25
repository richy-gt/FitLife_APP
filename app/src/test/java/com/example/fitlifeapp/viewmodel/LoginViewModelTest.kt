package com.example.fitlifeapp.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.fitlifeapp.data.local.SessionManager
import com.example.fitlifeapp.data.local.UserPreferences
import com.example.fitlifeapp.data.remote.ApiService
import com.example.fitlifeapp.data.remote.dto.LoginRequest
import com.example.fitlifeapp.data.remote.dto.LoginResponse
import com.example.fitlifeapp.data.remote.dto.UserInfo
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: LoginViewModel
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
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createViewModel(): LoginViewModel {

        return LoginViewModel(
            mockApplication,
            mockApiService,
            mockSessionManager,
            mockUserPreferences
        )
    }

    @Test
    fun `login con credenciales vacías debe retornar error`() = runTest(testDispatcher) {
        viewModel = createViewModel()

        viewModel.uiState.test {

            val initialState = awaitItem()
            assertFalse(initialState.isLoading)
            assertNull(initialState.errorMessage)
            assertFalse(initialState.isSuccess)


            viewModel.login("", "password123")
            advanceUntilIdle()


            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("Por favor completa todos los campos", errorState.errorMessage)
            assertFalse(errorState.isSuccess)
        }
    }

    @Test
    fun `login exitoso debe guardar token y actualizar estado`() = runTest(testDispatcher) {
        viewModel = createViewModel()

        val testEmail = "test@example.com"
        val testPassword = "password123"
        val testToken = "test_jwt_token"

        val mockResponse = LoginResponse(
            message = "Login exitoso",
            user = UserInfo(
                id = "user123",
                name = "Test User",
                email = testEmail
            ),
            token = testToken
        )

        coEvery {
            mockApiService.login(LoginRequest(testEmail, testPassword))
        } returns Response.success(mockResponse)

        viewModel.uiState.test {

            val initialState = awaitItem()
            assertFalse(initialState.isLoading)


            viewModel.login(testEmail, testPassword)


            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.errorMessage)
            assertFalse(loadingState.isSuccess)

            advanceUntilIdle()


            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertNull(successState.errorMessage)
            assertTrue(successState.isSuccess)


            coVerify { mockSessionManager.saveToken(testToken) }
            coVerify { mockSessionManager.saveUserEmail(testEmail) }
            coVerify { mockUserPreferences.saveUserPassword(testPassword) }
            coVerify { mockSessionManager.saveLoginState(true) }
        }
    }

    @Test
    fun `login con credenciales incorrectas debe mostrar error 401`() = runTest(testDispatcher) {
        viewModel = createViewModel()

        val testEmail = "wrong@example.com"
        val testPassword = "wrongpassword"

        val httpException = HttpException(
            Response.error<LoginResponse>(
                401,
                "Unauthorized".toResponseBody()
            )
        )

        coEvery {
            mockApiService.login(LoginRequest(testEmail, testPassword))
        } throws httpException

        viewModel.uiState.test {

            awaitItem()


            viewModel.login(testEmail, testPassword)


            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            advanceUntilIdle()


            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("Usuario o contraseña incorrectos", errorState.errorMessage)
            assertFalse(errorState.isSuccess)


            coVerify(exactly = 0) { mockSessionManager.saveToken(any()) }
            coVerify(exactly = 0) { mockSessionManager.saveLoginState(true) }
        }
    }

    @Test
    fun `login sin conexión a internet debe mostrar error de red`() = runTest(testDispatcher) {
        viewModel = createViewModel()

        val testEmail = "test@example.com"
        val testPassword = "password123"

        coEvery {
            mockApiService.login(LoginRequest(testEmail, testPassword))
        } throws IOException("Network error")

        viewModel.uiState.test {

            awaitItem()


            viewModel.login(testEmail, testPassword)


            awaitItem()

            advanceUntilIdle()


            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("Sin conexión a Internet. Verifica tu red", errorState.errorMessage)
            assertFalse(errorState.isSuccess)
        }
    }

    @Test
    fun `login con error 500 debe mostrar mensaje de error del servidor`() = runTest(testDispatcher) {
        viewModel = createViewModel()

        val testEmail = "test@example.com"
        val testPassword = "password123"

        val httpException = HttpException(
            Response.error<LoginResponse>(
                500,
                "Internal Server Error".toResponseBody()
            )
        )

        coEvery {
            mockApiService.login(LoginRequest(testEmail, testPassword))
        } throws httpException

        viewModel.uiState.test {

            awaitItem()


            viewModel.login(testEmail, testPassword)


            awaitItem()

            advanceUntilIdle()


            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("Error en el servidor. Intenta más tarde", errorState.errorMessage)
            assertFalse(errorState.isSuccess)
        }
    }

    @Test
    fun `login con respuesta exitosa pero sin token debe manejar error`() = runTest(testDispatcher) {
        viewModel = createViewModel()

        val testEmail = "test@example.com"
        val testPassword = "password123"

        val mockResponse = LoginResponse(
            message = "Login exitoso",
            user = UserInfo(
                id = "user123",
                name = "Test User",
                email = testEmail
            ),
            token = null
        )

        coEvery {
            mockApiService.login(LoginRequest(testEmail, testPassword))
        } returns Response.success(mockResponse)

        viewModel.uiState.test {

            awaitItem()


            viewModel.login(testEmail, testPassword)


            awaitItem()

            advanceUntilIdle()


            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertTrue(successState.isSuccess)


            coVerify { mockSessionManager.saveToken("") }
        }
    }

    @Test
    fun `login con error 404 debe mostrar mensaje de servicio no encontrado`() = runTest(testDispatcher) {
        viewModel = createViewModel()

        val testEmail = "test@example.com"
        val testPassword = "password123"

        val httpException = HttpException(
            Response.error<LoginResponse>(
                404,
                "Not Found".toResponseBody()
            )
        )

        coEvery {
            mockApiService.login(LoginRequest(testEmail, testPassword))
        } throws httpException

        viewModel.uiState.test {
            awaitItem()
            viewModel.login(testEmail, testPassword)
            awaitItem()
            advanceUntilIdle()

            val errorState = awaitItem()
            assertEquals("Servicio no encontrado", errorState.errorMessage)
        }
    }

    @Test
    fun `verificar que status flow se actualiza correctamente`() = runTest(testDispatcher) {
        viewModel = createViewModel()

        val testEmail = "test@example.com"
        val testPassword = "password123"

        coEvery {
            mockApiService.login(LoginRequest(testEmail, testPassword))
        } returns Response.success(
            LoginResponse(
                message = "OK",
                user = UserInfo("1", "User", testEmail),
                token = "token"
            )
        )

        viewModel.status.test {

            assertNull(awaitItem())


            viewModel.login(testEmail, testPassword)
            advanceUntilIdle()


            assertEquals("ok", awaitItem())
        }
    }
}
