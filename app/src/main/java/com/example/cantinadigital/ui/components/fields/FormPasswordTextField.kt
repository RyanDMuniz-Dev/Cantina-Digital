package com.example.cantinadigital.ui.components.fields

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cantinadigital.R
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
    value: String,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions,
    contentDescription: String? = null,
    placeholder: String? = null,
    onValueChanged: (String) -> Unit
) {

    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier.fillMaxWidth().padding(5.dp),
        value = value,
        onValueChange = onValueChanged,
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        label = {
            Text(text = stringResource(label))
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        placeholder = {
            Text(
                text = placeholder ?: ""
            )
        },
        trailingIcon = {

            val image = if (isPasswordVisible) {
                R.drawable.ic_visibility_24dp
            } else {
                R.drawable.ic_visibility_off_24dp
            }

            IconButton(
                onClick = {isPasswordVisible = !isPasswordVisible}
            ) {
                Icon(
                    painter = painterResource(image),
                    contentDescription = contentDescription
                )
            }

        }
    )

}

@Preview (showBackground = true)
@Composable
private fun PasswordTextFieldPreview() {
    CantinaDigitalTheme {
        PasswordTextField(
            value = "myPassword",
            onValueChanged = {},
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            label = R.string.app_name
        )
    }
}