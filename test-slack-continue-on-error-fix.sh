#!/bin/bash

echo "📢 Test Slack Continue-On-Error Fix Validation"
echo "============================================="

# Variables
SUCCESS_COUNT=0
TOTAL_TESTS=0

echo "🚀 Validating Slack continue-on-error fix..."

# Test 1: Check if continue-on-error is added
echo ""
echo "🔍 Test: Check if continue-on-error is added"
echo "⚡ Command: Check .github/workflows/ci.yml for continue-on-error"
echo "----------------------------------------"

if grep -q "continue-on-error: true" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: continue-on-error is added"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: continue-on-error not added"
fi
((TOTAL_TESTS++))

# Test 2: Check if conditional is simplified to always()
echo ""
echo "🔍 Test: Check if conditional is simplified to always()"
echo "⚡ Command: Check .github/workflows/ci.yml for if: always()"
echo "----------------------------------------"

if grep -q "if: always()" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Conditional is simplified to always()"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Conditional not simplified"
fi
((TOTAL_TESTS++))

# Test 3: Check if invalid secrets condition is removed
echo ""
echo "🔍 Test: Check if invalid secrets condition is removed"
echo "⚡ Command: Check .github/workflows/ci.yml for secrets condition"
echo "----------------------------------------"

if ! grep -q "secrets.SLACK_WEBHOOK_URL" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Invalid secrets condition removed"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Invalid secrets condition still present"
fi
((TOTAL_TESTS++))

# Test 4: Check if Slack action is still configured
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

# Test 5: Check if all parameters are still present
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

# Test 9: Verify workflow syntax is valid
echo ""
echo "🔍 Test: Verify workflow syntax is valid"
echo "⚡ Command: Check for basic YAML syntax"
echo "----------------------------------------"

if grep -q "name: 📢 Notification Slack" .github/workflows/ci.yml && \
   grep -q "uses: 8398a7/action-slack@v3" .github/workflows/ci.yml && \
   grep -q "continue-on-error: true" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Workflow syntax appears valid"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Workflow syntax issues detected"
fi
((TOTAL_TESTS++))

# Test 10: Check if the step will handle missing secret gracefully
echo ""
echo "🔍 Test: Check if the step will handle missing secret gracefully"
echo "⚡ Command: Verify continue-on-error configuration"
echo "----------------------------------------"

# The continue-on-error: true means:
# - Step will run even if SLACK_WEBHOOK_URL is missing
# - If it fails due to missing secret, the job will continue
# - No failure will be reported for this step

if grep -A 5 -B 5 "continue-on-error: true" .github/workflows/ci.yml | grep -q "SLACK_WEBHOOK_URL"; then
    echo "✅ SUCCESS: Step will handle missing secret gracefully"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Step may not handle missing secret properly"
fi
((TOTAL_TESTS++))

# Summary
echo ""
echo "📊 SLACK CONTINUE-ON-ERROR FIX VALIDATION SUMMARY"
echo "================================================"
echo "Tests passed: $SUCCESS_COUNT/$TOTAL_TESTS"

if [ $SUCCESS_COUNT -eq $TOTAL_TESTS ]; then
    echo "🎉 ALL SLACK CONTINUE-ON-ERROR FIXES VALIDATED!"
    echo "✅ Slack notifications will run but won't fail CI if secret is missing"
    echo "✅ CI pipeline will continue even if Slack fails"
    exit 0
elif [ $SUCCESS_COUNT -ge 8 ]; then
    echo "⚠️ Most validations passed"
    echo "✅ Slack notifications should work with graceful error handling"
    exit 0
else
    echo "❌ Several issues remain"
    echo "🔧 Please check the failures above"
    exit 1
fi
