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

describe('BudgetItem e2e test', () => {
  const budgetItemPageUrl = '/budget-item';
  const budgetItemPageUrlPattern = new RegExp('/budget-item(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const budgetItemSample = { name: 'Granite', order: 32114 };

  let budgetItem;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/budget-items+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/budget-items').as('postEntityRequest');
    cy.intercept('DELETE', '/api/budget-items/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (budgetItem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/budget-items/${budgetItem.id}`,
      }).then(() => {
        budgetItem = undefined;
      });
    }
  });

  it('BudgetItems menu should load BudgetItems page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('budget-item');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('BudgetItem').should('exist');
    cy.url().should('match', budgetItemPageUrlPattern);
  });

  describe('BudgetItem page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(budgetItemPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create BudgetItem page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/budget-item/new$'));
        cy.getEntityCreateUpdateHeading('BudgetItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', budgetItemPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/budget-items',
          body: budgetItemSample,
        }).then(({ body }) => {
          budgetItem = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/budget-items+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [budgetItem],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(budgetItemPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details BudgetItem page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('budgetItem');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', budgetItemPageUrlPattern);
      });

      it('edit button click should load edit BudgetItem page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BudgetItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', budgetItemPageUrlPattern);
      });

      it.skip('edit button click should load edit BudgetItem page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BudgetItem');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', budgetItemPageUrlPattern);
      });

      it('last delete button click should delete instance of BudgetItem', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('budgetItem').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', budgetItemPageUrlPattern);

        budgetItem = undefined;
      });
    });
  });

  describe('new BudgetItem page', () => {
    beforeEach(() => {
      cy.visit(`${budgetItemPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('BudgetItem');
    });

    it('should create an instance of BudgetItem', () => {
      cy.get(`[data-cy="name"]`).type('Intelligent Berkshire Cotton').should('have.value', 'Intelligent Berkshire Cotton');

      cy.get(`[data-cy="order"]`).type('24786').should('have.value', '24786');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        budgetItem = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', budgetItemPageUrlPattern);
    });
  });
});
