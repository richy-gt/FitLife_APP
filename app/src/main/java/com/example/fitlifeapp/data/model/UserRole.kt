package com.example.fitlifeapp.data.model


//roles
enum class UserRole(val displayName: String) {
    MIEMBRO("Miembro"),
    ENTRENADOR("Entrenador"),
    NUTRICIONISTA("Nutricionista"),
    ADMINISTRADOR("Administrador");

    companion object {
        fun fromString(value: String?): UserRole {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: MIEMBRO
        }
    }
}


data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String? = "MIEMBRO",
    val avatar: String? = null
)


data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String? = null,
    val role: String? = "MIEMBRO"
)