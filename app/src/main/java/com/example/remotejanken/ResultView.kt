package com.example.remotejanken

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.io.File

@Composable
fun ResultView(navController: NavController, viewModel: MainViewModel, myJanken: String?) {

    val context = LocalContext.current
    val outputDir = context.filesDir.absolutePath
    val myImage = File(outputDir, "my.jpg")
    val yourImage = File(outputDir, "your.jpg")

    BoxWithConstraints {
        val w = with(LocalDensity.current) {
            constraints.maxWidth.toDp()
        }
        Column {
            Row {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = myImage,
                        contentDescription = null,
                        Modifier
                            .height((w.value / 2).dp)
                            .width((w.value / 2).dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(text = "自分", fontSize = 20.sp)
                    Text(text = "$myJanken", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (viewModel.imageSaved.observeAsState().value!!) {
                        AsyncImage(
                            model = yourImage,
                            contentDescription = null,
                            Modifier
                                .height((w.value / 2).dp)
                                .width((w.value / 2).dp),
                            contentScale = ContentScale.Crop
                        )
                        Text(text = "相手", fontSize = 20.sp)
                        Text(
                            text = viewModel.msg.observeAsState().value!!,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                JankenResult(myJanken, viewModel.msg.observeAsState().value!!)
            }

            Box(
                modifier = Modifier.height(100.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
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
                    Text(text = "もう一度対戦する")
                }
            }
            Box(modifier = Modifier.height(60.dp), contentAlignment = Alignment.BottomCenter) {
                Button(
                    onClick = {
                        navController.navigate("main")
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Color.DarkGray,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "別の相手と対戦する")
                }
            }
        }
    }
}

@Composable
fun JankenResult(me: String?, you: String?) {
    var result = ""
    if (me != null && you != null) {
        when (me) {
            "グー" -> {
                when (you) {
                    "グー" -> {
                        result = "あいこ"
                    }
                    "チョキ" -> {
                        result = "勝ち"

                    }
                    "パー" -> {
                        result = "負け"

                    }
                }
            }
            "チョキ" -> {
                when (you) {
                    "グー" -> {
                        result = "負け"
                    }
                    "チョキ" -> {
                        result = "あいこ"
                    }
                    "パー" -> {
                        result = "勝ち"
                    }
                }
            }
            "パー" -> {
                when (you) {
                    "グー" -> {
                        result = "勝ち"
                    }
                    "チョキ" -> {
                        result = "負け"
                    }
                    "パー" -> {
                        result = "あいこ"
                    }
                }
            }
        }
    }
    when (result) {
        "勝ち" -> {
            Text(text = "勝ち", fontSize = 80.sp, color = Color.Red)
        }
        "あいこ" -> {
            Text(text = "あいこ", fontSize = 80.sp, color = Color(50, 50, 50))

        }
        "負け" -> {
            Text(text = "負け", fontSize = 80.sp, color = Color.Blue)
        }
    }
}