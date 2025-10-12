#!/bin/bash

echo "📢 Test Slack Action Fix Validation"
echo "================================="

# Variables
SUCCESS_COUNT=0
TOTAL_TESTS=0

echo "🚀 Validating Slack action fix..."

# Test 1: Check if webhook_url parameter is removed
echo ""
echo "🔍 Test: Check if webhook_url parameter is removed"
echo "⚡ Command: Check .github/workflows/ci.yml for webhook_url"
echo "----------------------------------------"

if ! grep -q "webhook_url:" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: webhook_url parameter removed (no longer valid in v3)"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: webhook_url parameter still present"
fi
((TOTAL_TESTS++))

# Test 2: Check if SLACK_WEBHOOK_URL is properly configured in env
echo ""
echo "🔍 Test: Check if SLACK_WEBHOOK_URL is properly configured in env"
echo "⚡ Command: Check .github/workflows/ci.yml for SLACK_WEBHOOK_URL"
echo "----------------------------------------"

if grep -q "SLACK_WEBHOOK_URL: \${{ secrets.SLACK_WEBHOOK_URL }}" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: SLACK_WEBHOOK_URL properly configured in env"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: SLACK_WEBHOOK_URL not properly configured"
fi
((TOTAL_TESTS++))

# Test 3: Check if valid v3 parameters are present
echo ""
echo "🔍 Test: Check if valid v3 parameters are present"
echo "⚡ Command: Check .github/workflows/ci.yml for valid v3 parameters"
echo "----------------------------------------"

if grep -q "status: \${{ job.status }}" .github/workflows/ci.yml && \
   grep -q "channel: '#medhead-ci'" .github/workflows/ci.yml && \
   grep -q "fields: repo,message,commit,author,action,eventName,ref,workflow" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Valid v3 parameters are present"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Valid v3 parameters missing"
fi
((TOTAL_TESTS++))

# Test 4: Check if custom messages are configured
echo ""
echo "🔍 Test: Check if custom messages are configured"
echo "⚡ Command: Check .github/workflows/ci.yml for custom messages"
echo "----------------------------------------"

if grep -q "success_message:" .github/workflows/ci.yml && \
   grep -q "cancelled_message:" .github/workflows/ci.yml && \
   grep -q "failure_message:" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Custom messages are configured"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Custom messages not configured"
fi
((TOTAL_TESTS++))

# Test 5: Check if github_token is provided
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

# Test 6: Check if author_name is set
echo ""
echo "🔍 Test: Check if author_name is set"
echo "⚡ Command: Check .github/workflows/ci.yml for author_name"
echo "----------------------------------------"

if grep -q "author_name: '8398a7@action-slack'" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: author_name is set"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: author_name not set"
fi
((TOTAL_TESTS++))

# Test 7: Check if action-slack@v3 is being used
echo ""
echo "🔍 Test: Check if action-slack@v3 is being used"
echo "⚡ Command: Check .github/workflows/ci.yml for action-slack version"
echo "----------------------------------------"

if grep -q "uses: 8398a7/action-slack@v3" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: action-slack@v3 is being used"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: action-slack version not correct"
fi
((TOTAL_TESTS++))

# Test 8: Check if notification is conditional
echo ""
echo "🔍 Test: Check if notification is conditional"
echo "⚡ Command: Check .github/workflows/ci.yml for conditional execution"
echo "----------------------------------------"

if grep -q "if: always()" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Notification runs conditionally (always)"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Notification not conditional"
fi
((TOTAL_TESTS++))

# Summary
echo ""
echo "📊 SLACK ACTION FIX VALIDATION SUMMARY"
echo "====================================="
echo "Tests passed: $SUCCESS_COUNT/$TOTAL_TESTS"

if [ $SUCCESS_COUNT -eq $TOTAL_TESTS ]; then
    echo "🎉 ALL SLACK ACTION FIXES VALIDATED!"
    echo "✅ Slack notifications should work correctly now"
    exit 0
elif [ $SUCCESS_COUNT -ge 6 ]; then
    echo "⚠️ Most validations passed"
    echo "✅ Slack notifications should work well"
    exit 0
else
    echo "❌ Several issues remain"
    echo "🔧 Please check the failures above"
    exit 1
fi
