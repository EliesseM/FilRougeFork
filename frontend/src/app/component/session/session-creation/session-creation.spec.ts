import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SessionCreation } from './session-creation';

describe('SessionCreation', () => {
  let component: SessionCreation;
  let fixture: ComponentFixture<SessionCreation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessionCreation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SessionCreation);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
