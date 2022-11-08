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
import { IBankAccount } from 'app/entities/bank-account/bank-account.model';
import { BankAccountService } from 'app/entities/bank-account/service/bank-account.service';

import { StockPortfolioItemUpdateComponent } from './stock-portfolio-item-update.component';

describe('StockPortfolioItem Management Update Component', () => {
  let comp: StockPortfolioItemUpdateComponent;
  let fixture: ComponentFixture<StockPortfolioItemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let stockPortfolioItemFormService: StockPortfolioItemFormService;
  let stockPortfolioItemService: StockPortfolioItemService;
  let bankAccountService: BankAccountService;

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
    bankAccountService = TestBed.inject(BankAccountService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call BankAccount query and add missing value', () => {
      const stockPortfolioItem: IStockPortfolioItem = { id: 456 };
      const bankAccount: IBankAccount = { id: 30479 };
      stockPortfolioItem.bankAccount = bankAccount;

      const bankAccountCollection: IBankAccount[] = [{ id: 99697 }];
      jest.spyOn(bankAccountService, 'query').mockReturnValue(of(new HttpResponse({ body: bankAccountCollection })));
      const additionalBankAccounts = [bankAccount];
      const expectedCollection: IBankAccount[] = [...additionalBankAccounts, ...bankAccountCollection];
      jest.spyOn(bankAccountService, 'addBankAccountToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ stockPortfolioItem });
      comp.ngOnInit();

      expect(bankAccountService.query).toHaveBeenCalled();
      expect(bankAccountService.addBankAccountToCollectionIfMissing).toHaveBeenCalledWith(
        bankAccountCollection,
        ...additionalBankAccounts.map(expect.objectContaining)
      );
      expect(comp.bankAccountsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const stockPortfolioItem: IStockPortfolioItem = { id: 456 };
      const bankAccount: IBankAccount = { id: 86574 };
      stockPortfolioItem.bankAccount = bankAccount;

      activatedRoute.data = of({ stockPortfolioItem });
      comp.ngOnInit();

      expect(comp.bankAccountsSharedCollection).toContain(bankAccount);
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

  describe('Compare relationships', () => {
    describe('compareBankAccount', () => {
      it('Should forward to bankAccountService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(bankAccountService, 'compareBankAccount');
        comp.compareBankAccount(entity, entity2);
        expect(bankAccountService.compareBankAccount).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
