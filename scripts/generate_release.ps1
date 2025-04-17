# Clean previous builds
./gradlew clean

# Build release APK
./gradlew assembleRelease

# Generate mapping file
./gradlew mappingFileRelease

# Create releases directory if it doesn't exist
if (-not (Test-Path -Path "releases")) {
    New-Item -ItemType Directory -Path "releases"
}

# Copy APK to releases folder
Copy-Item -Path "app/build/outputs/apk/release/app-release.apk" -Destination "releases/TimeTracker-v1.0.0.apk"

Write-Host "Release APK generated successfully!"
Write-Host "APK location: releases/TimeTracker-v1.0.0.apk" 