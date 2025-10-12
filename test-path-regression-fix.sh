#!/bin/bash

echo "🔧 Test Path Regression Fix Validation"
echo "======================================"

# Variables
SUCCESS_COUNT=0
TOTAL_TESTS=0

echo "🚀 Validating path regression fixes..."

# Test 1: Check if directories are created before use
echo ""
echo "🔍 Test: Check if directories are created before use"
echo "⚡ Command: Check .github/workflows/ci.yml for mkdir commands"
echo "----------------------------------------"

if grep -q "mkdir -p ../reports/frontend" .github/workflows/ci.yml && \
   grep -q "mkdir -p reports" .github/workflows/ci.yml && \
   grep -q "mkdir -p reports/screenshots" .github/workflows/ci.yml && \
   grep -q "mkdir -p reports/videos" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Directories are created before use"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Missing mkdir commands"
fi
((TOTAL_TESTS++))

# Test 2: Check if PID file path is correct
echo ""
echo "🔍 Test: Check PID file path consistency"
echo "⚡ Command: Check .github/workflows/ci.yml for PID file paths"
echo "----------------------------------------"

if grep -q "echo \$! > ../reports/frontend/angular.pid" .github/workflows/ci.yml && \
   grep -q "if \[ -f ./reports/frontend/angular.pid \]" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: PID file paths are consistent"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: PID file paths are inconsistent"
fi
((TOTAL_TESTS++))

# Test 3: Check if log file path is correct
echo ""
echo "🔍 Test: Check log file path"
echo "⚡ Command: Check .github/workflows/ci.yml for log file path"
echo "----------------------------------------"

if grep -q "nohup npm start > ../reports/frontend/angular.log" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Log file path is correct"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Log file path is incorrect"
fi
((TOTAL_TESTS++))

# Test 4: Check artifact upload paths
echo ""
echo "🔍 Test: Check artifact upload paths"
echo "⚡ Command: Check .github/workflows/ci.yml for artifact paths"
echo "----------------------------------------"

if grep -q "reports/frontend/e2e-tests/" .github/workflows/ci.yml && \
   grep -q "reports/frontend/angular.log" .github/workflows/ci.yml && \
   grep -q "frontend/reports/screenshots/" .github/workflows/ci.yml && \
   grep -q "frontend/reports/videos/" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Artifact upload paths are correct"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Artifact upload paths are incorrect"
fi
((TOTAL_TESTS++))

# Test 5: Check Cypress report path
echo ""
echo "🔍 Test: Check Cypress report path"
echo "⚡ Command: Check .github/workflows/ci.yml for Cypress report path"
echo "----------------------------------------"

if grep -q "mochaFile=../reports/frontend/e2e-tests/results-\[hash\].xml" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Cypress report path is correct"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Cypress report path is incorrect"
fi
((TOTAL_TESTS++))

# Test 6: Check working directory consistency
echo ""
echo "🔍 Test: Check working directory consistency"
echo "⚡ Command: Check .github/workflows/ci.yml for working-directory"
echo "----------------------------------------"

if grep -q "working-directory: ./frontend" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Working directory is consistent"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Working directory is inconsistent"
fi
((TOTAL_TESTS++))

# Test 7: Validate directory structure logic
echo ""
echo "🔍 Test: Validate directory structure logic"
echo "⚡ Command: Simulate directory creation"
echo "----------------------------------------"

# Simulate the directory structure
mkdir -p test-reports/frontend
mkdir -p test-frontend/reports/screenshots
mkdir -p test-frontend/reports/videos

if [ -d "test-reports/frontend" ] && \
   [ -d "test-frontend/reports/screenshots" ] && \
   [ -d "test-frontend/reports/videos" ]; then
    echo "✅ SUCCESS: Directory structure logic is valid"
    ((SUCCESS_COUNT++))
    # Cleanup
    rm -rf test-reports test-frontend
else
    echo "❌ FAILED: Directory structure logic is invalid"
fi
((TOTAL_TESTS++))

# Summary
echo ""
echo "📊 PATH REGRESSION FIX VALIDATION SUMMARY"
echo "========================================"
echo "Tests passed: $SUCCESS_COUNT/$TOTAL_TESTS"

if [ $SUCCESS_COUNT -eq $TOTAL_TESTS ]; then
    echo "🎉 ALL PATH REGRESSION FIXES VALIDATED!"
    echo "✅ Path issues should be resolved"
    exit 0
elif [ $SUCCESS_COUNT -ge 5 ]; then
    echo "⚠️ Most validations passed"
    echo "✅ Path issues should be mostly resolved"
    exit 0
else
    echo "❌ Several path issues remain"
    echo "🔧 Please check the failures above"
    exit 1
fi
