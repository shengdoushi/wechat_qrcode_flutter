# wechat_qrcode

scan qrcode by wechat_qrcode lib.

## Getting Started

```
dependencies:
  wechat_qrcode: ^0.0.1
```

```
	// scan camera, return [] if not found
    List<String> result = await WechatQrcode.scanCamera();

	// scan image, return [] if not found
    List<String> result = await WechatQrcode.scanImage(imagePath);

```

