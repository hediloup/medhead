describe.skip('Form Validation Tests - Disabled for CI (requires Angular validation and API)', () => {
  beforeEach(() => {
    cy.visit('/')
  })

  it('should validate required specialty field', () => {
    // Try to submit without selecting specialty
    cy.get('button[type="submit"]').click()
    
    // Check if specialty field is invalid
    cy.get('#specialty').should('have.class', 'is-invalid')
    cy.get('#specialty:invalid').should('exist')
    
    // Check if error message appears
    cy.contains('This field is required').should('be.visible')
  })

  it('should validate required address field', () => {
    // Select specialty but leave address empty
    cy.get('#specialty').select('Cardiology')
    cy.get('button[type="submit"]').click()
    
    // Check if address field is invalid
    cy.get('#address').should('have.class', 'is-invalid')
    cy.get('#address:invalid').should('exist')
    
    // Check if error message appears
    cy.contains('This field is required').should('be.visible')
  })

  it('should validate minimum length for address', () => {
    // Select specialty and enter short address
    cy.get('#specialty').select('Neurology')
    cy.get('#address').type('abc') // Less than 5 characters
    cy.get('button[type="submit"]').click()
    
    // Check if address field is invalid
    cy.get('#address').should('have.class', 'is-invalid')
    
    // Check if error message appears
    cy.contains('Minimum 5 characters required').should('be.visible')
  })

  it('should validate all fields together', () => {
    // Try to submit empty form
    cy.get('button[type="submit"]').click()
    
    // Both fields should be invalid
    cy.get('#specialty').should('have.class', 'is-invalid')
    cy.get('#address').should('have.class', 'is-invalid')
    
    // Error messages should appear
    cy.contains('This field is required').should('have.length.at.least', 1)
  })

  it('should clear validation errors when form is filled correctly', () => {
    // First, trigger validation errors
    cy.get('button[type="submit"]').click()
    cy.get('#specialty').should('have.class', 'is-invalid')
    cy.get('#address').should('have.class', 'is-invalid')
    
    // Fill form correctly
    cy.get('#specialty').select('Emergency Medicine')
    cy.get('#address').type('123 Rue de la Paix, Paris, France')
    
    // Validation errors should be cleared
    cy.get('#specialty').should('not.have.class', 'is-invalid')
    cy.get('#address').should('not.have.class', 'is-invalid')
    
    // Submit button should be enabled
    cy.get('button[type="submit"]').should('not.be.disabled')
  })

  it('should validate specialty selection', () => {
    // Check that placeholder option is not valid
    cy.get('#specialty').should('have.value', '')
    cy.get('button[type="submit"]').click()
    cy.get('#specialty').should('have.class', 'is-invalid')
    
    // Select a valid specialty
    cy.get('#specialty').select('Pediatrics')
    cy.get('#specialty').should('not.have.class', 'is-invalid')
  })

  it('should validate address length dynamically', () => {
    cy.get('#specialty').select('Orthopedics')
    
    // Test with short address
    cy.get('#address').type('abc')
    cy.get('button[type="submit"]').click()
    cy.get('#address').should('have.class', 'is-invalid')
    cy.contains('Minimum 5 characters required').should('be.visible')
    
    // Clear and type longer address
    cy.get('#address').clear().type('Valid address with enough characters')
    cy.get('#address').should('not.have.class', 'is-invalid')
  })

  it('should handle form reset correctly', () => {
    // Fill form with invalid data
    cy.get('#specialty').select('Cardiology')
    cy.get('#address').type('abc') // Short address
    cy.get('button[type="submit"]').click()
    
    // Verify validation errors
    cy.get('#specialty').should('have.class', 'is-invalid')
    cy.get('#address').should('have.class', 'is-invalid')
    
    // Reset form
    cy.get('button').contains('🔄 Nouvelle recherche').click()
    
    // Verify form is cleared and validation errors are gone
    cy.get('#specialty').should('have.value', '')
    cy.get('#address').should('have.value', '')
    cy.get('#specialty').should('not.have.class', 'is-invalid')
    cy.get('#address').should('not.have.class', 'is-invalid')
  })

  it('should validate form on blur events', () => {
    // Focus and blur specialty field
    cy.get('#specialty').focus().blur()
    cy.get('#specialty').should('have.class', 'is-invalid')
    
    // Focus and blur address field
    cy.get('#address').focus().blur()
    cy.get('#address').should('have.class', 'is-invalid')
  })

  it('should show appropriate error messages for different validation errors', () => {
    // Test required field error for specialty
    cy.get('#specialty').focus().blur()
    cy.contains('This field is required').should('be.visible')
    
    // Test required field error for address
    cy.get('#address').focus().blur()
    cy.contains('This field is required').should('be.visible')
    
    // Test minimum length error for address
    cy.get('#address').clear().type('abc').blur()
    cy.contains('Minimum 5 characters required').should('be.visible')
  })

  it('should disable submit button when form is invalid', () => {
    // Submit button should be disabled with empty form
    cy.get('button[type="submit"]').should('be.disabled')
    
    // Select specialty but leave address empty
    cy.get('#specialty').select('Dermatology')
    cy.get('button[type="submit"]').should('be.disabled')
    
    // Enter valid address
    cy.get('#address').type('Valid address with enough characters')
    cy.get('button[type="submit"]').should('not.be.disabled')
  })

  it('should maintain validation state during API calls', () => {
    // Mock API responses
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

    // Fill form with valid data
    cy.get('#specialty').select('Cardiology')
    cy.get('#address').type('Paris, France')
    
    // Submit form
    cy.get('button[type="submit"]').click()
    
    // Form should be disabled during API call
    cy.get('#specialty').should('be.disabled')
    cy.get('#address').should('be.disabled')
    cy.get('button[type="submit"]').should('be.disabled')
    
    // Wait for API calls to complete
    cy.wait('@geocodingRequest')
    cy.wait('@allocationRequest')
    
    // Form should be re-enabled after completion
    cy.get('#specialty').should('not.be.disabled')
    cy.get('#address').should('not.be.disabled')
    cy.get('button[type="submit"]').should('not.be.disabled')
  })
})
