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

describe('StockPortfolioItem e2e test', () => {
  const stockPortfolioItemPageUrl = '/stock-portfolio-item';
  const stockPortfolioItemPageUrlPattern = new RegExp('/stock-portfolio-item(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const stockPortfolioItemSample = {
    stockSymbol: 'TCP',
    stockCurrency: 'GBP',
    stockAcquisitionDate: '2022-11-06',
    stockSharesNumber: 97897,
    stockAcquisitionPrice: 32029,
    stockCurrentPrice: 14577,
    stockCurrentDate: '2022-11-06',
    stockAcquisitionCurrencyFactor: 86462,
    stockCurrentCurrencyFactor: 48249,
    stockPriceAtAcquisitionDate: 33006,
  };

  let stockPortfolioItem;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/stock-portfolio-items+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/stock-portfolio-items').as('postEntityRequest');
    cy.intercept('DELETE', '/api/stock-portfolio-items/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (stockPortfolioItem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/stock-portfolio-items/${stockPortfolioItem.id}`,
      }).then(() => {
        stockPortfolioItem = undefined;
      });
    }
  });

  it('StockPortfolioItems menu should load StockPortfolioItems page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('stock-portfolio-item');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('StockPortfolioItem').should('exist');
    cy.url().should('match', stockPortfolioItemPageUrlPattern);
  });

  describe('StockPortfolioItem page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(stockPortfolioItemPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create StockPortfolioItem page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/stock-portfolio-item/new$'));
        cy.getEntityCreateUpdateHeading('StockPortfolioItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', stockPortfolioItemPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/stock-portfolio-items',
          body: stockPortfolioItemSample,
        }).then(({ body }) => {
          stockPortfolioItem = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/stock-portfolio-items+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/stock-portfolio-items?page=0&size=20>; rel="last",<http://localhost/api/stock-portfolio-items?page=0&size=20>; rel="first"',
              },
              body: [stockPortfolioItem],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(stockPortfolioItemPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details StockPortfolioItem page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('stockPortfolioItem');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', stockPortfolioItemPageUrlPattern);
      });

      it('edit button click should load edit StockPortfolioItem page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StockPortfolioItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', stockPortfolioItemPageUrlPattern);
      });

      it.skip('edit button click should load edit StockPortfolioItem page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StockPortfolioItem');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', stockPortfolioItemPageUrlPattern);
      });

      it('last delete button click should delete instance of StockPortfolioItem', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('stockPortfolioItem').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', stockPortfolioItemPageUrlPattern);

        stockPortfolioItem = undefined;
      });
    });
  });

  describe('new StockPortfolioItem page', () => {
    beforeEach(() => {
      cy.visit(`${stockPortfolioItemPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('StockPortfolioItem');
    });

    it('should create an instance of StockPortfolioItem', () => {
      cy.get(`[data-cy="stockSymbol"]`).type('info-media').should('have.value', 'info-media');

      cy.get(`[data-cy="stockCurrency"]`).select('EUR');

      cy.get(`[data-cy="stockAcquisitionDate"]`).type('2022-11-06').blur().should('have.value', '2022-11-06');

      cy.get(`[data-cy="stockSharesNumber"]`).type('71677').should('have.value', '71677');

      cy.get(`[data-cy="stockAcquisitionPrice"]`).type('43414').should('have.value', '43414');

      cy.get(`[data-cy="stockCurrentPrice"]`).type('40333').should('have.value', '40333');

      cy.get(`[data-cy="stockCurrentDate"]`).type('2022-11-06').blur().should('have.value', '2022-11-06');

      cy.get(`[data-cy="stockAcquisitionCurrencyFactor"]`).type('90326').should('have.value', '90326');

      cy.get(`[data-cy="stockCurrentCurrencyFactor"]`).type('12974').should('have.value', '12974');

      cy.get(`[data-cy="stockPriceAtAcquisitionDate"]`).type('96686').should('have.value', '96686');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        stockPortfolioItem = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', stockPortfolioItemPageUrlPattern);
    });
  });
});
