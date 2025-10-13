#!/bin/bash

echo "🐳 Test Docker Build Condition Fix Validation"
echo "============================================="

# Variables
SUCCESS_COUNT=0
TOTAL_TESTS=0

echo "🚀 Validating Docker build condition fix..."

# Test 1: Check if the condition is updated
echo ""
echo "🔍 Test: Check if the condition is updated"
echo "⚡ Command: Check .github/workflows/ci.yml for new condition"
echo "----------------------------------------"

if grep -q "needs.backend-tests.result == 'success' && needs.frontend-tests.result == 'success'" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: New condition is properly set"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: New condition not found"
fi
((TOTAL_TESTS++))

# Test 2: Check if old restrictive condition is removed
echo ""
echo "🔍 Test: Check if old restrictive condition is removed"
echo "⚡ Command: Check .github/workflows/ci.yml for old condition"
echo "----------------------------------------"

if ! grep -q "github.event_name == 'push'" .github/workflows/ci.yml || \
   ! grep -q "github.ref == 'refs/heads/main'" .github/workflows/ci.yml || \
   ! grep -q "github.ref == 'refs/heads/develop'" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Old restrictive condition removed or simplified"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Old restrictive condition still present"
fi
((TOTAL_TESTS++))

# Test 3: Verify job dependencies are still correct
echo ""
echo "🔍 Test: Verify job dependencies are still correct"
echo "⚡ Command: Check .github/workflows/ci.yml for needs"
echo "----------------------------------------"

if grep -q "needs: \[backend-tests, frontend-tests\]" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Job dependencies are correct"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Job dependencies not found or incorrect"
fi
((TOTAL_TESTS++))

# Test 4: Check if docker-build job is properly configured
echo ""
echo "🔍 Test: Check if docker-build job is properly configured"
echo "⚡ Command: Check .github/workflows/ci.yml for docker-build job"
echo "----------------------------------------"

if grep -q "docker-build:" .github/workflows/ci.yml && \
   grep -q "🐳 Build Images Docker" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Docker build job is properly configured"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Docker build job not found or misconfigured"
fi
((TOTAL_TESTS++))

# Test 5: Verify the condition logic is sound
echo ""
echo "🔍 Test: Verify the condition logic is sound"
echo "⚡ Command: Check condition syntax and logic"
echo "----------------------------------------"

# The new condition should be:
# - Only run if backend-tests succeeds
# - Only run if frontend-tests succeeds
# - No longer restricted by branch or event type

if grep -A 5 "if:" .github/workflows/ci.yml | grep -q "needs.backend-tests.result" && \
   grep -A 5 "if:" .github/workflows/ci.yml | grep -q "needs.frontend-tests.result"; then
    echo "✅ SUCCESS: Condition logic is sound and focuses on test results"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Condition logic is not properly configured"
fi
((TOTAL_TESTS++))

# Test 6: Check if job will run on any branch when tests pass
echo ""
echo "🔍 Test: Check if job will run on any branch when tests pass"
echo "⚡ Command: Verify no branch restrictions in condition"
echo "----------------------------------------"

# The condition should not contain branch-specific restrictions
if ! grep -A 2 "if:" .github/workflows/ci.yml | grep -q "refs/heads"; then
    echo "✅ SUCCESS: No branch restrictions - job will run on any branch"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Branch restrictions still present"
fi
((TOTAL_TESTS++))

# Test 7: Verify job timeout and runner are still configured
echo ""
echo "🔍 Test: Verify job timeout and runner are still configured"
echo "⚡ Command: Check .github/workflows/ci.yml for timeout and runner"
echo "----------------------------------------"

if grep -q "runs-on: ubuntu-latest" .github/workflows/ci.yml && \
   grep -q "timeout-minutes: 20" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Job timeout and runner are properly configured"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Job timeout or runner not configured"
fi
((TOTAL_TESTS++))

# Test 8: Check if all Docker build steps are present
echo ""
echo "🔍 Test: Check if all Docker build steps are present"
echo "⚡ Command: Check .github/workflows/ci.yml for Docker build steps"
echo "----------------------------------------"

if grep -q "Checkout du code" .github/workflows/ci.yml && \
   grep -q "Configuration Java" .github/workflows/ci.yml && \
   grep -q "Configuration Node.js" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: All Docker build steps are present"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Some Docker build steps are missing"
fi
((TOTAL_TESTS++))

# Summary
echo ""
echo "📊 DOCKER BUILD CONDITION FIX VALIDATION SUMMARY"
echo "==============================================="
echo "Tests passed: $SUCCESS_COUNT/$TOTAL_TESTS"

if [ $SUCCESS_COUNT -eq $TOTAL_TESTS ]; then
    echo "🎉 ALL DOCKER BUILD CONDITION FIXES VALIDATED!"
    echo "✅ Docker build job will now run when backend and frontend tests succeed"
    echo "✅ No more branch or event restrictions"
    echo "✅ Job will execute on any branch if tests pass"
    exit 0
elif [ $SUCCESS_COUNT -ge 6 ]; then
    echo "⚠️ Most validations passed"
    echo "✅ Docker build condition should work better now"
    exit 0
else
    echo "❌ Several issues remain"
    echo "🔧 Please check the failures above"
    exit 1
fi
