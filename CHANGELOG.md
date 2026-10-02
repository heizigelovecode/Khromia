# Changelog

All notable changes to Khromia will be documented in this file.

## 1.6.5 - 2026-10-02

### Added

- Added `validateEditFields(fields, values, invalidNumberError, rangeErrorTemplate, maxLengthErrorTemplate)`: the per-field validation rules previously inlined in `EditDialog`, extracted into a pure function with no Composable dependency. The error strings are passed in as parameters because the originals came from `stringResource` and a pure function cannot capture a Composable scope. `EditDialog` now calls it; the Miuix branch (`KedgeEditDialog` → `MiuixEditDialog` in Kedge) shares the same rules instead of reimplementing them.

### Changed

- `EditDialog` no longer returns immediately when `visible` is `false`. It now keeps the composition alive long enough to play the exit animation (`fadeOut` + `scaleOut`) and only then drops out, so dismissing the dialog is no longer an instant disappearance. **Callers must now always invoke the composable and drive it with `visible`** — the previous `if (show) EditDialog(visible = true)` pattern removes the dialog from the composition the instant `show` goes false, which makes the exit animation unreachable.
- `SliderItem`'s reset confirmation now uses `AnimatedAlertDialog` instead of the raw `androidx.compose.material3.AlertDialog`. The native dialog bypassed this library's window host and motion, and leaked MD3 chrome (corner radius, typography) under the Miuix style.

### Notes

- `OptionItem` (the slot-based overload) and `EditDialog` are **pure MD3 implementations**. The Miuix branches are provided by `heizige.kk.kedge.components.KedgeOptionItem` and `heizige.kk.kedge.overlays.KedgeEditDialog`. Do not add Miuix code here — Khromia intentionally carries no Miuix dependency.
- `material3` is pinned to `1.5.0-alpha29` and must stay in sync with KhatKit and Kedge: each module compiles against its own declaration but a single APK resolves to the highest version, and a mismatch compiles fine while throwing `NoSuchMethodError` at runtime (this is how `FancySlider` broke once already).