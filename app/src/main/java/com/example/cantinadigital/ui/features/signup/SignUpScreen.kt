package com.example.cantinadigital.ui.features.signup

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.example.cantinadigital.ui.components.SelectorBox
import com.example.cantinadigital.ui.features.signup.model.ThirdYearClass
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: SignUpScreenViewModel,
    onSignUpSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {

    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            Toast.makeText(context, R.string.success_sign_up, Toast.LENGTH_SHORT).show()
            onSignUpSuccess()
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color(0xFF003984)
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Box(
                modifier = Modifier.width(275.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.inside_app_logo),
                    contentDescription = null,
                )
            }

            Spacer(modifier = Modifier.padding(top = 32.dp))

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
                        text = stringResource(R.string.sign_up_title),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(fontSize = 32.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    )

                    Row (
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FormTextField(
                            label = R.string.label_name,
                            leadingIcon = R.drawable.ic_person_24dp,
                            value = state.name,
                            onValueChanged = { viewModel.onNameChange(it) },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        SelectorBox(
                            modifier = Modifier.padding(top = 7.dp),
                            classList = listOf("3W", "3X", "3Y"),
                            selectedClass = state.studentClass
                        ) {

                            var currentClass: ThirdYearClass = ThirdYearClass.Y

                            when(it) {
                                "3W" -> { currentClass = ThirdYearClass.W }
                                "3X" -> { currentClass = ThirdYearClass.X }
                                "3Y" -> { currentClass = ThirdYearClass.Y }
                            }

                            viewModel.onClassChange(currentClass)
                        }
                    }

                    FormTextField(
                        label = R.string.label_email,
                        leadingIcon = R.drawable.ic_mail_24dp,
                        value = state.email,
                        onValueChanged = { viewModel.onEmailChange(it) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                    )

                    PasswordTextField(
                        label = R.string.label_password,
                        value = state.password,
                        onValueChanged = { viewModel.onPasswordChange(it) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.padding(bottom = 15.dp)
                    )

                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        onClick = {
                            viewModel.onSignUp()
                        },
                        colors = ButtonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White,
                            disabledContainerColor = Color.Black.copy(alpha = 0.6f),
                            disabledContentColor = Color.White
                        )
                    ) {
                        if (state.isLoading){
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.label_sign_up),
                                color = Color.White
                            )
                        }
                    }

                    TextButton(
                        onClick = onNavigateToLogin,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = stringResource(R.string.already_have_an_account),
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

/*
@Preview
@Composable
private fun SignUpScreenPreview() {
    CantinaDigitalTheme {
        SignUpScreen(
            modifier = Modifier,
            viewModel = SignUpScreenViewModel(previewMode = true)
        )
    }
}
*/
