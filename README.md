# Khromia

一个基于 Jetpack Compose & Material 3 的 Android UI 组件库，提供精心设计的设置页面组件、弹窗系统、颜色选择器等常用 UI 构件，所有组件均支持颜色调和（Color Harmonization）与弹性动画。

## 特性

- **Material 3 设计语言** — 全面采用 Material 3 风格，支持 Dynamic Color（Material You）
- **颜色调和** — 所有组件表面色自动与主题主色融合，保持视觉一致性
- **弹性动画** — 展开/折叠、旋转、尺寸变化均使用 Spring 动画
- **预测性返回手势** — BottomSheet 与 EditDialog 支持 Android 13+ 预测性返回动画
- **大屏适配** — BottomSheet 在宽屏设备上自动调整为 80% 宽度
- **Painter / ImageVector 双支持** — 图标参数同时提供两种重载

## 组件一览

| 组件 | 说明 |
|------|------|
| `OptionItem` | 设置行：图标 + 标题 + 副标题 + 开关或自定义尾随内容 |
| `ButtonOption` | 按钮行：图标 + 标题 + 副标题，纯点击操作 |
| `ExpandableOptionItem` | 可展开行：支持外部控制（绑定 Switch）或内部自管理展开状态 |
| `OptionSwitch` | 定制化的 Material 3 开关，缩小尺寸并内置勾选图标 |
| `PrimaryBottomSheet` | 预样式化的底部弹窗，带拖拽手柄和底部栏 |
| `BasicBottomSheet` | 低层级底部弹窗 API，完全自定义拖拽手柄与底部栏 |
| `GlobalToastHost` / `Toast` | 全局 Toast 通知系统，支持图标、错误状态、滑入/滑出动画 |
| `EditDialog` | 多字段编辑对话框，内置数值范围校验、长度限制、自定义验证器 |
| `SquareColorPicker` | 方形 SV 颜色选择器 + 色相滑块 + 随机颜色按钮 |
| `AnimatedFloatingActionButton` | 按压时圆角动画变化的 FAB（标准 / 扩展 / 切换三种变体） |
| `TooltipScope.TextTooltip` | Material 3 Tooltip 扩展，自动颜色调和 |
| `Modifier.fadingEdge()` | 边缘渐隐 Modifier，支持任意方向与强度 |

## 集成

### 1. 添加 GitHub Packages 认证

在 `settings.gradle.kts` 中配置 Maven 仓库：

```kotlin
dependencyResolutionManagement {
    repositories {
        // ... 其他仓库
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/heizigelovecode/Khromia")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                    ?: System.getenv("GITHUB_USER")
                password = providers.gradleProperty("gpr.key").orNull
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

需要在 `gradle.properties` 中配置：

```properties
gpr.user=你的GitHub用户名
gpr.key=你的GitHub Personal Access Token
```

### 2. 添加依赖

```kotlin
dependencies {
    implementation("heizige.kk:khromia:1.6.2")
}
```

## 使用示例

### Toast 通知

```kotlin
// 在应用根组件放置 GlobalToastHost
@Composable
fun App() {
    MaterialTheme {
        GlobalToastHost()
        // ... 你的内容
    }
}

// 任意位置调用
Toast.show("操作成功")
Toast.show("出错了", isError = true)
Toast.show("提示", Icons.Default.Info, isError = false)
```

### 底部弹窗

```kotlin
var showSheet by remember { mutableStateOf(false) }

PrimaryBottomSheet(
    visible = showSheet,
    title = "设置",
    imageVector = Icons.Default.Settings,
    onDismiss = { showSheet = false }
) {
    // 弹窗内容
}
```

### 编辑对话框

```kotlin
EditDialog(
    visible = showDialog,
    title = "编辑信息",
    fields = listOf(
        EditFieldConfig(label = "姓名", maxLength = 20),
        EditFieldConfig(label = "年龄", keyboardType = KeyboardType.Number, range = 0.0..150.0),
        EditFieldConfig(label = "邮箱", onValidate = {
            if (it.contains("@")) null else "请输入有效邮箱"
        })
    ),
    onDismiss = { showDialog = false },
    onConfirm = { values -> /* values[0], values[1], values[2] */ }
)
```

### 颜色选择器

```kotlin
var color by remember { mutableStateOf(Color.Blue) }

SquareColorPicker(
    initialColor = color,
    onColorChanged = { color = it }
)
```

### 设置行

```kotlin
// 开关行
OptionItem(
    imageVector = Icons.Default.DarkMode,
    title = "深色模式",
    subtitle = "启用深色主题",
    checked = isDark,
    onCheckedChange = { isDark = it }
)

// 可展开行
ExpandableOptionItem(
    imageVector = Icons.Default.Notifications,
    title = "通知设置",
    initiallyExpanded = false
) {
    Text("展开后的内容")
}
```

### 边缘渐隐

```kotlin
LazyColumn(
    modifier = Modifier.fadingEdge(top = 16.dp, bottom = 16.dp, strength = 0.8f)
) {
    // ...
}
```

## 技术栈

- **Kotlin** 2.4.0
- **Jetpack Compose** BOM 2026.05.01
- **Material 3** (含 Window Size Class)
- **Min SDK**: 24 (Android 7.0)
- **Target / Compile SDK**: 37
- **Java**: 21

## 项目结构

```
Khromia/
├── app/                  # 演示应用
│   └── src/main/java/
│       └── heizige.kk.khromia/
│           ├── MainActivity.kt
│           └── ui/theme/
├── khromia/              # 组件库模块
│   └── src/main/java/
│       └── heizige.kk.khromia/
│           ├── components/   # UI 组件
│           ├── layout/       # 弹窗基础设施
│           ├── helper/       # 工具类
│           ├── data/         # 数据模型
│           └── text/         # 文本样式
└── gradle/
    └── libs.versions.toml
```

## 致谢

- Toast 通知组件与 BottomSheet 底部弹窗的设计灵感来源于 [ImageToolbox](https://github.com/T8RIN/ImageToolbox) 项目

## License

本项目仅供学习与个人使用。
