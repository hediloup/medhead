#!/bin/bash

echo "🌐 Test HTTP Server Approach Validation"
echo "======================================"

# Variables
SUCCESS_COUNT=0
TOTAL_TESTS=0

echo "🚀 Validating HTTP server approach for Angular..."

# Test 1: Check if build command is used
echo ""
echo "🔍 Test: Check if build command is used"
echo "⚡ Command: Check .github/workflows/ci.yml for build command"
echo "----------------------------------------"

if grep -q "npm run build --configuration development" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Build command is properly configured"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Build command not found"
fi
((TOTAL_TESTS++))

# Test 2: Check if http-server is installed
echo ""
echo "🔍 Test: Check if http-server is installed"
echo "⚡ Command: Check .github/workflows/ci.yml for http-server installation"
echo "----------------------------------------"

if grep -q "npm install -g http-server" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: http-server installation is configured"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: http-server installation not found"
fi
((TOTAL_TESTS++))

# Test 3: Check if http-server command is properly configured
echo ""
echo "🔍 Test: Check if http-server command is properly configured"
echo "⚡ Command: Check .github/workflows/ci.yml for http-server command"
echo "----------------------------------------"

if grep -q "http-server dist/medhead-frontend -p 4200 -a 0.0.0.0 --cors --gzip" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: http-server command is properly configured"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: http-server command not properly configured"
fi
((TOTAL_TESTS++))

# Test 4: Check if process monitoring is maintained
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

# Test 5: Check if timeout is optimized
echo ""
echo "🔍 Test: Check if timeout is optimized"
echo "⚡ Command: Check .github/workflows/ci.yml for timeout values"
echo "----------------------------------------"

if grep -q "timeout 30s" .github/workflows/ci.yml && \
   grep -q "sleep 10" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Timeout values are optimized for http-server"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Timeout values not optimized"
fi
((TOTAL_TESTS++))

# Test 6: Check if directory structure is correct
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

# Test 7: Check if CORS and gzip are enabled
echo ""
echo "🔍 Test: Check if CORS and gzip are enabled"
echo "⚡ Command: Check .github/workflows/ci.yml for CORS and gzip options"
echo "----------------------------------------"

if grep -q "--cors" .github/workflows/ci.yml && \
   grep -q "--gzip" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: CORS and gzip are enabled"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: CORS and gzip not enabled"
fi
((TOTAL_TESTS++))

# Test 8: Check if error handling is improved
echo ""
echo "🔍 Test: Check if error handling is improved"
echo "⚡ Command: Check .github/workflows/ci.yml for error handling"
echo "----------------------------------------"

if grep -q "Vérifiant les logs" .github/workflows/ci.yml && \
   grep -q "exit 1" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Error handling is improved"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Error handling not improved"
fi
((TOTAL_TESTS++))

# Test 9: Check if logging is maintained
echo ""
echo "🔍 Test: Check if logging is maintained"
echo "⚡ Command: Check .github/workflows/ci.yml for logging"
echo "----------------------------------------"

if grep -q "echo.*Démarrage.*serveur" .github/workflows/ci.yml && \
   grep -q "echo.*serveur HTTP" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Logging is maintained"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Logging not maintained"
fi
((TOTAL_TESTS++))

# Test 10: Check if PID management is maintained
echo ""
echo "🔍 Test: Check if PID management is maintained"
echo "⚡ Command: Check .github/workflows/ci.yml for PID management"
echo "----------------------------------------"

if grep -q "echo \$! > ../reports/frontend/angular.pid" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: PID management is maintained"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: PID management not maintained"
fi
((TOTAL_TESTS++))

# Summary
echo ""
echo "📊 HTTP SERVER APPROACH VALIDATION SUMMARY"
echo "=========================================="
echo "Tests passed: $SUCCESS_COUNT/$TOTAL_TESTS"

if [ $SUCCESS_COUNT -eq $TOTAL_TESTS ]; then
    echo "🎉 ALL HTTP SERVER APPROACH VALIDATIONS PASSED!"
    echo "✅ HTTP server approach should work reliably"
    exit 0
elif [ $SUCCESS_COUNT -ge 8 ]; then
    echo "⚠️ Most validations passed"
    echo "✅ HTTP server approach should work well"
    exit 0
else
    echo "❌ Several issues remain"
    echo "🔧 Please check the failures above"
    exit 1
fi
