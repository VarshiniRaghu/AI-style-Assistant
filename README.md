# SmartStyle

SmartStyle is an Android application that provides AI-powered fashion advice and style recommendations. It uses OpenAI's GPT models to generate personalized responses to user queries about fashion and style.

## Features

- **AI Chat Assistant**: Ask questions about fashion, style tips, outfit recommendations, and more
- **Image Analysis**: Upload photos of clothing items or outfits for AI analysis and recognition
- **Simple and Intuitive UI**: Clean interface for easy interaction with the AI assistant
- **Planned Features**:
  - Style Recommendations: Get personalized style recommendations based on your preferences
  - Fashion Trend Updates: Stay informed about the latest fashion trends
  - Similar Item Search: Find clothing items similar to ones in your photos

## Technologies Used

- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern UI toolkit for building native Android UI
- **Hilt**: Dependency injection
- **Retrofit**: Type-safe HTTP client for API calls
- **Room**: Database for local storage
- **Coroutines & Flow**: For asynchronous programming
- **OpenAI API**: For AI-powered responses (GPT-4o-mini)
- **ML Kit**: For image analysis and recognition
- **TensorFlow Lite**: For image embedding and feature extraction

## Setup

1. Clone the repository
2. Open the project in Android Studio
3. Replace the placeholder API key in `AIRepositoryImpl.kt` with your OpenAI API key
   ```kotlin
   val res = api.createChatCompletion("Bearer YOUR_KEY", req)
   ```
4. Build and run the application on an emulator or physical device

## Usage

1. Launch the app
2. Type your fashion or style question in the text field
3. Tap the "Send" button
4. View the AI's response

## Project Structure

- **app/src/main/java/com/smartstyle/**
  - **data/**: Contains repository implementations, API services, and local database
  - **di/**: Dependency injection modules
  - **domain/**: Use cases and business logic
  - **ml/**: Machine learning components for image analysis and embedding
  - **ui/**: UI components, screens, and ViewModels
  - **util/**: Utility classes and helper functions

## Requirements

- Android 6.0 (API level 23) or higher
- Internet connection for AI functionality
- OpenAI API key

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
