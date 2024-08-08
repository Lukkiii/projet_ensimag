describe('My Test4', () => {
  it('TP6_Frontend_groupAdmin', () => {
    cy.visit('http://localhost:5173/')

    cy.get('input[name="loginEmail"]').type('thory@gmail.com')
    cy.get('input[name="loginPassword"]').type('123456')
    cy.get('button.buttonLogin').click()
    
    cy.get('.listGroupAsAdmin').contains('Groupe 1').click()
    cy.get('.admin').should('be.visible')

    //check if add user is successful
    cy.get('select[name="addUserSelect"]').select('1')
    cy.get('.buttonAdd').click()
    cy.get('.listEmail').contains('Sebastien.Viardot@grenoble-inp.fr').should('be.visible')

    //check if remove user is successful
    cy.get('.buttonDelete').first().click()
    cy.get('.listEmail').first().contains('Sebastien.Viardot@grenoble-inp.fr').should('not.exist')

  })
})