import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProfileService } from '../../../core/services/profile.service';

@Component({
  selector: 'app-organizer-profile',
  templateUrl: './organizer-profile.component.html',
  styleUrls: ['./organizer-profile.component.css']
})
export class OrganizerProfileComponent implements OnInit {
  profileForm!: FormGroup;
  loading = true;
  saving = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private profileService: ProfileService
  ) {}

  ngOnInit(): void {
    this.profileForm = this.fb.group({
      organizationName: ['', Validators.required],
      description: ['', Validators.required],
      location: ['', Validators.required],
      website: ['']
    });

    this.loadProfile();
  }

  get f() { return this.profileForm.controls; }

  loadProfile(): void {
    this.profileService.getOrganizerProfile().subscribe({
      next: (profile) => {
        this.profileForm.patchValue({
          organizationName: profile.organizationName || '',
          description: profile.description || '',
          location: profile.location || '',
          website: profile.website || ''
        });
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Could not load your organization profile details.';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      return;
    }

    this.saving = true;
    this.successMessage = '';
    this.errorMessage = '';

    this.profileService.updateOrganizerProfile(this.profileForm.value).subscribe({
      next: () => {
        this.successMessage = 'Organization profile updated successfully!';
        this.saving = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update profile. Please verify your inputs.';
        this.saving = false;
      }
    });
  }
}
