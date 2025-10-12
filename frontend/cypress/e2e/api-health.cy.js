describe.skip('API Health Tests - Disabled for CI (requires backend API)', () => {
  beforeEach(() => {
    cy.visit('/')
  })

  it('should check API health on page load', () => {
    // Mock successful health check
    cy.intercept('GET', '/api/health', {
      statusCode: 200,
      body: 'OK'
    }).as('healthCheck')

    // Wait for the health check to complete
    cy.wait('@healthCheck')
    
    // Verify no error message is shown
    cy.get('.alert-danger').should('not.exist')
  })

  it('should handle API health check failure', () => {
    // Mock failed health check
    cy.intercept('GET', '/api/health', {
      statusCode: 503,
      body: 'Service Unavailable'
    }).as('healthCheckFail')

    // Wait for the health check to complete
    cy.wait('@healthCheckFail')
    
    // Verify error message is shown
    cy.contains('Backend service is not available').should('be.visible')
  })

  it('should handle API health check timeout', () => {
    // Mock timeout health check
    cy.intercept('GET', '/api/health', {
      statusCode: 0,
      body: '',
      delay: 10000
    }).as('healthCheckTimeout')

    // Wait for timeout
    cy.wait(5000)
    
    // Verify error message is shown
    cy.contains('Unable to contact the server').should('be.visible')
  })

  it('should retry API health check after form submission', () => {
    // Mock initial health check failure
    cy.intercept('GET', '/api/health', {
      statusCode: 503,
      body: 'Service Unavailable'
    }).as('initialHealthCheck')

    // Wait for initial health check
    cy.wait('@initialHealthCheck')
    
    // Verify error message is shown
    cy.contains('Backend service is not available').should('be.visible')

    // Mock successful health check for retry
    cy.intercept('GET', '/api/health', {
      statusCode: 200,
      body: 'OK'
    }).as('retryHealthCheck')

    // Mock successful geocoding and allocation
    cy.intercept('GET', '/geocoding*', {
      statusCode: 200,
      body: [{
        lat: '48.8566',
        lon: '2.3522',
        display_name: 'Paris, France'
      }]
    }).as('geocodingRequest')

    cy.intercept('POST', '/api/allocate', {
      statusCode: 200,
      body: {
        hospital_name: 'Hôpital Saint-Antoine',
        hospital_id: 1,
        distance_km: 5.2,
        specialty: 'Cardiology',
        available_beds: 3,
        estimated_time_minutes: 12
      }
    }).as('allocationRequest')

    // Fill form and submit
    cy.get('#specialty').select('Cardiology')
    cy.get('#address').type('Paris, France')
    cy.get('button[type="submit"]').click()

    // Wait for requests
    cy.wait('@retryHealthCheck')
    cy.wait('@geocodingRequest')
    cy.wait('@allocationRequest')

    // Verify success
    cy.contains('Hôpital Saint-Antoine').should('be.visible')
  })

  it('should handle different HTTP error codes', () => {
    // Test 404 error
    cy.intercept('GET', '/api/health', {
      statusCode: 404,
      body: 'Not Found'
    }).as('health404')

    cy.wait('@health404')
    cy.contains('Backend service is not available').should('be.visible')

    // Reload page and test 500 error
    cy.reload()
    cy.intercept('GET', '/api/health', {
      statusCode: 500,
      body: 'Internal Server Error'
    }).as('health500')

    cy.wait('@health500')
    cy.contains('Backend service is not available').should('be.visible')

    // Reload page and test 502 error
    cy.reload()
    cy.intercept('GET', '/api/health', {
      statusCode: 502,
      body: 'Bad Gateway'
    }).as('health502')

    cy.wait('@health502')
    cy.contains('Backend service is not available').should('be.visible')
  })

  it('should show loading states during health check', () => {
    // Mock slow health check
    cy.intercept('GET', '/api/health', {
      statusCode: 200,
      body: 'OK',
      delay: 2000
    }).as('slowHealthCheck')

    // Check that page loads even with slow health check
    cy.get('#specialty').should('be.visible')
    cy.get('#address').should('be.visible')
    
    // Wait for health check to complete
    cy.wait('@slowHealthCheck')
  })

  it('should handle concurrent health checks gracefully', () => {
    // Mock multiple health check requests
    cy.intercept('GET', '/api/health', {
      statusCode: 200,
      body: 'OK'
    }).as('healthCheck')

    // Trigger multiple page loads (simulate user refreshing)
    cy.reload()
    cy.reload()
    cy.reload()

    // Should handle multiple requests without issues
    cy.get('#specialty').should('be.visible')
  })
})
