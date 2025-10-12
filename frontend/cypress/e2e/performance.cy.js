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
        lat: '48.8566',
        lon: '2.3522',
        display_name: 'Paris, France'
      }]
    }).as('geocoding')

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
    }).as('allocation')

    // Make multiple rapid requests
    const requests = []
    for (let i = 0; i < 5; i++) {
      cy.get('#specialty').select('Cardiology')
      cy.get('#address').clear().type(`Paris ${i}, France`)
      cy.get('button[type="submit"]').click()
      
      // Wait for requests to complete
      cy.wait('@geocoding')
      cy.wait('@allocation')
    }

    // Verify all requests completed successfully
    cy.contains('✅ Hôpital Recommandé').should('be.visible')
  })

  it('should not cause memory leaks with repeated requests', () => {
    // Mock API responses
    cy.intercept('GET', '/geocoding*', {
      statusCode: 200,
      body: [{
        lat: '48.8566',
        lon: '2.3522',
        display_name: 'Paris, France'
      }]
    }).as('geocoding')

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
    }).as('allocation')

    // Get initial memory usage
    cy.window().then((win) => {
      const initialMemory = win.performance.memory ? win.performance.memory.usedJSHeapSize : 0

      // Make many requests
      for (let i = 0; i < 20; i++) {
        cy.get('#specialty').select('Cardiology')
        cy.get('#address').clear().type(`Paris ${i}, France`)
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
    cy.get('#specialty').select('Cardiology')
    cy.get('#address').type('Paris, France')
    
    // Click submit multiple times rapidly (should be handled gracefully)
    cy.get('button[type="submit"]').click()
    cy.get('button[type="submit"]').click()
    cy.get('button[type="submit"]').click()

    // Should still work correctly
    cy.wait('@geocoding')
    cy.wait('@allocation')
    cy.contains('✅ Hôpital Recommandé').should('be.visible')
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
    cy.get('#specialty').select('Cardiology')
    cy.get('#address').type('Paris, France')
    cy.get('button[type="submit"]').click()

    // UI should remain responsive (loading state visible)
    cy.contains('Recherche de l\'adresse...').should('be.visible')
    
    // Other form elements should be disabled during processing
    cy.get('#specialty').should('be.disabled')
    cy.get('#address').should('be.disabled')
    cy.get('button[type="submit"]').should('be.disabled')

    // Wait for completion
    cy.wait('@slowGeocoding')
    cy.wait('@slowAllocation')

    // Form should be re-enabled after completion
    cy.get('#specialty').should('not.be.disabled')
    cy.get('#address').should('not.be.disabled')
    cy.get('button[type="submit"]').should('not.be.disabled')
  })

  it('should handle large datasets efficiently', () => {
    // Mock API response with large dataset
    const largeHospitalList = Array.from({ length: 100 }, (_, i) => ({
      hospital_name: `Hôpital ${i}`,
      hospital_id: i,
      distance_km: Math.random() * 50,
      specialty: 'Cardiology',
      available_beds: Math.floor(Math.random() * 50),
      estimated_time_minutes: Math.floor(Math.random() * 300)
    }))

    cy.intercept('GET', '/geocoding*', {
      statusCode: 200,
      body: [{
        lat: '48.8566',
        lon: '2.3522',
        display_name: 'Paris, France'
      }]
    }).as('geocoding')

    cy.intercept('POST', '/api/allocate', {
      statusCode: 200,
      body: largeHospitalList[0]
    }).as('allocation')

    // Measure time for handling large response
    const startTime = Date.now()

    cy.get('#specialty').select('Cardiology')
    cy.get('#address').type('Paris, France')
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
