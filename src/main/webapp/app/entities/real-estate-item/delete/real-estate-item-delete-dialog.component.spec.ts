jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { RealEstateItemService } from '../service/real-estate-item.service';

import { RealEstateItemDeleteDialogComponent } from './real-estate-item-delete-dialog.component';

describe('RealEstateItem Management Delete Component', () => {
  let comp: RealEstateItemDeleteDialogComponent;
  let fixture: ComponentFixture<RealEstateItemDeleteDialogComponent>;
  let service: RealEstateItemService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [RealEstateItemDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(RealEstateItemDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(RealEstateItemDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RealEstateItemService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      })
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
