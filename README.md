# Outfit Analyser

An Android app that analyses outfit photos using on-device ML Kit and Google Gemini Vision, then saves your style history locally with Room.

---

## Features

- **Camera & gallery capture** — take a new photo or pick one from your library
- **On-device label detection** — ML Kit identifies clothing items instantly, no network needed
- **Gemini AI style advice** — `gemini-2.0-flash` returns a style assessment, actionable tips, and outfit tags
- **Outfit tags** — each analysis is tagged by style category, colours, season, occasion, and garments
- **History tab** — all saved analyses stored in Room; expand any card for full details or delete with one tap
- **Clean MVVM architecture** — Hilt DI, Jetpack Compose UI, Repository pattern, Kotlin Coroutines + Flow

---

## Tech Stack

| Category | Library / Version |
|----------|------------------|
| Language | Kotlin 2.1.21 |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose 2.8.9 |
| AI / Vision | Google Generative AI SDK 0.9.0 (`gemini-2.0-flash`) |
| On-device ML | ML Kit Image Labeling 17.0.9 |
| Database | Room 2.8.3 |
| DI | Hilt 2.56.2 |
| Image loading | Coil 2.7.0 |
| Async | Coroutines + Flow 1.10.2 |
| Min SDK | 24 (Android 7.0) |
| Compile SDK | 36 |

---

## Project Structure

```
app/src/main/java/com/smartstyle/
├── App.kt                          # Hilt application entry point
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt          # Room database
│   │   ├── OutfitAnalysisDao.kt    # Insert, delete, Flow queries
│   │   └── OutfitAnalysisEntity.kt # DB entity + toDomain() mapper
│   ├── remote/
│   │   └── GeminiService.kt        # Gemini Vision call + JSON parser
│   └── repository/
│       ├── OutfitRepository.kt     # Interface
│       └── OutfitRepositoryImpl.kt # ML Kit → Gemini → Room
├── di/
│   └── AppModule.kt                # Hilt providers
├── domain/
│   └── model/
│       └── OutfitAnalysis.kt       # Clean domain model
├── ml/
│   └── MlKitImageAnalyzer.kt       # On-device labeling, fashion keyword filter
├── ui/
│   ├── MainActivity.kt
│   ├── navigation/
│   │   └── AppNavigation.kt        # Bottom nav: Analyse ↔ History
│   └── screens/
│       ├── analyse/
│       │   ├── AnalyseScreen.kt
│       │   └── AnalyseViewModel.kt
│       └── history/
│           ├── HistoryScreen.kt
│           └── HistoryViewModel.kt
└── util/
    └── Result.kt                   # Success / Error sealed class
```

---

## Setup

### Prerequisites

- Android Studio Hedgehog or later
- Android device or emulator running API 24+
- A [Google AI Studio](https://aistudio.google.com) API key with Gemini access

### 1. Clone the repo

```bash
git clone https://github.com/yourusername/OutfitAnalyser.git
cd OutfitAnalyser
```

### 2. Add your Gemini API key

Open `local.properties` (project root) and add:

```properties
GEMINI_API_KEY=your_api_key_here
```

> `local.properties` is in `.gitignore` — your key will never be committed.

### 3. Build and run

Open the project in Android Studio, let Gradle sync, then run on a device or emulator.

---

## How It Works

```
Photo selected (camera / gallery)
        │
        ▼
ML Kit on-device labeling
  → detects clothing items at ≥60% confidence
  → filters to fashion-relevant labels
        │
        ▼
Gemini Vision (gemini-2.0-flash)
  → receives bitmap + ML Kit label hints
  → returns JSON: assessment, suggestions[], tags[]
        │
        ▼
OutfitAnalysis domain model
        │
    ┌───┴───────────────┐
    ▼                   ▼
Display results     Save to Room DB
(AnalyseScreen)     + JPEG to filesDir
                    (HistoryScreen)
```

### Gemini prompt format

The model is asked to return structured JSON only:

```json
{
  "assessment": "2-3 sentence style assessment",
  "suggestions": ["tip 1", "tip 2", "tip 3"],
  "tags": ["casual", "navy blue", "summer", "weekend", "denim jacket"]
}
```

Tags cover: style category · main colours · season · occasion · notable garments.

---

## Permissions

| Permission | Reason |
|-----------|--------|
| `CAMERA` | Take outfit photos |
| `INTERNET` | Gemini API calls |
| `READ_MEDIA_IMAGES` | Gallery access on Android 13+ |

---

## License

MIT License — see [LICENSE](LICENSE) for details.
