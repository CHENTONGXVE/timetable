# 从零开始：把课表工程变成 APK

全程约 15 分钟，不需要懂编程，不需要装 Android Studio。

---

## 第 0 步：先明确一件事

**GitHub 是一个网站，不需要下载。**

我们要做的是：把工程文件上传到这个网站 → 网站免费帮我们编译 → 下载编译好的 APK。

> 如果你还没注册过 GitHub，下面从注册开始讲。
> 如果你已经有账号，直接跳到第 3 步。

---

## 第 1 步：重新下载工程包（重要）

我更新了云端构建配置，**请用最新的一版**。

- 下载 `课表APK工程.zip`
- 在电脑上解压，得到一个 `android` 文件夹

**记住这个 `android` 文件夹在哪**（比如放在桌面最方便）。

> ⚠️ 解压后如果里面还有一个叫「网页版」的文件夹，那个不需要上传，忽略它。
> 我们只需要 `android` 文件夹里的内容（共 25 个文件）。

---

## 第 2 步：注册 GitHub 账号

1. 浏览器打开 **https://github.com**
2. 右上角点 **Sign up**
3. 填邮箱 → 密码 → 用户名
4. 邮箱会收到验证码，填进去
5. 后面的引导问题随便选（选 "Skip personalization" 也行）

注册完成后，**邮箱一定要验证**，否则后面无法创建仓库。

---

## 第 3 步：下载 GitHub Desktop（推荐，最省事）

虽然可以直接在网页上传，但网页上传**不支持拖整个文件夹**，我们工程有 25 个嵌套文件，一个个传会疯掉。

装了这个软件，拖一次就完事。

1. 打开 **https://desktop.github.com**
2. 点 **Download for Windows**（或 Mac 版）
3. 下载完点安装，一路 Next
4. 打开软件 → **Sign in to GitHub.com** → 浏览器授权登录

---

## 第 4 步：创建仓库

**方式 A：用 GitHub Desktop（推荐）**

1. 打开软件，点 **File → New repository**（或 "Create a New Repository on your hard drive"）
2. 填写：
   - **Name**：填 `timetable`（只能用英文和数字，别用中文）
   - **Local path**：点 Choose，选一个位置（比如桌面）
   - 其他保持默认
3. 点 **Create repository**

**方式 B：网页创建**

1. 打开 https://github.com/new
2. Repository name 填 `timetable`
3. **勾选 Public**（免费账号必须公开才能用 Actions）
4. ⚠️ **下面三个选项都不要勾**：Add README、Add .gitignore、Choose license
5. 点 **Create repository**

---

## 第 5 步：把工程文件放进去

这是最关键的一步，**注意目录结构**。

1. 打开第 4 步创建的仓库文件夹（桌面上应该有个 `timetable` 文件夹）
2. 把第 1 步解压出来的 **`android` 文件夹里的所有内容**，复制进去

**正确的结果应该是这样**：

```
timetable/                    ← 仓库根目录
├── .github/                  ← 必须有，隐藏文件夹
│   └── workflows/
│       └── build-apk.yml
├── app/
│   ├── build.gradle.kts
│   └── src/
│       └── main/
│           ├── assets/       ← 课表 + 识别引擎（11MB）
│           ├── java/
│           ├── res/
│           └── AndroidManifest.xml
├── .gitignore
├── build.gradle.kts
├── gradle.properties
├── README.md
└── settings.gradle.kts
```

⚠️ **常见错误**：变成 `timetable/android/app/...`（多了一层 android）。
如果是这样就错了，自动化脚本找不到文件。请确认 `app` 和 `.github` 是直接在 `timetable` 下面的。

> 💡 看不到 `.github`？Windows 资源管理器默认显示隐藏文件，点开头的文件要开启显示：
> 资源管理器 → 查看 → 勾选「隐藏的项目」。
> 但即使看不见也没关系，复制时它会被一起带上。

---

## 第 6 步：提交并推送

用 GitHub Desktop 的话：

1. 软件会自动发现所有新文件，左下角显示 "25 files"
2. 左下角 **Summary** 框里随便填，比如 `init`
3. 点 **Commit to main**
4. 然后点顶部 **Publish repository**
   - 如果问 Keep this code private，**取消勾选**（免费账号私有仓库用不了 Actions）
5. 点 **Publish**

推送完成后，浏览器打开 https://github.com/你的用户名/timetable 就能看到文件了。

---

## 第 7 步：看它编译

刚才那一步推送完，**编译会自动开始**。

1. 打开 https://github.com/你的用户名/timetable
2. 点顶部 **Actions** 标签
3. 会看到一条正在运行的任务（黄色圆点 = 进行中）
4. **等 5–10 分钟**
5. 变成**绿色 ✓** 就成功了；**红色 ✗** 是失败（看第 9 步）

> 如果没自动开始：进 Actions → 左边点 **Build APK** → 右边点 **Run workflow** → 再点一次 Run workflow。

---

## 第 8 步：下载 APK

1. 点进那条绿色 ✓ 的任务
2. 页面往下拉，找到 **Artifacts** 区域
3. 点 **timetable-debug-apk** 开始下载（是个 zip）
4. 下载完解压，得到 **`app-debug.apk`**
5. 把它传到手机上（微信文件传输助手、QQ、数据线都行）
6. 手机点开安装 → 提示「未知来源应用」→ 允许

装完桌面就有「课表」图标了，点开即用，**飞行模式下也能识别截图**。

---

## 第 9 步：如果编译失败（红色 ✗）

点进失败的任务 → 点 **build** 那一行 → 展开看红色报错文字。

**把报错内容整段复制发给我**，我来判断怎么改。

最常见的几种：
- `Gradle version` 相关 → 我已加固配置，一般不会再出
- `SDK` 相关 → 云端环境问题，重跑一次可能就好
- `assets` 找不到 → 第 5 步目录结构放错了

修改后重新上传：GitHub Desktop 里改完文件 → Commit → **Push origin**（顶部按钮），会自动重新编译。

---

## 常见问题

**Q：仓库必须公开吗？**
免费账号是的。代码公开但只有你自己能改，别人看到也没关系（就是个课表 App）。

**Q：APK 会被别人下载吗？**
不会。Artifacts 需要登录 GitHub 才能下载，而且默认 90 天后过期。

**Q：以后改了课表 HTML 怎么办？**
把新的 HTML 覆盖 `app/src/main/assets/index.html`，然后 Desktop 里 Commit → Push，自动重新编译出新 APK。

**Q：手机上怎么更新？**
新 APK 直接覆盖安装就行，数据不会丢。

---

## 将来加 GPS 时

工程里的 `AppBridge.kt` 已经预留了五个接口，现在返回 `not-implemented`。等你要做动态提醒时，只改那一个文件即可，课表网页不用动。
