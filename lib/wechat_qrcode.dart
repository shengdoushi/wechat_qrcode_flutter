
import 'dart:async';

import 'package:flutter/services.dart';

class WechatQrcode {
  static const MethodChannel _channel = MethodChannel('wechat_qrcode');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }


  static Future<List<String>> scanImage(String path) async {
    final List<dynamic> result = await _channel.invokeMethod("scanImage", {'path': path});
    return Future.value(result.cast());
  }


  static Future<List<String>> scanCamera() async {
    final List<dynamic>? result = await _channel.invokeMethod("scanCamera");
    if (result == null) return Future.value([]);
    return Future.value(result.cast());
  }
}
