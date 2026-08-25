import { Component, OnInit } from '@angular/core';
import { ApplicationService } from '../../../core/services/application.service';

@Component({
  selector: 'app-my-applications',
  templateUrl: './my-applications.component.html',
  styleUrls: ['./my-applications.component.css']
})
export class MyApplicationsComponent implements OnInit {
  applications: any[] = [];
  loading = true;
  errorMessage = '';

  constructor(private applicationService: ApplicationService) {}

  ngOnInit(): void {
    this.loadApplications();
  }

  loadApplications(): void {
    this.loading = true;
    this.applicationService.getMyApplications().subscribe({
      next: (data) => {
        // Sort descending by id to get latest applications first
        this.applications = data.sort((a, b) => b.id - a.id);
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Could not load your applications.';
        this.loading = false;
      }
    });
  }

  getStatusClass(status: string): string {
    if (!status) return 'pending';
    return status.toLowerCase();
  }
}
