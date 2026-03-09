import { Component } from '@angular/core';
import { OrganizationService } from '../Services/Organization/organization.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NewOrganizationRequest, Organization, OrganizationUpdate } from '../interfaces/Organization';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-organization',
  imports: [CommonModule, FormsModule],
  templateUrl: './organization.component.html',
  styleUrl: './organization.component.css'
})
export class OrganizationComponent {
  constructor(private service: OrganizationService) { }

  organizations: Organization[] = [];
  editingId: number | null = null;
  orgCreate: NewOrganizationRequest = { name: '', image: '' };
  orgUpdate: OrganizationUpdate = { name: '', image: '', canCreateCongress: false };

  ngOnInit() {
    this.service.getAll().subscribe({
      next: (res) => { this.organizations = res; }
    });
  }

  onImageSelected(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      const base64 = reader.result as string;
      if (this.editingId) this.orgUpdate.image = base64;
      else this.orgCreate.image = base64;
    };
    reader.readAsDataURL(file);
  }

  startEdit(org: Organization) {
    this.editingId = org.id;
    this.orgUpdate = { name: org.name, image: org.image, canCreateCongress: org.canCreateCongress };
  }

  cancelEdit() {
    this.editingId = null;
    this.orgUpdate = { name: '', image: '', canCreateCongress: false };
  }

  saveCreate() {
    this.service.create(this.orgCreate).subscribe({
      next: (res) => {
        this.organizations.push(res);
        this.orgCreate = { name: '', image: '' };
        Swal.fire({ title: 'OK', text: 'Organización creada', icon: 'success' });
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

  saveUpdate() {
    this.service.update(this.editingId!, this.orgUpdate).subscribe({
      next: (res) => {
        const index = this.organizations.findIndex(o => o.id === this.editingId);
        if (index !== -1) this.organizations[index] = res;
        this.cancelEdit();
        Swal.fire({ title: 'OK', text: 'Organización actualizada', icon: 'success' });
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }
}
