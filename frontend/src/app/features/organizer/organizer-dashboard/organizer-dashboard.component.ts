import { Component, OnInit } from '@angular/core';
import { AuditionService } from '../../../core/services/audition.service';

@Component({
  selector: 'app-organizer-dashboard',
  templateUrl: './organizer-dashboard.component.html',
  styleUrls: ['./organizer-dashboard.component.css']
})
export class OrganizerDashboardComponent implements OnInit {
  auditions: any[] = [];
  loading = true;
  errorMessage = '';

  constructor(private auditionService: AuditionService) {}

  ngOnInit(): void {
    this.loadMyAuditions();
  }

  loadMyAuditions(): void {
    this.loading = true;
    this.auditionService.getMyAuditions().subscribe({
      next: (data) => {
        this.auditions = data.sort((a, b) => b.id - a.id);
        this.loading = false;
      },
      error: (err) => {
        // If profile doesn't exist, it might return 400. That's fine, let's catch it.
        this.errorMessage = err.error?.message || 'Could not load your auditions. Please ensure your organization profile details are filled out first.';
        this.loading = false;
      }
    });
  }

  onDelete(id: number): void {
    if (confirm('Are you sure you want to delete this audition post? This action cannot be undone.')) {
      this.auditionService.deleteAudition(id).subscribe({
        next: () => {
          this.loadMyAuditions();
        },
        error: (err) => {
          alert(err.error?.message || 'Failed to delete audition.');
        }
      });
    }
  }
}
