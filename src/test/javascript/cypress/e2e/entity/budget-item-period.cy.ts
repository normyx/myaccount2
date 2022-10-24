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

describe('BudgetItemPeriod e2e test', () => {
  const budgetItemPeriodPageUrl = '/budget-item-period';
  const budgetItemPeriodPageUrlPattern = new RegExp('/budget-item-period(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const budgetItemPeriodSample = { month: '2022-08-17', amount: 32351 };

  let budgetItemPeriod;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/budget-item-periods+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/budget-item-periods').as('postEntityRequest');
    cy.intercept('DELETE', '/api/budget-item-periods/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (budgetItemPeriod) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/budget-item-periods/${budgetItemPeriod.id}`,
      }).then(() => {
        budgetItemPeriod = undefined;
      });
    }
  });

  it('BudgetItemPeriods menu should load BudgetItemPeriods page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('budget-item-period');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('BudgetItemPeriod').should('exist');
    cy.url().should('match', budgetItemPeriodPageUrlPattern);
  });

  describe('BudgetItemPeriod page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(budgetItemPeriodPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create BudgetItemPeriod page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/budget-item-period/new$'));
        cy.getEntityCreateUpdateHeading('BudgetItemPeriod');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', budgetItemPeriodPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/budget-item-periods',
          body: budgetItemPeriodSample,
        }).then(({ body }) => {
          budgetItemPeriod = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/budget-item-periods+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [budgetItemPeriod],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(budgetItemPeriodPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details BudgetItemPeriod page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('budgetItemPeriod');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', budgetItemPeriodPageUrlPattern);
      });

      it('edit button click should load edit BudgetItemPeriod page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BudgetItemPeriod');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', budgetItemPeriodPageUrlPattern);
      });

      it.skip('edit button click should load edit BudgetItemPeriod page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BudgetItemPeriod');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', budgetItemPeriodPageUrlPattern);
      });

      it('last delete button click should delete instance of BudgetItemPeriod', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('budgetItemPeriod').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', budgetItemPeriodPageUrlPattern);

        budgetItemPeriod = undefined;
      });
    });
  });

  describe('new BudgetItemPeriod page', () => {
    beforeEach(() => {
      cy.visit(`${budgetItemPeriodPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('BudgetItemPeriod');
    });

    it('should create an instance of BudgetItemPeriod', () => {
      cy.get(`[data-cy="date"]`).type('2022-08-16').blur().should('have.value', '2022-08-16');

      cy.get(`[data-cy="month"]`).type('2022-08-17').blur().should('have.value', '2022-08-17');

      cy.get(`[data-cy="amount"]`).type('33331').should('have.value', '33331');

      cy.get(`[data-cy="isSmoothed"]`).should('not.be.checked');
      cy.get(`[data-cy="isSmoothed"]`).click().should('be.checked');

      cy.get(`[data-cy="isRecurrent"]`).should('not.be.checked');
      cy.get(`[data-cy="isRecurrent"]`).click().should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        budgetItemPeriod = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', budgetItemPeriodPageUrlPattern);
    });
  });
});
