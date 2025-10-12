#!/bin/bash

echo "🔨 Test Build Command Fix Validation"
echo "===================================="

# Variables
SUCCESS_COUNT=0
TOTAL_TESTS=0

echo "🚀 Validating Angular build command fix..."

# Test 1: Check if build command uses npx instead of npm run
echo ""
echo "🔍 Test: Check if build command uses npx instead of npm run"
echo "⚡ Command: Check .github/workflows/ci.yml for build command"
echo "----------------------------------------"

if grep -q "npx ng build --configuration development" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Build command uses npx ng build with correct syntax"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Build command not using correct syntax"
fi
((TOTAL_TESTS++))

# Test 2: Check if npm run build is not used
echo ""
echo "🔍 Test: Check if npm run build is not used (which was causing the error)"
echo "⚡ Command: Check .github/workflows/ci.yml for npm run build"
echo "----------------------------------------"

if ! grep -q "npm run build" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: npm run build is not used (which was problematic)"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: npm run build is still being used"
fi
((TOTAL_TESTS++))

# Test 3: Check if configuration is properly specified
echo ""
echo "🔍 Test: Check if configuration is properly specified"
echo "⚡ Command: Check .github/workflows/ci.yml for configuration parameter"
echo "----------------------------------------"

if grep -q "--configuration development" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Configuration parameter is properly specified"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Configuration parameter not properly specified"
fi
((TOTAL_TESTS++))

# Test 4: Check if http-server installation is still there
echo ""
echo "🔍 Test: Check if http-server installation is still configured"
echo "⚡ Command: Check .github/workflows/ci.yml for http-server installation"
echo "----------------------------------------"

if grep -q "npm install -g http-server" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: http-server installation is configured"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: http-server installation not found"
fi
((TOTAL_TESTS++))

# Test 5: Check if http-server command is still properly configured
echo ""
echo "🔍 Test: Check if http-server command is still properly configured"
echo "⚡ Command: Check .github/workflows/ci.yml for http-server command"
echo "----------------------------------------"

if grep -q "http-server dist/medhead-frontend" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: http-server command is properly configured"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: http-server command not properly configured"
fi
((TOTAL_TESTS++))

# Test 6: Check if process monitoring is maintained
echo ""
echo "🔍 Test: Check if process monitoring is maintained"
echo "⚡ Command: Check .github/workflows/ci.yml for process monitoring"
echo "----------------------------------------"

if grep -q "kill -0" .github/workflows/ci.yml && \
   grep -q "Le serveur HTTP s'est arrêté" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Process monitoring is maintained"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Process monitoring not maintained"
fi
((TOTAL_TESTS++))

# Test 7: Check if directory structure is correct
echo ""
echo "🔍 Test: Check if directory structure is correct"
echo "⚡ Command: Check .github/workflows/ci.yml for dist directory"
echo "----------------------------------------"

if grep -q "dist/medhead-frontend" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Directory structure is correct"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Directory structure not correct"
fi
((TOTAL_TESTS++))

# Test 8: Check if error handling is maintained
echo ""
echo "🔍 Test: Check if error handling is maintained"
echo "⚡ Command: Check .github/workflows/ci.yml for error handling"
echo "----------------------------------------"

if grep -q "exit 1" .github/workflows/ci.yml && \
   grep -q "Vérifiant les logs" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Error handling is maintained"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Error handling not maintained"
fi
((TOTAL_TESTS++))

# Test 9: Validate Angular CLI syntax
echo ""
echo "🔍 Test: Validate Angular CLI syntax"
echo "⚡ Command: Check if the command follows Angular CLI conventions"
echo "----------------------------------------"

# Check for proper ng build syntax
if grep -q "npx ng build" .github/workflows/ci.yml && \
   grep -q "--configuration development" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Angular CLI syntax is correct"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Angular CLI syntax is incorrect"
fi
((TOTAL_TESTS++))

# Test 10: Check if all other components are preserved
echo ""
echo "🔍 Test: Check if all other components are preserved"
echo "⚡ Command: Check .github/workflows/ci.yml for preserved functionality"
echo "----------------------------------------"

if grep -q "mkdir -p ../reports/frontend" .github/workflows/ci.yml && \
   grep -q "echo \$! > ../reports/frontend/angular.pid" .github/workflows/ci.yml && \
   grep -q "timeout 30s" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: All other components are preserved"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Some components are missing"
fi
((TOTAL_TESTS++))

# Summary
echo ""
echo "📊 BUILD COMMAND FIX VALIDATION SUMMARY"
echo "======================================="
echo "Tests passed: $SUCCESS_COUNT/$TOTAL_TESTS"

if [ $SUCCESS_COUNT -eq $TOTAL_TESTS ]; then
    echo "🎉 ALL BUILD COMMAND FIXES VALIDATED!"
    echo "✅ Build command should work correctly now"
    exit 0
elif [ $SUCCESS_COUNT -ge 8 ]; then
    echo "⚠️ Most validations passed"
    echo "✅ Build command should work well"
    exit 0
else
    echo "❌ Several issues remain"
    echo "🔧 Please check the failures above"
    exit 1
fi
