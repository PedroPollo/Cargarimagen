package com.example.cargarimagen.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cargarimagen.io.APIService
import com.example.cargarimagen.io.response.PredictResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class PredictViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = APIService.create()

    private val _predictResponse = MutableStateFlow<PredictResponse?>(null)
    val predictResponse: StateFlow<PredictResponse?> = _predictResponse

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun enviarImagen(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>().applicationContext
                val file = uriToFile(uri, context)
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val body = MultipartBody.Part.createFormData("imagen", file.name, requestFile)

                apiService.subirImagen(body).enqueue(object : Callback<PredictResponse> {
                    override fun onResponse(
                        call: Call<PredictResponse>,
                        response: Response<PredictResponse>
                    ) {
                        if (response.isSuccessful) {
                            _predictResponse.value = response.body()
                            _errorMessage.value = null
                        } else {
                            _errorMessage.value = "Error: ${response.code()}"
                        }
                    }

                    override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                        _errorMessage.value = "Fallo: ${t.message}"
                    }
                })

            } catch (e: Exception) {
                _errorMessage.value = "Excepción: ${e.message}"
            }
        }
    }

    private fun uriToFile(uri: Uri, context: Context): File {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("imagen", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)

        inputStream?.copyTo(outputStream)
        outputStream.close()
        inputStream?.close()

        return tempFile
    }
}
