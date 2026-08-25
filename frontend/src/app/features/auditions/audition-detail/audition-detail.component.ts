import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuditionService } from '../../../core/services/audition.service';
import { ApplicationService } from '../../../core/services/application.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-audition-detail',
  templateUrl: './audition-detail.component.html',
  styleUrls: ['./audition-detail.component.css']
})
export class AuditionDetailComponent implements OnInit {
  auditionId!: number;
  audition: any = null;
  loading = true;
  submitting = false;

  // Auth Context
  isLoggedIn = false;
  isArtist = false;
  hasApplied = false;

  // Form State
  applicationMessage = '';
  successMessage = '';
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private auditionService: AuditionService,
    private applicationService: ApplicationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.auditionId = +params['id'];
      this.loadAuditionDetails();
    });

    this.authService.currentUser.subscribe(user => {
      this.isLoggedIn = !!user;
      this.isArtist = user?.role === 'ARTIST';
      if (this.isArtist) {
        this.checkIfAlreadyApplied();
      }
    });
  }

  loadAuditionDetails(): void {
    this.loading = true;
    this.auditionService.getAuditionDetails(this.auditionId).subscribe({
      next: (data) => {
        this.audition = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Could not load audition details.';
        this.loading = false;
      }
    });
  }

  checkIfAlreadyApplied(): void {
    this.applicationService.getMyApplications().subscribe({
      next: (apps) => {
        this.hasApplied = apps.some(app => app.auditionId === this.auditionId);
      }
    });
  }

  submitApplication(): void {
    if (!this.isArtist) return;

    this.submitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.applicationService.applyToAudition(this.auditionId, this.applicationMessage).subscribe({
      next: () => {
        this.successMessage = 'Your application has been submitted successfully!';
        this.hasApplied = true;
        this.submitting = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to submit application. Please try again.';
        this.submitting = false;
      }
    });
  }
}
