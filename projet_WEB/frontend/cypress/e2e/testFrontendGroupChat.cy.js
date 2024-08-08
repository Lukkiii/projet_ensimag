describe('My Test5', () => {
  it('TP6_Frontend_groupAdmin', () => {
    cy.visit('http://localhost:5173/')

    cy.get('input[name="loginEmail"]').type('thory@gmail.com')
    cy.get('input[name="loginPassword"]').type('123456')
    cy.get('button.buttonLogin').click()
    
    cy.get('.listGroupAsMember').first().click()
    cy.get('.groupMessages').should('be.visible')

    cy.get('input[id="message"]').type('Hello group!')
    cy.get('.buttonEnvoyer').click()
    cy.get('.messageContent').last().should('have.text', 'Hello group!')
    cy.get('.messageAuthor').last().should('have.text', 'thory')
  })
})