package com.example.remotejanken

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.remotejanken.ui.theme.RemoteJankenTheme
import java.io.File

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RemoteJankenTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(), color = Color(230, 230, 255)
                ) {
                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") {
                            MainView(navController, viewModel, this@MainActivity)
                        }
                        composable("camera") {
                            CameraView(navController, viewModel)
                        }
                        composable("janken") {
                            JankenView(navController, viewModel)
                        }
                        composable("result/{janken}") { backStackEntry ->
                            ResultView(
                                navController,
                                viewModel,
                                backStackEntry.arguments?.getString("janken")
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun MainView(
    navController: NavController,
    viewModel: MainViewModel,
    activity: MainActivity,
    modifier: Modifier = Modifier
) {
    val localPeerId = viewModel.localPeerId.observeAsState()
    val remotePeerId = viewModel.remotePeerId.observeAsState()
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current
    val outputDir = context.filesDir.absolutePath
    val outputPath = File(outputDir, "your.jpg")
    LaunchedEffect(localPeerId) {
        if (localPeerId.value == null || localPeerId.value == "") {
            viewModel.setup(activity, navController, outputPath)
        }
        outputPath.delete()
    }
    Column(
        modifier = modifier
            .padding(10.dp)
            .clickable(
                interactionSource = interactionSource,
                enabled = true,
                indication = null,
                onClick = { focusRequester.requestFocus() }
            )
            .focusRequester(focusRequester)
            .focusTarget(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "自分のID",
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.Start)
        )
        SelectionContainer {
            Text(
                text = if (localPeerId.value != null) localPeerId.value!! else "",
                fontSize = 30.sp,
                modifier = Modifier.height(50.dp)
            )
        }

        Text(
            text = "相手のID ",
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 10.dp)
        )

        var inputText by remember { mutableStateOf("") }
        remotePeerId.value?.let {
            if (it.isNotEmpty()) {
                inputText = it
            }
        }
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            placeholder = { Text("Peer ID") },
            modifier = Modifier
                .height(50.dp),


            )
        Box(modifier = Modifier.height(170.dp), contentAlignment = Alignment.BottomCenter) {
            Button(
                onClick = {
                    viewModel.connectPeer(inputText, outputPath)
                    navController.navigate("camera")
                },
                modifier = Modifier
                    .height(70.dp)
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    ,
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = Color(50,100,250),
                    contentColor = Color.White
                ),
            ) {
                Text("対戦", fontSize = 40.sp)
            }
        }
    }

}
