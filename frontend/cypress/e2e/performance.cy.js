describe('Performance Tests', () => {
  beforeEach(() => {
    cy.visit('/')
  })

  it('should load the page within acceptable time', () => {
    // Measure page load performance
    cy.window().then((win) => {
      const navigation = win.performance.getEntriesByType('navigation')[0]
      const loadTime = navigation.loadEventEnd - navigation.loadEventStart
      
      // Page should load within 3 seconds
      expect(loadTime).to.be.lessThan(3000)
    })
  })

  it('should handle multiple rapid requests', () => {
    // Mock API responses
    cy.intercept('GET', '/geocoding*', {
      statusCode: 200,
      body: [{
        lat: '51.5074',
        lon: '-0.1278',
        display_name: 'London, UK'
      }]
    }).as('geocoding')

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
    }).as('allocation')

    // Make multiple rapid requests
    const requests = []
    for (let i = 0; i < 5; i++) {
      cy.get('select[name="specialty"]').select('Cardiology')
      cy.get('input[name="address"]').clear().type(`London ${i}, UK`)
      cy.get('button[type="submit"]').click()
      
      // Wait for requests to complete
      cy.wait('@geocoding')
      cy.wait('@allocation')
    }

    // Verify all requests completed successfully
    cy.get('[data-testid="result"]').should('be.visible')
  })

  it('should not cause memory leaks with repeated requests', () => {
    // Mock API responses
    cy.intercept('GET', '/geocoding*', {
      statusCode: 200,
      body: [{
        lat: '51.5074',
        lon: '-0.1278',
        display_name: 'London, UK'
      }]
    }).as('geocoding')

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
    }).as('allocation')

    // Get initial memory usage
    cy.window().then((win) => {
      const initialMemory = win.performance.memory ? win.performance.memory.usedJSHeapSize : 0

      // Make many requests
      for (let i = 0; i < 20; i++) {
        cy.get('select[name="specialty"]').select('Cardiology')
        cy.get('input[name="address"]').clear().type(`London ${i}, UK`)
        cy.get('button[type="submit"]').click()
        
        cy.wait('@geocoding')
        cy.wait('@allocation')
      }

      // Check memory usage after requests
      cy.window().then((win) => {
        const finalMemory = win.performance.memory ? win.performance.memory.usedJSHeapSize : 0
        const memoryIncrease = finalMemory - initialMemory
        
        // Memory increase should be reasonable (less than 50MB)
        expect(memoryIncrease).to.be.lessThan(50 * 1024 * 1024)
      })
    })
  })

  it('should handle concurrent user interactions', () => {
    // Mock API responses with delays to simulate real conditions
    cy.intercept('GET', '/geocoding*', {
      statusCode: 200,
      body: [{
        lat: '51.5074',
        lon: '-0.1278',
        display_name: 'London, UK'
      }],
      delay: 500
    }).as('geocoding')

    cy.intercept('POST', '/api/allocate', {
      statusCode: 200,
      body: {
        hospitalName: 'St George\'s Hospital',
        hospitalId: 1,
        distanceKm: 9.5,
        specialty: 'Cardiology',
        availableBeds: 25,
        estimatedTimeMinutes: 114
      },
      delay: 300
    }).as('allocation')

    // Simulate rapid user interactions
    cy.get('select[name="specialty"]').select('Cardiology')
    cy.get('input[name="address"]').type('London, UK')
    
    // Click submit multiple times rapidly (should be handled gracefully)
    cy.get('button[type="submit"]').click()
    cy.get('button[type="submit"]').click()
    cy.get('button[type="submit"]').click()

    // Should still work correctly
    cy.wait('@geocoding')
    cy.wait('@allocation')
    cy.get('[data-testid="result"]').should('be.visible')
  })

  it('should maintain responsive UI during API calls', () => {
    // Mock slow API response
    cy.intercept('GET', '/geocoding*', {
      statusCode: 200,
      body: [{
        lat: '51.5074',
        lon: '-0.1278',
        display_name: 'London, UK'
      }],
      delay: 3000
    }).as('slowGeocoding')

    cy.intercept('POST', '/api/allocate', {
      statusCode: 200,
      body: {
        hospitalName: 'St George\'s Hospital',
        hospitalId: 1,
        distanceKm: 9.5,
        specialty: 'Cardiology',
        availableBeds: 25,
        estimatedTimeMinutes: 114
      },
      delay: 2000
    }).as('slowAllocation')

    // Submit form
    cy.get('select[name="specialty"]').select('Cardiology')
    cy.get('input[name="address"]').type('London, UK')
    cy.get('button[type="submit"]').click()

    // UI should remain responsive (loading state visible)
    cy.contains('Recherche en cours').should('be.visible')
    
    // Other form elements should be disabled during processing
    cy.get('select[name="specialty"]').should('be.disabled')
    cy.get('input[name="address"]').should('be.disabled')
    cy.get('button[type="submit"]').should('be.disabled')

    // Wait for completion
    cy.wait('@slowGeocoding')
    cy.wait('@slowAllocation')

    // Form should be re-enabled after completion
    cy.get('select[name="specialty"]').should('not.be.disabled')
    cy.get('input[name="address"]').should('not.be.disabled')
    cy.get('button[type="submit"]').should('not.be.disabled')
  })

  it('should handle large datasets efficiently', () => {
    // Mock API response with large dataset
    const largeHospitalList = Array.from({ length: 100 }, (_, i) => ({
      hospitalName: `Hospital ${i}`,
      hospitalId: i,
      distanceKm: Math.random() * 50,
      specialty: 'Cardiology',
      availableBeds: Math.floor(Math.random() * 50),
      estimatedTimeMinutes: Math.floor(Math.random() * 300)
    }))

    cy.intercept('GET', '/geocoding*', {
      statusCode: 200,
      body: [{
        lat: '51.5074',
        lon: '-0.1278',
        display_name: 'London, UK'
      }]
    }).as('geocoding')

    cy.intercept('POST', '/api/allocate', {
      statusCode: 200,
      body: largeHospitalList[0]
    }).as('allocation')

    // Measure time for handling large response
    const startTime = Date.now()

    cy.get('select[name="specialty"]').select('Cardiology')
    cy.get('input[name="address"]').type('London, UK')
    cy.get('button[type="submit"]').click()

    cy.wait('@geocoding')
    cy.wait('@allocation')

    cy.then(() => {
      const endTime = Date.now()
      const processingTime = endTime - startTime
      
      // Should handle large dataset within reasonable time
      expect(processingTime).to.be.lessThan(5000)
    })
  })
})
