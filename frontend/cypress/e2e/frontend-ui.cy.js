describe('MedHead Application E2E Tests', () => {
  beforeEach(() => {
    // Visit the application before each test
    cy.visit('/')
  })

  it('should load the homepage with all elements', () => {
    // Check if the main title is visible
    cy.contains('MedHead - Allocation d\'Lits d\'Hôpital').should('be.visible')
    
    // Check if the form elements are present
    cy.get('select[name="specialty"]').should('be.visible')
    cy.get('input[name="address"]').should('be.visible')
    cy.get('button[type="submit"]').should('be.visible')
  })

  it('should display specialty options', () => {
    // Click on the specialty dropdown
    cy.get('select[name="specialty"]').click()
    
    // Check if common specialties are available
    cy.contains('Cardiology').should('be.visible')
    cy.contains('Neurology').should('be.visible')
    cy.contains('Emergency Medicine').should('be.visible')
  })

  it('should handle form submission with valid data', () => {
    // Select a specialty
    cy.get('select[name="specialty"]').select('Cardiology')
    
    // Enter an address
    cy.get('input[name="address"]').type('London, UK')
    
    // Mock the API response
    cy.intercept('POST', '/api/allocate', {
      statusCode: 200,
      body: {
        hospitalName: 'St George\'s Hospital',
        hospitalId: 1,
        distanceKm: 9.5,
        specialty: 'Cardiology',
        availableBeds: 25,
        estimatedTimeMinutes: 114
      }
    }).as('allocationRequest')

    // Mock the geocoding response
    cy.intercept('GET', '/geocoding*', {
      statusCode: 200,
      body: [{
        lat: '51.5074',
        lon: '-0.1278',
        display_name: 'London, UK'
      }]
    }).as('geocodingRequest')

    // Submit the form
    cy.get('button[type="submit"]').click()

    // Wait for API calls
    cy.wait('@geocodingRequest')
    cy.wait('@allocationRequest')

    // Check if results are displayed
    cy.contains('St George\'s Hospital').should('be.visible')
    cy.contains('9.5 km').should('be.visible')
    cy.contains('25').should('be.visible')
  })

  it('should handle geocoding errors gracefully', () => {
    // Select a specialty
    cy.get('select[name="specialty"]').select('Cardiology')
    
    // Enter an invalid address
    cy.get('input[name="address"]').type('InvalidAddress12345')
    
    // Mock geocoding error
    cy.intercept('GET', '/geocoding*', {
      statusCode: 404,
      body: []
    }).as('geocodingError')

    // Submit the form
    cy.get('button[type="submit"]').click()

    // Wait for the error response
    cy.wait('@geocodingError')

    // Check if error message is displayed
    cy.contains('Adresse non trouvée').should('be.visible')
  })

  it('should handle API errors gracefully', () => {
    // Select a specialty
    cy.get('select[name="specialty"]').select('Cardiology')
    
    // Enter a valid address
    cy.get('input[name="address"]').type('London, UK')
    
    // Mock successful geocoding
    cy.intercept('GET', '/geocoding*', {
      statusCode: 200,
      body: [{
        lat: '51.5074',
        lon: '-0.1278',
        display_name: 'London, UK'
      }]
    }).as('geocodingRequest')

    // Mock API error
    cy.intercept('POST', '/api/allocate', {
      statusCode: 500,
      body: { error: 'Internal Server Error' }
    }).as('allocationError')

    // Submit the form
    cy.get('button[type="submit"]').click()

    // Wait for API calls
    cy.wait('@geocodingRequest')
    cy.wait('@allocationError')

    // Check if error message is displayed
    cy.contains('Erreur lors de l\'allocation').should('be.visible')
  })

  it('should validate required fields', () => {
    // Try to submit without selecting specialty
    cy.get('button[type="submit"]').click()
    
    // Check if validation message appears
    cy.get('select[name="specialty"]:invalid').should('exist')
  })

  it('should show loading state during API calls', () => {
    // Select a specialty
    cy.get('select[name="specialty"]').select('Cardiology')
    
    // Enter an address
    cy.get('input[name="address"]').type('London, UK')
    
    // Mock slow API response
    cy.intercept('GET', '/geocoding*', {
      statusCode: 200,
      body: [{
        lat: '51.5074',
        lon: '-0.1278',
        display_name: 'London, UK'
      }],
      delay: 2000
    }).as('slowGeocoding')

    // Submit the form
    cy.get('button[type="submit"]').click()

    // Check if loading state is shown
    cy.contains('Recherche en cours').should('be.visible')
    
    // Wait for completion
    cy.wait('@slowGeocoding')
  })

  it('should handle network timeout', () => {
    // Select a specialty
    cy.get('select[name="specialty"]').select('Cardiology')
    
    // Enter an address
    cy.get('input[name="address"]').type('London, UK')
    
    // Mock network timeout
    cy.intercept('GET', '/geocoding*', {
      statusCode: 200,
      body: [],
      delay: 10000
    }).as('timeoutRequest')

    // Submit the form
    cy.get('button[type="submit"]').click()

    // Check if timeout error is handled
    cy.wait(5000) // Wait for timeout
    cy.contains('Timeout').should('be.visible')
  })

  it('should be responsive on mobile devices', () => {
    // Set mobile viewport
    cy.viewport('iphone-6')
    
    // Check if elements are still visible and functional
    cy.get('select[name="specialty"]').should('be.visible')
    cy.get('input[name="address"]').should('be.visible')
    cy.get('button[type="submit"]').should('be.visible')
    
    // Test form functionality on mobile
    cy.get('select[name="specialty"]').select('Cardiology')
    cy.get('input[name="address"]').type('London, UK')
    cy.get('button[type="submit"]').should('be.enabled')
  })

  it('should maintain state after page refresh', () => {
    // Fill form partially
    cy.get('select[name="specialty"]').select('Cardiology')
    cy.get('input[name="address"]').type('London, UK')
    
    // Refresh page
    cy.reload()
    
    // Check if form is reset (expected behavior)
    cy.get('select[name="specialty"]').should('have.value', '')
    cy.get('input[name="address"]').should('have.value', '')
  })
})