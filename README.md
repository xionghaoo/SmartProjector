# 智能投影仪

## 主板信息

```
display info: DisplayMetrics{density=1.0, width=1920, height=1080, scaledDensity=1.0, xdpi=101.6, ydpi=101.6}
```

1、缩放因子 = 1，因此屏幕的资源匹配属于mdpi，实际显示的图片会非常模糊，应该替换mdpi的图片为xxhdpi图片

2、最小适配sw宽度 = 1080(竖直方向)，可是实际的屏幕宽度是1920(水平方向)。因为设计稿是横屏，因此宽度是手机的高度(假设为1920)，
这里将设计稿映射到1920宽度的屏幕上，图标太大，将1080 dimens的尺寸按1920/1080的比例改小一点。

## 测试机分辨率

1、小米4pad
```
DisplayMetrics{density=2.0, width=1920, height=1104, scaledDensity=2.0, xdpi=284.859, ydpi=283.534}
```

## 待开发

1. 提分系统

## 相关资源

### 腾讯小微接入

1. [TVS SDK接入说明](https://dingdang.qq.com/doc/page/432)

### 消息推送
2. [阿里云辅助通道](https://help.aliyun.com/document_detail/30067.html?spm=a2c4g.11186623.6.586.57a82e97w5yHgn)

## 其他

wifi密码存储位置：
```
$ cd /data/misc/wifi/
```

## 问题记录

1. google官方的串口库支持7.1.1(25)以下的targetSdk，8.0以上的设备没有权限读取/proc/tty/drivers设备

2. ViewPager2对于焦点处理有问题，nextFocusDown等方向键焦点无法自动获取，ViewPager正常（只有在使用导航键时）

3. 涂鸦的用户注册需要放到后端

## 命令

1. 获取应用包名
```shell script
aapt dump badging <path-to-apk> | grep package:\ name
```

2. 获取应用启动页
```shell script
aapt d badging jxwgb_kypc.apk | grep launchable-activity
```

3. 列出系统安装的apk包名
```shell script
pm list packages -f
```