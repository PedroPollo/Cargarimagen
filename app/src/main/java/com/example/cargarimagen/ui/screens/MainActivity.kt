package com.example.cargarimagen.ui.screens

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cargarimagen.R
import coil.compose.AsyncImage
import com.example.cargarimagen.ui.theme.CargarImagenTheme
import com.example.cargarimagen.viewmodels.PredictViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CargarImagenTheme {
                Surface {
                    PrincipalImg()
                }
            }
        }
    }
}

@Composable
fun PrincipalImg(predictViewModel: PredictViewModel = viewModel()) {
    val predictResponse by predictViewModel.predictResponse.collectAsState()
    val errorMessage by predictViewModel.errorMessage.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(184.dp, 202.dp),
            painter = painterResource(id = R.drawable.img),
            contentDescription = "logo"
        )

        Spacer(modifier = Modifier.height(40.dp))

        if (selectedImageUri == null) {
            Image(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
                painter = painterResource(id = R.drawable.ic_watermark),
                contentDescription = "Imagen seleccionada"
            )
        } else {
            AsyncImage(
                model = selectedImageUri,
                contentDescription = "Imagen Seleccionada",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = {
                imagePickerLauncher.launch("image/*")
            }) {
                Text(text = if (selectedImageUri != null) "Cambiar foto" else "Subir foto")
            }

            Button(
                onClick = {
                    predictViewModel.enviarImagen(uri = selectedImageUri!!)
                },
                enabled = selectedImageUri != null
                ) {
                Text(text = "Procesar imagen")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Resultado
        if (predictResponse != null){
            Text("Predicción: ${predictResponse!!.prediccion}")
            Text("Confianza: ${predictResponse!!.confianza}")
        } else {
            Text("El resultado se mostrara aqui")
        }

        errorMessage?.let {
            Toast.makeText(LocalContext.current, "Error: $it", Toast.LENGTH_SHORT).show()
            Log.e("Error Toast:", it)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowPre(){
    CargarImagenTheme {
        Surface {
            PrincipalImg()
        }
    }
}