import {ComponentFixture, TestBed} from '@angular/core/testing';

import {InscriptionService} from './inscriptionService';

describe('InscriptionService', () => {
  let component: InscriptionService;
  let fixture: ComponentFixture<InscriptionService>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InscriptionService]
    })
      .compileComponents();

    fixture = TestBed.createComponent(InscriptionService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
