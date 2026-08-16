package com.example.cantinadigital.ui.components.appbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cantinadigital.R
import com.example.cantinadigital.ui.theme.CantinaDigitalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CantinaTopBar(
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.inside_app_logo),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp), // Define tamanho fixo para não esticar a TopBar
                    contentScale = ContentScale.Fit
                )

                // CORREÇÃO: Usar Modifier em vez de reusar o parâmetro 'modifier'
                Spacer(Modifier.width(10.dp))

                Text(
                    text = stringResource(R.string.app_name),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        actions = {
            IconButton(
                onClick = onNotificationClick
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = stringResource(R.string.notifications)
                )
            }

            IconButton(
                onClick = onProfileClick
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = stringResource(R.string.profile)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CantinaTopBarPreview() {
    CantinaDigitalTheme {
        CantinaTopBar(
            onProfileClick = {},
            onNotificationClick = {}
        )
    }
}