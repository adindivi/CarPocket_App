package com.example.receiver

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.service.DrivingTrackerService

class BluetoothDrivingReceiver : BroadcastReceiver() {

  companion object {
    private const val TAG = "BluetoothDrivingReceiver"
  }

  override fun onReceive(context: Context, intent: Intent) {
    val action = intent.action ?: return
    val device: BluetoothDevice? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
      intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
    } else {
      @Suppress("DEPRECATION")
      intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
    }

    when (action) {
      BluetoothDevice.ACTION_ACL_CONNECTED -> {
        Log.i(TAG, "Bluetooth connected: ${device?.name ?: "Unknown device"}")
        // If device is confirmed car audio/handsfree, can auto-start DrivingTrackerService
      }
      BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
        Log.i(TAG, "Bluetooth disconnected: ${device?.name ?: "Unknown device"}")
        // On car disconnect, can trigger auto-stop of drive session
        if (DrivingTrackerService.isServiceRunning) {
          DrivingTrackerService.stopService(context)
        }
      }
    }
  }
}
