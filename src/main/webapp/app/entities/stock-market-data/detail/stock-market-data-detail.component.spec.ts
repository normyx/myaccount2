import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { StockMarketDataDetailComponent } from './stock-market-data-detail.component';

describe('StockMarketData Management Detail Component', () => {
  let comp: StockMarketDataDetailComponent;
  let fixture: ComponentFixture<StockMarketDataDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StockMarketDataDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ stockMarketData: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(StockMarketDataDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(StockMarketDataDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load stockMarketData on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.stockMarketData).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
