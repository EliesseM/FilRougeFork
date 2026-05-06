import {CommonModule} from '@angular/common';
import {Component, inject} from '@angular/core';
import {Router} from '@angular/router';
import {AuthGoogleService} from '../../services/auth/auth-google.service';

const MODULES = [CommonModule];

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [MODULES],
  templateUrl: './dashboard-component.html',
  styleUrls: ['./dashboard-component.scss'],
})
export class DashboardComponent {
  authService = inject(AuthGoogleService);
  private router = inject(Router);

  profile = this.authService.profile;

  logOut() {
    this.authService.logout();
    this.router.navigate(['/login']).then(() => {
    });
  }
}
