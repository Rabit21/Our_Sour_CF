package cl.oursour.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cl.oursour.R

@Composable
fun WelcomeScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.safeDrawingPadding(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth()
                    .verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary)
                Text(stringResource(R.string.welcome_title), style = MaterialTheme.typography.headlineLarge)
                Text(stringResource(R.string.welcome_description), style = MaterialTheme.typography.bodyLarge)
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(stringResource(R.string.demo_notice), style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.initial_notice), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
