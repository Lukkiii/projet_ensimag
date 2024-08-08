describe('My Test3', () => {
  it('TP6_Frontend_mygroups', () => {
    cy.visit('http://localhost:5173/')

    cy.get('input[name="loginEmail"]').type('thory@gmail.com')
    cy.get('input[name="loginPassword"]').type('123456')
    cy.get('button.buttonLogin').click()
    
    cy.get('.listGroupAsMember').should('be.visible')
    cy.get('.listGroupAsAdmin').should('be.visible')

    cy.get('input[name="newGroupName"]').type('Ensimag')
    cy.get('.buttonCreateGroup').click()

    cy.get('.listGroupAsAdmin').contains('Ensimag').should('be.visible')
  })
})