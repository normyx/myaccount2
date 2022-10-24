import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BudgetItemPeriodFormService } from './budget-item-period-form.service';
import { BudgetItemPeriodService } from '../service/budget-item-period.service';
import { IBudgetItemPeriod } from '../budget-item-period.model';
import { IOperation } from 'app/entities/operation/operation.model';
import { OperationService } from 'app/entities/operation/service/operation.service';
import { IBudgetItem } from 'app/entities/budget-item/budget-item.model';
import { BudgetItemService } from 'app/entities/budget-item/service/budget-item.service';

import { BudgetItemPeriodUpdateComponent } from './budget-item-period-update.component';

describe('BudgetItemPeriod Management Update Component', () => {
  let comp: BudgetItemPeriodUpdateComponent;
  let fixture: ComponentFixture<BudgetItemPeriodUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let budgetItemPeriodFormService: BudgetItemPeriodFormService;
  let budgetItemPeriodService: BudgetItemPeriodService;
  let operationService: OperationService;
  let budgetItemService: BudgetItemService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BudgetItemPeriodUpdateComponent],
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
      .overrideTemplate(BudgetItemPeriodUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BudgetItemPeriodUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    budgetItemPeriodFormService = TestBed.inject(BudgetItemPeriodFormService);
    budgetItemPeriodService = TestBed.inject(BudgetItemPeriodService);
    operationService = TestBed.inject(OperationService);
    budgetItemService = TestBed.inject(BudgetItemService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call operation query and add missing value', () => {
      const budgetItemPeriod: IBudgetItemPeriod = { id: 456 };
      const operation: IOperation = { id: 65160 };
      budgetItemPeriod.operation = operation;

      const operationCollection: IOperation[] = [{ id: 17211 }];
      jest.spyOn(operationService, 'query').mockReturnValue(of(new HttpResponse({ body: operationCollection })));
      const expectedCollection: IOperation[] = [operation, ...operationCollection];
      jest.spyOn(operationService, 'addOperationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ budgetItemPeriod });
      comp.ngOnInit();

      expect(operationService.query).toHaveBeenCalled();
      expect(operationService.addOperationToCollectionIfMissing).toHaveBeenCalledWith(operationCollection, operation);
      expect(comp.operationsCollection).toEqual(expectedCollection);
    });

    it('Should call BudgetItem query and add missing value', () => {
      const budgetItemPeriod: IBudgetItemPeriod = { id: 456 };
      const budgetItem: IBudgetItem = { id: 98666 };
      budgetItemPeriod.budgetItem = budgetItem;

      const budgetItemCollection: IBudgetItem[] = [{ id: 1324 }];
      jest.spyOn(budgetItemService, 'query').mockReturnValue(of(new HttpResponse({ body: budgetItemCollection })));
      const additionalBudgetItems = [budgetItem];
      const expectedCollection: IBudgetItem[] = [...additionalBudgetItems, ...budgetItemCollection];
      jest.spyOn(budgetItemService, 'addBudgetItemToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ budgetItemPeriod });
      comp.ngOnInit();

      expect(budgetItemService.query).toHaveBeenCalled();
      expect(budgetItemService.addBudgetItemToCollectionIfMissing).toHaveBeenCalledWith(
        budgetItemCollection,
        ...additionalBudgetItems.map(expect.objectContaining)
      );
      expect(comp.budgetItemsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const budgetItemPeriod: IBudgetItemPeriod = { id: 456 };
      const operation: IOperation = { id: 89599 };
      budgetItemPeriod.operation = operation;
      const budgetItem: IBudgetItem = { id: 34160 };
      budgetItemPeriod.budgetItem = budgetItem;

      activatedRoute.data = of({ budgetItemPeriod });
      comp.ngOnInit();

      expect(comp.operationsCollection).toContain(operation);
      expect(comp.budgetItemsSharedCollection).toContain(budgetItem);
      expect(comp.budgetItemPeriod).toEqual(budgetItemPeriod);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBudgetItemPeriod>>();
      const budgetItemPeriod = { id: 123 };
      jest.spyOn(budgetItemPeriodFormService, 'getBudgetItemPeriod').mockReturnValue(budgetItemPeriod);
      jest.spyOn(budgetItemPeriodService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ budgetItemPeriod });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: budgetItemPeriod }));
      saveSubject.complete();

      // THEN
      expect(budgetItemPeriodFormService.getBudgetItemPeriod).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(budgetItemPeriodService.update).toHaveBeenCalledWith(expect.objectContaining(budgetItemPeriod));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBudgetItemPeriod>>();
      const budgetItemPeriod = { id: 123 };
      jest.spyOn(budgetItemPeriodFormService, 'getBudgetItemPeriod').mockReturnValue({ id: null });
      jest.spyOn(budgetItemPeriodService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ budgetItemPeriod: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: budgetItemPeriod }));
      saveSubject.complete();

      // THEN
      expect(budgetItemPeriodFormService.getBudgetItemPeriod).toHaveBeenCalled();
      expect(budgetItemPeriodService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBudgetItemPeriod>>();
      const budgetItemPeriod = { id: 123 };
      jest.spyOn(budgetItemPeriodService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ budgetItemPeriod });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(budgetItemPeriodService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareOperation', () => {
      it('Should forward to operationService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(operationService, 'compareOperation');
        comp.compareOperation(entity, entity2);
        expect(operationService.compareOperation).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareBudgetItem', () => {
      it('Should forward to budgetItemService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(budgetItemService, 'compareBudgetItem');
        comp.compareBudgetItem(entity, entity2);
        expect(budgetItemService.compareBudgetItem).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
