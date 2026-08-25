import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { HomeComponent } from './features/home/home.component';
import { AuditionListComponent } from './features/auditions/audition-list/audition-list.component';
import { AuditionDetailComponent } from './features/auditions/audition-detail/audition-detail.component';
import { ArtistProfileComponent } from './features/artist/artist-profile/artist-profile.component';
import { MyApplicationsComponent } from './features/artist/my-applications/my-applications.component';
import { OrganizerDashboardComponent } from './features/organizer/organizer-dashboard/organizer-dashboard.component';
import { CreateAuditionComponent } from './features/organizer/create-audition/create-audition.component';
import { OrganizerProfileComponent } from './features/organizer/organizer-profile/organizer-profile.component';
import { AuditionApplicantsComponent } from './features/organizer/audition-applicants/audition-applicants.component';
import { AdminDashboardComponent } from './features/admin/admin-dashboard/admin-dashboard.component';
import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard';

const routes: Routes = [
  // Public Routes
  { path: 'home', component: HomeComponent },
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/register', component: RegisterComponent },
  { path: 'auditions', component: AuditionListComponent },
  { path: 'auditions/:id', component: AuditionDetailComponent },

  // Artist Protected Routes
  { 
    path: 'artist/profile', 
    component: ArtistProfileComponent, 
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ARTIST'] }
  },
  { 
    path: 'artist/dashboard', 
    component: MyApplicationsComponent, 
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ARTIST'] }
  },
  { 
    path: 'artist/applications', 
    component: MyApplicationsComponent, 
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ARTIST'] }
  },

  // Organizer Protected Routes
  { 
    path: 'organizer/dashboard', 
    component: OrganizerDashboardComponent, 
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ORGANIZER'] }
  },
  { 
    path: 'organizer/auditions/create', 
    component: CreateAuditionComponent, 
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ORGANIZER'] }
  },
  { 
    path: 'organizer/auditions/:id/edit', 
    component: CreateAuditionComponent, 
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ORGANIZER'] }
  },
  { 
    path: 'organizer/auditions/:id/applicants', 
    component: AuditionApplicantsComponent, 
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ORGANIZER'] }
  },
  { 
    path: 'organizer/profile', 
    component: OrganizerProfileComponent, 
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ORGANIZER'] }
  },

  // Admin Placeholder
  { 
    path: 'admin/dashboard', 
    component: AdminDashboardComponent, 
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN'] }
  },

  // Redirects
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: '**', redirectTo: '/home' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
