import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { BudgetItemService } from '../service/budget-item.service';

import { BudgetItemComponent } from './budget-item.component';

describe('BudgetItem Management Component', () => {
  let comp: BudgetItemComponent;
  let fixture: ComponentFixture<BudgetItemComponent>;
  let service: BudgetItemService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'budget-item', component: BudgetItemComponent }]), HttpClientTestingModule],
      declarations: [BudgetItemComponent],
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
      .overrideTemplate(BudgetItemComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BudgetItemComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(BudgetItemService);

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
    expect(comp.budgetItems?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to budgetItemService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getBudgetItemIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getBudgetItemIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
