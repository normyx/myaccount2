import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { StockPortfolioItemFormService } from './stock-portfolio-item-form.service';
import { StockPortfolioItemService } from '../service/stock-portfolio-item.service';
import { IStockPortfolioItem } from '../stock-portfolio-item.model';

import { StockPortfolioItemUpdateComponent } from './stock-portfolio-item-update.component';

describe('StockPortfolioItem Management Update Component', () => {
  let comp: StockPortfolioItemUpdateComponent;
  let fixture: ComponentFixture<StockPortfolioItemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let stockPortfolioItemFormService: StockPortfolioItemFormService;
  let stockPortfolioItemService: StockPortfolioItemService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [StockPortfolioItemUpdateComponent],
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
      .overrideTemplate(StockPortfolioItemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StockPortfolioItemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    stockPortfolioItemFormService = TestBed.inject(StockPortfolioItemFormService);
    stockPortfolioItemService = TestBed.inject(StockPortfolioItemService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const stockPortfolioItem: IStockPortfolioItem = { id: 456 };

      activatedRoute.data = of({ stockPortfolioItem });
      comp.ngOnInit();

      expect(comp.stockPortfolioItem).toEqual(stockPortfolioItem);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStockPortfolioItem>>();
      const stockPortfolioItem = { id: 123 };
      jest.spyOn(stockPortfolioItemFormService, 'getStockPortfolioItem').mockReturnValue(stockPortfolioItem);
      jest.spyOn(stockPortfolioItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockPortfolioItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: stockPortfolioItem }));
      saveSubject.complete();

      // THEN
      expect(stockPortfolioItemFormService.getStockPortfolioItem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(stockPortfolioItemService.update).toHaveBeenCalledWith(expect.objectContaining(stockPortfolioItem));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStockPortfolioItem>>();
      const stockPortfolioItem = { id: 123 };
      jest.spyOn(stockPortfolioItemFormService, 'getStockPortfolioItem').mockReturnValue({ id: null });
      jest.spyOn(stockPortfolioItemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockPortfolioItem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: stockPortfolioItem }));
      saveSubject.complete();

      // THEN
      expect(stockPortfolioItemFormService.getStockPortfolioItem).toHaveBeenCalled();
      expect(stockPortfolioItemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStockPortfolioItem>>();
      const stockPortfolioItem = { id: 123 };
      jest.spyOn(stockPortfolioItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockPortfolioItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(stockPortfolioItemService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
