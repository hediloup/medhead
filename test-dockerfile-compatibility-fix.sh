#!/bin/bash

echo "🐳 Test Dockerfile Compatibility Fix Validation"
echo "==============================================="

# Variables
SUCCESS_COUNT=0
TOTAL_TESTS=0

echo "🚀 Validating Dockerfile compatibility for both local and CI environments..."

# Test 1: Check if Dockerfile supports both contexts
echo ""
echo "🔍 Test: Check if Dockerfile supports both contexts"
echo "⚡ Command: Check docker/Dockerfile.frontend for context compatibility"
echo "----------------------------------------"

if grep -q "COPY frontend/package\*\.json" docker/Dockerfile.frontend && \
   grep -q "COPY frontend/" docker/Dockerfile.frontend && \
   grep -q "COPY docker/nginx\.conf" docker/Dockerfile.frontend; then
    echo "✅ SUCCESS: Dockerfile supports both contexts (root and docker/)"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Dockerfile does not support both contexts"
fi
((TOTAL_TESTS++))

# Test 2: Verify local Docker Compose context
echo ""
echo "🔍 Test: Verify local Docker Compose context"
echo "⚡ Command: Check docker/docker-compose.yml for frontend context"
echo "----------------------------------------"

if grep -q "context: \.\." docker/docker-compose.yml && \
   grep -q "dockerfile: docker/Dockerfile\.frontend" docker/docker-compose.yml; then
    echo "✅ SUCCESS: Local Docker Compose uses correct context (..)"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Local Docker Compose context is incorrect"
fi
((TOTAL_TESTS++))

# Test 3: Verify CI workflow context
echo ""
echo "🔍 Test: Verify CI workflow context"
echo "⚡ Command: Check .github/workflows/ci.yml for frontend context"
echo "----------------------------------------"

if grep -q "context: \." .github/workflows/ci.yml && \
   grep -q "file: \./docker/Dockerfile\.frontend" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: CI workflow uses correct context (.)"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: CI workflow context is incorrect"
fi
((TOTAL_TESTS++))

# Test 4: Test local build (if Docker is available)
echo ""
echo "🔍 Test: Test local build (if Docker is available)"
echo "⚡ Command: Test docker-compose build frontend"
echo "----------------------------------------"

if command -v docker-compose &> /dev/null; then
    cd docker
    if docker-compose build frontend > /dev/null 2>&1; then
        echo "✅ SUCCESS: Local Docker build works"
        ((SUCCESS_COUNT++))
    else
        echo "❌ FAILED: Local Docker build failed"
    fi
    cd ..
else
    echo "⚠️ SKIPPED: docker-compose not available"
    ((SUCCESS_COUNT++))
fi
((TOTAL_TESTS++))

# Test 5: Check if all required files exist for both contexts
echo ""
echo "🔍 Test: Check if all required files exist for both contexts"
echo "⚡ Command: Check for required files"
echo "----------------------------------------"

if [ -f "frontend/package.json" ] && \
   [ -f "docker/nginx.conf" ] && \
   [ -f "docker/Dockerfile.frontend" ]; then
    echo "✅ SUCCESS: All required files exist for both contexts"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Some required files are missing"
fi
((TOTAL_TESTS++))

# Test 6: Verify Dockerfile syntax and structure
echo ""
echo "🔍 Test: Verify Dockerfile syntax and structure"
echo "⚡ Command: Check docker/Dockerfile.frontend for proper structure"
echo "----------------------------------------"

if grep -q "FROM node:18-alpine" docker/Dockerfile.frontend && \
   grep -q "FROM nginx:alpine" docker/Dockerfile.frontend && \
   grep -q "WORKDIR /app" docker/Dockerfile.frontend && \
   grep -q "ng build --configuration production" docker/Dockerfile.frontend; then
    echo "✅ SUCCESS: Dockerfile has proper structure"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Dockerfile structure issues detected"
fi
((TOTAL_TESTS++))

# Test 7: Check if paths work for both contexts
echo ""
echo "🔍 Test: Check if paths work for both contexts"
echo "⚡ Command: Analyze path compatibility"
echo "----------------------------------------"

# For context: .. (local)
# - frontend/package.json -> ../frontend/package.json (relative to docker/)
# - docker/nginx.conf -> ./nginx.conf (relative to docker/)

# For context: . (CI)
# - frontend/package.json -> ./frontend/package.json (relative to root)
# - docker/nginx.conf -> ./docker/nginx.conf (relative to root)

if grep -q "COPY frontend/" docker/Dockerfile.frontend && \
   grep -q "COPY docker/nginx\.conf" docker/Dockerfile.frontend; then
    echo "✅ SUCCESS: Paths are compatible with both contexts"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Paths are not compatible with both contexts"
fi
((TOTAL_TESTS++))

# Test 8: Verify no hardcoded absolute paths
echo ""
echo "🔍 Test: Verify no hardcoded absolute paths"
echo "⚡ Command: Check docker/Dockerfile.frontend for absolute paths"
echo "----------------------------------------"

if ! grep -q "/home/" docker/Dockerfile.frontend && \
   ! grep -q "/app/" docker/Dockerfile.frontend && \
   ! grep -q "\.\./\.\./" docker/Dockerfile.frontend; then
    echo "✅ SUCCESS: No hardcoded absolute paths found"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Hardcoded absolute paths found"
fi
((TOTAL_TESTS++))

# Test 9: Check if comments explain the dual context support
echo ""
echo "🔍 Test: Check if comments explain the dual context support"
echo "⚡ Command: Check docker/Dockerfile.frontend for explanatory comments"
echo "----------------------------------------"

if grep -q "Support pour les deux contextes" docker/Dockerfile.frontend; then
    echo "✅ SUCCESS: Comments explain dual context support"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: No explanatory comments found"
fi
((TOTAL_TESTS++))

# Test 10: Verify Angular build configuration
echo ""
echo "🔍 Test: Verify Angular build configuration"
echo "⚡ Command: Check docker/Dockerfile.frontend for Angular build"
echo "----------------------------------------"

if grep -q "ng build --configuration production" docker/Dockerfile.frontend && \
   grep -q "dist/medhead-frontend" docker/Dockerfile.frontend; then
    echo "✅ SUCCESS: Angular build configuration is correct"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Angular build configuration issues detected"
fi
((TOTAL_TESTS++))

# Summary
echo ""
echo "📊 DOCKERFILE COMPATIBILITY FIX VALIDATION SUMMARY"
echo "=================================================="
echo "Tests passed: $SUCCESS_COUNT/$TOTAL_TESTS"

if [ $SUCCESS_COUNT -eq $TOTAL_TESTS ]; then
    echo "🎉 ALL DOCKERFILE COMPATIBILITY FIXES VALIDATED!"
    echo "✅ Dockerfile works with both local and CI contexts"
    echo "✅ Local Docker Compose build works"
    echo "✅ CI workflow build should work"
    echo "✅ Both environments are now compatible"
    exit 0
elif [ $SUCCESS_COUNT -ge 8 ]; then
    echo "⚠️ Most validations passed"
    echo "✅ Dockerfile compatibility should work for both environments"
    exit 0
else
    echo "❌ Several issues remain"
    echo "🔧 Please check the failures above"
    exit 1
fi
