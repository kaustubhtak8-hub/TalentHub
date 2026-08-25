import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuditionService } from '../../../core/services/audition.service';

@Component({
  selector: 'app-create-audition',
  templateUrl: './create-audition.component.html',
  styleUrls: ['./create-audition.component.css']
})
export class CreateAuditionComponent implements OnInit {
  auditionForm!: FormGroup;
  isEditMode = false;
  auditionId!: number;
  loading = false;
  submitting = false;
  successMessage = '';
  errorMessage = '';

  categories = ['Acting', 'Singing', 'Dancing', 'Comedy', 'Theatre', 'Modeling', 'Music', 'Anchoring'];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private auditionService: AuditionService
  ) {}

  ngOnInit(): void {
    this.auditionForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      category: ['Acting', Validators.required],
      location: ['', Validators.required],
      auditionDate: [''],
      applicationDeadline: ['', [Validators.required, this.deadlineNotPastValidator]],
      requirements: [''],
      status: ['ACTIVE']
    }, {
      validators: this.dateOrderValidator
    });

    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.auditionId = +params['id'];
        this.loadAuditionDetails();
      }
    });
  }

  // Custom validator to ensure deadline is not in the past
  deadlineNotPastValidator(control: AbstractControl): { [key: string]: boolean } | null {
    if (!control.value) return null;
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const deadline = new Date(control.value);
    return deadline >= today ? null : { pastDeadline: true };
  }

  // Custom form-level validator to ensure audition date is after application deadline
  dateOrderValidator(control: AbstractControl): { [key: string]: boolean } | null {
    const deadline = control.get('applicationDeadline')?.value;
    const auditionDate = control.get('auditionDate')?.value;
    if (!deadline || !auditionDate) return null;
    
    const deadlineDate = new Date(deadline);
    const audDate = new Date(auditionDate);
    return audDate >= deadlineDate ? null : { dateOrderInvalid: true };
  }

  get f() { return this.auditionForm.controls; }

  loadAuditionDetails(): void {
    this.loading = true;
    this.auditionService.getAuditionDetails(this.auditionId).subscribe({
      next: (audition) => {
        this.auditionForm.patchValue({
          title: audition.title,
          description: audition.description,
          category: audition.category,
          location: audition.location,
          auditionDate: audition.auditionDate || '',
          applicationDeadline: audition.applicationDeadline,
          requirements: audition.requirements || '',
          status: audition.status
        });
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Could not load audition details.';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.auditionForm.invalid) {
      return;
    }

    this.submitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    const payload = this.auditionForm.value;

    if (this.isEditMode) {
      this.auditionService.updateAudition(this.auditionId, payload).subscribe({
        next: () => {
          this.successMessage = 'Audition post updated successfully!';
          setTimeout(() => this.router.navigate(['/organizer/dashboard']), 1500);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to update audition. Please verify inputs.';
          this.submitting = false;
        }
      });
    } else {
      this.auditionService.createAudition(payload).subscribe({
        next: () => {
          this.successMessage = 'Audition post created successfully!';
          setTimeout(() => this.router.navigate(['/organizer/dashboard']), 1500);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to create audition. Please verify inputs.';
          this.submitting = false;
        }
      });
    }
  }
}
