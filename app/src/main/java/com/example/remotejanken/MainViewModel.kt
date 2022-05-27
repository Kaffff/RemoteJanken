package com.example.remotejanken


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import io.skyway.Peer.ConnectOption
import io.skyway.Peer.DataConnection
import io.skyway.Peer.Peer
import io.skyway.Peer.PeerOption
import java.io.File
import java.io.FileOutputStream

class MainViewModel : ViewModel() {
    var localPeerId = MutableLiveData("")
    var remotePeerId = MutableLiveData("")
    var msg = MutableLiveData("")
    var imageSaved = MutableLiveData(false)
    private lateinit var peer: Peer
    private lateinit var dataConnection: DataConnection

    fun setup(
        activity: MainActivity,
        navController: NavController,
        outPutPath: File
    ) {
        startPeer(activity, navController, outPutPath)
    }

    fun connectPeer(peerId: String, outPutPath: File) {
        remotePeerId.value = peerId
        val option = ConnectOption()
        option.label = "chat"
        dataConnection = peer.connect(remotePeerId.value, option)
        if (this::dataConnection.isInitialized) {
            setupDataCallback(outPutPath)
        }
    }

    private fun startPeer(
        activity: MainActivity,
        navController: NavController,
        outPutPath: File
    ) {
        val option = PeerOption()
        option.key = BuildConfig.SKYWAY_APIKEY
        option.domain = BuildConfig.SKYWAY_DOMAIN
        this.peer = Peer(activity, option)
        if (this::peer.isInitialized) {
            setupPeerCallback(navController, outPutPath)
        }
    }

    fun sendMessage(msg: String) {
        dataConnection.send(msg)
    }

    fun sendImage(imageFile: File) {
        dataConnection.send(imageFile.readBytes())
    }


    private fun setupPeerCallback(navController: NavController, outPutPath: File) {
        this.peer.on(Peer.PeerEventEnum.OPEN) { p0 ->
            (p0 as String).let { peerID ->
                Log.d("debug", "peerID: $peerID")
                this@MainViewModel.localPeerId.value = peerID
            }
        }
        this.peer.on(Peer.PeerEventEnum.CONNECTION) { p0 ->
            (p0 as DataConnection).let { _dataConnection ->
                this@MainViewModel.dataConnection = _dataConnection
                setupDataCallback(outPutPath)
                navController.navigate("camera")
            }

        }
        this.peer.on(
            Peer.PeerEventEnum.ERROR
        ) { p0 -> Log.d("debug", "peer error $p0") }
        this.peer.on(
            Peer.PeerEventEnum.CLOSE
        ) { Log.d("debug", "close peer connection") }
    }

    private fun setupDataCallback(outPutPath: File) {
        dataConnection.on(DataConnection.DataEventEnum.DATA) { p0 ->
            when (p0) {
                "グー" -> msg.value = p0.toString()
                "チョキ" -> msg.value = p0.toString()
                "パー" -> msg.value = p0.toString()
                is ArrayList<*> -> {
                    Log.d("data type", "ArrayList ${p0[0].javaClass.name}")
                    val out = FileOutputStream(outPutPath)
                    val byteArray = (p0 as ArrayList<Byte>).toByteArray()
                    out.write(byteArray)
                    out.flush()
                    out.close()
                    imageSaved.value = true
                }
                else -> {
                    try {
                    } catch (e: Error) {
                        Log.e("setupDataCallback", "setupDataCallback: ${e.message}")
                    }
                }
            }
        }
        dataConnection.on(
            DataConnection.DataEventEnum.CLOSE
        ) { this@MainViewModel.remotePeerId.value = "" }
        dataConnection.on(
            DataConnection.DataEventEnum.ERROR
        ) { p0 -> Log.e("error", (p0 as Error).printStackTrace().toString()) }
    }


}