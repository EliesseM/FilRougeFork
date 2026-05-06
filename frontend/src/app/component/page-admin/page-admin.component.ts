import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AdminService, DemandeDeRoleDto, PageResponse} from '../../services/admin/adminService';

@Component({
  selector: 'app-page-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './page-admin.component.html',
  styleUrls: ['./page-admin.component.scss']
})
export class PageAdminComponent implements OnInit {

  loading = false;
  counts: { demandes: number; total: number } = {demandes: 0, total: 0};
  demandesPage!: PageResponse<DemandeDeRoleDto>;
  pageSize = 10;

  constructor(private adminService: AdminService) {
  }

  ngOnInit(): void {
    this.loadCounts();
    this.loadDemandes(0);
  }

  loadCounts(): void {
    this.adminService.getCounts().subscribe(c => this.counts = c);
  }

  loadDemandes(page: number): void {
    this.loading = true;
    this.adminService.getPendingDemandes(page, this.pageSize).subscribe({
      next: data => {
        this.demandesPage = data;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  approuverDemande(id: number): void {
    this.adminService.approuverDemande(id).subscribe(() => {
      this.loadDemandes(this.demandesPage.number);
      this.loadCounts();
    });
  }

  refuserDemande(id: number): void {
    this.adminService.refuserDemande(id).subscribe(() => {
      this.loadDemandes(this.demandesPage.number);
      this.loadCounts();
    });
  }

  getPagesArray(totalPages: number): number[] {
    return Array.from({length: totalPages}, (_, i) => i);
  }
}
