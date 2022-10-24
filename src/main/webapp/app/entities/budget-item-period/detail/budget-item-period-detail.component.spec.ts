import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BudgetItemPeriodDetailComponent } from './budget-item-period-detail.component';

describe('BudgetItemPeriod Management Detail Component', () => {
  let comp: BudgetItemPeriodDetailComponent;
  let fixture: ComponentFixture<BudgetItemPeriodDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BudgetItemPeriodDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ budgetItemPeriod: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(BudgetItemPeriodDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(BudgetItemPeriodDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load budgetItemPeriod on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.budgetItemPeriod).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
