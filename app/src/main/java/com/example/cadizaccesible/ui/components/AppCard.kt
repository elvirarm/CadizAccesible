package com.example.cadizaccesible.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.cadizaccesible.ui.theme.Dimens

@Composable
fun AppCard(
    title: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface // ✅ contraste real
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.CardPadding),
            content = {
                if (title != null) {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                }
                content()
            }
        )
    }
}
