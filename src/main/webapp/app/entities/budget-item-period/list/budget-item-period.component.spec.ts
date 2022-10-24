import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { BudgetItemPeriodService } from '../service/budget-item-period.service';

import { BudgetItemPeriodComponent } from './budget-item-period.component';

describe('BudgetItemPeriod Management Component', () => {
  let comp: BudgetItemPeriodComponent;
  let fixture: ComponentFixture<BudgetItemPeriodComponent>;
  let service: BudgetItemPeriodService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'budget-item-period', component: BudgetItemPeriodComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [BudgetItemPeriodComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
                'filter[someId.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(BudgetItemPeriodComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BudgetItemPeriodComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(BudgetItemPeriodService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.budgetItemPeriods?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to budgetItemPeriodService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getBudgetItemPeriodIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getBudgetItemPeriodIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
