# SmartStyle

SmartStyle is an Android application that provides AI-powered fashion advice and style recommendations. It uses OpenAI's GPT models to generate personalized responses to user queries about fashion and style, combined with advanced image analysis capabilities.

## Features

- **AI Chat Assistant**: Ask questions about fashion, style tips, outfit recommendations, and more
- **Image Analysis**: Upload photos of clothing items or outfits for AI analysis and recognition
- **Fashion-Specific Image Labeling**: Automatically identifies clothing items and accessories in photos
- **Image Embedding**: Extracts feature vectors from clothing images for similarity matching
- **Simple and Intuitive UI**: Clean Jetpack Compose interface for easy interaction with the AI assistant
- **Planned Features**:
  - Style Recommendations: Get personalized style recommendations based on your preferences
  - Fashion Trend Updates: Stay informed about the latest fashion trends
  - Similar Item Search: Find clothing items similar to ones in your photos

## Technologies Used

- **Kotlin 2.1.21**: Primary programming language
- **Jetpack Compose 1.9.4**: Modern UI toolkit for building native Android UI
- **Hilt 2.56.2**: Dependency injection
- **Retrofit 2.9.0/3.0.0**: Type-safe HTTP client for API calls
- **Room 2.8.3**: Database for local storage
- **Coroutines & Flow 1.10.2**: For asynchronous programming
- **OpenAI API**: For AI-powered responses using the GPT-4o-mini model
- **ML Kit 17.0.9**: For on-device image analysis and recognition with 60% confidence threshold
- **TensorFlow Lite 2.17.0**: For image embedding extraction using MobileNet V3
- **Timber 5.0.1**: For logging

## Machine Learning Features

### Image Analysis with ML Kit
The app uses Google's ML Kit for on-device image labeling with a 60% confidence threshold. It specifically filters for fashion-related items including:
- Clothing items (shirts, pants, dresses, etc.)
- Accessories (bags, shoes, watches, etc.)
- Style elements (outfits, fashion trends)

### Image Embedding with TensorFlow Lite
SmartStyle extracts 1024-dimensional feature vectors from clothing images using a MobileNet V3 model, enabling:
- Feature extraction for similarity search
- Potential for clothing item matching
- Foundation for recommendation systems

## Setup

1. Clone the repository
2. Open the project in Android Studio
3. Replace the placeholder API key in `AIRepositoryImpl.kt` with your OpenAI API key:
   - Open the file `app/src/main/java/com/smartstyle/data/repository/AIRepositoryImpl.kt`
   - Find the empty YOUR_KEY constant at the top of the file
   - Replace the empty string with your actual OpenAI API key
4. Ensure you have the necessary Android SDK components installed (compileSdk 36)
5. Build and run the application on an emulator or physical device (minimum SDK 24)

## Getting Started

### Prerequisites
- Android Studio Iguana or later
- OpenAI API key with access to GPT-4o-mini model
- Android device or emulator running Android 7.0 (API 24) or higher

### Installation
1. Clone the repository: `git clone https://github.com/yourusername/SmartStyle.git`
2. Open the project in Android Studio
3. Add your OpenAI API key to `AIRepositoryImpl.kt`
4. Sync Gradle and build the project
5. Run on your device or emulator

## Usage

1. Launch the app
2. Type your fashion or style question in the text field
3. Tap the "Send" button to get AI-powered fashion advice
4. Use the camera button to take or select photos for analysis
5. View detailed analysis of clothing items in your photos

## Project Structure

- **app/src/main/java/com/smartstyle/**
  - **data/**: Contains repository implementations, API services, and local database
  - **di/**: Dependency injection modules
  - **domain/**: Use cases and business logic
  - **ml/**: Machine learning components for image analysis and embedding
  - **ui/**: UI components, screens, and ViewModels
  - **util/**: Utility classes and helper functions

## Requirements

- Android 7.0 (API level 24) or higher
- Internet connection for AI functionality
- OpenAI API key
- ~100MB of storage space for the app and ML models

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

We welcome contributions to SmartStyle! Here's how you can help:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Commit your changes**: `git commit -m 'Add some amazing feature'`
4. **Push to the branch**: `git push origin feature/amazing-feature`
5. **Open a Pull Request**

Please make sure to update tests as appropriate and adhere to the existing coding style.
