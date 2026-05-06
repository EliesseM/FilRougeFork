import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DemandeDeRoleService} from './demandeDeRoleService';

describe('DemandeDeRoleService', () => {
  let component: DemandeDeRoleService;
  let fixture: ComponentFixture<DemandeDeRoleService>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DemandeDeRoleService]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DemandeDeRoleService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
