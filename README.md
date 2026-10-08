# 课表 App（WebView 外壳）

把现有 `本周课表.html` 打包成安卓 APK。课表的全部功能（总览、今日课程时间轴、
实时时间线、截图识别）原样保留，**不需要改一行网页代码**。

## 为什么必须用虚拟 https 域

识别引擎依赖 Web Worker，而 **Worker 无法在 `file://` 页面创建**（这类页面源
被视为 null，会被 CORS 拦截）。早期排查时抓到过原始报错：

```
Access to fetch at 'file:///.../tesseract.min.js' from origin 'null'
has been blocked by CORS policy
```

所以本工程用 `WebViewAssetLoader` 把内置资源映射为
`https://local.ck/assets/...` —— 页面拿到正常同源 https 域，Worker 可以创建，
而所有文件都来自 APK 内部，**识别过程完全离线、不需要联网**。

## 目录

```
app/src/main/assets/          课表网页 + 识别引擎（全部内置，约 11MB）
  ├─ index.html               本周课表.html
  ├─ lib/  core/  lang/       识别引擎与中文模型
app/src/main/java/.../MainActivity.kt   WebView 外壳 + 相册选图
app/src/main/java/.../AppBridge.kt     GPS/导航原生接口（占位，未来实现）
```

## 两种构建方式

### 方式 A：GitHub 云端构建（推荐，本机不用装任何东西）

1. 在本目录 `git init`，把所有文件提交并推送到你自己的 GitHub 仓库
2. 推送后 Actions 会自动运行；也可进仓库 → Actions → Build APK → Run workflow
3. 构建完在 Artifacts 里下载 `timetable-debug-apk`，解压得到 `app-debug.apk`

### 方式 B：本机用 Android Studio 构建

1. 安装 Android Studio（自带 SDK）
2. File → Open 选中本目录，等待 Gradle 同步
3. Build → Build Bundle(s)/APK(s) → Build APK(s)

## 安装到手机

把 APK 传到三星手机，点击安装。首次安装会有「未知来源应用」提示，允许即可
（这是侧载安装的正常流程，未使用官方签名密钥）。

## 已做的适配

- 关闭 WebView 强制深色模式，避免浅色课表被反色
- 实现 `onShowFileChooser`，「从相册选择课表截图」可正常调用系统相册
- 返回键：网页内可后退时先后退，否则退出
- 硬件加速开启，保证 canvas 图像处理性能
- 权限清单中已预留定位/通知条目，但**当前版本不会申请**，因此不会有任何弹窗

## 关于将来的 GPS 动态提醒

`AppBridge.kt` 已预留 `getLocation / scheduleReminder / cancelReminder /
navigateTo / getBuildingCoords` 五个接口，当前返回 `not-implemented`。

将来接入时：
1. 在 `AppBridge` 中补充真实实现（FusedLocationProvider + WorkManager 定时）
2. 在 `MainActivity` 增加运行时权限申请
3. 需要处理三星 OneUI 电池白名单（否则后台定时任务会被冻结）
4. 教学楼经纬度与路线折线由你实测后填入 `getBuildingCoords`

网页侧调用 `AppBridge.xxx()` 即可，课表 HTML 无需改动。
