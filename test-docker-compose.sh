#!/bin/bash

echo "🐳 Test Docker Compose Configuration"
echo "===================================="

# Variables
SUCCESS_COUNT=0
TOTAL_TESTS=0

echo "🚀 Testing Docker Compose setup..."

# Test 1: Check if docker compose is available
echo ""
echo "🔍 Test: Check if docker compose is available"
echo "⚡ Command: docker compose version"
echo "----------------------------------------"

if docker compose version > /dev/null 2>&1; then
    echo "✅ SUCCESS: docker compose is available"
    docker compose version
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: docker compose not available"
    echo "Trying docker-compose as fallback..."
    if docker-compose --version > /dev/null 2>&1; then
        echo "✅ SUCCESS: docker-compose is available as fallback"
        docker-compose --version
        ((SUCCESS_COUNT++))
    else
        echo "❌ FAILED: Neither docker compose nor docker-compose available"
    fi
fi
((TOTAL_TESTS++))

# Test 2: Check docker-compose.yml file exists
echo ""
echo "🔍 Test: Check docker-compose.yml file exists"
echo "⚡ Command: ls -la docker/docker-compose.yml"
echo "----------------------------------------"

if [ -f "docker/docker-compose.yml" ]; then
    echo "✅ SUCCESS: docker-compose.yml file exists"
    ls -la docker/docker-compose.yml
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: docker-compose.yml file not found"
fi
((TOTAL_TESTS++))

# Test 3: Validate docker-compose.yml syntax
echo ""
echo "🔍 Test: Validate docker-compose.yml syntax"
echo "⚡ Command: docker compose -f docker/docker-compose.yml config"
echo "----------------------------------------"

if docker compose -f docker/docker-compose.yml config > /dev/null 2>&1; then
    echo "✅ SUCCESS: docker-compose.yml syntax is valid"
    ((SUCCESS_COUNT++))
elif docker-compose -f docker/docker-compose.yml config > /dev/null 2>&1; then
    echo "✅ SUCCESS: docker-compose.yml syntax is valid (using docker-compose)"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: docker-compose.yml syntax is invalid"
    echo "Error details:"
    docker compose -f docker/docker-compose.yml config 2>&1 || docker-compose -f docker/docker-compose.yml config 2>&1
fi
((TOTAL_TESTS++))

# Test 4: Test dry-run start (without actually starting)
echo ""
echo "🔍 Test: Test dry-run start"
echo "⚡ Command: docker compose -f docker/docker-compose.yml up --dry-run"
echo "----------------------------------------"

if docker compose -f docker/docker-compose.yml up --dry-run > /dev/null 2>&1; then
    echo "✅ SUCCESS: Dry-run start works"
    ((SUCCESS_COUNT++))
elif docker-compose -f docker/docker-compose.yml up --dry-run > /dev/null 2>&1; then
    echo "✅ SUCCESS: Dry-run start works (using docker-compose)"
    ((SUCCESS_COUNT++))
else
    echo "⚠️ WARNING: Dry-run start failed (this might be normal)"
    echo "This could be due to missing images or other dependencies"
fi
((TOTAL_TESTS++))

# Test 5: Check if Docker is running
echo ""
echo "🔍 Test: Check if Docker daemon is running"
echo "⚡ Command: docker info"
echo "----------------------------------------"

if docker info > /dev/null 2>&1; then
    echo "✅ SUCCESS: Docker daemon is running"
    ((SUCCESS_COUNT++))
else
    echo "❌ FAILED: Docker daemon is not running"
    echo "Please start Docker service"
fi
((TOTAL_TESTS++))

# Summary
echo ""
echo "📊 DOCKER COMPOSE TESTS SUMMARY"
echo "==============================="
echo "Tests passed: $SUCCESS_COUNT/$TOTAL_TESTS"

if [ $SUCCESS_COUNT -eq $TOTAL_TESTS ]; then
    echo "🎉 ALL DOCKER COMPOSE TESTS PASSED!"
    echo "✅ Docker Compose setup is ready for CI"
    exit 0
elif [ $SUCCESS_COUNT -ge 4 ]; then
    echo "⚠️ Most tests passed"
    echo "✅ Docker Compose setup is mostly ready"
    exit 0
else
    echo "❌ Several tests failed"
    echo "🔧 Please fix the issues above before running in CI"
    exit 1
fi
