import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { JwtInterceptor } from './core/interceptors/jwt.interceptor';

// Shared Components
import { NavbarComponent } from './shared/components/navbar/navbar.component';

// Features Components
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

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    NavbarComponent,
    HomeComponent,
    AuditionListComponent,
    AuditionDetailComponent,
    ArtistProfileComponent,
    MyApplicationsComponent,
    OrganizerDashboardComponent,
    CreateAuditionComponent,
    OrganizerProfileComponent,
    AuditionApplicantsComponent,
    AdminDashboardComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

