package com.example.cantinadigital.ui.features.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cantinadigital.data.model.Product
import com.example.cantinadigital.data.repository.AuditLogRepository
import com.example.cantinadigital.data.repository.AuthRepository
import com.example.cantinadigital.data.repository.ProductRepository
import com.example.cantinadigital.ui.features.stock.model.AddProductUiState
import com.example.cantinadigital.ui.features.stock.model.DeleteProductUiState
import com.example.cantinadigital.ui.features.stock.model.UpdateProductUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StockViewModel(
    private val repository: ProductRepository = ProductRepository(),
    private val auditLogRepository: AuditLogRepository = AuditLogRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _addProductState = MutableStateFlow<AddProductUiState>(AddProductUiState.Idle)
    private val _updateProductState = MutableStateFlow<UpdateProductUiState>(UpdateProductUiState.Idle)
    private val _deleteProductState = MutableStateFlow<DeleteProductUiState>(DeleteProductUiState.Idle)

    val addProductState: StateFlow<AddProductUiState> = _addProductState.asStateFlow()
    val updateProductState: StateFlow<UpdateProductUiState> = _updateProductState.asStateFlow()
    val deleteProductState: StateFlow<DeleteProductUiState> = _deleteProductState.asStateFlow()

    val products: StateFlow<List<Product>> = repository.getProductFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private suspend fun getUserData(): Pair<String, String> {
        val userResult = authRepository.getDadosUsuarioLogado()
        val userData = userResult.getOrNull()
        val name = userData?.get("nome") as? String ?: "Atendente"
        val userClass = userData?.get("turma") as? String ?: ""
        return Pair(name, userClass)
    }

    fun addProduct(product: Product) {
        if (_addProductState.value is AddProductUiState.Loading) return

        viewModelScope.launch {
            _addProductState.value = AddProductUiState.Loading

            try {
                repository.addProduct(product) { success ->
                    if (success) {
                        _addProductState.value = AddProductUiState.Success
                        viewModelScope.launch {
                            val (userName, userClass) = getUserData()
                            auditLogRepository.logAction(
                                type = "PRODUTO",
                                action = "CRIAR",
                                description = "Cadastrou o produto '${product.nome}' (${product.vendedor})",
                                username = userName,
                                userClass = userClass
                            )
                        }
                    } else {
                        _addProductState.value = AddProductUiState.Error("Falha ao salvar produto no banco.")
                    }
                }
            } catch (e: Exception) {
                _addProductState.value = AddProductUiState.Error(e.localizedMessage ?: "Erro desconhecido")
            }
        }
    }

    fun updateProduct(oldProduct: Product, newProduct: Product) {
        viewModelScope.launch {
            try {
                repository.updateProduct(newProduct) { success ->
                    if (success) {
                        _updateProductState.value = UpdateProductUiState.Success
                        viewModelScope.launch {
                            val (userName, userClass) = getUserData()

                            // Constrói a lista do que realmente mudou
                            val changes = mutableListOf<String>()

                            if (oldProduct.nome != newProduct.nome) {
                                changes.add("nome de '${oldProduct.nome}' para '${newProduct.nome}'")
                            }
                            if (oldProduct.valor != newProduct.valor) {
                                changes.add("preço de R$ %.2f para R$ %.2f".format(oldProduct.valor, newProduct.valor))
                            }
                            if (oldProduct.quantidade != newProduct.quantidade) {
                                changes.add("estoque de ${oldProduct.quantidade} para ${newProduct.quantidade}")
                            }
                            if (oldProduct.vendedor != newProduct.vendedor) {
                                changes.add("vendedor de '${oldProduct.vendedor}' para '${newProduct.vendedor}'")
                            }

                            val description = if (changes.isNotEmpty()) {
                                "Atualizou ${newProduct.nome}: " + changes.joinToString(", ")
                            } else {
                                "Atualizou informações de '${newProduct.nome}'"
                            }

                            auditLogRepository.logAction(
                                type = "PRODUTO",
                                action = "ATUALIZAR",
                                description = description,
                                username = userName,
                                userClass = userClass
                            )
                        }
                    } else {
                        _updateProductState.value = UpdateProductUiState.Error("Falha ao atualizar produto no banco")
                    }
                }
            } catch (e: Exception) {
                _updateProductState.value = UpdateProductUiState.Error(e.localizedMessage ?: "Erro desconhecido")
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(product) { success ->
                    if (success) {
                        _deleteProductState.value = DeleteProductUiState.Success
                        viewModelScope.launch {
                            val (userName, userClass) = getUserData()
                            auditLogRepository.logAction(
                                type = "PRODUTO",
                                action = "EXCLUIR",
                                description = "Removeu o produto '${product.nome}'",
                                username = userName,
                                userClass = userClass
                            )
                        }
                    } else {
                        _deleteProductState.value = DeleteProductUiState.Error("Falha ao deletar produto no banco")
                    }
                }
            } catch (e: Exception) {
                _deleteProductState.value = DeleteProductUiState.Error(e.localizedMessage ?: "Erro desconhecido")
            }
        }
    }

    fun resetAddProductState() {
        _addProductState.value = AddProductUiState.Idle
    }

    fun resetUpdateProductState() {
        _updateProductState.value = UpdateProductUiState.Idle
    }
    fun resetDeletProductState() {
        _deleteProductState.value = DeleteProductUiState.Idle
    }

}