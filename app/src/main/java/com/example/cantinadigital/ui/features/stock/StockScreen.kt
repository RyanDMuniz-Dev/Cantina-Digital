package com.example.cantinadigital.ui.features.stock

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cantinadigital.data.model.Product
import com.example.cantinadigital.ui.components.StockItemCard
import com.example.cantinadigital.ui.components.forms.AddProductBottomSheet
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

    var showAddBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(addProductState) {
        when (val state = addProductState) {
            is AddProductUiState.Success -> {
                showAddBottomSheet = true
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
        onAddClick = { showAddBottomSheet = true },
        onDismissBottomSheet = { showAddBottomSheet = false },
        onConfirmAddProduct = { newProduct ->
            viewModel.addProduct(newProduct)
        },
        onEditClick = { selectedProduct ->

        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockContent(
    modifier: Modifier = Modifier,
    products: List<Product>,
    showAddBottomSheet: Boolean,
    isSavingProduct: Boolean,
    onAddClick: () -> Unit,
    onDismissBottomSheet: () -> Unit,
    onConfirmAddProduct: (Product) -> Unit,
    onEditClick: (Product) -> Unit
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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

    }

}



























