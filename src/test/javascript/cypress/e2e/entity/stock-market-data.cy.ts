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

describe('StockMarketData e2e test', () => {
  const stockMarketDataPageUrl = '/stock-market-data';
  const stockMarketDataPageUrlPattern = new RegExp('/stock-market-data(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const stockMarketDataSample = { symbol: 'value-adde', dataDate: '2022-11-11', closeValue: 65178 };

  let stockMarketData;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/stock-market-data+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/stock-market-data').as('postEntityRequest');
    cy.intercept('DELETE', '/api/stock-market-data/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (stockMarketData) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/stock-market-data/${stockMarketData.id}`,
      }).then(() => {
        stockMarketData = undefined;
      });
    }
  });

  it('StockMarketData menu should load StockMarketData page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('stock-market-data');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('StockMarketData').should('exist');
    cy.url().should('match', stockMarketDataPageUrlPattern);
  });

  describe('StockMarketData page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(stockMarketDataPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create StockMarketData page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/stock-market-data/new$'));
        cy.getEntityCreateUpdateHeading('StockMarketData');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', stockMarketDataPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/stock-market-data',
          body: stockMarketDataSample,
        }).then(({ body }) => {
          stockMarketData = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/stock-market-data+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/stock-market-data?page=0&size=20>; rel="last",<http://localhost/api/stock-market-data?page=0&size=20>; rel="first"',
              },
              body: [stockMarketData],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(stockMarketDataPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details StockMarketData page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('stockMarketData');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', stockMarketDataPageUrlPattern);
      });

      it('edit button click should load edit StockMarketData page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StockMarketData');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', stockMarketDataPageUrlPattern);
      });

      it.skip('edit button click should load edit StockMarketData page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StockMarketData');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', stockMarketDataPageUrlPattern);
      });

      it('last delete button click should delete instance of StockMarketData', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('stockMarketData').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', stockMarketDataPageUrlPattern);

        stockMarketData = undefined;
      });
    });
  });

  describe('new StockMarketData page', () => {
    beforeEach(() => {
      cy.visit(`${stockMarketDataPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('StockMarketData');
    });

    it('should create an instance of StockMarketData', () => {
      cy.get(`[data-cy="symbol"]`).type('Salad Lepi').should('have.value', 'Salad Lepi');

      cy.get(`[data-cy="dataDate"]`).type('2022-11-11').blur().should('have.value', '2022-11-11');

      cy.get(`[data-cy="closeValue"]`).type('70257').should('have.value', '70257');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        stockMarketData = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', stockMarketDataPageUrlPattern);
    });
  });
});
