import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BankAccountFormService } from './bank-account-form.service';
import { BankAccountService } from '../service/bank-account.service';
import { IBankAccount } from '../bank-account.model';
import { IApplicationUser } from 'app/entities/application-user/application-user.model';
import { ApplicationUserService } from 'app/entities/application-user/service/application-user.service';

import { BankAccountUpdateComponent } from './bank-account-update.component';

describe('BankAccount Management Update Component', () => {
  let comp: BankAccountUpdateComponent;
  let fixture: ComponentFixture<BankAccountUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let bankAccountFormService: BankAccountFormService;
  let bankAccountService: BankAccountService;
  let applicationUserService: ApplicationUserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BankAccountUpdateComponent],
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
      .overrideTemplate(BankAccountUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BankAccountUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    bankAccountFormService = TestBed.inject(BankAccountFormService);
    bankAccountService = TestBed.inject(BankAccountService);
    applicationUserService = TestBed.inject(ApplicationUserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ApplicationUser query and add missing value', () => {
      const bankAccount: IBankAccount = { id: 456 };
      const account: IApplicationUser = { id: 90927 };
      bankAccount.account = account;

      const applicationUserCollection: IApplicationUser[] = [{ id: 63244 }];
      jest.spyOn(applicationUserService, 'query').mockReturnValue(of(new HttpResponse({ body: applicationUserCollection })));
      const additionalApplicationUsers = [account];
      const expectedCollection: IApplicationUser[] = [...additionalApplicationUsers, ...applicationUserCollection];
      jest.spyOn(applicationUserService, 'addApplicationUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ bankAccount });
      comp.ngOnInit();

      expect(applicationUserService.query).toHaveBeenCalled();
      expect(applicationUserService.addApplicationUserToCollectionIfMissing).toHaveBeenCalledWith(
        applicationUserCollection,
        ...additionalApplicationUsers.map(expect.objectContaining)
      );
      expect(comp.applicationUsersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const bankAccount: IBankAccount = { id: 456 };
      const account: IApplicationUser = { id: 99405 };
      bankAccount.account = account;

      activatedRoute.data = of({ bankAccount });
      comp.ngOnInit();

      expect(comp.applicationUsersSharedCollection).toContain(account);
      expect(comp.bankAccount).toEqual(bankAccount);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBankAccount>>();
      const bankAccount = { id: 123 };
      jest.spyOn(bankAccountFormService, 'getBankAccount').mockReturnValue(bankAccount);
      jest.spyOn(bankAccountService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bankAccount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bankAccount }));
      saveSubject.complete();

      // THEN
      expect(bankAccountFormService.getBankAccount).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(bankAccountService.update).toHaveBeenCalledWith(expect.objectContaining(bankAccount));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBankAccount>>();
      const bankAccount = { id: 123 };
      jest.spyOn(bankAccountFormService, 'getBankAccount').mockReturnValue({ id: null });
      jest.spyOn(bankAccountService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bankAccount: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bankAccount }));
      saveSubject.complete();

      // THEN
      expect(bankAccountFormService.getBankAccount).toHaveBeenCalled();
      expect(bankAccountService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBankAccount>>();
      const bankAccount = { id: 123 };
      jest.spyOn(bankAccountService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bankAccount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(bankAccountService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareApplicationUser', () => {
      it('Should forward to applicationUserService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(applicationUserService, 'compareApplicationUser');
        comp.compareApplicationUser(entity, entity2);
        expect(applicationUserService.compareApplicationUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
