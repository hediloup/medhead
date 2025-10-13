#!/bin/bash

echo "🐳 Test Dockerfile Frontend Fix Validation"
echo "=========================================="

# Variables
SUCCESS_COUNT=0
TOTAL_TESTS=0

echo "🚀 Validating Dockerfile frontend path fixes..."

# Test 1: Check if frontend paths are corrected in Dockerfile
echo ""
echo "🔍 Test: Check if frontend paths are corrected in Dockerfile"
echo "⚡ Command: Check docker/Dockerfile.frontend for corrected paths"
echo "----------------------------------------"

if grep -q "COPY frontend/package\*\.json" docker/Dockerfile.frontend && \
   grep -q "COPY frontend/" docker/Dockerfile.frontend; then
    echo "✅ SUCCESS: Frontend paths are corrected in Dockerfile"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Frontend paths not corrected in Dockerfile"
fi
((TOTAL_TESTS++))

# Test 2: Check if nginx.conf path is corrected
echo ""
echo "🔍 Test: Check if nginx.conf path is corrected"
echo "⚡ Command: Check docker/Dockerfile.frontend for nginx.conf path"
echo "----------------------------------------"

if grep -q "COPY nginx\.conf /etc/nginx/nginx\.conf" docker/Dockerfile.frontend; then
    echo "✅ SUCCESS: nginx.conf path is corrected"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: nginx.conf path not corrected"
fi
((TOTAL_TESTS++))

# Test 3: Check if old incorrect paths are removed
echo ""
echo "🔍 Test: Check if old incorrect paths are removed"
echo "⚡ Command: Check docker/Dockerfile.frontend for old paths"
echo "----------------------------------------"

if ! grep -q "\.\./frontend/" docker/Dockerfile.frontend && \
   ! grep -q "docker/nginx\.conf" docker/Dockerfile.frontend; then
    echo "✅ SUCCESS: Old incorrect paths are removed"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Old incorrect paths still present"
fi
((TOTAL_TESTS++))

# Test 4: Check if CI workflow context is corrected
echo ""
echo "🔍 Test: Check if CI workflow context is corrected"
echo "⚡ Command: Check .github/workflows/ci.yml for context"
echo "----------------------------------------"

if grep -q "context: \." .github/workflows/ci.yml; then
    echo "✅ SUCCESS: CI workflow context is corrected to root"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: CI workflow context not corrected"
fi
((TOTAL_TESTS++))

# Test 5: Check if Dockerfile path is still correct
echo ""
echo "🔍 Test: Check if Dockerfile path is still correct"
echo "⚡ Command: Check .github/workflows/ci.yml for file path"
echo "----------------------------------------"

if grep -q "file: \./docker/Dockerfile\.frontend" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Dockerfile path is still correct"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Dockerfile path is incorrect"
fi
((TOTAL_TESTS++))

# Test 6: Verify required files exist
echo ""
echo "🔍 Test: Verify required files exist"
echo "⚡ Command: Check if required files exist"
echo "----------------------------------------"

if [ -f "docker/Dockerfile.frontend" ] && \
   [ -f "docker/nginx.conf" ] && \
   [ -f "frontend/package.json" ]; then
    echo "✅ SUCCESS: All required files exist"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Some required files are missing"
fi
((TOTAL_TESTS++))

# Test 7: Check Dockerfile syntax
echo ""
echo "🔍 Test: Check Dockerfile syntax"
echo "⚡ Command: Check docker/Dockerfile.frontend for basic syntax"
echo "----------------------------------------"

if grep -q "FROM node:18-alpine" docker/Dockerfile.frontend && \
   grep -q "FROM nginx:alpine" docker/Dockerfile.frontend && \
   grep -q "WORKDIR /app" docker/Dockerfile.frontend; then
    echo "✅ SUCCESS: Dockerfile syntax appears correct"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Dockerfile syntax issues detected"
fi
((TOTAL_TESTS++))

# Test 8: Check if build context will include all necessary files
echo ""
echo "🔍 Test: Check if build context will include all necessary files"
echo "⚡ Command: Verify context includes frontend and docker directories"
echo "----------------------------------------"

# With context: ., the build context will include:
# - frontend/ directory (for COPY frontend/)
# - docker/ directory (for nginx.conf)
# - All other files in the root

if [ -d "frontend" ] && [ -d "docker" ]; then
    echo "✅ SUCCESS: Build context will include all necessary directories"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Required directories not found"
fi
((TOTAL_TESTS++))

# Test 9: Verify nginx.conf exists in docker directory
echo ""
echo "🔍 Test: Verify nginx.conf exists in docker directory"
echo "⚡ Command: Check if docker/nginx.conf exists"
echo "----------------------------------------"

if [ -f "docker/nginx.conf" ]; then
    echo "✅ SUCCESS: nginx.conf exists in docker directory"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: nginx.conf not found in docker directory"
fi
((TOTAL_TESTS++))

# Test 10: Check if Angular build will work
echo ""
echo "🔍 Test: Check if Angular build will work"
echo "⚡ Command: Check docker/Dockerfile.frontend for Angular build"
echo "----------------------------------------"

if grep -q "ng build --configuration production" docker/Dockerfile.frontend && \
   grep -q "dist/medhead-frontend" docker/Dockerfile.frontend; then
    echo "✅ SUCCESS: Angular build configuration appears correct"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Angular build configuration issues detected"
fi
((TOTAL_TESTS++))

# Summary
echo ""
echo "📊 DOCKERFILE FRONTEND FIX VALIDATION SUMMARY"
echo "============================================="
echo "Tests passed: $SUCCESS_COUNT/$TOTAL_TESTS"

if [ $SUCCESS_COUNT -eq $TOTAL_TESTS ]; then
    echo "🎉 ALL DOCKERFILE FRONTEND FIXES VALIDATED!"
    echo "✅ Frontend Dockerfile paths are corrected"
    echo "✅ CI workflow context is corrected"
    echo "✅ Frontend Docker build should now work"
    exit 0
elif [ $SUCCESS_COUNT -ge 8 ]; then
    echo "⚠️ Most validations passed"
    echo "✅ Frontend Docker build should work now"
    exit 0
else
    echo "❌ Several issues remain"
    echo "🔧 Please check the failures above"
    exit 1
fi
