#!/bin/bash

# JMeter stress testing script for MedHead API
# This script runs comprehensive stress tests using Apache JMeter

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Configuration
API_BASE_URL="http://localhost:8080"
REPORTS_DIR="reports/jmeter"
JMETER_HOME="${JMETER_HOME:-/opt/apache-jmeter}"
TEST_PLAN="scripts/jmeter-stress-test.jmx"

# Create reports directory
mkdir -p "$REPORTS_DIR"

echo "🚀 MedHead API JMeter Stress Testing"
echo "===================================="

# Check if JMeter is available
if ! command -v jmeter >/dev/null 2>&1 && [ ! -f "$JMETER_HOME/bin/jmeter" ]; then
    print_error "JMeter is not installed or not in PATH"
    print_status "Please install JMeter:"
    print_status "  wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.4.1.tgz"
    print_status "  tar -xzf apache-jmeter-5.4.1.tgz"
    print_status "  sudo mv apache-jmeter-5.4.1 /opt/apache-jmeter"
    print_status "  export JMETER_HOME=/opt/apache-jmeter"
    print_status "  export PATH=\$JMETER_HOME/bin:\$PATH"
    exit 1
fi

# Set JMeter command
if command -v jmeter >/dev/null 2>&1; then
    JMETER_CMD="jmeter"
else
    JMETER_CMD="$JMETER_HOME/bin/jmeter"
fi

# Check if API is running
print_status "Checking if API is running..."
if ! curl -s "$API_BASE_URL/api/health" > /dev/null; then
    print_error "API is not running at $API_BASE_URL"
    print_status "Please start the application first:"
    print_status "  cd docker && ./start-medhead.sh"
    exit 1
fi
print_success "API is running"

# Check if test plan exists
if [ ! -f "$TEST_PLAN" ]; then
    print_error "Test plan not found: $TEST_PLAN"
    exit 1
fi

print_status "Starting JMeter stress tests..."

# Test 1: Basic Load Test
print_status "Running basic load test..."
"$JMETER_CMD" -n -t "$TEST_PLAN" \
    -l "$REPORTS_DIR/basic-load-results.jtl" \
    -e -o "$REPORTS_DIR/basic-load-report" \
    -JAPI_BASE_URL="$API_BASE_URL" \
    -JSPECIALTY="Cardiology" \
    -JLATITUDE="51.5074" \
    -JLONGITUDE="-0.1278" \
    -JThreadGroup.num_threads="10" \
    -JThreadGroup.ramp_time="30" \
    -JLoopController.loops="10"

if [ $? -eq 0 ]; then
    print_success "Basic load test completed"
else
    print_error "Basic load test failed"
    exit 1
fi

# Test 2: High Load Test
print_status "Running high load test..."
"$JMETER_CMD" -n -t "$TEST_PLAN" \
    -l "$REPORTS_DIR/high-load-results.jtl" \
    -e -o "$REPORTS_DIR/high-load-report" \
    -JAPI_BASE_URL="$API_BASE_URL" \
    -JSPECIALTY="Cardiology" \
    -JLATITUDE="51.5074" \
    -JLONGITUDE="-0.1278" \
    -JThreadGroup.num_threads="50" \
    -JThreadGroup.ramp_time="60" \
    -JLoopController.loops="20"

if [ $? -eq 0 ]; then
    print_success "High load test completed"
else
    print_error "High load test failed"
    exit 1
fi

# Test 3: Spike Test
print_status "Running spike test..."
"$JMETER_CMD" -n -t "$TEST_PLAN" \
    -l "$REPORTS_DIR/spike-test-results.jtl" \
    -e -o "$REPORTS_DIR/spike-test-report" \
    -JAPI_BASE_URL="$API_BASE_URL" \
    -JSPECIALTY="Cardiology" \
    -JLATITUDE="51.5074" \
    -JLONGITUDE="-0.1278" \
    -JThreadGroup.num_threads="100" \
    -JThreadGroup.ramp_time="10" \
    -JLoopController.loops="5"

if [ $? -eq 0 ]; then
    print_success "Spike test completed"
else
    print_error "Spike test failed"
    exit 1
fi

# Generate summary report
print_status "Generating summary report..."

cat > "$REPORTS_DIR/jmeter-test-summary.md" << EOF
# MedHead API JMeter Stress Test Summary

## Test Execution Date
$(date)

## Test Environment
- **API URL**: $API_BASE_URL
- **JMeter Version**: $("$JMETER_CMD" --version | head -1)
- **Test Plan**: $TEST_PLAN

## Test Results Overview

### 1. Basic Load Test
- **Threads**: 10
- **Ramp-up**: 30 seconds
- **Loops**: 10
- **Total Requests**: 100
- **Report**: [basic-load-report/index.html]($REPORTS_DIR/basic-load-report/index.html)

### 2. High Load Test
- **Threads**: 50
- **Ramp-up**: 60 seconds
- **Loops**: 20
- **Total Requests**: 1000
- **Report**: [high-load-report/index.html]($REPORTS_DIR/high-load-report/index.html)

### 3. Spike Test
- **Threads**: 100
- **Ramp-up**: 10 seconds
- **Loops**: 5
- **Total Requests**: 500
- **Report**: [spike-test-report/index.html]($REPORTS_DIR/spike-test-report/index.html)

## Performance Metrics

### Basic Load Test Results
- **Average Response Time**: Check basic-load-report for details
- **Throughput**: Check basic-load-report for details
- **Error Rate**: Check basic-load-report for details

### High Load Test Results
- **Average Response Time**: Check high-load-report for details
- **Throughput**: Check high-load-report for details
- **Error Rate**: Check high-load-report for details

### Spike Test Results
- **Average Response Time**: Check spike-test-report for details
- **Throughput**: Check spike-test-report for details
- **Error Rate**: Check spike-test-report for details

## Files Generated
- \`basic-load-results.jtl\`: Raw results for basic load test
- \`basic-load-report/\`: HTML report for basic load test
- \`high-load-results.jtl\`: Raw results for high load test
- \`high-load-report/\`: HTML report for high load test
- \`spike-test-results.jtl\`: Raw results for spike test
- \`spike-test-report/\`: HTML report for spike test
- \`jmeter-test-summary.md\`: This summary report

## How to View Reports
1. Open the HTML report files in your browser
2. Check the Summary Report section for key metrics
3. Review Response Times Over Time graphs
4. Analyze Error Rate trends
5. Compare Throughput metrics across different tests

## Recommendations
1. Review response time percentiles (90th, 95th, 99th)
2. Monitor error rates under different load conditions
3. Check for memory leaks during sustained load
4. Validate performance under spike conditions
5. Consider load balancing for higher throughput requirements

## Next Steps
1. Analyze detailed reports for performance bottlenecks
2. Optimize API based on test results
3. Set up continuous performance monitoring
4. Schedule regular stress testing
5. Consider auto-scaling for production deployment
EOF

print_success "JMeter test summary generated: $REPORTS_DIR/jmeter-test-summary.md"

echo ""
print_success "🎉 JMeter stress testing completed successfully!"
echo ""
print_status "Test Reports Location: $REPORTS_DIR/"
print_status "Summary Report: $REPORTS_DIR/jmeter-test-summary.md"
echo ""
print_status "HTML Reports:"
echo "  📊 Basic Load Test: $REPORTS_DIR/basic-load-report/index.html"
echo "  📊 High Load Test: $REPORTS_DIR/high-load-report/index.html"
echo "  📊 Spike Test: $REPORTS_DIR/spike-test-report/index.html"
echo ""
print_status "Raw Results:"
echo "  📄 Basic Load: $REPORTS_DIR/basic-load-results.jtl"
echo "  📄 High Load: $REPORTS_DIR/high-load-results.jtl"
echo "  📄 Spike Test: $REPORTS_DIR/spike-test-results.jtl"
echo ""
print_success "Open the HTML reports in your browser to view detailed results! 🚀"
