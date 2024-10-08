package com.example.datossinmvvm

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenUser() {
    val context = LocalContext.current
    val db = crearDatabase(context)
    val dao = db.userDao()
    val coroutineScope = rememberCoroutineScope()

    var id by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dataUser = remember { mutableStateOf("") }
    var userList by remember { mutableStateOf(listOf<User>()) }

    // Obtener usuarios al iniciar la pantalla
    LaunchedEffect(Unit) {
        userList = dao.getAll()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Spacer(Modifier.height(50.dp))
            TextField(
                value = id,
                onValueChange = { id = it },
                label = { Text("ID (opcional)") },
                singleLine = true
            )
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name: ") },
                singleLine = true
            )
            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name:") },
                singleLine = true
            )
            Row {
                Button(
                    onClick = {
                        val user = User(0, firstName, lastName)
                        coroutineScope.launch {
                            dao.insert(user)
                            firstName = ""
                            lastName = ""
                            userList = dao.getAll() // Refrescar la lista
                        }
                    }
                ) {
                    Text("Agregar Usuario", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val userId = id.toIntOrNull()
                        if (userId != null) {
                            coroutineScope.launch {
                                val user = dao.getUserById(userId)
                                if (user != null) {
                                    firstName = user.firstName ?: ""
                                    lastName = user.lastName ?: ""
                                }
                            }
                        }
                    }
                ) {
                    Text("Cargar Usuario", fontSize = 16.sp)
                }
            }
            Button(
                onClick = {
                    val userId = id.toIntOrNull()
                    if (userId != null) {
                        coroutineScope.launch {
                            val user = User(userId, firstName, lastName)
                            dao.update(user)
                            firstName = ""
                            lastName = ""
                            userList = dao.getAll() // Refrescar la lista
                        }
                    }
                }
            ) {
                Text("Actualizar Usuario", fontSize = 16.sp)
            }
            Button(
                onClick = {
                    val userId = id.toIntOrNull()
                    if (userId != null) {
                        coroutineScope.launch {
                            val user = dao.getUserById(userId)
                            if (user != null) {
                                dao.delete(user)
                                userList = dao.getAll() // Refrescar la lista
                                dataUser.value = "${user.firstName} ${user.lastName} eliminado."
                            }
                        }
                    }
                }
            ) {
                Text("Eliminar Usuario", fontSize = 16.sp)
            }
            Text(
                text = dataUser.value, fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            userList.forEach { user ->
                Text("${user.uid}: ${user.firstName} ${user.lastName}", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun crearDatabase(context: Context): UserDatabase {
    return Room.databaseBuilder(
        context,
        UserDatabase::class.java,
        "user_db"
    ).build()
}
