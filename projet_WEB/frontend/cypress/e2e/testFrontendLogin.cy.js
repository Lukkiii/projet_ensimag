describe('My Test1', () => {
  it('TP6_Frontend_login', () => {
    cy.visit('http://localhost:5173/')

    cy.get('input[name="loginEmail"]').type('thory@gmail.com')
    cy.get('input[name="loginPassword"]').type('123456')
    cy.get('button.buttonLogin').click()

    cy.get('.accueil').should('be.visible')
  })
})