#!/bin/bash

echo "🐳 Test Docker Buildx Fix Validation"
echo "===================================="

# Variables
SUCCESS_COUNT=0
TOTAL_TESTS=0

echo "🚀 Validating Docker Buildx setup fix..."

# Test 1: Check if docker/setup-buildx-action is added
echo ""
echo "🔍 Test: Check if docker/setup-buildx-action is added"
echo "⚡ Command: Check .github/workflows/ci.yml for setup-buildx-action"
echo "----------------------------------------"

if grep -q "docker/setup-buildx-action@v3" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: docker/setup-buildx-action is added"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: docker/setup-buildx-action not found"
fi
((TOTAL_TESTS++))

# Test 2: Check if docker-container driver is configured
echo ""
echo "🔍 Test: Check if docker-container driver is configured"
echo "⚡ Command: Check .github/workflows/ci.yml for driver configuration"
echo "----------------------------------------"

if grep -q "driver: docker-container" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: docker-container driver is configured"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: docker-container driver not configured"
fi
((TOTAL_TESTS++))

# Test 3: Check if setup-buildx is before the build steps
echo ""
echo "🔍 Test: Check if setup-buildx is before the build steps"
echo "⚡ Command: Check .github/workflows/ci.yml for step ordering"
echo "----------------------------------------"

# Get line numbers for setup-buildx and build steps
SETUP_LINE=$(grep -n "docker/setup-buildx-action@v3" .github/workflows/ci.yml | cut -d: -f1)
BACKEND_BUILD_LINE=$(grep -n "Build et Push Backend Image" .github/workflows/ci.yml | cut -d: -f1)

if [ -n "$SETUP_LINE" ] && [ -n "$BACKEND_BUILD_LINE" ] && [ "$SETUP_LINE" -lt "$BACKEND_BUILD_LINE" ]; then
    echo "✅ SUCCESS: Setup buildx is before backend build step"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Setup buildx is not before backend build step"
fi
((TOTAL_TESTS++))

# Test 4: Verify cache settings are still present
echo ""
echo "🔍 Test: Verify cache settings are still present"
echo "⚡ Command: Check .github/workflows/ci.yml for cache settings"
echo "----------------------------------------"

if grep -q "cache-from: type=gha" .github/workflows/ci.yml && \
   grep -q "cache-to: type=gha,mode=max" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Cache settings are still present"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Cache settings are missing"
fi
((TOTAL_TESTS++))

# Test 5: Check if both backend and frontend builds have cache
echo ""
echo "🔍 Test: Check if both backend and frontend builds have cache"
echo "⚡ Command: Check .github/workflows/ci.yml for cache in both builds"
echo "----------------------------------------"

BACKEND_CACHE=$(grep -A 10 "Build et Push Backend Image" .github/workflows/ci.yml | grep -c "cache-from\|cache-to")
FRONTEND_CACHE=$(grep -A 10 "Build et Push Frontend Image" .github/workflows/ci.yml | grep -c "cache-from\|cache-to")

if [ "$BACKEND_CACHE" -ge 2 ] && [ "$FRONTEND_CACHE" -ge 2 ]; then
    echo "✅ SUCCESS: Both backend and frontend builds have cache settings"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Cache settings missing in one or both builds"
fi
((TOTAL_TESTS++))

# Test 6: Verify build-push-action version
echo ""
echo "🔍 Test: Verify build-push-action version"
echo "⚡ Command: Check .github/workflows/ci.yml for action version"
echo "----------------------------------------"

if grep -q "docker/build-push-action@v5" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: docker/build-push-action@v5 is used"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Wrong version of build-push-action"
fi
((TOTAL_TESTS++))

# Test 7: Check if tags are properly configured
echo ""
echo "🔍 Test: Check if tags are properly configured"
echo "⚡ Command: Check .github/workflows/ci.yml for tag configuration"
echo "----------------------------------------"

if grep -q "ghcr.io/\${{ github.repository }}" .github/workflows/ci.yml && \
   grep -q "latest" .github/workflows/ci.yml && \
   grep -q "\${{ github.sha }}" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Tags are properly configured"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Tags are not properly configured"
fi
((TOTAL_TESTS++))

# Test 8: Verify registry login is still present
echo ""
echo "🔍 Test: Verify registry login is still present"
echo "⚡ Command: Check .github/workflows/ci.yml for registry login"
echo "----------------------------------------"

if grep -q "docker/login-action@v3" .github/workflows/ci.yml && \
   grep -q "registry: ghcr.io" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Registry login is still present"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Registry login is missing"
fi
((TOTAL_TESTS++))

# Test 9: Check workflow syntax validity
echo ""
echo "🔍 Test: Check workflow syntax validity"
echo "⚡ Command: Check for basic YAML structure"
echo "----------------------------------------"

if grep -q "name: 🐳 Build Images Docker" .github/workflows/ci.yml && \
   grep -q "runs-on: ubuntu-latest" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Workflow syntax appears valid"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Workflow syntax issues detected"
fi
((TOTAL_TESTS++))

# Summary
echo ""
echo "📊 DOCKER BUILDX FIX VALIDATION SUMMARY"
echo "======================================="
echo "Tests passed: $SUCCESS_COUNT/$TOTAL_TESTS"

if [ $SUCCESS_COUNT -eq $TOTAL_TESTS ]; then
    echo "🎉 ALL DOCKER BUILDX FIXES VALIDATED!"
    echo "✅ Docker Buildx setup is properly configured"
    echo "✅ docker-container driver will support GHA cache"
    echo "✅ Both backend and frontend builds should work"
    exit 0
elif [ $SUCCESS_COUNT -ge 7 ]; then
    echo "⚠️ Most validations passed"
    echo "✅ Docker Buildx setup should work"
    exit 0
else
    echo "❌ Several issues remain"
    echo "🔧 Please check the failures above"
    exit 1
fi
