package com.example.cantinadigital.ui.components.fields

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cantinadigital.R
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

@Composable
fun FormTextField(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
    @DrawableRes leadingIcon: Int,
    value: String,
    keyboardOptions: KeyboardOptions,
    singleLine: Boolean = true,
    placeholder: String? = null,
    contentDescription: String? = null,
    onValueChanged: (String) -> Unit
) {

    OutlinedTextField(
        modifier = modifier.fillMaxWidth().padding(5.dp),
        value = value,
        singleLine = singleLine,
        onValueChange = onValueChanged,
        placeholder = {
            Text(
                text = placeholder ?: ""
            )
        },
        label = {
            Text(
                text = stringResource(label)
            )
        },
        keyboardOptions = keyboardOptions,
        leadingIcon = {
            Icon(
                painter = painterResource(leadingIcon),
                contentDescription = contentDescription
            )
        }
    )

}

@Preview (showBackground = true)
@Composable
private fun CustomTextFieldPreview() {
    CantinaDigitalTheme {
        FormTextField(
            label = R.string.app_name,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            leadingIcon = R.drawable.ic_mail_24dp,
            value = "",
            placeholder = "",
        ) { }
    }
}