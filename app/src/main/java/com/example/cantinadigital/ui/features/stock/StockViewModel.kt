package com.example.cantinadigital.ui.features.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cantinadigital.data.model.Product
import com.example.cantinadigital.data.repository.ProductRepository
import com.example.cantinadigital.ui.features.stock.model.AddProductUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StockViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _addProductState = MutableStateFlow<AddProductUiState>(AddProductUiState.Idle)
    val addProductState: StateFlow<AddProductUiState> = _addProductState.asStateFlow()

    val products: StateFlow<List<Product>> = repository.getProductFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addProduct(product: Product) {
        viewModelScope.launch {
            _addProductState.value = AddProductUiState.Loading

            try {
                repository.addProduct(product) { success ->
                    if (success) {
                        _addProductState.value = AddProductUiState.Success
                    } else {
                        _addProductState.value = AddProductUiState.Error("Falha ao salvar produto no banco.")
                    }
                }
            } catch (e: Exception) {
                _addProductState.value = AddProductUiState.Error(e.localizedMessage ?: "Erro desconhecido")
            }
        }
    }

    fun resetAddProductState() {
        _addProductState.value = AddProductUiState.Idle
    }

}