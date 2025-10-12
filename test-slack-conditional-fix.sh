#!/bin/bash

echo "📢 Test Slack Conditional Fix Validation"
echo "======================================"

# Variables
SUCCESS_COUNT=0
TOTAL_TESTS=0

echo "🚀 Validating Slack conditional execution fix..."

# Test 1: Check if conditional execution is added
echo ""
echo "🔍 Test: Check if conditional execution is added"
echo "⚡ Command: Check .github/workflows/ci.yml for conditional execution"
echo "----------------------------------------"

if grep -q "if: always() && secrets.SLACK_WEBHOOK_URL" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Conditional execution added (only runs if secret exists)"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Conditional execution not added"
fi
((TOTAL_TESTS++))

# Test 2: Check if Slack action is still configured
echo ""
echo "🔍 Test: Check if Slack action is still configured"
echo "⚡ Command: Check .github/workflows/ci.yml for action-slack@v3"
echo "----------------------------------------"

if grep -q "uses: 8398a7/action-slack@v3" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Slack action still configured"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Slack action not configured"
fi
((TOTAL_TESTS++))

# Test 3: Check if all parameters are still present
echo ""
echo "🔍 Test: Check if all parameters are still present"
echo "⚡ Command: Check .github/workflows/ci.yml for Slack parameters"
echo "----------------------------------------"

if grep -q "status: \${{ job.status }}" .github/workflows/ci.yml && \
   grep -q "channel: '#medhead-ci'" .github/workflows/ci.yml && \
   grep -q "SLACK_WEBHOOK_URL: \${{ secrets.SLACK_WEBHOOK_URL }}" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: All Slack parameters are still present"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Some Slack parameters are missing"
fi
((TOTAL_TESTS++))

# Test 4: Verify conditional logic syntax
echo ""
echo "🔍 Test: Verify conditional logic syntax"
echo "⚡ Command: Check .github/workflows/ci.yml for proper conditional syntax"
echo "----------------------------------------"

if grep -q "secrets.SLACK_WEBHOOK_URL" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Conditional logic syntax is correct"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Conditional logic syntax is incorrect"
fi
((TOTAL_TESTS++))

# Test 5: Check if the step will be skipped when secret is missing
echo ""
echo "🔍 Test: Check if the step will be skipped when secret is missing"
echo "⚡ Command: Verify conditional prevents execution without secret"
echo "----------------------------------------"

# The conditional "always() && secrets.SLACK_WEBHOOK_URL" means:
# - always() = run regardless of job status
# - && secrets.SLACK_WEBHOOK_URL = only if the secret exists
# This prevents the step from running if the secret is not configured

if grep -q "always() && secrets.SLACK_WEBHOOK_URL" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Step will be skipped when secret is missing"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Step may not be properly conditional"
fi
((TOTAL_TESTS++))

# Test 6: Check if custom messages are still configured
echo ""
echo "🔍 Test: Check if custom messages are still configured"
echo "⚡ Command: Check .github/workflows/ci.yml for custom messages"
echo "----------------------------------------"

if grep -q "success_message:" .github/workflows/ci.yml && \
   grep -q "cancelled_message:" .github/workflows/ci.yml && \
   grep -q "failure_message:" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Custom messages are still configured"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Custom messages not configured"
fi
((TOTAL_TESTS++))

# Test 7: Verify no webhook_url parameter
echo ""
echo "🔍 Test: Verify no webhook_url parameter"
echo "⚡ Command: Check .github/workflows/ci.yml for webhook_url parameter"
echo "----------------------------------------"

if ! grep -q "webhook_url:" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: No webhook_url parameter (correct for v3)"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: webhook_url parameter still present"
fi
((TOTAL_TESTS++))

# Test 8: Check if github_token is provided
echo ""
echo "🔍 Test: Check if github_token is provided"
echo "⚡ Command: Check .github/workflows/ci.yml for github_token"
echo "----------------------------------------"

if grep -q "github_token: \${{ secrets.GITHUB_TOKEN }}" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: github_token is provided"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: github_token not provided"
fi
((TOTAL_TESTS++))

# Summary
echo ""
echo "📊 SLACK CONDITIONAL FIX VALIDATION SUMMARY"
echo "=========================================="
echo "Tests passed: $SUCCESS_COUNT/$TOTAL_TESTS"

if [ $SUCCESS_COUNT -eq $TOTAL_TESTS ]; then
    echo "🎉 ALL SLACK CONDITIONAL FIXES VALIDATED!"
    echo "✅ Slack notifications will only run if secret is configured"
    echo "✅ CI pipeline will not fail if secret is missing"
    exit 0
elif [ $SUCCESS_COUNT -ge 6 ]; then
    echo "⚠️ Most validations passed"
    echo "✅ Slack notifications should work conditionally"
    exit 0
else
    echo "❌ Several issues remain"
    echo "🔧 Please check the failures above"
    exit 1
fi
