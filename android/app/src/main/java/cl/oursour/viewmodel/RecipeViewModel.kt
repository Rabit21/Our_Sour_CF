package cl.oursour.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecipeViewModel : ViewModel() {
    private val _selected = MutableStateFlow<Set<Int>>(emptySet())
    val selected = _selected.asStateFlow()
    fun toggle(id: Int) { _selected.value = if (id in _selected.value) _selected.value - id else _selected.value + id }
    fun clear() { _selected.value = emptySet() }
}
