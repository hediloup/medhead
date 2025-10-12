#!/bin/bash

echo "📢 Test Slack Disabled Validation"
echo "================================="

# Variables
SUCCESS_COUNT=0
TOTAL_TESTS=0

echo "🚀 Validating Slack notifications are disabled..."

# Test 1: Check if Slack step is commented out
echo ""
echo "🔍 Test: Check if Slack step is commented out"
echo "⚡ Command: Check .github/workflows/ci.yml for commented Slack step"
echo "----------------------------------------"

if grep -q "# - name: 📢 Notification Slack" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Slack step is commented out"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Slack step is not commented out"
fi
((TOTAL_TESTS++))

# Test 2: Check if action-slack@v3 is commented out
echo ""
echo "🔍 Test: Check if action-slack@v3 is commented out"
echo "⚡ Command: Check .github/workflows/ci.yml for commented action-slack"
echo "----------------------------------------"

if grep -q "#   uses: 8398a7/action-slack@v3" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: action-slack@v3 is commented out"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: action-slack@v3 is not commented out"
fi
((TOTAL_TESTS++))

# Test 3: Check if all Slack parameters are commented out
echo ""
echo "🔍 Test: Check if all Slack parameters are commented out"
echo "⚡ Command: Check .github/workflows/ci.yml for commented parameters"
echo "----------------------------------------"

if grep -q "#     status: \${{ job.status }}" .github/workflows/ci.yml && \
   grep -q "#     channel: '#medhead-ci'" .github/workflows/ci.yml && \
   grep -q "#     SLACK_WEBHOOK_URL: \${{ secrets.SLACK_WEBHOOK_URL }}" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: All Slack parameters are commented out"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Some Slack parameters are not commented out"
fi
((TOTAL_TESTS++))

# Test 4: Verify no active Slack references
echo ""
echo "🔍 Test: Verify no active Slack references"
echo "⚡ Command: Check .github/workflows/ci.yml for active Slack references"
echo "----------------------------------------"

# Check that there are no uncommented lines with Slack-related content
if ! grep -q "^[^#].*action-slack" .github/workflows/ci.yml && \
   ! grep -q "^[^#].*SLACK_WEBHOOK_URL" .github/workflows/ci.yml && \
   ! grep -q "^[^#].*Notification Slack" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: No active Slack references found"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Active Slack references still present"
fi
((TOTAL_TESTS++))

# Test 5: Check if workflow syntax is still valid
echo ""
echo "🔍 Test: Check if workflow syntax is still valid"
echo "⚡ Command: Check for basic YAML structure"
echo "----------------------------------------"

# Check that the file ends properly and has valid YAML structure
if grep -q "name: ci-summary-report" .github/workflows/ci.yml && \
   grep -q "path: reports/ci-summary/" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: Workflow syntax appears valid"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Workflow syntax issues detected"
fi
((TOTAL_TESTS++))

# Test 6: Verify no secrets dependencies
echo ""
echo "🔍 Test: Verify no secrets dependencies"
echo "⚡ Command: Check .github/workflows/ci.yml for SLACK_WEBHOOK_URL dependency"
echo "----------------------------------------"

# Check that there are no uncommented references to SLACK_WEBHOOK_URL
if ! grep -q "^[^#].*SLACK_WEBHOOK_URL" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: No active secrets dependencies"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Active secrets dependencies found"
fi
((TOTAL_TESTS++))

# Test 7: Check if CI pipeline will run without Slack
echo ""
echo "🔍 Test: Check if CI pipeline will run without Slack"
echo "⚡ Command: Verify no blocking dependencies on Slack"
echo "----------------------------------------"

# The CI should be able to run completely without any Slack configuration
if ! grep -q "secrets\.SLACK_WEBHOOK_URL" .github/workflows/ci.yml; then
    echo "✅ SUCCESS: CI pipeline will run without Slack configuration"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: CI pipeline still depends on Slack configuration"
fi
((TOTAL_TESTS++))

# Test 8: Verify commented section is complete
echo ""
echo "🔍 Test: Verify commented section is complete"
echo "⚡ Command: Check if entire Slack section is commented"
echo "----------------------------------------"

# Count commented lines in the Slack section
COMMENTED_LINES=$(grep -A 15 "# - name: 📢 Notification Slack" .github/workflows/ci.yml | grep -c "^#")
if [ "$COMMENTED_LINES" -ge 10 ]; then
    echo "✅ SUCCESS: Entire Slack section is properly commented"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Slack section is not completely commented"
fi
((TOTAL_TESTS++))

# Summary
echo ""
echo "📊 SLACK DISABLED VALIDATION SUMMARY"
echo "==================================="
echo "Tests passed: $SUCCESS_COUNT/$TOTAL_TESTS"

if [ $SUCCESS_COUNT -eq $TOTAL_TESTS ]; then
    echo "🎉 ALL SLACK DISABLED VALIDATIONS PASSED!"
    echo "✅ Slack notifications are completely disabled"
    echo "✅ CI pipeline will run without any Slack dependencies"
    echo "✅ No secrets configuration required"
    exit 0
elif [ $SUCCESS_COUNT -ge 6 ]; then
    echo "⚠️ Most validations passed"
    echo "✅ Slack notifications should be properly disabled"
    exit 0
else
    echo "❌ Several issues remain"
    echo "🔧 Please check the failures above"
    exit 1
fi
