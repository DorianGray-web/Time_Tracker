#!/bin/bash

# Clean previous builds
./gradlew clean

# Build release APK
./gradlew assembleRelease

# Generate mapping file
./gradlew mappingFileRelease

# Copy APK to releases folder
mkdir -p releases
cp app/build/outputs/apk/release/app-release.apk releases/TimeTracker-v1.0.0.apk

# Generate QR code for APK
qrencode -o releases/TimeTracker-v1.0.0.png "https://example.com/download/TimeTracker-v1.0.0.apk"

echo "Release APK generated successfully!"
echo "APK location: releases/TimeTracker-v1.0.0.apk"
echo "QR code location: releases/TimeTracker-v1.0.0.png" 