import {ComponentFixture, TestBed} from "@angular/core/testing";

import {UtilisateurService} from './utilisateurService';


describe('UtilisateurService', () => {
  let component: UtilisateurService;
  let fixture: ComponentFixture<UtilisateurService>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UtilisateurService]
    })
      .compileComponents();

    fixture = TestBed.createComponent(UtilisateurService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
