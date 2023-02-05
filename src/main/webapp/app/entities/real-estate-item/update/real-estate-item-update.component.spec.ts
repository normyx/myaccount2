import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RealEstateItemFormService } from './real-estate-item-form.service';
import { RealEstateItemService } from '../service/real-estate-item.service';
import { IRealEstateItem } from '../real-estate-item.model';
import { IBankAccount } from 'app/entities/bank-account/bank-account.model';
import { BankAccountService } from 'app/entities/bank-account/service/bank-account.service';

import { RealEstateItemUpdateComponent } from './real-estate-item-update.component';

describe('RealEstateItem Management Update Component', () => {
  let comp: RealEstateItemUpdateComponent;
  let fixture: ComponentFixture<RealEstateItemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let realEstateItemFormService: RealEstateItemFormService;
  let realEstateItemService: RealEstateItemService;
  let bankAccountService: BankAccountService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RealEstateItemUpdateComponent],
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
      .overrideTemplate(RealEstateItemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RealEstateItemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    realEstateItemFormService = TestBed.inject(RealEstateItemFormService);
    realEstateItemService = TestBed.inject(RealEstateItemService);
    bankAccountService = TestBed.inject(BankAccountService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call BankAccount query and add missing value', () => {
      const realEstateItem: IRealEstateItem = { id: 456 };
      const bankAccount: IBankAccount = { id: 9129 };
      realEstateItem.bankAccount = bankAccount;

      const bankAccountCollection: IBankAccount[] = [{ id: 59941 }];
      jest.spyOn(bankAccountService, 'query').mockReturnValue(of(new HttpResponse({ body: bankAccountCollection })));
      const additionalBankAccounts = [bankAccount];
      const expectedCollection: IBankAccount[] = [...additionalBankAccounts, ...bankAccountCollection];
      jest.spyOn(bankAccountService, 'addBankAccountToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ realEstateItem });
      comp.ngOnInit();

      expect(bankAccountService.query).toHaveBeenCalled();
      expect(bankAccountService.addBankAccountToCollectionIfMissing).toHaveBeenCalledWith(
        bankAccountCollection,
        ...additionalBankAccounts.map(expect.objectContaining)
      );
      expect(comp.bankAccountsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const realEstateItem: IRealEstateItem = { id: 456 };
      const bankAccount: IBankAccount = { id: 62785 };
      realEstateItem.bankAccount = bankAccount;

      activatedRoute.data = of({ realEstateItem });
      comp.ngOnInit();

      expect(comp.bankAccountsSharedCollection).toContain(bankAccount);
      expect(comp.realEstateItem).toEqual(realEstateItem);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRealEstateItem>>();
      const realEstateItem = { id: 123 };
      jest.spyOn(realEstateItemFormService, 'getRealEstateItem').mockReturnValue(realEstateItem);
      jest.spyOn(realEstateItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ realEstateItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: realEstateItem }));
      saveSubject.complete();

      // THEN
      expect(realEstateItemFormService.getRealEstateItem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(realEstateItemService.update).toHaveBeenCalledWith(expect.objectContaining(realEstateItem));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRealEstateItem>>();
      const realEstateItem = { id: 123 };
      jest.spyOn(realEstateItemFormService, 'getRealEstateItem').mockReturnValue({ id: null });
      jest.spyOn(realEstateItemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ realEstateItem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: realEstateItem }));
      saveSubject.complete();

      // THEN
      expect(realEstateItemFormService.getRealEstateItem).toHaveBeenCalled();
      expect(realEstateItemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRealEstateItem>>();
      const realEstateItem = { id: 123 };
      jest.spyOn(realEstateItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ realEstateItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(realEstateItemService.update).toHaveBeenCalled();
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
