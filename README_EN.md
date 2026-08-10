# Khromia

An Android UI component library built on Jetpack Compose & Material 3, providing polished settings rows, popup systems, a color picker, and more. All components feature color harmonization and spring-based animations.

## Features

- **Material 3 Design** — Full Material 3 styling with Dynamic Color (Material You) support
- **Color Harmonization** — All surface colors are automatically blended with the theme primary for visual cohesion
- **Spring Animations** — Expand/collapse, rotation, and size transitions use carefully tuned spring specs
- **Predictive Back Gestures** — BottomSheet and EditDialog support Android 13+ predictive back animations
- **Adaptive Layout** — BottomSheet automatically adjusts to 80% width on large screens
- **Painter / ImageVector** — Icon parameters accept both `Painter` and `ImageVector` via overloads

## Components

| Component | Description |
|-----------|-------------|
| `OptionItem` | Settings row: icon + title + subtitle + switch or custom trailing content |
| `ButtonOption` | Button row: icon + title + subtitle, click-only action |
| `ExpandableOptionItem` | Expandable row: externally controlled (bound to Switch) or self-managed expansion |
| `OptionSwitch` | Customized Material 3 switch with reduced size and built-in check icon |
| `PrimaryBottomSheet` | Pre-styled bottom sheet with drag handle and bottom bar |
| `BasicBottomSheet` | Low-level bottom sheet API with fully customizable drag handle and bottom bar |
| `GlobalToastHost` / `Toast` | Global toast notification system with icon, error state, and slide animations |
| `EditDialog` | Multi-field edit dialog with numeric range validation, max length, and custom validators |
| `SquareColorPicker` | Square SV color picker + vertical hue slider + random color button |
| `AnimatedFloatingActionButton` | FAB with press-animated corner radius (Standard / Extended / Toggle variants) |
| `TooltipScope.TextTooltip` | Material 3 Tooltip extension with automatic color harmonization |
| `Modifier.fadingEdge()` | Edge fading modifier with configurable direction and strength |

## Integration

### 1. Configure GitHub Packages Authentication

Add the Maven repository in `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        // ... other repositories
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

Add credentials in `gradle.properties`:

```properties
gpr.user=your_github_username
gpr.key=your_github_personal_access_token
```

### 2. Add Dependency

```kotlin
dependencies {
    implementation("heizige.kk:khromia:1.6.2")
}
```

## Usage

### Toast Notifications

```kotlin
// Place GlobalToastHost at the root of your app
@Composable
fun App() {
    MaterialTheme {
        GlobalToastHost()
        // ... your content
    }
}

// Show toast from anywhere
Toast.show("Operation successful")
Toast.show("Something went wrong", isError = true)
Toast.show("Info", Icons.Default.Info, isError = false)
```

### Bottom Sheet

```kotlin
var showSheet by remember { mutableStateOf(false) }

PrimaryBottomSheet(
    visible = showSheet,
    title = "Settings",
    imageVector = Icons.Default.Settings,
    onDismiss = { showSheet = false }
) {
    // Sheet content
}
```

### Edit Dialog

```kotlin
EditDialog(
    visible = showDialog,
    title = "Edit Info",
    fields = listOf(
        EditFieldConfig(label = "Name", maxLength = 20),
        EditFieldConfig(label = "Age", keyboardType = KeyboardType.Number, range = 0.0..150.0),
        EditFieldConfig(label = "Email", onValidate = {
            if (it.contains("@")) null else "Please enter a valid email"
        })
    ),
    onDismiss = { showDialog = false },
    onConfirm = { values -> /* values[0], values[1], values[2] */ }
)
```

### Color Picker

```kotlin
var color by remember { mutableStateOf(Color.Blue) }

SquareColorPicker(
    initialColor = color,
    onColorChanged = { color = it }
)
```

### Settings Rows

```kotlin
// Switch row
OptionItem(
    imageVector = Icons.Default.DarkMode,
    title = "Dark Mode",
    subtitle = "Enable dark theme",
    checked = isDark,
    onCheckedChange = { isDark = it }
)

// Expandable row
ExpandableOptionItem(
    imageVector = Icons.Default.Notifications,
    title = "Notification Settings",
    initiallyExpanded = false
) {
    Text("Expanded content goes here")
}
```

### Fading Edge

```kotlin
LazyColumn(
    modifier = Modifier.fadingEdge(top = 16.dp, bottom = 16.dp, strength = 0.8f)
) {
    // ...
}
```

## Tech Stack

- **Kotlin** 2.4.0
- **Jetpack Compose** BOM 2026.05.01
- **Material 3** (with Window Size Class)
- **Min SDK**: 24 (Android 7.0)
- **Target / Compile SDK**: 37
- **Java**: 21

## Project Structure

```
Khromia/
├── app/                  # Demo application
│   └── src/main/java/
│       └── heizige.kk.khromia/
│           ├── MainActivity.kt
│           └── ui/theme/
├── khromia/              # Component library module
│   └── src/main/java/
│       └── heizige.kk.khromia/
│           ├── components/   # UI components
│           ├── layout/       # Popup infrastructure
│           ├── helper/       # Utilities
│           ├── data/         # Data models
│           └── text/         # Text styles
└── gradle/
    └── libs.versions.toml
```

## Acknowledgments

- Toast notification component design inspired by [ImageToolbox](https://github.com/T8RIN/ImageToolbox)

## License

This project is for educational and personal use only.
