import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuditionService } from '../../core/services/audition.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  latestAuditions: any[] = [];
  loading = true;
  isOrganizer = false;

  categories = [
    { name: 'Acting', icon: '🎭' },
    { name: 'Singing', icon: '🎤' },
    { name: 'Dancing', icon: '💃' },
    { name: 'Comedy', icon: '🃏' },
    { name: 'Theatre', icon: '🎬' },
    { name: 'Modeling', icon: '📸' },
    { name: 'Music', icon: '🎵' },
    { name: 'Anchoring', icon: '🎙️' }
  ];

  constructor(
    private auditionService: AuditionService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser.subscribe(user => {
      this.isOrganizer = user?.role === 'ORGANIZER';
    });

    this.auditionService.getActiveAuditions().subscribe({
      next: (data) => {
        // Sort descending by id to get latest postings first
        this.latestAuditions = data.sort((a, b) => b.id - a.id).slice(0, 3);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  postAuditionClick(): void {
    if (this.authService.isLoggedIn) {
      if (this.isOrganizer) {
        this.router.navigate(['/organizer/auditions/create']);
      } else {
        alert('You must be logged in as an Organizer to post auditions.');
      }
    } else {
      this.router.navigate(['/auth/login'], { queryParams: { returnUrl: '/organizer/auditions/create' } });
    }
  }

  filterByCategory(categoryName: string): void {
    this.router.navigate(['/auditions'], { queryParams: { category: categoryName } });
  }
}
