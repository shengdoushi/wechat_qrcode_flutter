package com.shengdoushipp.flutter.wechat_qrcode.wechat_qrcode

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.NonNull
import com.king.wechat.qrcode.WeChatQRCodeDetector

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import org.opencv.OpenCV

/** WechatQrcodePlugin */
class WechatQrcodePlugin: FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  private val RC_BARCODE_CAPTURE = 8901
  private lateinit var curActivity: Activity
  private lateinit var pendingResult: Result
  private lateinit var activityPluginBinding: ActivityPluginBinding

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "wechat_qrcode")
    channel.setMethodCallHandler(this)

    //初始化OpenCV
    OpenCV.initAsync(flutterPluginBinding.applicationContext)

    //初始化WeChatQRCodeDetector
    WeChatQRCodeDetector.init(flutterPluginBinding.applicationContext)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {

    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else if (call.method == "scanImage") {
      val path = call.argument<String>("path")
      val bitmap = BitmapFactory.decodeFile(path)
      val results = WeChatQRCodeDetector.detectAndDecode(bitmap)
      result.success(results)
    } else if (call.method == "scanCamera") {
      pendingResult = result
      val intent = Intent(curActivity, WeChatQRCodeActivity::class.java);
      curActivity.startActivityForResult(intent, RC_BARCODE_CAPTURE);
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    curActivity = binding.activity
    activityPluginBinding = binding
    binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivityForConfigChanges() {
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
  }

  override fun onDetachedFromActivity() {
    activityPluginBinding.removeActivityResultListener(this)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    if (requestCode == RC_BARCODE_CAPTURE){
      if (resultCode == Activity.RESULT_OK){
        try {
          val results = data!!.getStringArrayListExtra("result")
          pendingResult.success(results)
        }catch (exp : Exception) {
          exp.printStackTrace()
          pendingResult.success(null)
        }
      }else {
        pendingResult.success(null)
      }
      return true
    }
    return false
  }
}
