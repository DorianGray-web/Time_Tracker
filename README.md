# TimeTracker

A modern Android application for tracking work hours and managing time entries. Built with Jetpack Compose and Material 3 design.

## Features

- 🕒 Time entry management (CRUD operations)
- 📊 Work summary and overtime calculations
- 🌓 Light/Dark theme support with dynamic colors (Android 12+)
- 🌐 Multi-language support (English, Ukrainian, Dutch)
- 📱 Adaptive layouts for different screen sizes
- 🔍 Advanced filtering and sorting
- 📤 Export functionality (PDF/CSV)
- ♿ Accessibility features (TalkBack support)
- 🔄 Data persistence with DataStore
- 💾 Backup and restore functionality

## Technical Details

### Architecture
- MVVM (Model-View-ViewModel)
- Clean Architecture principles
- Dependency Injection with Hilt
- Coroutines for asynchronous operations
- StateFlow for reactive UI updates

### UI
- Jetpack Compose
- Material 3 design system
- Adaptive layouts for phones and tablets
- Dark/Light theme support
- RTL support
- Accessibility features
- Smooth animations and transitions

### Data Layer
- DataStore for preferences
- Room for local database
- Coroutines for async operations
- Flow for reactive data streams

### Libraries
- Jetpack Compose
- Material 3
- Hilt
- DataStore
- Room
- Coil
- Timber

## Building and Running

### Prerequisites
- Android Studio Hedgehog | 2023.1.1
- JDK 17
- Android SDK 34

### Building
1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build the project (Build > Make Project)

### Running
1. Connect an Android device or start an emulator
2. Click Run > Run 'app'

## Localization

The app supports three languages:
- English (default)
- Ukrainian
- Dutch

## Screenshots

[Add screenshots here]

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Release Notes

### v1.0.0
- Initial release
- Complete time tracking functionality
- Multi-language support
- Theme customization
- Backup and restore
- Export capabilities
- Accessibility features 