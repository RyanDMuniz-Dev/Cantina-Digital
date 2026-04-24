package com.example.cantinadigital.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.cantinadigital.R
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

@Composable
fun FormTextField(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
    @DrawableRes leadingIcon: Int,
    value: String,
    singleLine: Boolean = true,
    placeholder: String? = null,
    contentDescription: String? = null,
    onValueChanged: (String) -> Unit
) {

    TextField(
        modifier = modifier.fillMaxWidth(),
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
            leadingIcon = R.drawable.ic_mail_24dp,
            value = "",
            placeholder = "",
        ) { }
    }
}