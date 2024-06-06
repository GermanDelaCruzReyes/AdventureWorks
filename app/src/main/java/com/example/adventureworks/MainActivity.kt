package com.example.adventureworks

import android.util.Log
import Network.ApiServiceSalesTerritory
import Network.RetrofitClientTerritory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import Network.SalesTerritory
import android.annotation.SuppressLint
import android.widget.TextView
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.adventureworks.ui.theme.AdventureWorksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(text = "Territorios de Ventas", fontSize = 30.sp)

                Button(onClick = {
                    val navigate = Intent(this@MainActivity, MainActivity2::class.java)
                    startActivity(navigate)
                }) {
                    Text("Ir a Personal de Territorio")
                }
            
                MyComposeAppTheme {
                    MainScreen(RetrofitClientTerritory.instance)
                }
            }
        }
    }
}

@Composable
fun MainScreen(apiService: ApiServiceSalesTerritory) {
    var territoryId by remember { mutableStateOf("") }
    var territoryName by remember { mutableStateOf("") }
    var countryRegionCode by remember { mutableStateOf("") }
    var territoryGroup by remember { mutableStateOf("") }
    var buscarTerritorio by remember { mutableStateOf("") }
    val context = LocalContext.current

    val clearFields = {
        territoryId = ""
        territoryName = ""
        countryRegionCode = ""
        territoryGroup = ""
        buscarTerritorio = ""
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(value = buscarTerritorio,
            onValueChange = { buscarTerritorio = it },
            label = { Text("Buscar Territorio") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        Button(onClick = {
            val idText = buscarTerritorio
            if (idText.isNotEmpty()) {
                val id = idText.toInt()
                getTerritoryById(apiService, id) { territory ->
                    if (territory != null) {
                        territoryId = territory.territoryId.toString()
                        territoryName = territory.name
                        countryRegionCode = territory.countryRegionCode
                        territoryGroup = territory.groupTerritory
                    } else {
                        Toast.makeText(context, "Territorio no encontrado", Toast.LENGTH_SHORT).show()
                        Log.e(context.toString(), "Territorio no encontrado")
                    }
                }
            } else {
                Toast.makeText(context, "Ingrese un ID válido", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Buscar Territorio")
        }


        TextField(value = territoryId,
            onValueChange = { territoryId = it },
            label = { Text("Territory ID") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        TextField(value = territoryName,
            onValueChange = { newValue ->
                if (newValue.length <= 50) {
                     territoryName = newValue
                     }
                },
            label = { Text("Name Territory") })

        TextField(value = countryRegionCode,
            onValueChange = { newValue ->
                if (newValue.length <= 3) {
                    countryRegionCode = newValue
                }
            },
            label = { Text("Country Region Code")})

        TextField(value = territoryGroup,
            onValueChange = { newValue ->
                 if (newValue.length <= 50) {
                     territoryGroup = newValue
                 }
            },
            label = { Text("Group Territory") })


        Button(onClick = {
            val idText = territoryId
            val name = territoryName
            val regionCode = countryRegionCode
            val group = territoryGroup

            if (idText.isNotEmpty() && name.isNotEmpty() && regionCode.isNotEmpty() && group.isNotEmpty()) {
                val territory = SalesTerritory(
                    territoryId = idText.toInt(),
                    name = name,
                    countryRegionCode = regionCode,
                    groupTerritory = group
                )
                createTerritory(apiService, territory) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    clearFields()
                }
            } else {
                Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Crear Territorio")
        }

        Button(onClick = {
            val idText = territoryId
            val name = territoryName
            val regionCode = countryRegionCode
            val group = territoryGroup

            if (idText.isNotEmpty() && name.isNotEmpty() && regionCode.isNotEmpty() && group.isNotEmpty()) {
                val territory = SalesTerritory(
                    territoryId = idText.toInt(),
                    name = name,
                    countryRegionCode = regionCode,
                    groupTerritory = group
                )
                updateTerritory(apiService, idText.toInt(), territory) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    clearFields()
                }
            } else {
                Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Actualizar Territorio")
        }

        Button(onClick = {
            val idText = territoryId
            if (idText.isNotEmpty()) {
                val id = idText.toInt()
                deleteTerritory(apiService, id) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    clearFields()
                }
            } else {
                Toast.makeText(context, "Ingrese un ID válido", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Eliminar Territorio")
        }

    }
}

fun createTerritory(apiService: ApiServiceSalesTerritory, territory: SalesTerritory, onResult: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = apiService.createSalesTerritory(territory)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    onResult("Territorio creado exitosamente")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    onResult("Error al crear el territorio: $errorMessage")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onResult("Error: ${e.message}")
            }
        }
    }
}

fun updateTerritory(apiService: ApiServiceSalesTerritory, id: Int, territory: SalesTerritory, onResult: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = apiService.updateSalesTerritory(id, territory)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    onResult("Territorio actualizado exitosamente")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    onResult("Error al actualizar el territorio: $errorMessage")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onResult("Error: ${e.message}")
            }
        }
    }
}

fun deleteTerritory(apiService: ApiServiceSalesTerritory, id: Int, onResult: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = apiService.deleteSalesTerritory(id)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    onResult("Territorio eliminado exitosamente")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    onResult("Error al eliminar el territorio: $errorMessage")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onResult("Error: ${e.message}")
            }
        }
    }
}

/*@SuppressLint("CoroutineCreationDuringComposition")
@Composable*/
fun getTerritoryById(apiService: ApiServiceSalesTerritory, id: Int, onResult: (SalesTerritory?) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = apiService.getSalesTerritoryById(id)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    //Toast.makeText(LocalContext.current, "Error al obtener el territorio: $errorMessage", Toast.LENGTH_SHORT).show()
                    onResult(null)
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                //Toast.makeText(LocalContext.current, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                onResult(null)
            }
        }
    }
}

@Composable
fun MyComposeAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyComposeAppTheme {
        MainScreen(RetrofitClientTerritory.instance)
    }
}