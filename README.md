# Sambalpuri Keyboard (ସମ୍ବଲପୁରୀ କୀବୋର୍ଡ଼)

A native Android keyboard and cultural sticker platform celebrating the rich linguistic heritage, folklore, and traditions of Western Odisha (Kosali / Sambalpuri).

---

## What's New in this Update

### 1. Dual Language Typing (Odia & English)
- **Odia Script Layout**: Complete vowels, consonants, matras (vowel signs), punctuation (including the Odia full-stop danda `।` and double danda `॥`), and Kosali vocabulary.
- **English QWERTY Layout**: Full Roman alphabet with uppercase/lowercase shift, numbers, and symbols.
- **Instant Language Switching**:
  - Dedicated language-switch button (`🌐 ENG` / `🌐 ଓଡ଼ିଆ`) located directly on the bottom row alongside the spacebar.
  - Active language badge on the top toolbar and displayed on the spacebar label.
  - Switches instantly without clearing or altering already typed text.
  - Remembers your preferred starting language across sessions.
- **Numbers & Symbols**: Both Odia numerals (`୦ ୧ ୨ ୩ ୪ ୫ ୬ ୭ ୮ ୯`) and standard numbers (`0-9`) with punctuation.

### 2. Fully Customizable Stickers & Emojis
- **Dedicated Emoji & Sticker Panel**:
  - Unicode emoji categories with Sambalpuri cultural expressions.
  - **Favourites ❤️**: Mark any sticker or emoji as a favorite for instant access.
  - **Recently Used 🕒**: Automatically tracks your most recent stickers and emojis.
  - **My Stickers ⭐**: Browse your own personal creations separately from bundled items.
- **"+ Add My Own" One-Tap Import**:
  - A prominent "+ Add My Own" button right inside the keyboard's sticker panel.
  - Opens Android's system Photo Picker (no dangerous permissions needed).
  - Preview the image, name it, assign a Sambalpuri label, and save it instantly.
  - Persisted in app-private storage (`filesDir/custom_stickers/`) across device reboots.
- **Direct Keyboard Delivery & Fallback**:
  - Delivers images directly to supported apps (WhatsApp, Telegram, Messages) via `InputConnection.commitContent()`.
  - Automatically invokes the system Share Chooser if a text field doesn't support direct image insertion.

---

## How Users Can Add Their Own Emojis & Stickers

After installing the APK:
1. Open any chat app and open Sambalpuri Keyboard.
2. Tap the **😊 / 🎨** button to open the Emoji & Sticker panel.
3. Switch to the **Stickers** tab and tap **"+ Add My Own"** (or open the Sambalpuri Keyboard app and tap **"My Stickers"**).
4. Pick any image (PNG, WebP, JPEG) from your phone's photo library.
5. Preview the image, enter a name (e.g., *"My Reaction"* or *"ମୋର ଷ୍ଟିକର୍"*), and tap **Save**.
6. The custom sticker immediately appears in your keyboard and can be sent to friends anytime!

---

## Building the APK

### Automated GitHub Actions Build
This repository includes a GitHub Actions workflow at `.github/workflows/android-build.yml`.
On every push to `main`:
1. The project is checked out on `ubuntu-latest`.
2. Java 17 and Gradle are configured.
3. The debug APK is built via `gradle assembleDebug`.
4. The generated APK is uploaded as a downloadable artifact: `sambalpuri-keyboard-debug-apk`.

### Local Build via Terminal
```bash
# Build the debug APK
gradle assembleDebug

# Output location:
# app/build/outputs/apk/debug/app-debug.apk
```
