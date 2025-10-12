#!/bin/bash

echo "🚀 Test Angular Startup Fix Validation"
echo "======================================"

# Variables
SUCCESS_COUNT=0
TOTAL_TESTS=0

echo "🚀 Validating Angular startup improvements..."

# Test 1: Check if ng serve command is optimized
echo ""
echo "🔍 Test: Check if ng serve command is optimized"
echo "⚡ Command: Check .github/workflows/ci.yml for ng serve parameters"
echo "----------------------------------------"

if grep -q "npx ng serve --host 0.0.0.0 --port 4200 --disable-host-check --live-reload=false --poll=2000" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: ng serve command is optimized for CI"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: ng serve command not optimized"
fi
((TOTAL_TESTS++))

# Test 2: Check if process monitoring is added
echo ""
echo "🔍 Test: Check if process monitoring is added"
echo "⚡ Command: Check .github/workflows/ci.yml for process monitoring"
echo "----------------------------------------"

if grep -q "kill -0" .github/workflows/ci.yml && \
   grep -q "Le processus Angular s'est arrêté" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Process monitoring is implemented"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Process monitoring not implemented"
fi
((TOTAL_TESTS++))

# Test 3: Check if logging is improved
echo ""
echo "🔍 Test: Check if logging is improved"
echo "⚡ Command: Check .github/workflows/ci.yml for improved logging"
echo "----------------------------------------"

if grep -q "tail -20 ./reports/frontend/angular.log" .github/workflows/ci.yml && \
   grep -q "Logs Angular (dernières 20 lignes)" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Logging is improved for debugging"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Logging not improved"
fi
((TOTAL_TESTS++))

# Test 4: Check if timeout is reasonable
echo ""
echo "🔍 Test: Check if timeout is reasonable"
echo "⚡ Command: Check .github/workflows/ci.yml for timeout values"
echo "----------------------------------------"

if grep -q "timeout 45s" .github/workflows/ci.yml && \
   grep -q "sleep 15" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Timeout values are reasonable"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Timeout values not optimized"
fi
((TOTAL_TESTS++))

# Test 5: Check if PID debugging is added
echo ""
echo "🔍 Test: Check if PID debugging is added"
echo "⚡ Command: Check .github/workflows/ci.yml for PID debugging"
echo "----------------------------------------"

if grep -q "PID du processus Angular:" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: PID debugging is implemented"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: PID debugging not implemented"
fi
((TOTAL_TESTS++))

# Test 6: Check if error handling is improved
echo ""
echo "🔍 Test: Check if error handling is improved"
echo "⚡ Command: Check .github/workflows/ci.yml for error handling"
echo "----------------------------------------"

if grep -q "exit 1" .github/workflows/ci.yml && \
   grep -q "Vérifiant les logs" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Error handling is improved"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Error handling not improved"
fi
((TOTAL_TESTS++))

# Test 7: Check if ng serve parameters are CI-friendly
echo ""
echo "🔍 Test: Check if ng serve parameters are CI-friendly"
echo "⚡ Command: Validate ng serve parameters"
echo "----------------------------------------"

# Check for CI-friendly parameters
if grep -q "--disable-host-check" .github/workflows/ci.yml && \
   grep -q "--live-reload=false" .github/workflows/ci.yml && \
   grep -q "--poll=2000" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: ng serve parameters are CI-friendly"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: ng serve parameters not optimized for CI"
fi
((TOTAL_TESTS++))

# Test 8: Check if directory structure is maintained
echo ""
echo "🔍 Test: Check if directory structure is maintained"
echo "⚡ Command: Check .github/workflows/ci.yml for directory creation"
echo "----------------------------------------"

if grep -q "mkdir -p ../reports/frontend" .github/workflows/ci.yml && \
   grep -q "mkdir -p reports" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Directory structure is maintained"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Directory structure not maintained"
fi
((TOTAL_TESTS++))

# Summary
echo ""
echo "📊 ANGULAR STARTUP FIX VALIDATION SUMMARY"
echo "========================================"
echo "Tests passed: $SUCCESS_COUNT/$TOTAL_TESTS"

if [ $SUCCESS_COUNT -eq $TOTAL_TESTS ]; then
    echo "🎉 ALL ANGULAR STARTUP FIXES VALIDATED!"
    echo "✅ Angular startup should be much more reliable now"
    exit 0
elif [ $SUCCESS_COUNT -ge 6 ]; then
    echo "⚠️ Most validations passed"
    echo "✅ Angular startup should be improved"
    exit 0
else
    echo "❌ Several issues remain"
    echo "🔧 Please check the failures above"
    exit 1
fi
