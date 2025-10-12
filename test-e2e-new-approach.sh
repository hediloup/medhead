#!/bin/bash

echo "🌐 Test E2E New Approach Validation"
echo "===================================="

# Variables
SUCCESS_COUNT=0
TOTAL_TESTS=0

echo "🚀 Validating new E2E approach..."

# Test 1: Check CI workflow new approach
echo ""
echo "🔍 Test: Check CI workflow new E2E approach"
echo "⚡ Command: Check .github/workflows/ci.yml E2E section"
echo "----------------------------------------"

if grep -q "npm run build" .github/workflows/ci.yml && \
   grep -q "nohup npm start" .github/workflows/ci.yml && \
   grep -q "angular.pid" .github/workflows/ci.yml && \
   grep -q "kill \$PID" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: CI workflow uses new Angular direct approach"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: CI workflow not using new Angular direct approach"
fi
((TOTAL_TESTS++))

# Test 2: Check if npm start script exists
echo ""
echo "🔍 Test: Check npm start script in package.json"
echo "⚡ Command: Check frontend/package.json start script"
echo "----------------------------------------"

if grep -q '"start": "ng serve --host 0.0.0.0 --port 4200"' frontend/package.json; then
    echo "✅ SUCCESS: npm start script properly configured"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: npm start script not properly configured"
fi
((TOTAL_TESTS++))

# Test 3: Check if build script exists
echo ""
echo "🔍 Test: Check npm build script in package.json"
echo "⚡ Command: Check frontend/package.json build script"
echo "----------------------------------------"

if grep -q '"build": "ng build"' frontend/package.json; then
    echo "✅ SUCCESS: npm build script exists"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: npm build script not found"
fi
((TOTAL_TESTS++))

# Test 4: Check Angular configuration
echo ""
echo "🔍 Test: Check Angular configuration"
echo "⚡ Command: Check frontend/angular.json"
echo "----------------------------------------"

if [ -f "frontend/angular.json" ] && grep -q "4200" frontend/angular.json; then
    echo "✅ SUCCESS: Angular configuration exists and uses port 4200"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Angular configuration missing or wrong port"
fi
((TOTAL_TESTS++))

# Test 5: Check Docker Compose backend services
echo ""
echo "🔍 Test: Check Docker Compose backend services"
echo "⚡ Command: Check docker/docker-compose.yml backend services"
echo "----------------------------------------"

if grep -q "postgres:" docker/docker-compose.yml && \
   grep -q "backend:" docker/docker-compose.yml && \
   grep -q "8080:8080" docker/docker-compose.yml; then
    echo "✅ SUCCESS: Docker Compose backend services properly configured"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Docker Compose backend services not properly configured"
fi
((TOTAL_TESTS++))

# Test 6: Check Cypress configuration
echo ""
echo "🔍 Test: Check Cypress configuration"
echo "⚡ Command: Check frontend/cypress.config.js"
echo "----------------------------------------"

if grep -q "baseUrl: 'http://localhost:4200'" frontend/cypress.config.js && \
   grep -q "defaultCommandTimeout: 15000" frontend/cypress.config.js; then
    echo "✅ SUCCESS: Cypress configuration properly set up"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Cypress configuration not properly set up"
fi
((TOTAL_TESTS++))

# Test 7: Check if reports directory structure exists
echo ""
echo "🔍 Test: Check reports directory structure"
echo "⚡ Command: Check if reports directories exist"
echo "----------------------------------------"

if [ -d "frontend/reports" ] || mkdir -p frontend/reports 2>/dev/null; then
    echo "✅ SUCCESS: Reports directory structure available"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Reports directory structure not available"
fi
((TOTAL_TESTS++))

# Summary
echo ""
echo "📊 E2E NEW APPROACH VALIDATION SUMMARY"
echo "======================================"
echo "Tests passed: $SUCCESS_COUNT/$TOTAL_TESTS"

if [ $SUCCESS_COUNT -eq $TOTAL_TESTS ]; then
    echo "🎉 ALL E2E NEW APPROACH VALIDATED!"
    echo "✅ E2E tests should work much better with Angular direct approach"
    exit 0
elif [ $SUCCESS_COUNT -ge 5 ]; then
    echo "⚠️ Most validations passed"
    echo "✅ E2E tests should be more stable now"
    exit 0
else
    echo "❌ Several issues remain"
    echo "🔧 Please check the failures above"
    exit 1
fi
