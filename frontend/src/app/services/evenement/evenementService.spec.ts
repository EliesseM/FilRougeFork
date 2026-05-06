import {ComponentFixture, TestBed} from '@angular/core/testing';

import {EvenementService} from './evenementService';

describe('EvenementService', () => {
  let component: EvenementService;
  let fixture: ComponentFixture<EvenementService>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EvenementService]
    })
      .compileComponents();

    fixture = TestBed.createComponent(EvenementService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
