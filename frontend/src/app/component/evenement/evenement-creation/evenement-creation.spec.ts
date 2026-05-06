import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EvenementCreation } from './evenement-creation';

describe('EvenementCreation', () => {
  let component: EvenementCreation;
  let fixture: ComponentFixture<EvenementCreation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EvenementCreation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EvenementCreation);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
