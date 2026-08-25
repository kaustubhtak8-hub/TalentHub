import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProfileService } from '../../../core/services/profile.service';

@Component({
  selector: 'app-artist-profile',
  templateUrl: './artist-profile.component.html',
  styleUrls: ['./artist-profile.component.css']
})
export class ArtistProfileComponent implements OnInit {
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
      bio: [''],
      phone: ['', [Validators.pattern('^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$')]], // simple phone pattern
      location: [''],
      experience: [''],
      profileImageUrl: ['']
    });

    this.loadProfile();
  }

  get f() { return this.profileForm.controls; }

  loadProfile(): void {
    this.profileService.getArtistProfile().subscribe({
      next: (profile) => {
        this.profileForm.patchValue({
          bio: profile.bio || '',
          phone: profile.phone || '',
          location: profile.location || '',
          experience: profile.experience || '',
          profileImageUrl: profile.profileImageUrl || ''
        });
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Could not load your profile details.';
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

    this.profileService.updateArtistProfile(this.profileForm.value).subscribe({
      next: () => {
        this.successMessage = 'Your profile has been updated successfully!';
        this.saving = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update profile. Please check your inputs.';
        this.saving = false;
      }
    });
  }
}
