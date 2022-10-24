import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BudgetItemFormService } from './budget-item-form.service';
import { BudgetItemService } from '../service/budget-item.service';
import { IBudgetItem } from '../budget-item.model';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { IApplicationUser } from 'app/entities/application-user/application-user.model';
import { ApplicationUserService } from 'app/entities/application-user/service/application-user.service';

import { BudgetItemUpdateComponent } from './budget-item-update.component';

describe('BudgetItem Management Update Component', () => {
  let comp: BudgetItemUpdateComponent;
  let fixture: ComponentFixture<BudgetItemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let budgetItemFormService: BudgetItemFormService;
  let budgetItemService: BudgetItemService;
  let categoryService: CategoryService;
  let applicationUserService: ApplicationUserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BudgetItemUpdateComponent],
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
      .overrideTemplate(BudgetItemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BudgetItemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    budgetItemFormService = TestBed.inject(BudgetItemFormService);
    budgetItemService = TestBed.inject(BudgetItemService);
    categoryService = TestBed.inject(CategoryService);
    applicationUserService = TestBed.inject(ApplicationUserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Category query and add missing value', () => {
      const budgetItem: IBudgetItem = { id: 456 };
      const category: ICategory = { id: 91617 };
      budgetItem.category = category;

      const categoryCollection: ICategory[] = [{ id: 94144 }];
      jest.spyOn(categoryService, 'query').mockReturnValue(of(new HttpResponse({ body: categoryCollection })));
      const additionalCategories = [category];
      const expectedCollection: ICategory[] = [...additionalCategories, ...categoryCollection];
      jest.spyOn(categoryService, 'addCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ budgetItem });
      comp.ngOnInit();

      expect(categoryService.query).toHaveBeenCalled();
      expect(categoryService.addCategoryToCollectionIfMissing).toHaveBeenCalledWith(
        categoryCollection,
        ...additionalCategories.map(expect.objectContaining)
      );
      expect(comp.categoriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call ApplicationUser query and add missing value', () => {
      const budgetItem: IBudgetItem = { id: 456 };
      const account: IApplicationUser = { id: 51857 };
      budgetItem.account = account;

      const applicationUserCollection: IApplicationUser[] = [{ id: 88578 }];
      jest.spyOn(applicationUserService, 'query').mockReturnValue(of(new HttpResponse({ body: applicationUserCollection })));
      const additionalApplicationUsers = [account];
      const expectedCollection: IApplicationUser[] = [...additionalApplicationUsers, ...applicationUserCollection];
      jest.spyOn(applicationUserService, 'addApplicationUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ budgetItem });
      comp.ngOnInit();

      expect(applicationUserService.query).toHaveBeenCalled();
      expect(applicationUserService.addApplicationUserToCollectionIfMissing).toHaveBeenCalledWith(
        applicationUserCollection,
        ...additionalApplicationUsers.map(expect.objectContaining)
      );
      expect(comp.applicationUsersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const budgetItem: IBudgetItem = { id: 456 };
      const category: ICategory = { id: 82029 };
      budgetItem.category = category;
      const account: IApplicationUser = { id: 85064 };
      budgetItem.account = account;

      activatedRoute.data = of({ budgetItem });
      comp.ngOnInit();

      expect(comp.categoriesSharedCollection).toContain(category);
      expect(comp.applicationUsersSharedCollection).toContain(account);
      expect(comp.budgetItem).toEqual(budgetItem);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBudgetItem>>();
      const budgetItem = { id: 123 };
      jest.spyOn(budgetItemFormService, 'getBudgetItem').mockReturnValue(budgetItem);
      jest.spyOn(budgetItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ budgetItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: budgetItem }));
      saveSubject.complete();

      // THEN
      expect(budgetItemFormService.getBudgetItem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(budgetItemService.update).toHaveBeenCalledWith(expect.objectContaining(budgetItem));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBudgetItem>>();
      const budgetItem = { id: 123 };
      jest.spyOn(budgetItemFormService, 'getBudgetItem').mockReturnValue({ id: null });
      jest.spyOn(budgetItemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ budgetItem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: budgetItem }));
      saveSubject.complete();

      // THEN
      expect(budgetItemFormService.getBudgetItem).toHaveBeenCalled();
      expect(budgetItemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBudgetItem>>();
      const budgetItem = { id: 123 };
      jest.spyOn(budgetItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ budgetItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(budgetItemService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCategory', () => {
      it('Should forward to categoryService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(categoryService, 'compareCategory');
        comp.compareCategory(entity, entity2);
        expect(categoryService.compareCategory).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
