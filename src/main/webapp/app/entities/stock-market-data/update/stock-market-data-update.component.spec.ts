import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { StockMarketDataFormService } from './stock-market-data-form.service';
import { StockMarketDataService } from '../service/stock-market-data.service';
import { IStockMarketData } from '../stock-market-data.model';

import { StockMarketDataUpdateComponent } from './stock-market-data-update.component';

describe('StockMarketData Management Update Component', () => {
  let comp: StockMarketDataUpdateComponent;
  let fixture: ComponentFixture<StockMarketDataUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let stockMarketDataFormService: StockMarketDataFormService;
  let stockMarketDataService: StockMarketDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [StockMarketDataUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(StockMarketDataUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StockMarketDataUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    stockMarketDataFormService = TestBed.inject(StockMarketDataFormService);
    stockMarketDataService = TestBed.inject(StockMarketDataService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const stockMarketData: IStockMarketData = { id: 456 };

      activatedRoute.data = of({ stockMarketData });
      comp.ngOnInit();

      expect(comp.stockMarketData).toEqual(stockMarketData);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStockMarketData>>();
      const stockMarketData = { id: 123 };
      jest.spyOn(stockMarketDataFormService, 'getStockMarketData').mockReturnValue(stockMarketData);
      jest.spyOn(stockMarketDataService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockMarketData });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: stockMarketData }));
      saveSubject.complete();

      // THEN
      expect(stockMarketDataFormService.getStockMarketData).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(stockMarketDataService.update).toHaveBeenCalledWith(expect.objectContaining(stockMarketData));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStockMarketData>>();
      const stockMarketData = { id: 123 };
      jest.spyOn(stockMarketDataFormService, 'getStockMarketData').mockReturnValue({ id: null });
      jest.spyOn(stockMarketDataService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockMarketData: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: stockMarketData }));
      saveSubject.complete();

      // THEN
      expect(stockMarketDataFormService.getStockMarketData).toHaveBeenCalled();
      expect(stockMarketDataService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStockMarketData>>();
      const stockMarketData = { id: 123 };
      jest.spyOn(stockMarketDataService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockMarketData });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(stockMarketDataService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
