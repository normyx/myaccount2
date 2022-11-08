import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { StockPortfolioItemDetailComponent } from './stock-portfolio-item-detail.component';

describe('StockPortfolioItem Management Detail Component', () => {
  let comp: StockPortfolioItemDetailComponent;
  let fixture: ComponentFixture<StockPortfolioItemDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StockPortfolioItemDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ stockPortfolioItem: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(StockPortfolioItemDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(StockPortfolioItemDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load stockPortfolioItem on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.stockPortfolioItem).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
