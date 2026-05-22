package com.example.cantinadigital.ui.features.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cantinadigital.R
import com.example.cantinadigital.ui.components.FormTextField
import com.example.cantinadigital.ui.components.PasswordTextField
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginScreenViewModel,
    onLoginSuccess: () -> Unit = {},
    onNavigateToSignUp: () -> Unit = {}
) {

    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess){
            Toast.makeText(context, context.getString(R.string.success_login), Toast.LENGTH_SHORT).show()
            onLoginSuccess()
        }
    }

    Scaffold (
        modifier = modifier,
        containerColor = Color(0xFF003984)
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier.width(275.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.inside_app_logo),
                    contentDescription = null,
                )
            }

            Surface(
                modifier = Modifier.width(350.dp),
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 12.dp,
                color = MaterialTheme.colorScheme.surface
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {

                    Text(
                        text = stringResource(R.string.label_login),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(fontSize = 32.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    )

                    FormTextField(
                        label = R.string.label_email,
                        leadingIcon = R.drawable.ic_mail_24dp,
                        value = state.email,
                        onValueChanged = {  viewModel.onEmailChange(it) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        )
                    )

                    PasswordTextField(
                        label = R.string.label_password,
                        value = state.pass,
                        onValueChanged = { viewModel.onPasswordChange(it) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        )
                    )

                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        onClick = {
                            viewModel.onLogin()
                        },
                        colors = ButtonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White,
                            disabledContainerColor = Color.Black.copy(alpha = 0.6f),
                            disabledContentColor = Color.White
                        )
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.label_login),
                                color = Color.White
                            )
                        }
                    }

                    TextButton(
                        onClick = onNavigateToSignUp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = stringResource(R.string.don_t_have_an_account_sign_up),
                            color = Color(0xFF003984),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    state.errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                }

            }

        }
    }

}

@Preview
@Composable
private fun LoginScreenPreview() {
    CantinaDigitalTheme {
        LoginScreen(
            viewModel = LoginScreenViewModel(previewMode = true),
            onLoginSuccess = {}
        )
    }
}
