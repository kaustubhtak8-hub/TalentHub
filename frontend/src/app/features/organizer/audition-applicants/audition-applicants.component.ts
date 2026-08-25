import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApplicationService } from '../../../core/services/application.service';
import { AuditionService } from '../../../core/services/audition.service';

@Component({
  selector: 'app-audition-applicants',
  templateUrl: './audition-applicants.component.html',
  styleUrls: ['./audition-applicants.component.css']
})
export class AuditionApplicantsComponent implements OnInit {
  auditionId!: number;
  audition: any = null;
  applicants: any[] = [];
  loading = true;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private auditionService: AuditionService,
    private applicationService: ApplicationService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.auditionId = +params['id'];
      this.loadAuditionAndApplicants();
    });
  }

  loadAuditionAndApplicants(): void {
    this.loading = true;
    this.errorMessage = '';

    // Load audition details to display title
    this.auditionService.getAuditionDetails(this.auditionId).subscribe({
      next: (aud) => {
        this.audition = aud;
        
        // Load applicants list
        this.applicationService.getApplicationsForAudition(this.auditionId).subscribe({
          next: (apps) => {
            this.applicants = apps.sort((a, b) => b.id - a.id);
            this.loading = false;
          },
          error: (err) => {
            this.errorMessage = err.error?.message || 'Could not load applications for this audition.';
            this.loading = false;
          }
        });
      },
      error: () => {
        this.errorMessage = 'Could not load audition details.';
        this.loading = false;
      }
    });
  }

  updateStatus(appId: number, status: string): void {
    this.applicationService.updateApplicationStatus(appId, status).subscribe({
      next: () => {
        this.loadAuditionAndApplicants();
      },
      error: (err) => {
        alert(err.error?.message || 'Failed to update application status.');
      }
    });
  }

  getStatusClass(status: string): string {
    if (!status) return 'pending';
    return status.toLowerCase();
  }
}
