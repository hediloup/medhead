#!/bin/bash

# K6 Performance Testing script for MedHead API
# This script runs comprehensive performance tests using K6

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
REPORTS_DIR="reports/k6"
TEST_SCRIPT="scripts/k6-performance-test.js"

# Create reports directory
mkdir -p "$REPORTS_DIR"

echo "🚀 MedHead API K6 Performance Testing"
echo "====================================="

# Check if K6 is available
if ! command -v k6 >/dev/null 2>&1; then
    print_error "K6 is not installed or not in PATH"
    print_status "Please install K6:"
    print_status "  # Ubuntu/Debian:"
    print_status "  sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69"
    print_status "  echo \"deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main\" | sudo tee /etc/apt/sources.list.d/k6.list"
    print_status "  sudo apt-get update"
    print_status "  sudo apt-get install k6"
    print_status ""
    print_status "  # Or using Docker:"
    print_status "  docker run --rm -i grafana/k6 run - < $TEST_SCRIPT"
    exit 1
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

# Check if test script exists
if [ ! -f "$TEST_SCRIPT" ]; then
    print_error "Test script not found: $TEST_SCRIPT"
    exit 1
fi

print_status "Starting K6 performance tests..."

# Test 1: Basic Performance Test
print_status "Running basic performance test..."
k6 run \
    --env API_BASE_URL="$API_BASE_URL" \
    --out json="$REPORTS_DIR/basic-performance-results.json" \
    --summary-export="$REPORTS_DIR/basic-performance-summary.json" \
    "$TEST_SCRIPT" > "$REPORTS_DIR/basic-performance-output.log" 2>&1

if [ $? -eq 0 ]; then
    print_success "Basic performance test completed"
else
    print_error "Basic performance test failed"
    exit 1
fi

# Test 2: Load Test with Custom Configuration
print_status "Running load test with custom configuration..."
k6 run \
    --env API_BASE_URL="$API_BASE_URL" \
    --stage 30s:20,1m:20,30s:0 \
    --threshold http_req_duration=p(95)<1500 \
    --threshold http_req_failed=rate<0.03 \
    --out json="$REPORTS_DIR/load-test-results.json" \
    --summary-export="$REPORTS_DIR/load-test-summary.json" \
    "$TEST_SCRIPT" > "$REPORTS_DIR/load-test-output.log" 2>&1

if [ $? -eq 0 ]; then
    print_success "Load test completed"
else
    print_error "Load test failed"
    exit 1
fi

# Test 3: Stress Test
print_status "Running stress test..."
k6 run \
    --env API_BASE_URL="$API_BASE_URL" \
    --stage 10s:50,1m:50,10s:100,1m:100,10s:0 \
    --threshold http_req_duration=p(95)<3000 \
    --threshold http_req_failed=rate<0.1 \
    --out json="$REPORTS_DIR/stress-test-results.json" \
    --summary-export="$REPORTS_DIR/stress-test-summary.json" \
    "$TEST_SCRIPT" > "$REPORTS_DIR/stress-test-output.log" 2>&1

if [ $? -eq 0 ]; then
    print_success "Stress test completed"
else
    print_error "Stress test failed"
    exit 1
fi

# Test 4: Spike Test
print_status "Running spike test..."
k6 run \
    --env API_BASE_URL="$API_BASE_URL" \
    --stage 5s:10,10s:200,5s:10,10s:0 \
    --threshold http_req_duration=p(95)<5000 \
    --threshold http_req_failed=rate<0.2 \
    --out json="$REPORTS_DIR/spike-test-results.json" \
    --summary-export="$REPORTS_DIR/spike-test-summary.json" \
    "$TEST_SCRIPT" > "$REPORTS_DIR/spike-test-output.log" 2>&1

if [ $? -eq 0 ]; then
    print_success "Spike test completed"
else
    print_error "Spike test failed"
    exit 1
fi

# Generate HTML reports
print_status "Generating HTML reports..."

# Basic Performance Report
if [ -f "$REPORTS_DIR/basic-performance-summary.json" ]; then
    cat > "$REPORTS_DIR/basic-performance-report.html" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>MedHead API - Basic Performance Test Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .metric { background: #f5f5f5; padding: 15px; margin: 10px 0; border-radius: 5px; }
        .success { color: green; }
        .warning { color: orange; }
        .error { color: red; }
    </style>
</head>
<body>
    <h1>MedHead API - Basic Performance Test Report</h1>
    <h2>Test Summary</h2>
    <div class="metric">
        <h3>Test Duration</h3>
        <p>$(jq -r '.testRunDurationMs / 1000' "$REPORTS_DIR/basic-performance-summary.json") seconds</p>
    </div>
    <div class="metric">
        <h3>Total Requests</h3>
        <p>$(jq -r '.metrics.http_reqs.values.count' "$REPORTS_DIR/basic-performance-summary.json")</p>
    </div>
    <div class="metric">
        <h3>Request Rate</h3>
        <p>$(jq -r '.metrics.http_reqs.values.rate' "$REPORTS_DIR/basic-performance-summary.json") requests/second</p>
    </div>
    <div class="metric">
        <h3>Average Response Time</h3>
        <p>$(jq -r '.metrics.http_req_duration.values.avg' "$REPORTS_DIR/basic-performance-summary.json") ms</p>
    </div>
    <div class="metric">
        <h3>95th Percentile Response Time</h3>
        <p>$(jq -r '.metrics.http_req_duration.values.p95' "$REPORTS_DIR/basic-performance-summary.json") ms</p>
    </div>
    <div class="metric">
        <h3>Error Rate</h3>
        <p>$(jq -r '.metrics.http_req_failed.values.rate * 100' "$REPORTS_DIR/basic-performance-summary.json")%</p>
    </div>
    <h2>Threshold Results</h2>
    <div class="metric">
        <p>$(jq -r '.thresholds | to_entries[] | "\(.key): \(.value.ok)"' "$REPORTS_DIR/basic-performance-summary.json")</p>
    </div>
</body>
</html>
EOF
fi

# Generate summary report
print_status "Generating summary report..."

cat > "$REPORTS_DIR/k6-test-summary.md" << EOF
# MedHead API K6 Performance Test Summary

## Test Execution Date
$(date)

## Test Environment
- **API URL**: $API_BASE_URL
- **K6 Version**: $(k6 version)
- **Test Script**: $TEST_SCRIPT

## Test Results Overview

### 1. Basic Performance Test
- **Duration**: $(jq -r '.testRunDurationMs / 1000' "$REPORTS_DIR/basic-performance-summary.json" 2>/dev/null || echo "N/A") seconds
- **Total Requests**: $(jq -r '.metrics.http_reqs.values.count' "$REPORTS_DIR/basic-performance-summary.json" 2>/dev/null || echo "N/A")
- **Request Rate**: $(jq -r '.metrics.http_reqs.values.rate' "$REPORTS_DIR/basic-performance-summary.json" 2>/dev/null || echo "N/A") requests/second
- **Average Response Time**: $(jq -r '.metrics.http_req_duration.values.avg' "$REPORTS_DIR/basic-performance-summary.json" 2>/dev/null || echo "N/A") ms
- **95th Percentile**: $(jq -r '.metrics.http_req_duration.values.p95' "$REPORTS_DIR/basic-performance-summary.json" 2>/dev/null || echo "N/A") ms
- **Error Rate**: $(jq -r '.metrics.http_req_failed.values.rate * 100' "$REPORTS_DIR/basic-performance-summary.json" 2>/dev/null || echo "N/A")%

### 2. Load Test
- **Configuration**: 20 users for 1 minute, then ramp down
- **Thresholds**: 95th percentile < 1500ms, error rate < 3%
- **Results**: Check load-test-summary.json for details

### 3. Stress Test
- **Configuration**: 50 users for 1 minute, then 100 users for 1 minute
- **Thresholds**: 95th percentile < 3000ms, error rate < 10%
- **Results**: Check stress-test-summary.json for details

### 4. Spike Test
- **Configuration**: Spike to 200 users for 10 seconds
- **Thresholds**: 95th percentile < 5000ms, error rate < 20%
- **Results**: Check spike-test-summary.json for details

## Performance Metrics Summary

| Test Type | Avg Response Time | 95th Percentile | Error Rate | Status |
|-----------|------------------|-----------------|------------|---------|
| Basic Performance | $(jq -r '.metrics.http_req_duration.values.avg' "$REPORTS_DIR/basic-performance-summary.json" 2>/dev/null || echo "N/A") ms | $(jq -r '.metrics.http_req_duration.values.p95' "$REPORTS_DIR/basic-performance-summary.json" 2>/dev/null || echo "N/A") ms | $(jq -r '.metrics.http_req_failed.values.rate * 100' "$REPORTS_DIR/basic-performance-summary.json" 2>/dev/null || echo "N/A")% | ✅ |
| Load Test | Check summary | Check summary | Check summary | ✅ |
| Stress Test | Check summary | Check summary | Check summary | ✅ |
| Spike Test | Check summary | Check summary | Check summary | ✅ |

## Files Generated
- \`basic-performance-results.json\`: Raw results for basic performance test
- \`basic-performance-summary.json\`: Summary metrics for basic performance test
- \`basic-performance-report.html\`: HTML report for basic performance test
- \`load-test-results.json\`: Raw results for load test
- \`load-test-summary.json\`: Summary metrics for load test
- \`stress-test-results.json\`: Raw results for stress test
- \`stress-test-summary.json\`: Summary metrics for stress test
- \`spike-test-results.json\`: Raw results for spike test
- \`spike-test-summary.json\`: Summary metrics for spike test
- \`k6-test-summary.md\`: This summary report

## How to View Results
1. Open the HTML report: \`basic-performance-report.html\`
2. Check JSON summary files for detailed metrics
3. Review threshold results for pass/fail status
4. Analyze response time percentiles
5. Monitor error rates under different load conditions

## Recommendations
1. **Performance**: Monitor 95th percentile response times
2. **Scalability**: Test with higher user loads if needed
3. **Reliability**: Ensure error rates stay below thresholds
4. **Monitoring**: Set up continuous performance monitoring
5. **Optimization**: Focus on response time improvements

## Next Steps
1. Analyze detailed results for performance bottlenecks
2. Optimize API based on test results
3. Set up continuous performance monitoring
4. Schedule regular performance testing
5. Consider auto-scaling for production deployment
EOF

print_success "K6 test summary generated: $REPORTS_DIR/k6-test-summary.md"

echo ""
print_success "🎉 K6 performance testing completed successfully!"
echo ""
print_status "Test Reports Location: $REPORTS_DIR/"
print_status "Summary Report: $REPORTS_DIR/k6-test-summary.md"
print_status "HTML Report: $REPORTS_DIR/basic-performance-report.html"
echo ""
print_status "JSON Results:"
echo "  📊 Basic Performance: $REPORTS_DIR/basic-performance-summary.json"
echo "  📊 Load Test: $REPORTS_DIR/load-test-summary.json"
echo "  📊 Stress Test: $REPORTS_DIR/stress-test-summary.json"
echo "  📊 Spike Test: $REPORTS_DIR/spike-test-summary.json"
echo ""
print_success "API performance testing completed! 🚀"
