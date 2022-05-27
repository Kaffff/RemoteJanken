package com.example.remotejanken

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import java.io.File

@Composable
fun JankenView(navController: NavController, viewModel: MainViewModel) {
    Column(
        modifier = Modifier.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        val outputDir = context.filesDir.absolutePath
        val imageFile = File(outputDir, "my.jpg")
        BoxWithConstraints {
            val w = with(LocalDensity.current) {
                constraints.maxWidth.toDp()
            }
            val h = with(LocalDensity.current) {
                constraints.maxHeight.toDp()
            }
            var min = w
            if (h < w) min = h
            AsyncImage(
                model = imageFile,
                contentDescription = null,
                Modifier
                    .height(min)
                    .width(min),
                contentScale = ContentScale.Crop
            )
        }
        Row {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(120.dp), contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    onClick = {
                        viewModel.sendMessage("グー")
                        navController.navigate("result/グー")
                    },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp),
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Color(50,100,250),
                        contentColor = Color.White
                    ),
                ) {
                    Text(text = "グー")
                }
            }
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(120.dp), contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    onClick = {
                        viewModel.sendMessage("チョキ")
                        navController.navigate("result/チョキ")
                    },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp),
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Color(50,100,250),
                        contentColor = Color.White
                    ),
                ) {
                    Text(text = "チョキ")
                }
            }
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(120.dp), contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    onClick = {
                        viewModel.sendMessage("パー")
                        navController.navigate("result/パー")
                    },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp),
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Color(50,100,250),
                        contentColor = Color.White
                    ),
                ) {
                    Text(text = "パー")
                }
            }
        }
        Box(modifier = Modifier.height(100.dp), contentAlignment = Alignment.BottomCenter) {
            Button(
                onClick = {
                    navController.navigate("camera")
                },
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = Color.DarkGray,
                    contentColor = Color.White
                )
            ) {
                Text(text = "取り直す")
            }
        }
    }
}


@Preview
@Composable
fun JunkenPreview() {
    val navController = rememberNavController()
    val viewModel = MainViewModel()
    JankenView(navController, viewModel)
}