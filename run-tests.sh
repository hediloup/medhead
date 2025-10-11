#!/bin/bash

# Simple wrapper script to run all tests from the project root
# This script delegates to the main test runner in the scripts directory

echo "🧪 MedHead Application - Test Runner"
echo "===================================="
echo "Delegating to comprehensive test suite..."
echo ""

# Change to the scripts directory and run the main test script
cd scripts

# Check if the main test script exists
if [ ! -f "run-all-tests.sh" ]; then
    echo "❌ Error: Main test script not found in scripts/run-all-tests.sh"
    exit 1
fi

# Make sure the script is executable
chmod +x run-all-tests.sh

# Run the comprehensive test suite
./run-all-tests.sh

# Return to the original directory
cd ..
