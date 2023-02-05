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

describe('RealEstateItem e2e test', () => {
  const realEstateItemPageUrl = '/real-estate-item';
  const realEstateItemPageUrlPattern = new RegExp('/real-estate-item(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const realEstateItemSample = {"loanValue":43157,"totalValue":13168,"percentOwned":69,"itemDate":"2023-01-02"};

  let realEstateItem;
  // let bankAccount;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/bank-accounts',
      body: {"accountName":"Savings Account","accountBank":"Coordinateur","initialAmount":52891,"archived":true,"shortName":"Midi-Pyrénées mindshare optical","accountType":"SAVINGSACCOUNT"},
    }).then(({ body }) => {
      bankAccount = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/real-estate-items+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/real-estate-items').as('postEntityRequest');
    cy.intercept('DELETE', '/api/real-estate-items/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/bank-accounts', {
      statusCode: 200,
      body: [bankAccount],
    });

  });
   */

  afterEach(() => {
    if (realEstateItem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/real-estate-items/${realEstateItem.id}`,
      }).then(() => {
        realEstateItem = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
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
   */

  it('RealEstateItems menu should load RealEstateItems page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('real-estate-item');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('RealEstateItem').should('exist');
    cy.url().should('match', realEstateItemPageUrlPattern);
  });

  describe('RealEstateItem page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(realEstateItemPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create RealEstateItem page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/real-estate-item/new$'));
        cy.getEntityCreateUpdateHeading('RealEstateItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', realEstateItemPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/real-estate-items',
          body: {
            ...realEstateItemSample,
            bankAccount: bankAccount,
          },
        }).then(({ body }) => {
          realEstateItem = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/real-estate-items+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/real-estate-items?page=0&size=20>; rel="last",<http://localhost/api/real-estate-items?page=0&size=20>; rel="first"',
              },
              body: [realEstateItem],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(realEstateItemPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(realEstateItemPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details RealEstateItem page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('realEstateItem');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', realEstateItemPageUrlPattern);
      });

      it('edit button click should load edit RealEstateItem page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RealEstateItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', realEstateItemPageUrlPattern);
      });

      it.skip('edit button click should load edit RealEstateItem page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RealEstateItem');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', realEstateItemPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of RealEstateItem', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('realEstateItem').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', realEstateItemPageUrlPattern);

        realEstateItem = undefined;
      });
    });
  });

  describe('new RealEstateItem page', () => {
    beforeEach(() => {
      cy.visit(`${realEstateItemPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('RealEstateItem');
    });

    it.skip('should create an instance of RealEstateItem', () => {
      cy.get(`[data-cy="loanValue"]`).type('55821').should('have.value', '55821');

      cy.get(`[data-cy="totalValue"]`).type('88330').should('have.value', '88330');

      cy.get(`[data-cy="percentOwned"]`).type('20').should('have.value', '20');

      cy.get(`[data-cy="itemDate"]`).type('2023-01-03').blur().should('have.value', '2023-01-03');

      cy.get(`[data-cy="bankAccount"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        realEstateItem = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', realEstateItemPageUrlPattern);
    });
  });
});
