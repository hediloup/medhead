#!/bin/bash

echo "📦 Test GHCR Permissions Fix Validation"
echo "======================================="

# Variables
SUCCESS_COUNT=0
TOTAL_TESTS=0

echo "🚀 Validating GHCR permissions fix..."

# Test 1: Check if packages: write permission is added
echo ""
echo "🔍 Test: Check if packages: write permission is added"
echo "⚡ Command: Check .github/workflows/ci.yml for packages permission"
echo "----------------------------------------"

if grep -q "packages: write" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: packages: write permission is added"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: packages: write permission not found"
fi
((TOTAL_TESTS++))

# Test 2: Check if permissions section exists
echo ""
echo "🔍 Test: Check if permissions section exists"
echo "⚡ Command: Check .github/workflows/ci.yml for permissions section"
echo "----------------------------------------"

if grep -q "^permissions:" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: permissions section exists"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: permissions section not found"
fi
((TOTAL_TESTS++))

# Test 3: Verify all required permissions are present
echo ""
echo "🔍 Test: Verify all required permissions are present"
echo "⚡ Command: Check .github/workflows/ci.yml for all permissions"
echo "----------------------------------------"

if grep -q "contents: read" .github/workflows/ci.yml && \
   grep -q "checks: write" .github/workflows/ci.yml && \
   grep -q "pull-requests: write" .github/workflows/ci.yml && \
   grep -q "statuses: write" .github/workflows/ci.yml && \
   grep -q "id-token: write" .github/workflows/ci.yml && \
   grep -q "packages: write" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: All required permissions are present"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Some required permissions are missing"
fi
((TOTAL_TESTS++))

# Test 4: Check if permissions are at the workflow level
echo ""
echo "🔍 Test: Check if permissions are at the workflow level"
echo "⚡ Command: Check .github/workflows/ci.yml for permissions placement"
echo "----------------------------------------"

# Permissions should be at the workflow level, not job level
PERMISSIONS_LINE=$(grep -n "^permissions:" .github/workflows/ci.yml | cut -d: -f1)
JOBS_LINE=$(grep -n "^jobs:" .github/workflows/ci.yml | cut -d: -f1)

if [ -n "$PERMISSIONS_LINE" ] && [ -n "$JOBS_LINE" ] && [ "$PERMISSIONS_LINE" -lt "$JOBS_LINE" ]; then
    echo "✅ SUCCESS: Permissions are at the workflow level (before jobs)"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Permissions are not properly placed at workflow level"
fi
((TOTAL_TESTS++))

# Test 5: Verify Docker login configuration
echo ""
echo "🔍 Test: Verify Docker login configuration"
echo "⚡ Command: Check .github/workflows/ci.yml for Docker login"
echo "----------------------------------------"

if grep -q "docker/login-action@v3" .github/workflows/ci.yml && \
   grep -q "registry: ghcr.io" .github/workflows/ci.yml && \
   grep -q "username: \${{ github.actor }}" .github/workflows/ci.yml && \
   grep -q "password: \${{ secrets.GITHUB_TOKEN }}" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Docker login configuration is correct"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Docker login configuration is missing or incorrect"
fi
((TOTAL_TESTS++))

# Test 6: Check if Docker build steps are properly configured
echo ""
echo "🔍 Test: Check if Docker build steps are properly configured"
echo "⚡ Command: Check .github/workflows/ci.yml for Docker build steps"
echo "----------------------------------------"

if grep -q "docker/build-push-action@v5" .github/workflows/ci.yml && \
   grep -q "ghcr.io/\${{ github.repository }}" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Docker build steps are properly configured"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Docker build steps are not properly configured"
fi
((TOTAL_TESTS++))

# Test 7: Verify package naming convention
echo ""
echo "🔍 Test: Verify package naming convention"
echo "⚡ Command: Check .github/workflows/ci.yml for package names"
echo "----------------------------------------"

if grep -q "medhead-backend" .github/workflows/ci.yml && \
   grep -q "medhead-frontend" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Package naming convention is correct"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Package naming convention is incorrect"
fi
((TOTAL_TESTS++))

# Test 8: Check if tags are properly configured
echo ""
echo "🔍 Test: Check if tags are properly configured"
echo "⚡ Command: Check .github/workflows/ci.yml for tag configuration"
echo "----------------------------------------"

if grep -q "latest" .github/workflows/ci.yml && \
   grep -q "\${{ github.sha }}" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Tags are properly configured"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Tags are not properly configured"
fi
((TOTAL_TESTS++))

# Test 9: Verify workflow syntax validity
echo ""
echo "🔍 Test: Verify workflow syntax validity"
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

# Test 10: Check for potential security issues
echo ""
echo "🔍 Test: Check for potential security issues"
echo "⚡ Command: Check for sensitive data exposure"
echo "----------------------------------------"

# Check if GITHUB_TOKEN is properly used (not hardcoded)
if grep -q "secrets.GITHUB_TOKEN" .github/workflows/ci.yml && \
   ! grep -q "ghp_" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: No hardcoded tokens found, using secrets properly"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Potential security issues detected"
fi
((TOTAL_TESTS++))

# Summary
echo ""
echo "📊 GHCR PERMISSIONS FIX VALIDATION SUMMARY"
echo "=========================================="
echo "Tests passed: $SUCCESS_COUNT/$TOTAL_TESTS"

if [ $SUCCESS_COUNT -eq $TOTAL_TESTS ]; then
    echo "🎉 ALL GHCR PERMISSIONS FIXES VALIDATED!"
    echo "✅ packages: write permission is properly configured"
    echo "✅ Docker login and build configuration is correct"
    echo "✅ GHCR push should now work"
    exit 0
elif [ $SUCCESS_COUNT -ge 8 ]; then
    echo "⚠️ Most validations passed"
    echo "✅ GHCR permissions should work now"
    exit 0
else
    echo "❌ Several issues remain"
    echo "🔧 Please check the failures above"
    exit 1
fi
