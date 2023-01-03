import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('BankAccount e2e test', () => {
  const bankAccountPageUrl = '/bank-account';
  const bankAccountPageUrlPattern = new RegExp('/bank-account(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const bankAccountSample = {"accountName":"Savings Account","accountBank":"Computers","initialAmount":66347,"archived":true,"accountType":"SAVINGSACCOUNT"};

  let bankAccount;
  // let applicationUser;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/application-users',
      body: {"nickName":"Bacon Toys action-items"},
    }).then(({ body }) => {
      applicationUser = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/bank-accounts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/bank-accounts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/bank-accounts/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/application-users', {
      statusCode: 200,
      body: [applicationUser],
    });

    cy.intercept('GET', '/api/stock-portfolio-items', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (bankAccount) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/bank-accounts/${bankAccount.id}`,
      }).then(() => {
        bankAccount = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (applicationUser) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/application-users/${applicationUser.id}`,
      }).then(() => {
        applicationUser = undefined;
      });
    }
  });
   */

  it('BankAccounts menu should load BankAccounts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('bank-account');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('BankAccount').should('exist');
    cy.url().should('match', bankAccountPageUrlPattern);
  });

  describe('BankAccount page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(bankAccountPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create BankAccount page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/bank-account/new$'));
        cy.getEntityCreateUpdateHeading('BankAccount');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', bankAccountPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/bank-accounts',
          body: {
            ...bankAccountSample,
            account: applicationUser,
          },
        }).then(({ body }) => {
          bankAccount = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/bank-accounts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [bankAccount],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(bankAccountPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(bankAccountPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details BankAccount page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('bankAccount');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', bankAccountPageUrlPattern);
      });

      it('edit button click should load edit BankAccount page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BankAccount');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', bankAccountPageUrlPattern);
      });

      it.skip('edit button click should load edit BankAccount page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BankAccount');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', bankAccountPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of BankAccount', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('bankAccount').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', bankAccountPageUrlPattern);

        bankAccount = undefined;
      });
    });
  });

  describe('new BankAccount page', () => {
    beforeEach(() => {
      cy.visit(`${bankAccountPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('BankAccount');
    });

    it.skip('should create an instance of BankAccount', () => {
      cy.get(`[data-cy="accountName"]`).type('Money Market Account').should('have.value', 'Money Market Account');

      cy.get(`[data-cy="accountBank"]`).type('b Joubert').should('have.value', 'b Joubert');

      cy.get(`[data-cy="initialAmount"]`).type('50453').should('have.value', '50453');

      cy.get(`[data-cy="archived"]`).should('not.be.checked');
      cy.get(`[data-cy="archived"]`).click().should('be.checked');

      cy.get(`[data-cy="shortName"]`).type('dedicated deposit Handcrafted').should('have.value', 'dedicated deposit Handcrafted');

      cy.get(`[data-cy="accountType"]`).select('STOCKPORTFOLIO');

      cy.get(`[data-cy="account"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        bankAccount = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', bankAccountPageUrlPattern);
    });
  });
});
