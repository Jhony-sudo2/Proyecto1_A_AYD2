import { Component } from '@angular/core';
import { UserService } from '../../Services/User/user.service';
import { Organization } from '../../interfaces/Organization';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-create',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create.component.html',
  styleUrl: './create.component.css'
})
export class CreateComponent {
  organizations: Organization[] = [];
  form;
  imagePreview: string | null = null;
  imageError: string | null = null;
  constructor(private service: UserService, private fb: FormBuilder) {
    this.form = this.fb.group({
      identification: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(30), Validators.pattern(/^[A-Za-z0-9-]+$/)]],
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(60)]],
      lastName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(60)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(120)]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9+()\-\s]{8,20}$/)]],
      imageUrl: ['', []],
      nacionality: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(60)]],
      organization: [null as number | null, [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(64), Validators.pattern(/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d).{8,64}$/)]],
      rol: 1
    });
  }

  ngOnInit() {
    this.service.getOrganizations().subscribe({
      next: (response) => (this.organizations = response),
      error: (err) => console.log(err),
    });
  }

  createUser() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const payload = this.form.getRawValue();
    this.service.saveUser(payload as any).subscribe({
      next: (response) => {
        Swal.fire({title:'ok',text:'usuario creado',icon:'success'})
        this.form.reset()
      },
      error: (err) => {
        Swal.fire({title:'error',text:err.error,icon:'error'})
      },
    });
  }

  onImageSelected(event: Event) {
    this.imageError = null;

    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) {
      this.clearImage();
      return;
    }

    const file = input.files[0];

    // Validaciones básicas
    const allowedTypes = ['image/png', 'image/jpeg', 'image/webp'];
    const maxBytes = 2 * 1024 * 1024; // 2MB

    if (!allowedTypes.includes(file.type)) {
      this.imageError = 'Formato no permitido. Usa PNG, JPG o WEBP.';
      this.clearImage(false);
      return;
    }

    if (file.size > maxBytes) {
      this.imageError = 'La imagen supera el tamaño máximo (2MB).';
      this.clearImage(false);
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      const base64 = String(reader.result); // DataURL: data:image/png;base64,....
      this.imagePreview = base64;

      // Guardar en el form (esto es lo que mandarás al backend)
      this.form.patchValue({ imageUrl: base64 });
      this.form.get('imageUrl')?.markAsDirty();
    };
    reader.onerror = () => {
      this.imageError = 'No se pudo leer la imagen.';
      this.clearImage(false);
    };
    reader.readAsDataURL(file);
  }

  clearImage(clearControl = true) {
    this.imagePreview = null;
    if (clearControl) this.form.patchValue({ imageUrl: '' });
  }
  hasError(controlName: string, error: string) {
    const c = this.form.get(controlName);
    return !!(c && c.touched && c.hasError(error));
  }

  isInvalid(controlName: string) {
    const c = this.form.get(controlName);
    return !!(c && c.touched && c.invalid);
  }
}
