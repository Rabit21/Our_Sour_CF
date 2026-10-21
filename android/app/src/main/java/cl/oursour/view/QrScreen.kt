package cl.oursour.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import cl.oursour.model.Product
import cl.oursour.model.ProductQr
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun QrScreen(products: List<Product>, onProduct: (Int) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    var message by rememberSaveable { mutableStateOf<String?>(null) }
    var scanning by rememberSaveable { mutableStateOf(false) }
    var denied by rememberSaveable { mutableStateOf(false) }
    val scanner = rememberLauncherForActivityResult(ScanContract()) { result ->
        scanning = false
        val contents = result.contents
        if (contents == null) message = "Lectura cancelada. Puedes intentarlo nuevamente."
        else {
            val id = ProductQr.productId(contents)
            when {
                id == null -> message = "QR inválido. Utiliza un código de producto Our Sour."
                products.none { it.id == id } -> message = "El producto del QR no existe en el catálogo local."
                else -> { message = null; onProduct(id) }
            }
        }
    }
    fun openScanner() {
        if (scanning) return
        scanning = true
        message = null
        try {
            scanner.launch(ScanOptions().setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                .setPrompt("Apunta al QR de un producto Our Sour")
                .setBeepEnabled(false).setOrientationLocked(false))
        } catch (_: Exception) {
            scanning = false
            message = "No se pudo abrir la cámara. Revisa los permisos e intenta nuevamente."
        }
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        denied = !granted
        if (granted) openScanner()
        else message = "Permiso de cámara rechazado. Puedes habilitarlo en los ajustes de la aplicación."
    }
    Page("Leer código QR", onBack) {
        Text("Escanea un QR Our Sour para abrir un producto del catálogo local. No necesita Internet.", style = MaterialTheme.typography.bodyLarge)
        NoticeCard("La cámara se usa solo durante la lectura. No se guardan fotos.")
        message?.let { Text(it, color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.labelLarge) }
        Button(onClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                denied = false
                openScanner()
            } else permission.launch(Manifest.permission.CAMERA)
        }, enabled = !scanning, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Abrir cámara") }
        if (denied) OutlinedButton(onClick = {
            context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}")))
        }, modifier = Modifier.fillMaxWidth()) { Text("Abrir ajustes de permisos") }
    }
}
