package com.example.cantinadigital.ui.features.stock

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cantinadigital.data.model.Product
import com.example.cantinadigital.ui.components.cards.StockItemCard
import com.example.cantinadigital.ui.components.forms.AddProductBottomSheet
import com.example.cantinadigital.ui.components.forms.EditProductBottomSheet
import com.example.cantinadigital.ui.features.stock.model.AddProductUiState

// Componente principal que se conecta ao ViewModel
@Composable
fun StockScreen(
    modifier: Modifier = Modifier,
    viewModel: StockViewModel = viewModel()
) {

    val context = LocalContext.current
    val products by viewModel.products.collectAsState()

    val addProductState by viewModel.addProductState.collectAsState()
    val updateProductState by viewModel.updateProductState.collectAsState()
    val deleteProductState by viewModel.deleteProductState.collectAsState()

    var showAddBottomSheet by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(addProductState) {
        when (val state = addProductState) {
            is AddProductUiState.Success -> {
                showAddBottomSheet = false
                Toast.makeText(context, "Produto cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                viewModel.resetAddProductState()
            }
            is AddProductUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetAddProductState()
            }
            else -> {}
        }
    }

    StockContent(
        modifier = modifier,
        products = products,
        showAddBottomSheet = showAddBottomSheet,
        isSavingProduct = addProductState is AddProductUiState.Loading,
        productToEdit = productToEdit,
        onAddClick = { showAddBottomSheet = true },
        onDismissBottomSheet = { showAddBottomSheet = false },
        onConfirmAddProduct = { newProduct ->
            viewModel.addProduct(newProduct)
        },
        onEditClick = { selectedProduct ->
            productToEdit = selectedProduct
        },
        onDismissEditBottomSheet = {
            productToEdit = null
        },
        onConfirmUpdateProduct = { updatedProduct ->
            viewModel.updateProduct(updatedProduct)
            productToEdit = null
        },
        onConfirmDeleteProduct = { productTodDelete ->
            viewModel.deleteProduct(productTodDelete)
            productToEdit = null
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockContent(
    modifier: Modifier = Modifier,
    products: List<Product>,
    showAddBottomSheet: Boolean,
    productToEdit: Product?,
    isSavingProduct: Boolean,
    onAddClick: () -> Unit,
    onDismissBottomSheet: () -> Unit,
    onConfirmAddProduct: (Product) -> Unit,
    onEditClick: (Product) -> Unit,
    onDismissEditBottomSheet: () -> Unit,
    onConfirmUpdateProduct: (Product) -> Unit,
    onConfirmDeleteProduct: (Product) -> Unit
) {

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar produto"
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier.padding(innerPadding)
        ) {

            Text(
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                text = "Lista de produtos",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {



                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {

                    items(
                        items = products,
                        key = { product -> product.id }
                    ) { product ->
                        StockItemCard(
                            item = product,
                            onEditClick = onEditClick
                        )
                    }

                }

                if (isSavingProduct) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

            }

            if (showAddBottomSheet) {
                AddProductBottomSheet(
                    onDismissRequest = onDismissBottomSheet,
                    onConfirmRequest = onConfirmAddProduct
                )
            }

            productToEdit?.let { product ->
                EditProductBottomSheet(
                    modifier = Modifier,
                    productToEdit = product,
                    onDismissRequest = onDismissEditBottomSheet,
                    onConfirmUpdate = onConfirmUpdateProduct,
                    onConfirmDelete = onConfirmDeleteProduct
                )
            }

        }

        }



}