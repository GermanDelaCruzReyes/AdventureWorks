package com.example.adventureworks

import android.util.Log
import Network.ApiServiceSalesPerson
import Network.ApiServiceSalesTerritory
import Network.RetrofitClientPerson
import Network.SalesPerson
import Network.SalesTerritory
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
import android.annotation.SuppressLint
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.adventureworks.ui.theme.AdventureWorksTheme

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Personal de Territorio", fontSize = 30.sp)

                Button(onClick = {
                    val navigate = Intent(this@MainActivity2, MainActivity::class.java)
                    startActivity(navigate)
                }) {
                    Text("Ir a Territorio de Ventas")
                }
                MyComposeAppTheme {
                    MainScreen(RetrofitClientPerson.instance)
                }
            }
        }
    }
}
@Composable
private fun MainScreen(apiService: ApiServiceSalesPerson) {
    var buscarBusinessEntityId by remember { mutableStateOf("") }
    var businessEntityId by remember { mutableStateOf("") }
    var territoryId by remember { mutableStateOf("") }
    var salesQuota by remember { mutableStateOf("") }
    var bonus by remember { mutableStateOf("") }
    var commissionPct by remember { mutableStateOf("") }
    val context = LocalContext.current

    val clearFields = {
        buscarBusinessEntityId = ""
        businessEntityId = ""
        territoryId = ""
        salesQuota = ""
        bonus = ""
        commissionPct = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(value = buscarBusinessEntityId,
            onValueChange = { buscarBusinessEntityId = it },
            label = { Text("Buscar Entidad de Negocio (Personal)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        Button(onClick = {
            val idText = buscarBusinessEntityId
            if (idText.isNotEmpty()) {
                val id = idText.toInt()
                getSalesPersonById(apiService, id) { person ->
                    if (person != null) {
                        businessEntityId = person.businessEntityId.toString()
                        territoryId = person.territoryId.toString()
                        salesQuota = person.salesQuota.toString()
                        bonus = person.bonus.toString()
                        commissionPct = person.commissionPct.toString()
                    } else {
                        Toast.makeText(context, "Personal no encontrado", Toast.LENGTH_SHORT).show()
                        Log.e(context.toString(), "Personal no encontrado")
                    }
                }
            } else {
                Toast.makeText(context, "Ingrese un ID válido", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Buscar Personal")
        }


        TextField(value = businessEntityId,
            onValueChange = { businessEntityId = it },
            label = { Text("Business Entity Id") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        TextField(value = territoryId,
            onValueChange = { territoryId = it },
            label = { Text("Territory Id") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))

        TextField(value = salesQuota,
            onValueChange = { salesQuota = it },
            label = { Text("Cuota de Ventas")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))

        TextField(value = bonus,
            onValueChange = { bonus = it },
            label = { Text("Bonus") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))

        TextField(value = commissionPct,
            onValueChange = { commissionPct = it },
            label = { Text("Porcentaje de Comisión") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))


        Button(onClick = {
            val idText = businessEntityId
            val territoryId = territoryId
            val salesQuota = salesQuota
            val bonus = bonus
            val commissionPct = commissionPct

            if (idText.isNotEmpty() && territoryId.isNotEmpty() && salesQuota.isNotEmpty() && bonus.isNotEmpty() && commissionPct.isNotEmpty()) {
                val personal = SalesPerson(
                    businessEntityId = idText.toInt(),
                    territoryId = territoryId.toInt(),
                    salesQuota = salesQuota.toDouble(),
                    bonus = bonus.toDouble(),
                    commissionPct = commissionPct.toDouble()
                )
                createSalesPerson(apiService, personal) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    clearFields()
                }
            } else {
                Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Asignar Personal")
        }

        Button(onClick = {
            val idText = businessEntityId
            val territoryId = territoryId
            val salesQuota = salesQuota
            val bonus = bonus
            val commissionPct = commissionPct

            if (idText.isNotEmpty() && territoryId.isNotEmpty() && salesQuota.isNotEmpty() && bonus.isNotEmpty() && commissionPct.isNotEmpty()) {
                val personal = SalesPerson(
                    businessEntityId = idText.toInt(),
                    territoryId = territoryId.toInt(),
                    salesQuota = salesQuota.toDouble(),
                    bonus = bonus.toDouble(),
                    commissionPct = commissionPct.toDouble()
                )
                updateSalesPerson(apiService, idText.toInt(), personal) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    clearFields()
                }
            } else {
                Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Actualizar Personal")
        }

        Button(onClick = {
            val idText = businessEntityId
            if (idText.isNotEmpty()) {
                val id = idText.toInt()
                deleteSalesPerson(apiService, id) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    clearFields()
                }
            } else {
                Toast.makeText(context, "Ingrese un ID válido", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Eliminar Personal")
        }
    }
}

fun createSalesPerson(apiService: ApiServiceSalesPerson, person: SalesPerson, onResult: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = apiService.createSalesPerson(person)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    onResult("Personal creado exitosamente")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    onResult("Error al crear el personal: $errorMessage")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onResult("Error: ${e.message}")
            }
        }
    }
}

fun updateSalesPerson(apiService: ApiServiceSalesPerson, id: Int, person: SalesPerson, onResult: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = apiService.updateSalesPerson(id, person)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    onResult("Personal actualizado exitosamente")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    onResult("Error al actualizar el personal: $errorMessage")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onResult("Error: ${e.message}")
            }
        }
    }
}

fun deleteSalesPerson(apiService: ApiServiceSalesPerson, id: Int, onResult: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = apiService.deleteSalesPerson(id)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    onResult("Personal eliminado exitosamente")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    onResult("Error al eliminar el personal: $errorMessage")
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
fun getSalesPersonById(apiService: ApiServiceSalesPerson, id: Int, onResult: (SalesPerson?) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = apiService.getSalesPersonById(id)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    //Toast.makeText(LocalContext.current, "Error al obtener el personal: $errorMessage", Toast.LENGTH_SHORT).show()
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
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AdventureWorksTheme {
        Greeting("Android")
    }
}