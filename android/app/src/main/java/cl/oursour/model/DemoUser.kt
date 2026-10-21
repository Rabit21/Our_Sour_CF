package cl.oursour.model

enum class Role { CLIENTE, ADMIN }
data class DemoUser(val name: String, val email: String, val role: Role)
