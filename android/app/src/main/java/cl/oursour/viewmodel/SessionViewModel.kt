package cl.oursour.viewmodel

import androidx.lifecycle.ViewModel
import cl.oursour.model.DemoUser
import cl.oursour.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionViewModel : ViewModel() {
    private val _user = MutableStateFlow<DemoUser?>(null)
    val user = _user.asStateFlow()

    fun login(role: Role) {
        _user.value = if (role == Role.ADMIN) DemoUser("Admin demo", "admin@example.test", role)
            else DemoUser("Cliente demo", "cliente@example.test", role)
    }

    fun register(name: String, email: String): String? {
        if (name.trim().length !in 2..60) return "Escribe un nombre de 2 a 60 caracteres."
        if (!email.trim().matches(Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))) return "Escribe un correo válido."
        _user.value = DemoUser(name.trim(), email.trim(), Role.CLIENTE)
        return null
    }

    fun logout() { _user.value = null }
}
