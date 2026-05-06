import {ComponentFixture, TestBed} from '@angular/core/testing';
import {Evenement} from "../../models/evenement";
import {EvenementComponent} from "./evenement-component";


describe('Evenement', () => {
  let component: EvenementComponent;
  let fixture: ComponentFixture<EvenementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [component]
    })
      .compileComponents();

    fixture = TestBed.createComponent(EvenementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
