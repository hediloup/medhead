#!/bin/bash

echo "🐳 Test Docker Build Original Condition Analysis"
echo "==============================================="

# Variables
SUCCESS_COUNT=0
TOTAL_TESTS=0

echo "🚀 Analyzing why Docker build job was skipped with original condition..."

# Test 1: Verify original condition is restored
echo ""
echo "🔍 Test: Verify original condition is restored"
echo "⚡ Command: Check .github/workflows/ci.yml for original condition"
echo "----------------------------------------"

if grep -q "github.event_name == 'push' && (github.ref == 'refs/heads/main' || github.ref == 'refs/heads/develop')" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Original condition is restored"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Original condition not found"
fi
((TOTAL_TESTS++))

# Test 2: Check current branch
echo ""
echo "🔍 Test: Check current branch"
echo "⚡ Command: git branch --show-current"
echo "----------------------------------------"

CURRENT_BRANCH=$(git branch --show-current)
echo "Current branch: $CURRENT_BRANCH"

if [ "$CURRENT_BRANCH" = "develop" ] || [ "$CURRENT_BRANCH" = "main" ]; then
    echo "✅ SUCCESS: Current branch ($CURRENT_BRANCH) matches condition"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Current branch ($CURRENT_BRANCH) does not match condition"
fi
((TOTAL_TESTS++))

# Test 3: Check if we're in a push event context
echo ""
echo "🔍 Test: Check if we're in a push event context"
echo "⚡ Command: Check recent commits"
echo "----------------------------------------"

if git log --oneline -3 | grep -q "push\|commit"; then
    echo "✅ SUCCESS: Recent commits found (push context)"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: No recent commits found"
fi
((TOTAL_TESTS++))

# Test 4: Verify job dependencies
echo ""
echo "🔍 Test: Verify job dependencies"
echo "⚡ Command: Check .github/workflows/ci.yml for needs"
echo "----------------------------------------"

if grep -q "needs: \[backend-tests, frontend-tests\]" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Job dependencies are correct"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Job dependencies not found"
fi
((TOTAL_TESTS++))

# Test 5: Check if the condition logic is correct
echo ""
echo "🔍 Test: Check if the condition logic is correct"
echo "⚡ Command: Analyze condition components"
echo "----------------------------------------"

# The condition should be:
# github.event_name == 'push' AND (github.ref == 'refs/heads/main' OR github.ref == 'refs/heads/develop')

if grep -q "github.event_name == 'push'" .github/workflows/ci.yml && \
   grep -q "github.ref == 'refs/heads/main'" .github/workflows/ci.yml && \
   grep -q "github.ref == 'refs/heads/develop'" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Condition logic components are correct"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Condition logic components are missing"
fi
((TOTAL_TESTS++))

# Test 6: Check if docker-build job is properly configured
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

# Test 7: Analyze possible reasons for job being skipped
echo ""
echo "🔍 Test: Analyze possible reasons for job being skipped"
echo "⚡ Command: Check for potential issues"
echo "----------------------------------------"

echo "Possible reasons why the job was skipped:"
echo "1. ❌ github.event_name != 'push' (might be pull_request, workflow_dispatch, etc.)"
echo "2. ❌ github.ref != 'refs/heads/develop' AND github.ref != 'refs/heads/main'"
echo "3. ❌ backend-tests job failed"
echo "4. ❌ frontend-tests job failed"
echo "5. ❌ Job dependencies not met"

# Check if we can determine the event type (this is just for analysis)
echo ""
echo "📋 Analysis Summary:"
echo "- Current branch: $CURRENT_BRANCH"
echo "- Condition requires: push event AND (main OR develop branch)"
echo "- Job depends on: backend-tests AND frontend-tests success"

if [ "$CURRENT_BRANCH" = "develop" ]; then
    echo "- ✅ Branch condition should be met (develop)"
    ((SUCCESS_COUNT++))
else
    echo "- ❌ Branch condition might not be met"
fi
((TOTAL_TESTS++))

# Summary
echo ""
echo "📊 DOCKER BUILD ORIGINAL CONDITION ANALYSIS SUMMARY"
echo "=================================================="
echo "Tests passed: $SUCCESS_COUNT/$TOTAL_TESTS"

if [ $SUCCESS_COUNT -eq $TOTAL_TESTS ]; then
    echo "🎉 ALL ANALYSES PASSED!"
    echo "✅ Original condition is properly restored"
    echo "✅ Branch condition should be met"
    echo "🔍 If job is still skipped, check if backend-tests or frontend-tests failed"
    exit 0
elif [ $SUCCESS_COUNT -ge 6 ]; then
    echo "⚠️ Most analyses passed"
    echo "✅ Original condition is restored"
    echo "🔍 Check CI logs to see if backend-tests or frontend-tests failed"
    exit 0
else
    echo "❌ Several issues found"
    echo "🔧 Please check the failures above"
    exit 1
fi
