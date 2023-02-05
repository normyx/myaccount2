import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RealEstateItemDetailComponent } from './real-estate-item-detail.component';

describe('RealEstateItem Management Detail Component', () => {
  let comp: RealEstateItemDetailComponent;
  let fixture: ComponentFixture<RealEstateItemDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RealEstateItemDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ realEstateItem: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(RealEstateItemDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(RealEstateItemDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load realEstateItem on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.realEstateItem).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
