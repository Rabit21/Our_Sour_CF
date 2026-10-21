package cl.oursour.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.oursour.database.OurSourDatabase
import cl.oursour.repository.FrequentIngredientRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

class FrequentIngredientViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FrequentIngredientRepository(OurSourDatabase.getInstance(application).frequentIngredientDao())
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    val ingredients = repository.ingredients.catch { _error.value = "No se pudieron cargar los frecuentes." }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun save(id: Int) { update { repository.save(id) } }
    fun remove(id: Int) { update { repository.remove(id) } }
    private fun update(action: suspend () -> Unit) {
        viewModelScope.launch {
            try { action(); _error.value = null }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { _error.value = "No se pudo guardar el cambio. Intenta nuevamente." }
        }
    }
}
