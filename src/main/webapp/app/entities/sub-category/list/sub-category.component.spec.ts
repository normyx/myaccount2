import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SubCategoryService } from '../service/sub-category.service';

import { SubCategoryComponent } from './sub-category.component';

describe('SubCategory Management Component', () => {
  let comp: SubCategoryComponent;
  let fixture: ComponentFixture<SubCategoryComponent>;
  let service: SubCategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'sub-category', component: SubCategoryComponent }]), HttpClientTestingModule],
      declarations: [SubCategoryComponent],
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
      .overrideTemplate(SubCategoryComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SubCategoryComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SubCategoryService);

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
    expect(comp.subCategories?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to subCategoryService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getSubCategoryIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getSubCategoryIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
