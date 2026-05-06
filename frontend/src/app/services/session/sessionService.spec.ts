import {ComponentFixture, TestBed} from "@angular/core/testing";

import {SessionService} from './sessionService';


describe('SessionService', () => {
  let component: SessionService;
  let fixture: ComponentFixture<SessionService>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessionService]
    })
      .compileComponents();

    fixture = TestBed.createComponent(SessionService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
