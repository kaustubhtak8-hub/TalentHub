import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuditionService } from '../../../core/services/audition.service';

@Component({
  selector: 'app-audition-list',
  templateUrl: './audition-list.component.html',
  styleUrls: ['./audition-list.component.css']
})
export class AuditionListComponent implements OnInit {
  auditions: any[] = [];
  filteredAuditions: any[] = [];
  loading = true;

  // Filter state
  searchText = '';
  selectedCategory = '';
  selectedLocation = '';

  // Options extracted from data
  categories = ['Acting', 'Singing', 'Dancing', 'Comedy', 'Theatre', 'Modeling', 'Music', 'Anchoring'];
  locations: string[] = [];

  constructor(
    private auditionService: AuditionService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Read route query parameters (for category filter from home page)
    this.route.queryParams.subscribe(params => {
      if (params['category']) {
        this.selectedCategory = params['category'];
      }
      this.loadAuditions();
    });
  }

  loadAuditions(): void {
    this.loading = true;
    this.auditionService.getActiveAuditions().subscribe({
      next: (data) => {
        this.auditions = data;
        // Dynamically extract unique locations from the auditions list
        const uniqueLocs = new Set(data.map(aud => aud.location).filter(Boolean));
        this.locations = Array.from(uniqueLocs).sort();
        
        this.applyFilters();
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredAuditions = this.auditions.filter(aud => {
      const matchSearch = !this.searchText || 
        aud.title.toLowerCase().includes(this.searchText.toLowerCase()) || 
        aud.description.toLowerCase().includes(this.searchText.toLowerCase());

      const matchCategory = !this.selectedCategory || 
        aud.category === this.selectedCategory;

      const matchLocation = !this.selectedLocation || 
        aud.location === this.selectedLocation;

      return matchSearch && matchCategory && matchLocation;
    });
  }

  resetFilters(): void {
    this.searchText = '';
    this.selectedCategory = '';
    this.selectedLocation = '';
    this.applyFilters();
  }
}
